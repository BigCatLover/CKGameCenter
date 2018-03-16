package com.jingyue.lygame.modules.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.VersionInfoBean;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.modules.personal.present.VersionCheckPresenter;
import com.jingyue.lygame.modules.personal.view.VersionCheckView;
import com.jingyue.lygame.utils.update.InstallDialog;
import com.jingyue.lygame.utils.update.UpdateVersionDialog;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.luck.picture.lib.tools.DoubleUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity implements VersionCheckView {

    @BindView(R.id.update)
    TextView update;
    @BindView(R.id.switchaccout)
    TextView switchaccout;
    @BindView(R.id.logout)
    TextView logout;
    @BindView(R.id.version)
    TextView version;

    private VersionCheckPresenter mMp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableEventBus();
        super.onCreate(savedInstanceState);
        ActionBarUtil.inject(this).title(R.string.a_0147).hideShare().hideCollect();
        version.setText(getText(R.string.a_0088) + BuildConfig.VERSION_NAME);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }


    public static void start(Context context) {
        Intent starter = new Intent(context, SettingActivity.class);
        context.startActivity(starter);
    }

    @OnClick({R.id.version_container, R.id.switchaccout, R.id.logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.version_container:
                if (!DoubleUtils.isFastDoubleClick()) {
                    doCheckUpdate();
                }
                break;
            case R.id.switchaccout:
                LoginActivity.open(this, LoginActivity.BACK_BACK_LOGIN_BACK);
                break;
            case R.id.logout:
                LoginManager.getInstance().loginOut(this);
                break;
        }
    }

    private void doCheckUpdate() {
        if (mMp == null) {
            add(mMp = new VersionCheckPresenter(this, this));
        }
        mMp.startVersionCheck(true, true);
    }

    @Override
    public void onCheckVersion(VersionInfoBean bean, boolean needDownload) {
        if (needDownload) {
            new UpdateVersionDialog(this).show();
        } else {
            new InstallDialog(this).show();
        }
    }

    @Override
    public void onVersionCheckFailure() {
        ToastUtils.showShort(R.string.a_0233);
    }
}
