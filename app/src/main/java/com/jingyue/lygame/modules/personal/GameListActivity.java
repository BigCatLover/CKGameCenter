package com.jingyue.lygame.modules.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.BaseFragment;
import com.jingyue.lygame.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class GameListActivity extends BaseActivity {

    @BindView(R.id.tab_gamelist)
    SlidingTabLayout tabGamelist;
    @BindView(R.id.vp_gamelist)
    ViewPager vpGamelist;

    private String[] titleNames;
    private List<BaseFragment> fragmentList;
    private CommonVpAdapter commonVpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarUtil.inject(this).title(R.string.a_0140).hideCollect().hideShare();
        titleNames = new String[]{getString(R.string.a_0148), getString(R.string.a_0149)};
        fragmentList = new ArrayList<>();
        fragmentList.add(LikeGameFragment.getInstance(LikeGameFragment.TYPE_COLLECT));
        fragmentList.add(LikeGameFragment.getInstance(LikeGameFragment.TYPE_SUB));
        commonVpAdapter = new CommonVpAdapter(getSupportFragmentManager(), fragmentList, titleNames);
        vpGamelist.setAdapter(commonVpAdapter);
        tabGamelist.setViewPager(vpGamelist);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_gamelist;
    }


    public static void start(Context context) {
        Intent starter = new Intent(context, GameListActivity.class);
        context.startActivity(starter);
    }
}
