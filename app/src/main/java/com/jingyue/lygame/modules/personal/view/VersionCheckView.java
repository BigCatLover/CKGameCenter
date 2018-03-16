package com.jingyue.lygame.modules.personal.view;

import com.jingyue.lygame.bean.VersionInfoBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-24 18:33
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public interface VersionCheckView extends BaseMvpView{

    void onCheckVersion(VersionInfoBean bean,boolean needDownLoad);
    void onVersionCheckFailure();
}
