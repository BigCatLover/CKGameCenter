package com.laoyuegou.android.lib.mvp;

import android.content.Context;

import io.reactivex.disposables.Disposable;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-25 15:22
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public interface BaseImpl {

    /**
     * 会影响事件统计
     * @return
     */
    String name();

    boolean addRxStop(Disposable disposable);

    boolean addRxDestroy(Disposable disposable);

    void remove(Disposable disposable);

    /**
     * 显示ProgressDialog
     */
    void showProgress(String msg);

    /**
     * 取消ProgressDialog
     */
    void dismissProgress();

    Context getContext();
}
