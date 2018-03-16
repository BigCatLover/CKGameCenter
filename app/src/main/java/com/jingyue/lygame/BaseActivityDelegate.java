package com.jingyue.lygame;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jingyue.lygame.widget.LoadingDialog;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-22 15:38
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 *
 *    主要是为了减少base activity中的基础功能代码
 */
public class BaseActivityDelegate {

    private final int MSG_HIDEN_KEYBOARD = BaseActivity.MSG_HIDEN_KEYBOARD;

    private final int MSG_SHOW_DIALOG = BaseActivity.MSG_SHOW_DIALOG;
    private final int MSG_SHOW_FORCE_DIALOG = BaseActivity.MSG_SHOW_FORCE_DIALOG;
    private final int MSG_DISMISS_DIALOG = BaseActivity.MSG_DISMISS_DIALOG;

    private Handler mBaseHandler;
    private BaseActivity mBase;
    private LoadingDialog mLoadingDialog;

    public Handler getHandler() {
        return mBaseHandler;
    }

    public void onCreate(BaseActivity baseActivity) {
        mBase = baseActivity;
        initHandler();
    }

    private void initHandler() {
        mBaseHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_HIDEN_KEYBOARD:
                        hideSoftInput();
                        break;
                    case MSG_SHOW_DIALOG:
                        showLoadingDialog();
                        break;
                    case MSG_SHOW_FORCE_DIALOG:
                        showLoadingDialog(false);
                        break;
                    case MSG_DISMISS_DIALOG:
                        dismissLoadingDialog();
                        break;
                }
                return false;
            }
        });
    }


    public void ondestroy(){
        mBaseHandler = null;
        mIm = null;
        mLoadingDialog = null;
        mBase = null;
    }

    /************************************************
     *        loading view start
     ************************************************/
    public void showLoadingDialog() {
        showLoadingDialog(true);
    }

    public void showLoadingDialog(final boolean cancel) {
        mBase.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog == null) {
                    mLoadingDialog = new LoadingDialog(mBase);
                    mLoadingDialog.setTouchCanceled(cancel);
                    mLoadingDialog.setCancelable(cancel);
                }
                if(!mLoadingDialog.isShowing()){
                    mLoadingDialog.show();
                }
            }
        });
    }

    public void dismissLoadingDialog() {
        try {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
                mLoadingDialog = null;
            }
        } catch (Exception ex) {
            mLoadingDialog = null;
            LogUtils.e(ex.getMessage());
        }
    }

    /************************************************
     *                 软键盘相关
     ************************************************/
    private InputMethodManager mIm;
    private boolean mShowSoftInput = false;

    protected void hideSoftInput(View view) {
        if (mIm == null) {
            mIm = (InputMethodManager) mBase.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        mShowSoftInput = false;
        mIm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void hideSoftInput() {
        if (mIm == null) {
            mIm = (InputMethodManager) mBase.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        mShowSoftInput = false;
        mIm.hideSoftInputFromWindow(mBase.getWindow().getDecorView().getWindowToken(), 0);
    }

    protected void showSoftInput(View view) {
        showSoftInput(view, false);
    }

    protected void showSoftInput(final View view, boolean delay) {
        if (mIm == null) {
            mIm = (InputMethodManager) mBase.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        mShowSoftInput = true;
        if (mBaseHandler == null) mBaseHandler = new Handler();
        final WeakReference<View> viewWeakReference = new WeakReference<View>(view);
        mBaseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIm != null){
                    final View view = viewWeakReference.get();
                    if(view != null){
                        mIm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }
        }, delay ? 500 : 0);
    }

    public void showError(String errMes) {
        ToastUtils.showShort(errMes);
    }

}
