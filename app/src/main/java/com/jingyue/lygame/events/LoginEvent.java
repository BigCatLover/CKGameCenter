package com.jingyue.lygame.events;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-24 15:39
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 *
 * 登录事件订阅管理类
 *
 */
public class LoginEvent {
    public static final int LOGIN = 0X1;
    public static final int LOGOUT = 0X2;
    public static final int UPDATE_STATE = 0x3;
    public static final int UPDATE_LOGIN_INFO = 0x4;
    public int loginEvent;

    public LoginEvent(int loginEvent) {
        this.loginEvent = loginEvent;
    }
}
