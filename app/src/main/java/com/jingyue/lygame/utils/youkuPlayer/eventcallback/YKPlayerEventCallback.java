package com.jingyue.lygame.utils.youkuPlayer.eventcallback;

public interface YKPlayerEventCallback {

    void onLoad();
    void onLoading();
    void onAdEnd();
    void onError(int code);
    void showErrorDialog();
    void onAdStart();
    void onRealStart();
    void onComplete();
    void onChangingPosition(long originPosition, long seekTimePosition, long totalTimeDuration);
    void showCache();
    void controlFrame(boolean isPlaying);
    /**
     * 播放进度更新
     * @param progress      播放百分比
     * @param secProgress   缓冲百分比
     * @param currentTime   当前播放位置
     * @param totalTime     总时长
     */
    void onPlayerProgressUpdate(int progress, int secProgress, long currentTime, long totalTime);

    /**
     * 暂停
     */
    void onPlayerPause();
    /**
     * 继续播放
     */
    void onPlayerResume();
    void release();
    void onVoiceChange(boolean isOpen);
}
