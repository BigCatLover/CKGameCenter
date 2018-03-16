package com.jingyue.lygame.events;

/**
 * Created by zhanglei on 2017/10/31.
 * 用于更新下载量
 */
public class DownloadEvent {
    public int downcnt;
    public DownloadEvent(int value) {
        this.downcnt = value;
    }
}
