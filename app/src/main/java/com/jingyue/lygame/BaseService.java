package com.jingyue.lygame;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.jingyue.lygame.widget.LoadingDialog;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-29 16:54
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public abstract class BaseService extends Service implements BaseImpl {

    private CompositeDisposable disposables2Destroy = new CompositeDisposable();
    private LoadingDialog mLoadingDialog;

    private List<BaseMvpPresenter> mMvpPresenters;

    public void add(BaseMvpPresenter mvpPresenter) {
        if (mvpPresenter == null) return;
        if (mMvpPresenters == null) {
            mMvpPresenters = new ArrayList<>();
        }
        mMvpPresenters.add(mvpPresenter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables2Destroy.dispose();
        for (final BaseMvpPresenter mvpPresenter : mMvpPresenters) {
            mvpPresenter.ondestroy();
        }
        mMvpPresenters.clear();
    }

    public void showError(String errMes) {
    }

    public void showNull() {
    }

    public String name() {
        return getClass().getSimpleName();
    }

    public boolean addRxStop(Disposable disposable) {
        return false;
    }

    public boolean addRxDestroy(Disposable disposable) {
        disposables2Destroy.add(disposable);
        return false;
    }

    public void remove(Disposable disposable) {
        disposables2Destroy.remove(disposable);
    }

    public void showProgress(String msg) {
        ToastUtils.showShort(msg);
    }

    public void dismissProgress() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    public Context getContext() {
        return this;
    }
}