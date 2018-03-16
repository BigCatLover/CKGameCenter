package com.jingyue.lygame.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.bumptech.glide.util.Util;
import com.laoyuegou.android.lib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanglei on 2017/6/14.
 */

public class WebDataControl {
    private static Context context;
    public static final String USER_SELECTED_LABEL1 = "user_selected_label1";
    public static final String USER_SELECTED_LABEL2 = "user_selected_label2";
    public static final String USER_SELECTED_LABEL3 = "user_selected_label3";

    protected static SharedPreferences label_sp = null;

    public static void ensureSp() {
        context = Utils.getContext();
        if (label_sp == null) {
            label_sp = context.getSharedPreferences(WebDataControl.class.getName(), Context.MODE_PRIVATE);
        }
    }

    public static List<String> getUserLabel() {
        List<String> labels = new ArrayList<>(3);
        String temp = "";
        temp = label_sp.getString(USER_SELECTED_LABEL1, "");
        if (!TextUtils.isEmpty(temp)) {
            labels.add(temp);
        }
        temp = label_sp.getString(USER_SELECTED_LABEL2, "");
        if (!TextUtils.isEmpty(temp)) {
            labels.add(temp);
        }
        temp = label_sp.getString(USER_SELECTED_LABEL3, "");
        if (!TextUtils.isEmpty(temp)) {
            labels.add(temp);
        }
        return labels;
    }

    public static void saveUserLabel1(String value) {
        ensureSp();
        label_sp.edit().putString(USER_SELECTED_LABEL1, value).commit();
    }

    public static void saveUserLabel2(String value) {
        ensureSp();
        label_sp.edit().putString(USER_SELECTED_LABEL2, value).commit();
    }

    public static void saveUserLabel3(String value) {
        ensureSp();
        label_sp.edit().putString(USER_SELECTED_LABEL3, value).commit();
    }

}
