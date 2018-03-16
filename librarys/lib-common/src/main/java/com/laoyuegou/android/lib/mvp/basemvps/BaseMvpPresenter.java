package com.laoyuegou.android.lib.mvp.basemvps;

import com.laoyuegou.android.lib.mvp.BaseImpl;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-23 14:49
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public abstract class BaseMvpPresenter<T extends BaseMvpView> {

    protected T mView;

    protected BaseImpl baseImpl;

    /**
     * activity fragment不能和view进行耦合
     * 他们是 1..n 的关系
     * BaseImpl是activity的抽象
     * @param mView
     * @param baseImpl
     */
    public BaseMvpPresenter(T mView, BaseImpl baseImpl) {
        this.mView = mView;
        this.baseImpl = baseImpl;
    }

    /**
     * 释放引用O
     */
    public void ondestroy(){
        mView = null;
        baseImpl = null;
    }

}
