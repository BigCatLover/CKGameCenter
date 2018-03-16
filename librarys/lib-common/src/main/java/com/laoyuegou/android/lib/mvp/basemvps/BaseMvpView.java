package com.laoyuegou.android.lib.mvp.basemvps;

import android.content.Context;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-23 14:49
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public interface BaseMvpView {
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
