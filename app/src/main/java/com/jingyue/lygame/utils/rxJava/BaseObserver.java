package com.jingyue.lygame.utils.rxJava;

import android.text.TextUtils;

import com.laoyuegou.android.lib.utils.LogUtils;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-24 18:10
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class BaseObserver<T> implements Observer<T> {

    /**
     * 控制线程，并发起订阅
     * @param observable
     * @param observer
     * @param <T>
     */
    protected  <T> void makeSubscribe(Observable<T> observable, Observer<T> observer){
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private String name;

    public BaseObserver(String name) {
        if(TextUtils.isEmpty(name)){
            throw new IllegalStateException("name 不能为空!");
        }
        this.name = name;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull T t) {
        LogUtils.d(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        LogUtils.e(name,e);
    }

    @Override
    public void onComplete() {

    }
}
