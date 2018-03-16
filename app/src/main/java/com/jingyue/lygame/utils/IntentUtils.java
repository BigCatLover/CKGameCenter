package com.jingyue.lygame.utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;


import com.jingyue.lygame.R;
import com.laoyuegou.android.lib.utils.ToastUtils;

import java.io.File;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-06-18 0:29
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class IntentUtils {

    private static final String TAG = IntentUtils.class.getSimpleName();

    public static void newCall(Context context, String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:".concat(phoneNumber));
            intent.setData(data);

            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, ">>>>>>IntentUtils#newCall : " + e.getMessage());
        }
    }

    public static void toContactDetail(Context context, String lookupKey, long contactId) {
        StringBuilder sb = new StringBuilder();
        sb.append("content://com.android.contacts/contacts/lookup/")
                .append(lookupKey)
                .append("/")
                .append(contactId);
        Uri personUri = Uri.parse(sb.toString());
        Intent contactIntent = new Intent();
        contactIntent.setData(personUri);
        contactIntent.setAction(Intent.ACTION_VIEW);
        context.startActivity(contactIntent);
    }

    public static void toContact(Context context) {

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_VIEW);

        intent.setData(Contacts.People.CONTENT_URI);

        context.startActivity(intent);
    }

    public static boolean toApp(Context context, ComponentName componentName) {
        try {
            Intent intent = new Intent();
            intent.setComponent(componentName);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, ">>>>>>IntentUtils#toApp : " + e.getMessage());
        }
        return false;
    }

    public static boolean toApp(Context context, String packageName, String className) {
        try {
            if(TextUtils.isEmpty(packageName)){
                return false;
            }
            Intent intent;
            if (TextUtils.isEmpty(className)) {
                PackageManager manager = context.getPackageManager();
                intent = manager.getLaunchIntentForPackage(packageName);
            } else {
                intent = Intent.makeMainActivity(new ComponentName(packageName, className));
            }
            if (intent == null) {
                return false;
            }
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, ">>>>>>IntentUtils#toApp : " + e.getMessage());
        }
        return false;
    }

    public static final void downloadApp(Context context, String url) {
        try {
            Intent webIntent = new Intent(Intent.ACTION_VIEW);
            webIntent.setData(Uri.parse(url));
            webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(webIntent);
        } catch (ActivityNotFoundException e) {
            ToastUtils.showShort(R.string.a_0001);
        }
    }

    public static void openUrl(Context context, String url) {
        if(!url.startsWith("http://")&&!url.startsWith("https://")){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//Min SDK 15
        context.startActivity(intent);
    }

    /**
     * 调用的是系统的音乐播放器
     */
    public static void openMusic(Context context, String id, String minetype) {
        try {
            Intent musicIntent = new Intent(Intent.ACTION_VIEW);
            if (!TextUtils.isEmpty(minetype)) {
                minetype = "audio/*";
                //add this will jump to playlist
                //"vnd.android.cursor.dir/playlist";
            }
            musicIntent.setDataAndType(Uri.parse(MediaStore.Audio.Media.INTERNAL_CONTENT_URI.toString().concat(File.separator).concat(id)), minetype);
            musicIntent.putExtra("withtabs", true); // 显示tab选项卡
//            Intent j = Intent.createChooser(musicIntent, "Choose an application to open with:");
            context.startActivity(musicIntent);
        } catch (Exception e) {
            Log.e(TAG, ">>>>>>IntentUtils#openMusic : " + e.getMessage());
        }
    }

    public static void toMusicPlay(Context context) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= 15) {
                Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");//Min SDK 8
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, ">>>>>>IntentUtils#toMusicPlay : " + e.getMessage());
        }
    }
}
