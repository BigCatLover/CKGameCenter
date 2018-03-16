package com.lygame.libadapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.laoyuegou.android.lib.utils.GsonUtils;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.lygame.libadapter.utils.AddCookiesInterceptor;
import com.lygame.libadapter.utils.CacheNetworkInterceptor;
import com.lygame.libadapter.utils.LoggingInterceptor;
import com.lygame.libadapter.utils.SaveCookiesInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-22 11:20
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class HttpRequestFactory {

    /**
     * 京东数据显示用户能忍受的平均最长超时等待时间是10s
     */
    public static final int DEFAULT_TIME_OUT = 10;

    private static final AtomicReference<HttpRequestFactory> INSTANCE = new AtomicReference<HttpRequestFactory>();

    private HttpRequestFactory() {
    }

    public static void init(Context context, String baseUrl) {
        mContext = context.getApplicationContext();
        sBaseUrl = baseUrl;
    }

    private static Context mContext;

    public static HttpRequestFactory getInstance() {
        for (; ; ) {
            HttpRequestFactory current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new HttpRequestFactory();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    private static String sBaseUrl;
    private static RetrofitDelegate sRetrofitDelegate;
    private static HashMap<String, RetrofitDelegate> retrofitMap;
    private OkHttpClient client;

    public static class RetrofitDelegate {

        private HashMap<Class, Object> serviceContainer;

        private Retrofit retrofit;

        public RetrofitDelegate(Retrofit retrofit) {
            this.retrofit = retrofit;
            serviceContainer = new HashMap<>();
        }

        public <T> T create(Class<T> service) {
            T result = (T) serviceContainer.get(service);
            if (result == null) {
                LogUtils.d("Class = " + service);
                result = retrofit.create(service);
                serviceContainer.put(service, result);
                Set<Class> classSet = serviceContainer.keySet();
                for (Class clz : classSet) {
                    LogUtils.d("class cache = " + clz);
                }
            }
            return result;
        }

        public void resetDefault(Retrofit retrofit) {
            this.retrofit = retrofit;
            serviceContainer.clear();
        }
    }

    public static RetrofitDelegate retrofit() {
        return retrofitWithBaseUrl(sBaseUrl, true);
    }

    /**
     * @param needCache
     * @return
     */
    public static RetrofitDelegate retrofitWithBaseUrl(String url, boolean needCache) {
        if (TextUtils.isEmpty(url)) {
            LogUtils.e("获取retrofit时，传递的url为空！防止奔溃传入上一次初始化retrofit对象。");
            return sRetrofitDelegate;
        }
        if (retrofitMap == null) {
            retrofitMap = new HashMap<>();
        }
        RetrofitDelegate retrofitDelegate = retrofitMap.get(url);
        if (retrofitDelegate == null) {
            sRetrofitDelegate = retrofitDelegate = new RetrofitDelegate(retrofit(url));
            if (needCache) {
                LogUtils.e("url = " +url  + "\n retrofit" + sRetrofitDelegate.retrofit);
                retrofitMap.put(url, retrofitDelegate);
            }
        }
        return retrofitDelegate;
    }


    private static Retrofit retrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getInstance().getHttpClient())//添加自定义OkHttpClient
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.getInstance().getGson()))
                .build();
    }

    public OkHttpClient getHttpClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new CacheNetworkInterceptor())
                    .addInterceptor(new LoggingInterceptor()) //日志，可以配置 level 为 BASIC / HEADERS / BODY
                    .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                    .addInterceptor(new SaveCookiesInterceptor(mContext))
                    .addInterceptor(new AddCookiesInterceptor(mContext))
                    .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                    .cache(provideCache())
                    .retryOnConnectionFailure(true)
                    .build();
        }
        return client;
    }

    private Cache cache;

    public Cache provideCache() {
        if (cache == null) {
            cache = new Cache(mContext.getCacheDir(), 10 * 1240 * 1024);
        }
        return cache;
    }

    public RetrofitDelegate resetDefualt(Interceptor interceptor) {
        if (interceptor == null) {
            LogUtils.e("interceptor can not be null!");
        }
        if (sRetrofitDelegate == null) {
            retrofit();
        }
        client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(new CacheNetworkInterceptor())
                .addInterceptor(new LoggingInterceptor()) //日志，可以配置 level 为 BASIC / HEADERS / BODY
                .addInterceptor(new SaveCookiesInterceptor(mContext))
                .addInterceptor(new AddCookiesInterceptor(mContext))
                .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .cache(provideCache())
                .retryOnConnectionFailure(true)
                .build();
        final RetrofitDelegate retrofitDelegate = retrofitMap.get(sBaseUrl);
        retrofitDelegate.resetDefault(new Retrofit.Builder()
                .baseUrl(sBaseUrl)
                .client(client)//添加自定义OkHttpClient
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.getInstance().getGson()))
                .build());
        retrofitMap.put(sBaseUrl, retrofitDelegate);
        return sRetrofitDelegate;
    }

    public void clearCache() {
        try {
            client.cache().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCookie(String domain) {
        return AddCookiesInterceptor.getCookie(mContext, domain, domain);
    }
}
