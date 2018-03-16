package com.jingyue.lygame.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.events.VoiceChangeEvent;
import com.laoyuegou.android.lib.utils.EBus;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by zhanglei on 2017/6/5.
 */

public class ToolUtils {

    public static synchronized String encode(String val) {
        String encodeStr = Uri.encode(val, "utf-8");
        if (!StringUtils.isEmptyOrNull(encodeStr)) {
            encodeStr = encodeStr.replace(" ", "%20");
            encodeStr = encodeStr.replace("!", "%21");
//            encodeStr.replace("#", "%23");
//            encodeStr.replace("%", "%25");
//            encodeStr.replace("&", "%26");
            encodeStr = encodeStr.replace("'", "%27");
            encodeStr = encodeStr.replace("(", "%28");
            encodeStr = encodeStr.replace(")", "%29");
            encodeStr = encodeStr.replace("*", "%2A");
            encodeStr.replace("+", "%2B");
//            encodeStr.replace(",", "%2C");
//            encodeStr.replace("/", "%2F");
//            encodeStr.replace(":", "%3A");
//            encodeStr.replace(";", "%3B");
//            encodeStr.replace("=", "%3D");
//            encodeStr.replace("?", "%3F");
//            encodeStr.replace("@", "%40");
//            encodeStr.replace("[", "%5B");
//            encodeStr.replace("/", "%5C");
//            encodeStr.replace("]", "%5D");

            return encodeStr;
        }
        return "";
    }


    public static boolean isUtf8Url(String text) {
        text = text.toLowerCase();
        int p = text.indexOf("%");
        if (p != -1 && text.length() - p > 9) {
            text = text.substring(p, p + 9);
        }
        return Utf8codeCheck(text);
    }

    private static boolean Utf8codeCheck(String text) {
        String sign = "";
        if (text.startsWith("%e"))
            for (int i = 0, p = 0; p != -1; i++) {
                p = text.indexOf("%", p);
                if (p != -1)
                    p++;
                sign += p;
            }
        return sign.equals("147-1");
    }


    public static String getTime_zone() {
        String time_zone;
        time_zone = String.valueOf(TimeZone.getDefault().getRawOffset() / 1000);
        return time_zone;
    }

    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        String md5Str = hex.toString();
        int length = md5Str.length();
        if (length < 16) {
            md5Str = "1BF29B766F14C2DA";
        }
        return md5Str;
    }

    /**
     * 打开指定包名的App
     *
     * @param context     上下文
     * @param packageName 包名
     * @return true: 打开成功<br>false: 打开失败
     */
    public static boolean openAppByPackageName(Context context, String packageName) {
        Intent intent = getIntentByPackageName(context, packageName);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 根据包名获取意图
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 意图
     */
    private static Intent getIntentByPackageName(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }


    public static boolean isPkgInstalled(Context context, String pkgName) {
        if (pkgName == null) {
            return false;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    /**
     * 安装apk
     *
     * @param context
     * @param file
     */
    public static void installApk(Context context, Uri file) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setType("application/vnd.android.package-archive");
            intent.setData(file);
            intent.setDataAndType(file, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileUtils.getUriForFile(context.getApplicationContext(),
                    new File(file.getPath()));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 安装apk
     *
     * @param context
     * @param file
     */
    public static void installApk(Context context, File file) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context.getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".fileProvider", file);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void controlVolumeAndNotify(AudioManager mAudioManager,boolean isUp){
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, isUp ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        int newValue = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newValue, AudioManager.STREAM_MUSIC);
        EBus.getDefault().post(new VoiceChangeEvent(newValue));
    }

}
