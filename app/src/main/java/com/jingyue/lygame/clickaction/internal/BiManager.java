package com.jingyue.lygame.clickaction.internal;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.interfaces.BiService;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.utils.GlobalParamsUtils;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.ToolUtils;
import com.jingyue.lygame.utils.rxJava.BaseObserver;
import com.laoyuegou.android.lib.utils.DebugUtils;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.RxMap;
import com.laoyuegou.android.lib.utils.Utils;
import com.luck.picture.lib.tools.DebugUtil;
import com.lygame.libadapter.HttpRequestFactory;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.AsyncModel;
import com.raizlabs.android.dbflow.structure.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 埋点的管理类
 * 1.app启动时上传;
 * 2.每xx分钟上传一次;
 * 3.app切到后台时上传;
 * 4.用户点击注销后上传.
 * <p/>
 * [
 * {
 * "eid":"事件名称(eventId)",
 * "uid":"用户(userId)",
 * "rt":10位时间戳(recordTime),
 * "ch":"渠道名(Channel)",
 * "v":"2.2.1(Version)",
 * "pf":平台(platform)(1:ios,2:android),
 * "p":[     //params
 * "自定义keyName1":"自定义值1",
 * "自定义keyName2":"自定义值2",
 * "自定义keyName3":"自定义值3",
 * ...
 * ]
 * },...
 * ]
 * Created by TonyChen on 15/8/28.
 */
public class BiManager {
    /**
     * 设置上传埋点间隔为20秒
     */
    private final static long UPLOAD_INTERVAL = TimeUnit.MILLISECONDS.toMicros(20);
    /**
     * 单线程池,保证顺序调用
     */
    private ExecutorService mExecutor;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable uploadTask;

    private class UploadTask implements Runnable {
        @Override
        public void run() {
            uploadData();
        }
    }

    private static final AtomicReference<BiManager> INSTANCE = new AtomicReference<BiManager>();

    private BiManager() {
        mExecutor = Executors.newSingleThreadExecutor();
        uploadTask = new UploadTask();
        mHandler.postDelayed(uploadTask, UPLOAD_INTERVAL);
    }

    private Context mContext;

    public static BiManager getInstance() {
        for (; ; ) {
            BiManager current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new BiManager();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    private void sendRetryMessage() {
        //retrey later
        if (null != mHandler) {
            mHandler.postDelayed(uploadTask, UPLOAD_INTERVAL);
        }
    }

    /**
     * 上传事件记录
     */
    public void uploadData() {
        Observable query = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<String> e) throws Exception {
                if (!NetworkUtils.hasNetwork(Utils.getContext())) {
                    sendRetryMessage();
                    return;
                }
                JSONArray jsonArray = null;
                List<BiAction> actions = SQLite.select()
                        .from(BiAction.class).where().queryList();
                BiAction actionTemp;
                if (actions != null && !actions.isEmpty()) {
                    jsonArray = new JSONArray();
                    JSONObject jsonObject = null;
                    if (actions.size() == 1) {
                        actionTemp = actions.get(0);
                        jsonObject = new JSONObject(actionTemp.event);
                        actionTemp.delete();
                        e.onNext(jsonObject.toString());
                        return;
                    }
                    for (final BiAction action : actions) {
                        try {
                            jsonObject = new JSONObject(action.event);
                            jsonArray.put(jsonObject);
                            action.delete();
                        } catch (JSONException e1) {
                            LogUtils.e("获取事件错误\n" + e1);
                        }
                    }
                }

                if (jsonArray == null || jsonArray.length() == 0) {
                    sendRetryMessage();
                    return;
                }

                e.onNext(jsonArray.toString());
            }
        });

        query.map(new Function<String, Observable<BaseResponse>>() {
            @Override
            public Observable apply(@io.reactivex.annotations.NonNull String str) throws Exception {
                long time = System.currentTimeMillis();
                time = time / 1000;
                String sign = getSignString(time);

                return HttpRequestFactory.retrofitWithBaseUrl(UrlConstant.BI_URL, true)
                        .create(BiService.class)
                        .logEvent(String.valueOf(time),
                                sign,
                                str);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new BaseObserver<Observable>("BiManager#uploadData") {

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Observable o) {
                        o.subscribe(new BaseObserver("BiManager#onNext") {
                            @Override
                            public void onComplete() {
                                super.onComplete();
                            }
                        });
                    }
                });
    }


