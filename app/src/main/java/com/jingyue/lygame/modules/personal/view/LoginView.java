package com.jingyue.lygame.modules.personal.view;

import com.jingyue.lygame.bean.LoginInfoBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

import java.util.List;

public interface LoginView extends BaseMvpView {
    void onGetLoginHistory(List<LoginInfoBean> loginInfoList);
}
