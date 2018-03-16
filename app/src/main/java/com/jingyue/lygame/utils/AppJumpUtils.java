package com.jingyue.lygame.utils;

import android.content.Context;
import android.content.Intent;

import com.jingyue.lygame.bean.LoginInfoBean;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.modules.common.MainActivity;
import com.jingyue.lygame.modules.common.SplashActivity;
import com.jingyue.lygame.modules.guide.GuideActivity;
import com.laoyuegou.android.lib.utils.Utils;


/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-06-13 15:31
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class AppJumpUtils {

    public static final String KEY_FORCE_CLOSE_APP = "key_force_close_app";
    public static final String KEY_START_LOGIN = "key_start_login";

    /**
     * exit app
     *
     * @param context
     */
    public static void exitApp(Context context) {
        if (context instanceof SplashActivity) {
            System.exit(0);
            return;
        }
        context = context.getApplicationContext();
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 登录
     *
     * @param context
     */
    public static void startLogin(Context context) {
        context = context.getApplicationContext();
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_START_LOGIN, true);
        context.startActivity(intent);
    }

    /**
     * 回到首页
     *
     * @param context
     */
    public static void startRecommend(Context context) {
        context = context.getApplicationContext();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_START_LOGIN, true);
        context.startActivity(intent);
    }

    /**
     * @return false 如果没有开启过引导页
     */
    public static boolean goToGuide(Context context) {
        if(AppConstants.IGNORE_GUIDE) return false;
        LoginInfoBean bean = LoginManager.getInstance().getLoginInfo();
        if (bean != null && !bean.isGuided) {
            Utils.startActivity(context, GuideActivity.class);
            return true;
        }
        return false;
    }

    /**
     * @return false 如果没有开启过引导页
     */
    public static boolean goToGuideForResult(Context context,int requestCode) {
        if(AppConstants.IGNORE_GUIDE) return false;
        LoginInfoBean bean = LoginManager.getInstance().getLoginInfo();
        if (bean != null && !bean.isGuided) {
            GuideActivity.start(context,requestCode);
            return true;
        }
        return false;
    }
}
