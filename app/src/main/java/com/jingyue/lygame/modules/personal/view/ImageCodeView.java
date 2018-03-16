package com.jingyue.lygame.modules.personal.view;

import com.jingyue.lygame.bean.CodeinfoBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-05 16:15
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public interface ImageCodeView extends BaseMvpView {

    void onRegByUserSucess();
    void onRegByPhoneSucess();
    void onGetCodeInfo(CodeinfoBean bean);
    void onRegisterFailure();
}
