package com.lygame.libadapter.utils;

import com.laoyuegou.android.lib.utils.LogUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-23 20:53
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class CacheNetworkInterceptor implements Interceptor {
    public static final String CACH_KEY = "Cache-Time";
    public static final long DEFAULT_CACH_TIME = HttpConfig.DEFAULT_CACH_TIME;
    public static final String DEFUALT_CACHE_HEADER = CACH_KEY + ":" + DEFAULT_CACH_TIME;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        //自定义的cach事件 对json进行缓存
        String cache = request.header(CACH_KEY);
        if (!Utils.checkNULL(cache)) {//缓存时间不为空
            LogUtils.d(request.url()+ " is cached || cache time : " + cache);
            Response response1 = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    //cache for cache seconds
                    .header("Cache-Control", "max-age="+cache)
                    .build();
            return response1;
        } else {
            return response;
        }
    }
}
