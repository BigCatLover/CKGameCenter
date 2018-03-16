package com.jingyue.lygame.events;

/**
 * Created by zhanglei on 2017/9/26.
 */
public class GameSubEvent {
    public static final int TYPE_SUB = 0X1;
    public static final int TYPE_UNSUB= 0X2;
    public int currentStatus;
    public String appid;

    public GameSubEvent(int status,String appid) {
        this.currentStatus = status;
        this.appid = appid;
    }
}
