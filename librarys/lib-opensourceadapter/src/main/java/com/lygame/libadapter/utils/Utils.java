package com.lygame.libadapter.utils;


import android.text.TextUtils;

import java.io.IOException;

import okhttp3.RequestBody;
import okio.Buffer;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-23 20:53
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class Utils {

    public static void main(String[] args) {
        System.out.println("1231231".matches(REGX));
    }

    public static final String REGX = "^[0-9]+\\*?[0-9]*$";

    public static boolean checkNULL(String cache) {
        return TextUtils.isEmpty(cache)||!cache.matches(REGX);
    }

    public static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
