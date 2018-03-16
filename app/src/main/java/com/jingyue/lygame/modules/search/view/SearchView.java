package com.jingyue.lygame.modules.search.view;

import com.jingyue.lygame.bean.GameBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;
import java.util.List;

/**
 * Created by zhanglei on 2017/9/18.
 */
public interface SearchView extends BaseMvpView{
    void setDataOnMain(List<GameBean> beans,boolean isRefresh);
    void noMore();
}
