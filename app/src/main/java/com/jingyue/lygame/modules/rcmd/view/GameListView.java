package com.jingyue.lygame.modules.rcmd.view;

import com.jingyue.lygame.bean.RcmdGameListBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

import java.util.List;

/**
 * Created by zhanglei on 2017/9/7.
 */

public interface GameListView extends BaseMvpView {
    void setDataOnMain(List<RcmdGameListBean> beans,boolean isRefresh);
    void noData();
}
