package com.jingyue.lygame.utils.update;

import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-29 16:48
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class UpdatePresenter extends BaseMvpPresenter<UpdateView> implements Observer {

    private UpdateModel mCommonModel;
    private BaseImpl baseImpl;

    /**
     * activity fragment不能和view进行耦合
     * 他们是 1..n 的关系
     * BaseImpl是activity的抽象
     *
     * @param mView
     * @param baseImpl
     * @param newVersion
     * @param commonModel
     */
    public UpdatePresenter(UpdateView mView, BaseImpl baseImpl, String newVersion, String url, UpdateModel commonModel) {
        super(mView, baseImpl);
        this.newVersion = newVersion;
        this.url = url;
        this.baseImpl = baseImpl;
        mCommonModel = commonModel;
    }

    public void startDownload(String dir, long lastDownloadSize) {
        mCommonModel.downloadNewVersionIOThread(url, dir, newVersion, lastDownloadSize, this);
    }

    private String newVersion;
    private String url;

    @Override
    public void ondestroy() {
        super.ondestroy();
    }

    //
    // laoyuegou  定义的CommonModel是个Observer对象，
    // laoyuegame 因为和laoyuegou 之间的底层下载工具用的不同
    // 接口之间有区别所以 这里继承laoyuegou的CommonModel并
    // 对laoyuegame这边的下载的下载监听IDownloadObserver进行实现。
    //
    @Override
    public void onSubscribe(@NonNull Disposable d) {
    }

    @Override
    public void onNext(@NonNull Object o) {
    }

    @Override
    public void onError(@NonNull Throwable e) {
    }

    @Override
    public void onComplete() {
    }
}
