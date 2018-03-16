package com.jingyue.lygame.modules.rcmd.view;

import com.jingyue.lygame.bean.GameBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-19 15:31
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 *
 *
 */
public interface TagListView extends BaseMvpView{
    void setDataOnMain(List<GameBean> bean);
}
