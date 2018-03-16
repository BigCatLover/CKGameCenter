package com.jingyue.lygame.constant;

import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.LoginInfoBean;
import com.jingyue.lygame.bean.TagBusinessViewBean;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-12 21:13
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class _Tables {
    /**
     * 游戏
     */
    public static final Class GAME_INFO = GameBean.class;
    /**
     * 标签
     */
    public static final Class TAG_INFO = GameBean.Tag.class;
    /**
     * 登录信息
     */
    public static final Class LOGIN_INFO = LoginInfoBean.class;
    /**
     * 主要是用来存储标签页的数据获取
     */
    public static final Class TAG_BUSINESS = TagBusinessViewBean.class;
}
