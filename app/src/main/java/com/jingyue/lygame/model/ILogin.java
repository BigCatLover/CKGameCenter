package com.jingyue.lygame.model;

import com.laoyuegou.android.lib.mvp.BaseImpl;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-12 17:33
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public interface ILogin {
    /**
     * 是否登录
     */
    boolean isLogin();

    /**
     * 登录
     * @param context
     * @param userName
     * @param passWord
     */
    void login(BaseImpl context, String userName, String passWord);

    /**
     * 登出
     */
    void loginOut(BaseImpl base);

    /**
     * 检查登录状态
     */
    void checkLoginState(BaseImpl base);

    /**
     * 更新登录信息
     */
    void updateUserInfo(BaseImpl base,boolean needProgress);
}