package com.jingyue.lygame.modules.personal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jingyue.lygame.BaseFragment;

import java.util.List;

/**
 * Created by zhanglei on 2017/9/21.
 */

public class CommonVpAdapter extends FragmentPagerAdapter {
    private  List<BaseFragment> fragmentList;
    private  String[] titleNames;

    public CommonVpAdapter(FragmentManager fm, List<BaseFragment> fragmentList, String[] titleNames) {
        super(fm);
        this.fragmentList=fragmentList;
        this.titleNames=titleNames;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleNames[position];
    }
}
