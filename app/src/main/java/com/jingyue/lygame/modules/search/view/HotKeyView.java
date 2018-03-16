package com.jingyue.lygame.modules.search.view;

import com.jingyue.lygame.bean.SearchHotKeyBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

import java.util.List;

/**
 * Created by zhanglei on 2017/9/18.
 */
public interface HotKeyView extends BaseMvpView {
    void setDataOnMain(SearchHotKeyBean bean,boolean onlyGames);
}
