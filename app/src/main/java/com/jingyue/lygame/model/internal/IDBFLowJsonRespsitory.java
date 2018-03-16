package com.jingyue.lygame.model.internal;

import android.text.TextUtils;

import com.jingyue.lygame.bean.AppDataBase;
import com.jingyue.lygame.bean.JsonDataBean;
import com.jingyue.lygame.bean.JsonDataBean_Table;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.model.LoginManager;
import com.laoyuegou.android.lib.utils.GsonUtils;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.RxMap;
import com.lygame.libadapter.HttpRequestFactory;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-11-20 15:48
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * 对象存储model
 */
public abstract class IDBFLowJsonRespsitory<BeanContainer> implements IJsonRepository<BaseResponse<BeanContainer>> {

    public IDBFLowJsonRespsitory() {
        DBRpManager.getInstance();
    }

    private BaseResponse convert(String json) {
        if(TextUtils.isEmpty(json)) return null;
        return GsonUtils.getInstance().fromJson(json, GsonUtils.type(BaseResponse.class, getTClass()));
    }

    @Override
    public Observable<BaseResponse<BeanContainer>> getEntry(String url, Map<String, String> queryMap, final boolean needCache, boolean forceRefresh) {

        final String key = getCacheKey(url, queryMap);

        //get cache
        Observable<BaseResponse<BeanContainer>> fromCache = Observable.create(new ObservableOnSubscribe<BaseResponse<BeanContainer>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<BaseResponse<BeanContainer>> e) throws Exception {
                final String cacheJson = getCache(key);
                if (!TextUtils.isEmpty(cacheJson)) {
                    BaseResponse baseResponse = convert(cacheJson);
                    LogUtils.e("Cache hint  | key = " + key);
                    baseResponse.errCode = BaseResponse.INVLIDE_CODE;
                    baseResponse.fromCache = true;
                    e.onNext(baseResponse);
                }else{
                    e.onNext(new BaseResponse<BeanContainer>());
                }
                e.onComplete();

            }
        });

        //save cache
        Observable<BaseResponse<BeanContainer>> fromNet = getEntryFromNet(url, queryMap)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, BaseResponse<BeanContainer>>() {
                    @Override
                    public BaseResponse<BeanContainer> apply(@NonNull String tBaseResponse) throws Exception {
                        if (needCache) saveCache(key, tBaseResponse);
                        return convert(tBaseResponse);
                    }
                });

        return Observable.fromArray(fromCache,fromNet)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(new Function<Observable<BaseResponse<BeanContainer>>, ObservableSource<? extends BaseResponse<BeanContainer>>>() {
                    @Override
                    public ObservableSource<? extends BaseResponse<BeanContainer>> apply(@NonNull Observable<BaseResponse<BeanContainer>> baseResponseObservable) throws Exception {
                        LogUtils.e("ProgressObserver.java");
                        return baseResponseObservable;
                    }
                });
    }


    public String getCache(String url) throws Exception {
        JsonDataBean jsonDataBean = SQLite.select().from(JsonDataBean.class).where(JsonDataBean_Table.url.eq(url))
                .querySingle();
        if (jsonDataBean != null && !TextUtils.isEmpty(jsonDataBean.json)) {
            return jsonDataBean.json;
        }
        return "";
    }

    public Observable<String> getEntryFromNet(String url, Map<String, String> queryMap) {
        Map<String, String> headMap = getHeader();
        return HttpRequestFactory.retrofit().create(ApiService.class)
                .executeGet(url, headMap, queryMap).map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(@NonNull ResponseBody responseBody) throws Exception {
                        try{
                            return responseBody.string();
                        }catch (Exception e){
                            return "";
                        }
                    }
                });
    }

    public void saveCache(final String url, final String baseBeanList) {
        if (!TextUtils.isEmpty(baseBeanList)) {
            FlowManager.getDatabase(AppDataBase.class)
                    .beginTransactionAsync(new ITransaction() {
                        @Override
                        public void execute(DatabaseWrapper databaseWrapper) {
                            final JsonDataBean jsonDataBean = new JsonDataBean();
                            jsonDataBean.json = baseBeanList;
                            jsonDataBean.url = url;
                            jsonDataBean.save();
                        }
                    }).execute();
        }
    }

    @Override
    public String getCacheKey(String url, Map<String, String> queryMap) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (queryMap != null && !queryMap.isEmpty()) {
            Set<String> keys = queryMap.keySet();
            sb.append("?");
            for (String key : keys) {
                sb.append(key).append("=").append(queryMap.get(key));
            }
        }
        return sb.toString();
    }

    @Override
    public void clearCache() {
        SQLite.delete(JsonDataBean.class).execute();
    }

    public Map<String, String> getHeader() {
        String h5Token = LoginManager.getInstance().getEncryptHsToken();
        if (TextUtils.isEmpty(h5Token)) {
            h5Token = "";
        }
        return new RxMap<String, String>()
                .put(KeyConstant.KEY_TOKEN, h5Token)
                .build();
    }
}

