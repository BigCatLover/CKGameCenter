package com.jingyue.lygame.modules.rcmd.view;

import com.jingyue.lygame.bean.GameBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

import java.util.List;

public interface GameView extends BaseMvpView {
    void setDataOnMain(GameBean bean,boolean fromCache);
    void onTagReload(List<GameBean.Tag> tags);
}
