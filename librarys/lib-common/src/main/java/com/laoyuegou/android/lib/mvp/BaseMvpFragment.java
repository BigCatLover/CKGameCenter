package com.laoyuegou.android.lib.mvp;

import android.support.v4.app.Fragment;

import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-03 18:10
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class BaseMvpFragment extends Fragment {

    protected List<BaseMvpPresenter> mBussness;

    public List<BaseMvpPresenter> getBussness() {
        if (mBussness == null) {
            mBussness = new ArrayList<>();
        }
        return Collections.unmodifiableList(mBussness);
    }

    protected void add(BaseMvpPresenter present) {
        if (mBussness == null) {
            getBussness();
        }
        mBussness.add(present);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBussness == null) return;
        for (BaseMvpPresenter p : mBussness) {
            p.ondestroy();
        }
        mBussness.clear();
        mBussness = null;
    }
}
