package com.laoyuegou.android.lib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.laoyuegou.android.lib.widget.ripple.Ripple;


/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-06-12 15:47
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class Utils {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(@NonNull Context context) {
        Utils.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }

    private static String lastOpenClass;
    private static long lastOpenTime = 0;

    public static void startActivity(Context context, Class tClass, Bundle bundle) {
        try {
            long now = System.currentTimeMillis();
            long del = Math.abs(now - lastOpenTime);
            if (del < 400 && lastOpenClass != null && lastOpenClass.equals(tClass.getName())) {
                return;
            }
            Intent intent = new Intent(context, tClass);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            context.startActivity(intent);
            lastOpenClass = tClass.getName();
            lastOpenTime = now;
        } catch (ActivityNotFoundException e) {
            LogUtils.e(e);
        }
    }

    public static void startActivityForResult(Context context, Class tClass, Bundle bundle, int requestCode) {
        try {
            Intent intent = new Intent(context, tClass);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, requestCode);
                return;
            } else {
                LogUtils.e("start activity for result must from activity");
            }
        } catch (ActivityNotFoundException e) {
            LogUtils.e(e);
        }
    }

    public static void startActivity(Context context, Class tClass) {
        startActivity(context, tClass, null);
    }

    public static void startActivityForResult(Context context, Class tClass, int requestCode) {
        startActivityForResult(context, tClass, null, requestCode);
    }

    public static void startActivityWithAnim(Context context, Class tClass, View v) {
        Ripple.startActivity(context, tClass, v);
    }

    public static void startActivityWithAnim(Context context, Class tClass, View v, Bundle bundle) {
        Ripple.startActivity(context, tClass, v, bundle);
    }


    public static void startActivityForResultWithAnim(Activity context, Class tClass, int requestCode, View v) {
        Ripple.startActivityForResult(context, tClass, requestCode, v);
    }

    public static void startActivityForResultWithAnim(Fragment context, Class tClass, int requestCode, View v) {
        Ripple.startActivityForResult(context, tClass, requestCode, v);
    }


}
