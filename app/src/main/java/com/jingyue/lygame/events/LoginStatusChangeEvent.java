package com.jingyue.lygame.events;

/**
 * Created by zhanglei on 2017/11/12.
 */
public class LoginStatusChangeEvent {
    public static final int LOGIN_STATUS_CAHNGE = 0X1;
    public int type;
    public LoginStatusChangeEvent(int type) {
        this.type = type;
    }
}
