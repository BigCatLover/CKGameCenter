package com.jingyue.lygame.utils.youkuPlayer.utils;

import android.app.Activity;
import android.util.SparseArray;
import android.widget.FrameLayout;
import com.jingyue.lygame.utils.youkuPlayer.PlayerStateInfo;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.YKFullScreenEventCallback;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.YKPlayerEventCallback;
import com.laoyuegou.android.lib.utils.LogUtils;

public class YKListUtil extends YKBaseListUtil {

    protected int mPlayingIndex = -1;  //当前正在播放的item索引

    private SparseArray<PlayerStateInfo> stateInfoList;     //已经播放过的视频信息列表
    public YKListUtil(Activity activity) {
        super(activity);
        mPlayingIndex = -1;
        stateInfoList = new SparseArray<>();
    }

    /**
     * 全屏播放
     *
     * @param url   视频地址
     * @param listPosition      在list中的索引
     * @param itemContainer     播放器容器
     * @param eventCallback     播放器事件回调
     */
    public void startFullScreen(String url, int listPosition, FrameLayout itemContainer,
                                YKPlayerEventCallback eventCallback, YKFullScreenEventCallback fullScreenEventCallback,String imgUrl) {
        super.startFullScreen(url, listPosition,itemContainer,
                eventCallback,fullScreenEventCallback,imgUrl);
    }

    public boolean isFullscreen(){
        return  super.isFullscreen();
    }


    /**
     * 跳转快进
     * @param url   播放地址
     * @param listPosition  播放器在list中的索引
     * @param itemContainer 播放器容器
     * @param eventCallback 播放器事件回调
     * @param progress  播放进度
     * @param maxProgress   视频进度最大值
     */
    public void seekTo(String url, int listPosition, FrameLayout itemContainer, YKPlayerEventCallback eventCallback,
                       int progress, int maxProgress){
        super.seekTo(url, listPosition,itemContainer, eventCallback, progress, maxProgress);
    }

    /**
     * 切换播放状态
     *
     * @param url   播放地址
     * @param listPosition  播放器在list中的索引
     * @param itemContainer 播放器容器
     * @param eventCallback 播放器事件回调
     */
    public void togglePlay(String url, int listPosition, FrameLayout itemContainer, YKPlayerEventCallback eventCallback) {
        super.togglePlay(url, listPosition, itemContainer, eventCallback);

    }


    /**
     * 根据position和播放器的状态，来确定itemContainer中的内容
     *
     * @param listPosition  播放器在list中的索引
     * @param itemContainer 播放器容器
     * @param eventCallback 播放器事件回调
     * @return 播放状态
     */
    public PlayerStateInfo resolveItem(int listPosition, FrameLayout itemContainer, YKPlayerEventCallback eventCallback) {

        return super.resolveItem(listPosition, itemContainer, eventCallback);
    }

    /**
     * 移出播放器
     * @param listPosition  播放器在list中的索引
     */
    public void removePlayer(int listPosition){
        if (listPosition == mPlayingIndex) {
            removePlayerFromParent();
        }

    }

    public void removePlayerFromParent(){
        super.removePlayerFromParent();
    }

    public boolean isCurrentPlayingIndex(int listPosition) {
        return mPlayingIndex == listPosition;
    }

    public int getPlayingIndex() {
        return mPlayingIndex;
    }

    @Override
    protected PlayerStateInfo getPlayingStateInfo() {
        if(stateInfoList == null){
            return null;
        }else {
            return stateInfoList.get(mPlayingIndex);
        }
    }

    @Override
    protected void setPlayingStateInfo(PlayerStateInfo playerStateInfo) {
        if(stateInfoList!=null){
            stateInfoList.put(mPlayingIndex, playerStateInfo);
        }
    }

    @Override
    protected PlayerStateInfo getPlayingStateInfoByIndex(Object targetIndex) {
        if (!(targetIndex instanceof Integer)) {
            throw new IllegalArgumentException("param 'targetIndex' must be int");
        }
        if(stateInfoList==null){
            return null;
        }

        return stateInfoList.get((int)targetIndex);
    }

    @Override
    protected boolean isPlayingIndex(Object targetIndex) {
        if (!(targetIndex instanceof Integer)) {
            throw new IllegalArgumentException("param 'targetIndex' must be int");
        }

        return mPlayingIndex == (int)targetIndex;
    }

    @Override
    protected boolean isPlayingIndexNull() {
        return mPlayingIndex == -1;
    }

    @Override
    protected void setPlayingIndex(Object targetIndex) {
        if (!(targetIndex instanceof Integer)) {
            throw new IllegalArgumentException("param 'targetIndex' must be int");
        }
        mPlayingIndex = (int) targetIndex;
    }
    @Override
    public void release() {
        mYkVideoPlayer.release();
        if (stateInfoList != null) {
            stateInfoList.clear();
            stateInfoList = null;
        }
    }
}
