package com.lygame.libadapter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.laoyuegou.android.lib.utils.LogUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-25 16:00
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class SaveCookiesInterceptor implements Interceptor {
    private static final String COOKIE_PREF = "cookies_prefs";
    private Context mContext;

    public SaveCookiesInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        //set-cookie可能为多个
        if (!response.headers("set-cookie").isEmpty()) {
            List<String> cookies = response.headers("set-cookie");
            String cookie = encodeCookie(cookies);
            saveCookie(request.url().toString(), request.url().host(), cookie);
        }
        return response;
    }

    public static void syncPhpCookie(Context context, String domain, String url) {
        String cookie = AddCookiesInterceptor.getCookie(context, url, domain);
        String phpCookie = getPhpCookie(cookie);
        syncCookieInternal(context, url, phpCookie);
    }

    private static String getPhpCookie(String cookie) {
        if (!TextUtils.isEmpty(cookie)) {
            String[] cookies = cookie.split(";");
            if (cookies != null) {
                for (final String s : cookies) {
                    if (s.contains("PHPSESSID")) {
                        return s;
                    }
                }
            }
        }
        return "";
    }

    private static void syncCookieInternal(Context context, String url, String cookies) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.setCookie(url, cookies);//cookies是在OkHttp中获得的cookie
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        } else {
            CookieSyncManager.getInstance().sync();
        }
    }

    //整合cookie为唯一字符串
    private String encodeCookie(List<String> cookies) {
        StringBuilder sb = new StringBuilder();
        Set<String> set = new HashSet<>();
        for (String cookie : cookies) {
            String[] arr = cookie.split(";");
            for (String s : arr) {
                if (set.contains(s)) continue;
                set.add(s);

            }
        }

        Iterator<String> ite = set.iterator();
        while (ite.hasNext()) {
            String cookie = ite.next();
            sb.append(cookie).append(";");
        }

        int last = sb.lastIndexOf(";");
        if (sb.length() - 1 == last) {
            sb.deleteCharAt(last);
        }

        return sb.toString();
    }

    //保存cookie到本地，这里我们分别为该url和host设置相同的cookie，其中host可选
    //这样能使得该cookie的应用范围更广
    private void saveCookie(String url, String domain, String cookies) {
        SharedPreferences sp = mContext.getSharedPreferences(COOKIE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (TextUtils.isEmpty(url)) {
            LogUtils.e("url is null!!!");
        } else {
            editor.putString(Uri.parse(url).getHost(), cookies);
        }

        if (!TextUtils.isEmpty(domain)) {
            editor.putString(domain, cookies);
        }

        editor.apply();

    }
}
