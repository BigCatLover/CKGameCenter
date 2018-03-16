package com.jingyue.lygame.utils;


import android.content.Context;

import com.jingyue.lygame.bean.DeviceBean;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.lygame.libadapter.HttpRequestFactory;
import com.lygame.libadapter.utils.BasicParamsInterceptor;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-24 15:57
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 *
 *  全局公共配置参数
 */
public class GlobalParamsUtils {
    private HttpParams httpParams;
    private DeviceBean deviceBean;
    /**
     * 重置httpClient
     * 重置httpclient主要是为了将通用的params和header添加逻辑由底层实现
     */
    public void resetHttpFactory() {
        HttpRequestFactory.getInstance()
                .resetDefualt(new BasicParamsInterceptor.Builder()
                        .addHeaderParamsMap(httpParams.headers())
                        .addQueryParamsMap(httpParams.params(deviceBean))
                        .build());
    }

    public GlobalParamsUtils init(Context context) {
        mContext = context.getApplicationContext();
        httpParams = new HttpParams();
        httpParams.init(context);
        deviceBean = DeviceUtil.getDeviceBean(context);
        HttpRequestFactory.getInstance()
                .resetDefualt(new BasicParamsInterceptor.Builder()
                        .addHeaderParamsMap(httpParams.headers())
                        .addQueryParamsMap(httpParams.params(deviceBean))
                        .build());
        return this;
    }

    public HttpParams httpParams() {
        return httpParams;
    }

    private static final AtomicReference<GlobalParamsUtils> INSTANCE = new AtomicReference<GlobalParamsUtils>();

    private GlobalParamsUtils() {
        init(Utils.getContext());
    }

    private Context mContext;

    public static GlobalParamsUtils getInstance() {
        for (; ; ) {
            GlobalParamsUtils current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new GlobalParamsUtils();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

}
