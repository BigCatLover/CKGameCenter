package com.jingyue.lygame.events;

/**
 * Created by liu hong liang on 2016/11/23.
 */

public class NetConnectEvent {
    public static final int TYPE_WIFI_DISCONNECTED=1;
    public static final int TYPE_NETWORK_DISCONNECTED=2;
    public int type=1;

    public NetConnectEvent(int type) {
        this.type = type;
    }
}
