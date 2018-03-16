package com.jingyue.lygame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;

import com.jingyue.lygame.clickaction.internal.BaseSelfCountAction;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.events.EventDownloadEnd;
import com.jingyue.lygame.utils.AppJumpUtils;
import com.jingyue.lygame.utils.update.InstallDialog;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.BaseMvpActivity;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.lygame.libadapter.OpenEventCollectManager;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 所有activity的基类
 * Created by wang on 15/1/20.
 */
public abstract class BaseActivity extends BaseMvpActivity implements BaseImpl {

    public static final int EMPTY_LAYOUT = 0x0;

    public static final int MSG_HIDEN_KEYBOARD = 0X05;
    public static final int MSG_SHOW_DIALOG = 0X06;
    public static final int MSG_SHOW_FORCE_DIALOG = 0X09;
    public static final int MSG_DISMISS_DIALOG = 0X07;

    private boolean mNeedAnim = true;
    private BaseActivityDelegate mDelegate;
    private CompositeDisposable disposables2Stop;// 管理Stop取消订阅者者
    private CompositeDisposable disposables2Destroy;// 管理Destroy取消订阅者者
    private boolean isFirstIn = true;   //是否第一次进入当前页

    private BaseSelfCountAction mBaseSelfCountAction;

    protected void recordPageEvent(BaseSelfCountAction baseSelfCountAction){
        mBaseSelfCountAction = baseSelfCountAction;
        mBaseSelfCountAction.onRecord();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainApplication.isTopActivity(this) && !isFirstIn){
            onResumeFromHome();
        }
        isFirstIn = false;
        OpenEventCollectManager.getInstance().onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        OpenEventCollectManager.getInstance().onPause(this);
    }

    /**
     * 是否从home页返回
     */
    protected void onResumeFromHome(){
        //LogUtils.e("onResumeFromHome");
        if(mBaseSelfCountAction != null){
            mBaseSelfCountAction.onRecord();
        }
    }

    @org.greenrobot.eventbus.Subscribe
    public void onDownloadFinished(EventDownloadEnd downloadEnd){
        new InstallDialog(this).show();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        MainApplication.setCurrentActivity(this);
        final int layoutId = getLayoutId();
        if (layoutId > 0) {
            setContentView(layoutId);
        }
        mDelegate = new BaseActivityDelegate();
        mDelegate.onCreate(this);

        if (enableEventBus) {
            EventBus.getDefault().register(this);
        }
        if (disposables2Destroy != null) {
            throw new IllegalStateException("onCreate called multiple times");
        }
        disposables2Destroy = new CompositeDisposable();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public String name() {
        return getClass().getSimpleName();
    }

    /**
     * 显示ProgressDialog
     */
    public void showProgress(String msg) {
        mDelegate.showLoadingDialog();
    }

    @Override
    public void dismissProgress() {
        mDelegate.dismissLoadingDialog();
    }

    public boolean addRxStop(Disposable disposable) {
        if (disposables2Stop == null) {
            throw new IllegalStateException(
                    "addUtilStop should be called between onStart and onStop");
        }
        return disposables2Stop.add(disposable);
    }

    public boolean addRxDestroy(Disposable disposable) {
        if (disposables2Destroy == null) {
            throw new IllegalStateException(
                    "addUtilDestroy should be called between onCreate and onDestroy");
        }
        return disposables2Destroy.add(disposable);
    }

    public void remove(Disposable disposable) {
        if (disposables2Stop == null && disposables2Destroy == null) {
            throw new IllegalStateException("remove should not be called after onDestroy");
        }
        if (disposables2Stop != null) {
            disposables2Stop.remove(disposable);
        }
        if (disposables2Destroy != null) {
            disposables2Destroy.remove(disposable);
        }
    }

    public void onStart() {
        super.onStart();
        if (disposables2Stop != null) {
            throw new IllegalStateException("onStart called multiple times");
        }
        disposables2Stop = new CompositeDisposable();
    }

    public void onStop() {
        super.onStop();
        if (disposables2Stop == null) {
            throw new IllegalStateException("onStop called multiple times or onStart not called");
        }
        disposables2Stop.dispose();
        disposables2Stop = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaseSelfCountAction = null;
        if (disposables2Destroy == null) {
            throw new IllegalStateException(
                    "onDestroy called multiple times or onCreate not called");
        }
        if (enableEventBus && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        disposables2Destroy.dispose();
        disposables2Destroy = null;
        if (mDelegate != null) {
            mDelegate.ondestroy();
            mDelegate = null;
        }
    }

    private boolean enableEventBus = true;

    public abstract int getLayoutId();

    public void enableEventBus() {
        this.enableEventBus = true;
    }

    public static final int EXIT_ALLOW_TIME = AppConstants.EXIT_ALLOW_TIME;

    private long mFirstTimeExit;

    public void exitApp() {
        if (System.currentTimeMillis() - mFirstTimeExit > EXIT_ALLOW_TIME) {
            mFirstTimeExit = System.currentTimeMillis();
            ToastUtils.showShort(R.string.a_0180);
        } else {
            AppJumpUtils.exitApp(this);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mNeedAnim) {
            overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //登录完成返回页面
        if (requestCode == KeyConstant.LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                onLoginFailure();
            } else if (resultCode == RESULT_OK) {
                onLoginSucess();
            }
        }
    }

    /**
     * 当请求发起服务器返回{@link com.jingyue.lygame.bean.internal.BaseResponse#UN_LOGIN}
     * 则跳转登录页面进行登录，等完成功会结束登录页面，回调该接口
     * 对应的登录失败回调{@link BaseActivity#onLoginFailure()}
     */
    public void onLoginSucess() {}

    /**
     * 当请求发起服务器返回{@link com.jingyue.lygame.bean.internal.BaseResponse#UN_LOGIN}
     * 则跳转登录页面进行登录，如果取消登录，会回调该接口
     * 对应的登录成功回调{@link BaseActivity#onLoginSucess()}
     */
    public void onLoginFailure() {}

    public void showLoadingDialog() {
        mDelegate.showLoadingDialog();
    }

    public void showLoadingDialog(boolean cancel) {
        mDelegate.showLoadingDialog(cancel);
    }

    public void dismissLoadingDialog() {
        mDelegate.dismissLoadingDialog();
    }

    public void hideSoftInput(View view) {
        mDelegate.hideSoftInput(view);
    }

    public void hideSoftInput() {
        mDelegate.hideSoftInput();
    }

    public void showSoftInput(View view) {
        mDelegate.showSoftInput(view);
    }

    public void showSoftInput(View view, boolean delay) {
        mDelegate.showSoftInput(view, delay);
    }

    public void showLoading() {
        mDelegate.showLoadingDialog();
    }

    public void hideLoading() {
        mDelegate.dismissLoadingDialog();
    }

    public Context getContext() {
        return this;
    }

}
