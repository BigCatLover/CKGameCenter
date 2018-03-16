package com.jingyue.lygame.events;

/**
 * Created by zhanglei on 2017/9/22.
 */
public class GameCollectEvent {
    public static final int TYPE_LIKE = 0X1;
    public static final int TYPE_UNLIKE = 0X2;
    public int currentStatus;
    public String appid;

    public GameCollectEvent(int status,String appid) {
        this.currentStatus = status;
        this.appid = appid;
    }
}
