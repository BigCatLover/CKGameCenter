package com.jingyue.lygame.utils.youkuPlayer.eventcallback;

import android.view.ViewGroup;

public interface YKFullScreenEventCallback {

    /**
     * 进入全屏回调
     * @return 返回ViewGroup作为全屏播放的容器
     */
    ViewGroup onEnterFullScreen(boolean showcache,String url);

    /**
     * 退出全屏回调
     */
    void onQuitFullScreen(boolean showcache,String url);
}
