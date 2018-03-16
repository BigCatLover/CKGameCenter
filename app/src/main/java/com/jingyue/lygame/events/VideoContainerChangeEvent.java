package com.jingyue.lygame.events;

/**
 * Created by zhanglei on 2017/10/18.
 */
public class VideoContainerChangeEvent {
    public static final int TYPE_NEED_CREATE = 0X1;
    public int from;
    public VideoContainerChangeEvent(int from) {
        this.from = from;
    }
}
