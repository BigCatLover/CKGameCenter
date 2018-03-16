package com.jingyue.lygame.utils;

import android.content.Context;
import android.text.TextUtils;

import com.jingyue.lygame.bean.DeviceBean;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.model.LoginManager;
import com.laoyuegou.android.lib.utils.AppUtils;
import com.laoyuegou.android.lib.utils.UrlEncodeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-24 11:28
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * http 相关参数
 */
public class HttpParams {
    public static final String FROM = "from";//1-WEB、2-WAP、3-Android、4-IOS、5-WP
    public Map<String, String> headers;
    public Map<String, String> params;
    public String appid;
    public String clientId;
    public String appKey;
    public String clientKey;

    public static final String CODE_RSA_KEY_ERROR = "1001";//1001	请求KEY错误	rsakey	解密错误
    public static final String CODE_SESSION_ERROR = "1002";//1002	登陆已过期, 重新登录	session过期	session过期 返回信息CODE

    public void init(Context context) {
        appid = AppUtils.getMetaData(context, "HS_APPID");
        clientId = AppUtils.getMetaData(context, "HS_CLIENTID");
        appKey = AppUtils.getMetaData(context, "HS_APPKEY");
        clientKey = AppUtils.getMetaData(context, "HS_CLIENTKEY");
    }

    public Map<String, String> headers() {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.clear();
        /**
         * 事件记录用做区分app
         */
        headers.put("Auth-Appkey", appid);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }


    public Map<String, String> params(DeviceBean deviceBean) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.clear();

        if (LoginManager.getInstance().getLoginInfo().isLogin) {
            //params.put(KeyConstant.KEY_TOKEN, UrlEncodeUtils.encode(LoginManager.getInstance().getEncryptHsToken()));
            params.put(KeyConstant.KEY_TOKEN, LoginManager.getInstance().getEncryptHsToken());
        }
        params.put("aid", appid);

        //统一添加设备码
        if (deviceBean != null && !TextUtils.isEmpty(deviceBean.deviceId)) {
            params.put("deviceId", deviceBean.deviceId);
        }

        if (deviceBean != null && !TextUtils.isEmpty(deviceBean.deviceinfo)) {
            params.put("deviceInfo", deviceBean.deviceinfo);
        }
        if (deviceBean != null && !TextUtils.isEmpty(deviceBean.local_ip)) {
            params.put("loginIp", deviceBean.login_ip);
        }
        return params;
    }

    @Override
    public String toString() {
        return "HttpParams{" +
                "headers=" + headers() +
                ", params=" + params +
                ", appid='" + appid + '\'' +
                ", clientId='" + clientId + '\'' +
                ", appKey='" + appKey + '\'' +
                ", clientKey='" + clientKey + '\'' +
                '}';
    }
}
