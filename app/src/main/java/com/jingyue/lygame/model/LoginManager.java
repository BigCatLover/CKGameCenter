package com.jingyue.lygame.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.CookieManager;

import com.google.gson.JsonObject;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.LoginInfoBean;
import com.jingyue.lygame.bean.LoginInfoBean_Table;
import com.jingyue.lygame.bean.LoginstateBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.events.LoginEvent;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.modules.personal.LoginActivity;
import com.jingyue.lygame.utils.AppJumpUtils;
import com.jingyue.lygame.utils.AuthCodeUtil;
import com.jingyue.lygame.utils.GlobalParamsUtils;
import com.jingyue.lygame.utils.SettingUtil;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.utils.EBus;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.lygame.libadapter.HttpRequestFactory;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-12 17:14
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class LoginManager implements ILogin {

    private LoginInfoBean loginInfo;
    private String hsToken;
    private String preff;
    private String stuff;

    private static final AtomicReference<LoginManager> INSTANCE = new AtomicReference<LoginManager>();

    private LoginManager() {
        init(Utils.getContext());
    }

    private Context mContext;

    public static LoginManager getInstance() {
        for (; ; ) {
            LoginManager current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new LoginManager();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(10);
            sb.append(number);
        }
        return sb.toString();
    }

    /**
     * 请用token
     *
     * @return
     */
    @Deprecated
    public String getHsToken() {
        return isLogin() ? getEncryptHsToken() : "";
    }

    public String getEncryptHsToken() {
        final String hsToken = this.hsToken;
        return !TextUtils.isEmpty(hsToken) ? (preff.concat(hsToken).concat(stuff)) : "";
    }

    public LoginInfoBean getLoginInfo() {
        try {
            return (LoginInfoBean) loginInfo.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LoginInfoBean();
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        final String act = SettingUtil.readString(mContext, KeyConstant.KEY_TOKEN, "");
        SQLite.select().from(LoginInfoBean.class)
                .where(LoginInfoBean_Table.accesstoken.eq(act))
                .async().querySingleResultCallback(new QueryTransaction.QueryResultSingleCallback<LoginInfoBean>() {
            @Override
            public void onSingleQueryResult(QueryTransaction transaction, @Nullable LoginInfoBean loginInfoBean) {
                LogUtils.e("-----init login manager", "login bean is init" + loginInfoBean);
                loginInfo = loginInfoBean;
                if (loginInfo == null) {
                    loginInfo = new LoginInfoBean();
                } else {
                    hsToken = loginInfoBean.h5Token;
                    preff = getRandomString(2);
                    stuff = getRandomString(2);
                    GlobalParamsUtils.getInstance().resetHttpFactory();
                }
            }
        }).execute();
    }

    @Override
    public boolean isLogin() {
        return loginInfo != null && loginInfo.isLogin;
    }

    @Override
    public void login(final BaseImpl context, String userName, String passWord) {
        CookieManager.getInstance().acceptCookie();
        HttpRequestFactory.retrofit().create(ApiService.class)
                .login(userName,
                        passWord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressObserver<BaseResponse<LoginInfoBean>>(context, context != null) {
                    @Override
                    public void onSuccess(BaseResponse<LoginInfoBean> response) {
                        saveLoginInfo(response.realData, context);
                    }
                });
    }

    public void saveLoginInfo(LoginInfoBean infoBean, BaseImpl context) {
        if (infoBean != null) {
            loginInfo = infoBean;
            loginInfo.isLogin = true;
            loginInfo.loginTime = System.currentTimeMillis();
            loginInfo.h5Token = hsToken = createHsToken(infoBean);
            loginInfo.async().save();
            SettingUtil.write(mContext, KeyConstant.KEY_TOKEN, infoBean.accesstoken);
            SettingUtil.write(mContext, KeyConstant.KEY_EXPIRE, infoBean.expaireTime);

            if (loginInfo.isScoreValid()) {
                ToastUtils.showShortSafe(mContext.getString(R.string.a_0286) +
                        loginInfo.addScore +
                        mContext.getString(R.string.a_0287));
            }

            preff = getRandomString(2);
            stuff = getRandomString(2);
            GlobalParamsUtils.getInstance().resetHttpFactory();
            //登录广播必须放在http初始化之后
            EBus.getDefault().post(new LoginEvent(LoginEvent.LOGIN));
            updateUserInfo(context, false);
        }
    }


    @Override
    public void loginOut(BaseImpl base) {
        HttpRequestFactory.retrofit().create(ApiService.class)
                .loginOut(loginInfo.h5Token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ProgressObserver<BaseResponse>(base, base != null) {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        loginInfo.isLogin = false;
                        loginInfo.async().save();
                        SettingUtil.delete(mContext, KeyConstant.KEY_TOKEN);
                        EBus.getDefault().post(new LoginEvent(LoginEvent.LOGOUT));
                    }
                });
        AppJumpUtils.startRecommend(base.getContext());
    }

    @Override
    public void checkLoginState(BaseImpl base) {
        HttpRequestFactory.retrofit().create(ApiService.class)
                .loginState(loginInfo.h5Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressObserver<BaseResponse<LoginstateBean>>(base, base != null) {
                    @Override
                    public void onSuccess(BaseResponse<LoginstateBean> response) {
                        loginInfo.icon = response.realData.icon;
                        loginInfo.async().save();
                        EventBus.getDefault().post(new LoginEvent(LoginEvent.UPDATE_STATE));
                    }
                });
    }


    public static String createHsToken(LoginInfoBean bean) {
        if (bean != null) {
            final String clientKey = GlobalParamsUtils.getInstance().httpParams().clientKey;
            LogUtils.e(" HS_APPKEY = " + clientKey);
            String usernameEcode = AuthCodeUtil.authcodeEncode(bean.identifier, clientKey);
            String passwordEcode = AuthCodeUtil.authcodeEncode(bean.accesstoken, clientKey);
            JsonObject jsonObject = new JsonObject();
            if (usernameEcode != null) {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(usernameEcode);
                usernameEcode = m.replaceAll("");
            }
            if (passwordEcode != null) {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(passwordEcode);
                passwordEcode = m.replaceAll("");
            }
            jsonObject.addProperty("identify", usernameEcode);
            jsonObject.addProperty("accesstoken", passwordEcode);
            String jsonEncode = jsonObject.toString();
            String hsToken = AuthCodeUtil.authcodeEncode(jsonEncode, clientKey);
            hsToken = hsToken.replaceAll("\n", "");
            LogUtils.e("createHsToken", "hs-token=" + hsToken);
            return hsToken;
        }
        return null;
    }

    /**
     * 重新登录
     *
     * @param context 必须是activity的context 因为需要startactivityforresult
     */
    public void reLogin(Context context) {
        loginInfo.isLogin = false;
        loginInfo.h5Token = "";
        loginInfo.accesstoken = "";
        loginInfo.async().save();
        SettingUtil.delete(context, KeyConstant.KEY_TOKEN);
        LoginActivity.open(context, LoginActivity.BACK_BACK_LOGIN_BACK);
    }

    public void updateUserInfo(BaseImpl base, boolean showProsgress) {
        HttpRequestFactory.retrofit().create(ApiService.class)
                .updateUserInfo(LoginManager.getInstance().getLoginInfo().h5Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressObserver<BaseResponse<LoginInfoBean>>(base, showProsgress) {
                    @Override
                    public void onSuccess(BaseResponse<LoginInfoBean> response) {
                        loginInfo.id = response.realData.id;
                        loginInfo.avatar = response.realData.avatar;
                        loginInfo.username = response.realData.username;
                        loginInfo.score = response.realData.score;
                        loginInfo.commentHoner = response.realData.commentHoner;
                        loginInfo.async().save();
                        EBus.getDefault().post(new LoginEvent(LoginEvent.UPDATE_LOGIN_INFO));
                    }
                });
    }
}
