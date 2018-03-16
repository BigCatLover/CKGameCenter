package com.jingyue.lygame.modules.comment.view;

import com.jingyue.lygame.bean.GameScoreBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

/**
 * Created by zhanglei on 2017/9/16.
 */
public interface GameScoreView extends BaseMvpView {
    void setDataOnMain(GameScoreBean bean);
}
