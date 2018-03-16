package com.jingyue.lygame.utils.youkuPlayer.listener;

public interface YKMediaListener {

    void onAdBegin(int index);
    void onComplete();
    void onError(int code, String des);
    void controlFrame();
    void onAdEnd(int index);
    void showErrorDialog();
    void onLoaded();
    void onLoading();
    void onRealVideoStart();
    void onPlayStart();
    void onBufferingUpdate(int i);
    void onPlayPause();
    void onVideoSizeChanged(int width, int height);
    void playPrepare();
}
