package com.jingyue.lygame.modules.find.view;

import com.jingyue.lygame.bean.SubjectDetailBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

/**
 * Created by zhanglei on 2017/9/21.
 */
public interface SubjectDetailView extends BaseMvpView {
    void setDataOnMain(SubjectDetailBean beans,boolean isRefresh);
    void noMore();
}
