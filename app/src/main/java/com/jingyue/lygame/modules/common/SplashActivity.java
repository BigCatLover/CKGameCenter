package com.jingyue.lygame.modules.common;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.utils.AppJumpUtils;
import com.jingyue.lygame.utils.SettingUtil;
import com.laoyuegou.android.lib.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-17 17:04
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class SplashActivity extends BaseActivity implements DialogInterface.OnClickListener {

    public static final int PERMISSIONS_REQUEST_CODE = 200;
    public static final int SINGLE_PERMISSIONS_REQUEST_CODE = 201;
    private final List<String> mPermissions = new ArrayList<>();

    //权限申请对话框
    private AlertDialog phoneInfoMD;
    private AlertDialog positionMD;
    private AlertDialog storageMD;
    private final List<AlertDialog> requestMD = new ArrayList<>();
    private boolean isInited = false;
    private boolean isCheckPermission = false;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isCheckPermission) {
                start();
                LogUtils.e("onReceive - start ");
            }
        }
    };

    @Override
    public int getLayoutId() {
        return EMPTY_LAYOUT;
    }

    @Override
    public void onBackPressed() {
        AppJumpUtils.exitApp(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.welcomeTheme);
        registerReceiver(mBroadcastReceiver, new IntentFilter(AppConstants.ACTION_INITED));
        if (!SettingUtil.readBoolean(this, KeyConstant.KEY_FIRST_OEPN, false)) {
            if (checkPermission()) {
                isCheckPermission = true;
                if (AppConstants.isInited) {
                    start();
                    LogUtils.e("checkPermission true - start ");
                }
            }
        } else {
            isCheckPermission = true;
            if (AppConstants.isInited) {
                start();
                LogUtils.e("checkPermission false - start ");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void start() {
        SettingUtil.write(SplashActivity.this, KeyConstant.KEY_FIRST_OEPN, true);
        //LoginActivity.open(SplashActivity.this, LoginActivity.BACK_EXIT_LOGIN_MAIN);
        //WebViewActivity.start(SplashActivity.this, AppConstants.WEB_APP, "https://s3-test-imgx.gank.tv/dao/dist/gamecenter/#/mainpage/comments");
        //Utils.startActivity(this,MainActivity.class);
        MainActivity.start(this);
        //RcmdDetailActivity.open(this,"362959");
        //Utils.startActivity(this, RegisterActivity.class);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        final boolean startLogin = intent.getBooleanExtra(AppJumpUtils.KEY_START_LOGIN,false);
        if(!startLogin){
            finish();
        }else {
            start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {//搜索权限申请
            LogUtils.d(requestCode + "," + permissions + "," + grantResults);
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PERMISSION_GRANTED) {
                    requestMD.get(i).show();
                    return;
                }
            }
            if (checkPermission()) {
                start();
            }
        } else if (requestCode == SINGLE_PERMISSIONS_REQUEST_CODE) {//单个权限申请
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PERMISSION_GRANTED) {
                    requestMD.get(i).show();
                    return;
                }
            }
            if (checkPermission()) {
                start();
            }
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            mPermissions.clear();
            requestMD.clear();
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PERMISSION_GRANTED) {
                mPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (storageMD == null) {
                    storageMD = new AlertDialog.Builder(this)
                            .setMessage(R.string.a_0183)
                            .setTitle(R.string.a_0184)
                            .setPositiveButton(R.string.a_0133, this)
                            .setNegativeButton(R.string.a_0134, this)
                            .setCancelable(false)
                            .show();
                } else {
                    storageMD.show();
                }
                requestMD.add(storageMD);
                return false;
            }
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PERMISSION_GRANTED) {
                mPermissions.add(Manifest.permission.READ_PHONE_STATE);
                if (phoneInfoMD == null) {
                    phoneInfoMD = new AlertDialog.Builder(this)
                            .setMessage(R.string.a_0181)
                            .setTitle(R.string.a_0182)
                            .setPositiveButton(R.string.a_0133, this)
                            .setNegativeButton(R.string.a_0134, this)
                            .setCancelable(false)
                            .show();
                } else {
                    phoneInfoMD.show();
                }
                requestMD.add(phoneInfoMD);
                return false;
            }
            /*if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PERMISSION_GRANTED) {
                mPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                positionMD = new AlertDialog.Builder(this)
                        .setMessage("我们需要获取位置信息，为了方便用户找到您；否则您将无法正常使用小工匠师傅端")
                        .setTitle("请允许获取位置信息")
                        .setPositiveButton(R.string.yes, this)
                        .setNegativeButton(R.string.no, this)
                        .show();
                requestMD.add(positionMD);
            }*/
            /*if (mPermissions.size() > 0) {
                requestPermissions(mPermissions.toArray(
                        new String[mPermissions.size()]), PERMISSIONS_REQUEST_CODE);
            } else {
                return true;
            }*/
        } else {
            return true;
        }
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            AppJumpUtils.exitApp(this);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dialog == storageMD) {//存储空间
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SINGLE_PERMISSIONS_REQUEST_CODE);
            } else if (dialog == positionMD) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, SINGLE_PERMISSIONS_REQUEST_CODE);
            } else if (dialog == phoneInfoMD) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, SINGLE_PERMISSIONS_REQUEST_CODE);
            }
        }
    }
}
