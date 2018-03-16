package com.jingyue.lygame.modules.find.view;

import com.jingyue.lygame.bean.SubjectListBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;
import java.util.List;

/**
 * Created by zhanglei on 2017/9/21.
 */
public interface SubjectListView extends BaseMvpView {
    void setDataOnMain(List<SubjectListBean> beans);
    void setNoMore();
}
