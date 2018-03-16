package com.laoyuegou.android.lib.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-23 14:47
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public abstract class BaseMvpActivity extends AppCompatActivity {

    protected List<BaseMvpPresenter> mBussness;

    public List<BaseMvpPresenter> getBussness() {
        if(mBussness == null){
            mBussness = new ArrayList<>();
        }
        return Collections.unmodifiableList(mBussness);
    }

    protected void add(BaseMvpPresenter present) {
        if(mBussness == null){
            getBussness();
        }
        mBussness.add(present);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBussness == null) return;
        for (BaseMvpPresenter p: mBussness){
            p.ondestroy();
        }
        mBussness.clear();
        mBussness = null;
    }
}
