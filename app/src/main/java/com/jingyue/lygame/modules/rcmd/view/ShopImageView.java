package com.jingyue.lygame.modules.rcmd.view;

import com.jingyue.lygame.bean.ShopImageBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-15 17:54
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public interface ShopImageView extends BaseMvpView {

    /**
     * @param bean   圖片实体数据
     * @param append 是否为添加数据，一般分页需要
     */
    void setDataOnMain(List<ShopImageBean> bean, boolean append);

}
