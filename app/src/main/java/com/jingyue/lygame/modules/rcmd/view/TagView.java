package com.jingyue.lygame.modules.rcmd.view;

import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.TagBusinessViewBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-18 20:47
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * 标签页
 */
public interface TagView extends BaseMvpView{
    void setDataOnMain(TagBusinessViewBean bean);
    void onTagAdded(GameBean.Tag tag);
}
