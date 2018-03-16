package com.jingyue.lygame.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.jingyue.lygame.constant.KeyConstant;

import java.util.Set;

/**
 * 应用配置情报处理对象
 */
public class SettingUtil {

    /**
     * 将 Boolean 类型内容写入SharedPreferences
     */
    public static void write(Context context, String key, Boolean value) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 将 String 类型内容写入SharedPreferences
     */
    public static void write(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 将 int 类型内容写入SharedPreferences
     */
    public static void write(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 将 float 类型内容写入SharedPreferences
     */
    public static void write(Context context, String key, float value) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * 将 long 类型内容写入SharedPreferences
     */
    public static void write(Context context, String key, long value) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 将 Set<String> 类型内容写入SharedPreferences
     */
    public static void write(Context context, String key, Set<String> value) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }


    /**
     * 从SharedPreferences读取 int
     */
    public static int readInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }
    /**
     * 从SharedPreferences读取 long
     */
    public static long readLong(Context context, String key, long defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);
    }
    /**
     * 从SharedPreferences读取 float
     */
    public static float readFloat(Context context, String key, float defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        return sp.getFloat(key, defaultValue);
    }

    /**
     * 从SharedPreferences读取 Stirng
     */
    public static String readString(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    /**
     * 从SharedPreferences读取 Boolean
     */
    public static boolean readBoolean(Context context, String key, Boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    /**
     * 从SharedPreferences读取 Set<String>
     */
    public static Set<String> readStringSet(Context context, String key, Set<String> defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        return sp.getStringSet(key, defaultValue);
    }

    /**
     * 从SharedPreferences删除指定的数据
     *
     * @param context
     * @param key
     */
    public static void delete(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 查询SharedPreferences是否包含指定的key
     *
     * @param context
     * @param key
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(KeyConstant.LAOYUEGOU_SP, Context.MODE_PRIVATE);
        return sp.contains(key);
    }
}
