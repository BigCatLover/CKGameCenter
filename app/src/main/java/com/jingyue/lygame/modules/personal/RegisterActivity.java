package com.jingyue.lygame.modules.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.R;
import com.jingyue.lygame.clickaction.RegisterAction;
import com.jingyue.lygame.modules.personal.adapter.CommonVpAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.tab_register)
    SlidingTabLayout tabRegister;
    @BindView(R.id.vp_register)
    ViewPager vpRegister;

    private List<Fragment> fragmentList;
    private String[] titleNames = null ;
    private CommonVpAdapter commonVpAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recordPageEvent(new RegisterAction());
        setupUI();
    }

    private void setupUI() {
        ActionBarUtil.inject(this).title(R.string.a_0185).hideCollect().hideShare();
        titleNames = new String[] {getResources().getString(R.string.a_0223),
                getResources().getString(R.string.a_0224)};
        fragmentList = new ArrayList<>();
        if ("1".equals(BuildConfig.USE_MESSGAE)) {
            fragmentList.add(new PhoneRegisterFragment());
            fragmentList.add(new UserNameRegisterFragment());
        } else {
            fragmentList.add(new UserNameRegisterFragment());
            titleNames = Arrays.copyOfRange(titleNames, 1, 2);
        }
        commonVpAdapter = new CommonVpAdapter(getSupportFragmentManager(), fragmentList, titleNames);
        vpRegister.setAdapter(commonVpAdapter);
        tabRegister.setViewPager(vpRegister);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterActivity.class);
        context.startActivity(starter);
    }
}