    protected String getSignString(long time) {
        StringBuilder sb = new StringBuilder();

        sb.append("POST#");
        sb.append(BuildConfig.BI_URL).append("/stat/log2").append("#");
        sb.append(time).append("#");
        sb.append(GlobalParamsUtils.getInstance().httpParams().appid).append("#");
        sb.append("omg#");
        sb.append(AppConstants.APP_SECRET + "#");
        ArrayList<String> paramsList = getParamsList();
        if (null != paramsList) {
            Collections.sort(paramsList, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            });
            int size = paramsList.size();
            for (int i = 0; i < size; i++) {
                sb.append(paramsList.get(i));
            }
        }
        LogUtils.e("BaseService", "Sign String : " + sb.toString());
        return ToolUtils.stringToMD5(sb.toString());
    }

    private Map<String, String> mParams;

    protected void builtBaseParams() {
        if (mParams == null) {
            mParams = new HashMap<>();
        }
        mParams.put("appfrom", BuildConfig.FLAVOR);
        mParams.put("time_zone", ToolUtils.getTime_zone());
        mParams.put("platform", "2");
        mParams.put("region", "");
        mParams.put("appver", BuildConfig.VERSION_NAME);
        mParams.put("version_code", BuildConfig.VERSION_CODE + "");
        if(LoginManager.getInstance().isLogin()){
            mParams.put("user_id", LoginManager.getInstance().getLoginInfo().identifier);
        }
    }

    protected ArrayList<String> getParamsList() {
        builtBaseParams();
        ArrayList<String> paramsList = new ArrayList<>();
        Set<String> keys = mParams.keySet();
        try {
            for (String key : keys) {
                String val = mParams.get(key);
                if (null != val) {
                    try {
                        if (ToolUtils.isUtf8Url(val)) {
                            val = Uri.decode(val);
                        }
                    } catch (Exception ex) {

                    }
                    if (null != val) {
                        paramsList.add(key + "=" + ToolUtils.encode(val));
                    }
                }
            }
        } catch (Exception e) {
            String errStr = e.getMessage();
            LogUtils.e(errStr == null ? "" : errStr);
        }
        return paramsList;
    }

    private void writeDB(final String eventId, final JSONObject json) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<Boolean> e) throws Exception {
                final String data = json.toString();
                LogUtils.i("MD", "insert data:" + data);
                BiAction mBiAction = new BiAction();
                mBiAction.eventId = eventId;
                mBiAction.event = data;
                mBiAction.save();
                e.onNext(true);
            }
        }).subscribeOn(Schedulers.io()).subscribe(new BaseObserver<Boolean>("BiManager#writeDB") {
            @Override
            public void onNext(@io.reactivex.annotations.NonNull Boolean aBoolean) {
                uploadData();
            }
        });
    }

    /**
     * 记录点击事件
     *
     * @param eventId 事件Id
     * @param params  参数
     */
    public void logEvent(String eventId, Map<String, String> params) {
        if (StringUtils.isEmptyOrNull(eventId)) {
            throw new IllegalArgumentException("event id is null");
        }
        JSONObject json = new JSONObject();

        try {
            /*json.put(BiAction.EID, eventId);
            json.put(BiAction.RT, System.currentTimeMillis() / 1000);
            final String userId = LoginManager.getInstance().getLoginInfo().id;
            if (!StringUtils.isEmptyOrNull(userId)) {
                json.put(BiAction.EUID, userId);
            }*/
            if (params != null && params.size() > 0) {
                Iterator iterator = params.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    json.put(entry.getKey().toString(), entry.getValue());
                }
                //for test
                if(DebugUtils.DEBUG){
                    json.put("test", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                }
            }
            writeDB(eventId, json);
        } catch (JSONException e) {
            LogUtils.e(e.getMessage());
        }
    }

}
