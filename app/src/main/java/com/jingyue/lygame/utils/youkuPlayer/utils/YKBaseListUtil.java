package com.jingyue.lygame.utils.youkuPlayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.utils.youkuPlayer.PlayerStateInfo;
import com.jingyue.lygame.utils.youkuPlayer.YKMediaManager;
import com.jingyue.lygame.utils.youkuPlayer.YKVideoPlayer;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.YKFullScreenEventCallback;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.YKPlayerActionEventCallback;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.YKPlayerEventCallback;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public abstract class YKBaseListUtil {

    protected static final String KEY_ITEM_CONTAINER = "itemContainer";
    protected static final String KEY_EVENT_CALLBACK = "eventCallback";
    protected static final String KEY_POSITION = "position";
    protected static final String KEY_URL = "url";
    protected static final String KEY_LAST_STATE = "lastState";
    protected YKFullScreenEventCallback mFullScreenEventCallback;

    protected static final int MSG_START_PLAY = 0;
    protected YKPlayerEventCallback eventCallback;   //播放器事件回调接口
    protected Context context;
    protected int mXibaVideoPlayerWidth;
    protected int mXibaVideoPlayerHeight;

    //切换播放条目接口
    public interface PlayingItemPositionChange{
        void prePlayingItemPositionChange(Message utilMsg);     //正常播放时，切换播放条目
        void prePlayingItemChangeOnPause();     //当播放器状态为暂停时，切换播放条目
    }

    protected PlayingItemPositionChange playingItemPositionChangeImpl;

    /**
     * 设置水平滑动的影响值
     * 例如参数为2，那么滑动整个屏幕改变整个播放器时长的一半
     * 参数需要大于0的整数，传入参数如果小于等于0，自动将参数修改为1
     * 推荐在onPlayerPrepare事件中，获取视频长度之后，根据需求修改此参数
     * @param horizontalSlopInfluenceValue
     */
    public void setHorizontalSlopInfluenceValue(int horizontalSlopInfluenceValue) {
        mYkVideoPlayer.setHorizontalSlopInfluenceValue(horizontalSlopInfluenceValue);
    }

    /**
     * 设置动作事件相关回调接口
     * @param actionEventCallback
     */
    public void setPlayerActionEventCallback(YKPlayerActionEventCallback actionEventCallback){
        mYkVideoPlayer.setPlayerActionEventCallback(actionEventCallback);
    }

    /**
     * 设置接口
     * @param playingItemPositionChangeImpl
     */
    public void setPlayingItemPositionChangeImpl(PlayingItemPositionChange playingItemPositionChangeImpl){
        this.playingItemPositionChangeImpl = playingItemPositionChangeImpl;
    }
    protected UtilHandler mUtilHandler;
    protected YKVideoPlayer mYkVideoPlayer;
    protected boolean isStratFullScreen = false;
    public static final String PLAYER_TAG_NO_CONTAINER = "";                 //没有父容器
    public static final String PLAYER_TAG_ITEM_CONTAINER = "itemContainer";  //父容器是itemContainer
    public static final String PLAYER_TAG_CONTENT_VIEW = "contentView";

    /**
     * 此Handler用于切换播放条目的过程中，
     * 原来的播放控件完成UI逻辑处理
     * 并将焦点切换到目标播放位置的UI之后，发送消息到此handler，进行目标文件的播放
     */
    protected class UtilHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_PLAY:

                    if (msg != null) {
                        Map<String, Object> msgObj = (Map<String, Object>) msg.obj;
                        ViewGroup itemContainer = (ViewGroup) msgObj.get(KEY_ITEM_CONTAINER);
                        YKPlayerEventCallback eventCallback = (YKPlayerEventCallback) msgObj.get(KEY_EVENT_CALLBACK);
                        Object targetIndex = msgObj.get(KEY_POSITION);
                        String url = (String) msgObj.get(KEY_URL);
                        int lastState = (int) msgObj.get(KEY_LAST_STATE);

                        startPlay(url, targetIndex, itemContainer, eventCallback, lastState);

                    }

                    break;
            }
        }
    }

    public YKBaseListUtil(Context context) {
        this.context = context;
        init(context);
    }

    protected void init(Context context) {
        mYkVideoPlayer = new YKVideoPlayer(context);
        mUtilHandler = new UtilHandler();
    }

    /**
     * 全屏播放
     * @param url
     * @param position
     * @param itemContainer
     * @param eventCallback
     */
    protected void startFullScreen(String url, Object position, FrameLayout itemContainer,
                                   YKPlayerEventCallback eventCallback, YKFullScreenEventCallback fullScreenEventCallback,String imgUrl){
        if (getYKVideoPlayer().getCurrentScreen() == YKVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
            return;
        }
        this.mFullScreenEventCallback = fullScreenEventCallback;
        mYkVideoPlayer.setCacheImageUrl(imgUrl);
        if (!isPlayingIndex(position)) {
            isStratFullScreen = true;
            togglePlay(url, position, itemContainer, eventCallback);
        } else {
            enterFullScreen();
        }
    }

    protected void enterFullScreen(){
        getYKVideoPlayer().setFullScreenEventCallback(mFullScreenEventCallback);
        getYKVideoPlayer().startFullScreen(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getYKVideoPlayer().setAutoRotate(false);
    }

    /**
     * 退出全屏
     */
    public void quitFullScreen(){
        getYKVideoPlayer().quitFullScreen();
        savePlayerInfo();
        getYKVideoPlayer().setFullScreenEventCallback(null);
    }

    /**
     * 跳转快进
     * @param url   播放地址
     * @param targetIndex  在列表中的位置
     * @param itemContainer 播放器容器
     * @param eventCallback 回调事件接口
     * @param progress
     * @param maxProgress
     */
    protected void seekTo(String url, Object targetIndex, FrameLayout itemContainer, YKPlayerEventCallback eventCallback,
                          int progress, int maxProgress){
        //如果当前播放索引不是目标播放索引，切换播放位置
        if (!isPlayingIndex(targetIndex)) {
            PlayerStateInfo stateInfo = getPlayingStateInfoByIndex(targetIndex);
            if (stateInfo != null) {
                //根据进度百分比，得出目标位置
                long seekPosition = stateInfo.getDuration() * progress / maxProgress;
                stateInfo.setPosition(seekPosition);
            }

            togglePlay(url, targetIndex, itemContainer, eventCallback);
        } else {
            getYKVideoPlayer().seekTo(progress);
        }
    }

    public YKVideoPlayer getYKVideoPlayer(){
        return mYkVideoPlayer;
    }

    /**
     * 获取当前视频总时长
     * @return
     */
    public long getDuration(){
        return mYkVideoPlayer.getDuration();
    }

    /**
     * 获取当前播放位置
     * @return
     */
    public long getCurrentPosition(){
        return mYkVideoPlayer.getCurrentPositionWhenPlaying();
    }


    /**
     * 为播放器设置事件回调
     * @param eventCallback
     */
    public void setEventCallback(YKPlayerEventCallback eventCallback){
        if (eventCallback != null) {
            mYkVideoPlayer.setEventCallback(eventCallback);
        }
    }
    public boolean isFullscreen(){
        return mYkVideoPlayer.getCurrentScreen() == YKVideoPlayer.SCREEN_WINDOW_FULLSCREEN;
    }
    public boolean onBackPress(){
        if (mYkVideoPlayer.getCurrentScreen() == YKVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
            quitFullScreen();
            return true;
        }
        return false;
    }

    /**
     * 1.开始播放
     * 2.暂停
     * 3.恢复播放 播放位置
     *
     * @param url
     * @param targetIndex
     * @param itemContainer
     * @param eventCallback
     */
    protected void togglePlay(String url, Object targetIndex, FrameLayout itemContainer, YKPlayerEventCallback eventCallback) {
        if(!NetworkUtils.hasNetwork(Utils.getContext())){
            ToastUtils.showShort(R.string.a_0019);
            return;
        }
        int lastState = getCurrentState();
        if (!isPlayingIndex(targetIndex)) {
            //如果播放器在全屏播放状态，先退出全屏状态
            if (mYkVideoPlayer.getCurrentScreen() == YKVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
                quitFullScreen();
            }

            /**
             * 先保存，删除，然后setUp播放器，最后再添加到itemContainer
             */
            if (!isPlayingIndexNull()) {

                if (playingItemPositionChangeImpl != null) {

                    //由于屏幕滑动的过程中，会将eventCallback设成null，导致无法调用暂停回调
                    //因此，这里需要设置eventCallback
                    mYkVideoPlayer.setEventCallback(eventCallback);
//                    this.eventCallback = eventCallback;

                    //创建handler消息
                    Map<String, Object> msgObj = new HashMap<>();
                    msgObj.put(KEY_ITEM_CONTAINER, itemContainer);
                    msgObj.put(KEY_EVENT_CALLBACK, eventCallback);
                    msgObj.put(KEY_POSITION, targetIndex);
                    msgObj.put(KEY_URL, url);
                    msgObj.put(KEY_LAST_STATE, lastState);
                    Message utilMsg = mUtilHandler.obtainMessage(MSG_START_PLAY, msgObj);

                    //如果播放器为播放状态，暂停播放器
                    if (getCurrentState() == YKVideoPlayer.STATE_PLAYING) {
                        //调用播放位置变更接口
                        playingItemPositionChangeImpl.prePlayingItemPositionChange(utilMsg);
                        mYkVideoPlayer.pausePlayer();
                    } else {
                        mYkVideoPlayer.showCache();
                        playingItemPositionChangeImpl.prePlayingItemChangeOnPause();
                        startPlay(url, targetIndex, itemContainer, eventCallback, lastState);
                    }

                } else {
                    //如果播放器为播放状态，暂停播放器
                    if (getCurrentState() == YKVideoPlayer.STATE_PLAYING
                            || getCurrentState() == YKVideoPlayer.STATE_PAUSE) {
                        mYkVideoPlayer.pausePlayer();
                    }
                    startPlay(url, targetIndex, itemContainer, eventCallback, lastState);
                }

            } else {
                //如果播放器为播放状态，暂停播放器
                if (getCurrentState() == YKVideoPlayer.STATE_PLAYING
                        || getCurrentState() == YKVideoPlayer.STATE_PAUSE) {
                    mYkVideoPlayer.pausePlayer();
                }else {
                    startPlay(url, targetIndex, itemContainer, eventCallback, lastState);
                }
            }
        } else{
            if(getCurrentState() == YKVideoPlayer.STATE_ERROR||getCurrentState() == -1){
                startPlay(url, targetIndex, itemContainer, eventCallback, lastState);
            }else {
                mYkVideoPlayer.togglePlayPause();
            }
        }

    }

    protected void startPlay(String url, Object targetIndex, ViewGroup itemContainer, YKPlayerEventCallback eventCallback, int lastState){
        //将播放器从当前父容器中移出
        removePlayerFromParent(lastState);

        //设置播放索引为当前索引
        setPlayingIndex(targetIndex);

        //如果有保存播放信息，恢复上次播放位置
        PlayerStateInfo playerStateInfo = getPlayingStateInfoByIndex(targetIndex);
        if (playerStateInfo != null) {
            mYkVideoPlayer.setUp(url, YKVideoPlayer.SCREEN_LIST, playerStateInfo.getPosition());
        } else {
//            mXibaVideoPlayer.setUp(url, YKVideoPlayer.SCREEN_LIST, new Object() {});
            mYkVideoPlayer.setUp(url, YKVideoPlayer.SCREEN_LIST);
        }

        addToListItem(itemContainer, eventCallback);    //添加到目标容器中

        mYkVideoPlayer.togglePlayPause();     //开始播放

        //如果是在全屏播放的过程中，开始播放之后进入全屏状态
        if (isStratFullScreen) {
            enterFullScreen();
            isStratFullScreen = false;
        }
    }

    /**
     * 获得当前状态
     * @return
     */
    public int getCurrentState(){
        return mYkVideoPlayer.getCurrentState();
    }

    /**
     * 根据position和播放器的状态，来确定itemContainer中的内容
     * @param targetIndex
     * @param itemContainer
     * @param eventCallback
     * @return
     */
    protected PlayerStateInfo resolveItem(Object targetIndex, ViewGroup itemContainer, YKPlayerEventCallback eventCallback) {

        if (itemContainer != null) {
            PlayerStateInfo stateInfo = getPlayingStateInfoByIndex(targetIndex);
            if (stateInfo != null && stateInfo.getCurrentState() ==
                    YKVideoPlayer.STATE_PAUSE && !isPlayingIndex(targetIndex)) {
                //如果当前item为暂停状态，添加暂停图片
//                addCacheImageView(itemContainer, stateInfo.getCacheBitmap());
            } else {
                //如果有缓存图片就删除
//                removeCacheImageView(itemContainer);
            }

            if (isPlayingIndex(targetIndex)) {
                //如果item为正在播放的item，将播放器添加到item中
                if (itemContainer.indexOfChild(YKMediaManager.getInstance(context).getMediaPlayer()) == -1) {
                    addToListItem(itemContainer, eventCallback);
                }

            } else {
                //如果播放器被复用，但又不是当前播放的索引，将播放器从容器中移出，并添加到contentView中
                //如果播放器被复用，但又不是当前播放的索引，将播放器从容器中移出，并添加到contentView中
                if (itemContainer.indexOfChild(YKMediaManager.getInstance(context).getMediaPlayer()) != -1) {
//                    removeFromList(-1);
                    removePlayerFromParent();
                    addToContentView(itemContainer);
                }
            }

        }

        return getPlayingStateInfoByIndex(targetIndex);
    }

    /**
     * 将播放器添加到Item中
     *
     * @param itemContainer
     * @param eventCallback
     */
    public void addToListItem(ViewGroup itemContainer, YKPlayerEventCallback eventCallback) {

        if (mYkVideoPlayer.getCurrentScreen() == YKVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
            return;
        }
        FrameLayout parent = (FrameLayout) mYkVideoPlayer.getParent();
        //如果播放器已经在目标容器中，直接返回
        if (parent != null) {
            if (parent == itemContainer) {
                return;
            } else {
                removePlayerFromParent();
            }
        }

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        itemContainer.addView(mYkVideoPlayer,layoutParams);
//        mYkVideoPlayer.setY(0);
        mYkVideoPlayer.setTag(PLAYER_TAG_ITEM_CONTAINER);
        mYkVideoPlayer.setEventCallback(eventCallback);
    }

    public int getCurrentScreen(){
        return mYkVideoPlayer.getCurrentScreen();
    }

    /**
     * 将播放器从父容器中移出
     */
    protected void removePlayerFromParent(){
        if (mYkVideoPlayer.getCurrentScreen() == YKVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
            return;
        }

        //保存移出时候的状态
        savePlayerInfo();

//        mXibaVideoPlayer.setEventCallback(null);
        this.eventCallback = null;
//        LinearLayout parent = (LinearLayout) YKMediaManager.getInstance(context).getMediaPlayer().getParent();
        ViewGroup parent = (ViewGroup) mYkVideoPlayer.getParent();

        if (parent != null) {
//
            mXibaVideoPlayerWidth = mYkVideoPlayer.getWidth();    //获取播放器宽
            mXibaVideoPlayerHeight = mYkVideoPlayer.getHeight();  //获取播放器高

            parent.removeView(mYkVideoPlayer);
            mYkVideoPlayer.setTag(PLAYER_TAG_NO_CONTAINER);
        }
    }

    /**
     * 将播放器从父容器中移出
     * @param lastState 根据状态判断是否需要添加暂停图片
     */
    protected void removePlayerFromParent(int lastState){

        if (mYkVideoPlayer.getCurrentScreen() == YKVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
            return;
        }

        //保存移出时候的状态
        savePlayerInfo();

        mYkVideoPlayer.setEventCallback(null);

//        LinearLayout parent = (LinearLayout) YKMediaManager.getInstance(context).getMediaPlayer().getParent();
        ViewGroup parent = (ViewGroup) mYkVideoPlayer.getParent();
        if (parent != null) {

//            if (lastState == YKVideoPlayer.STATE_PLAYING || lastState == YKVideoPlayer.STATE_PAUSE) {
//                Log.e(TAG,"需要添加暂停图片");
//                addCacheImageView(parent, playerStateInfo.getCacheBitmap());    //添加暂停图片
//            }

            mXibaVideoPlayerWidth = mYkVideoPlayer.getWidth();    //获取播放器宽
            mXibaVideoPlayerHeight = mYkVideoPlayer.getHeight();  //获取播放器高

            parent.removeView(mYkVideoPlayer);
            mYkVideoPlayer.setTag(PLAYER_TAG_NO_CONTAINER);
        }
    }

    /**
     * 将播放器添加到ContentView中
     */
    protected void addToContentView(ViewGroup itemContainer){
        if (mYkVideoPlayer.getCurrentScreen() == YKVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
            return;
        }

        ViewGroup contentView = (ViewGroup) ((Activity)context).getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        //如果ContentView中已经有播放器，直接返回
        if (contentView.indexOfChild(mYkVideoPlayer) != -1) {
            return;
        }

        mYkVideoPlayer.setTag(PLAYER_TAG_CONTENT_VIEW);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(mXibaVideoPlayerWidth, mXibaVideoPlayerHeight);
        contentView.addView(mYkVideoPlayer, 0, params);

        //让播放器在屏幕上方不可见，这样暂停的时候，依然可以拿到暂停图片
        mYkVideoPlayer.setY(-mXibaVideoPlayerHeight);

    }


    /**
     * 保存当前正在播放的播放器状态
     */
    public PlayerStateInfo savePlayerInfo(){
        PlayerStateInfo playerStateInfo = getPlayingStateInfo();

        if (playerStateInfo == null) {
            playerStateInfo = new PlayerStateInfo();
        }

        playerStateInfo.setCurrentState(mYkVideoPlayer.getCurrentState());
//        playerStateInfo.setCacheBitmap(mXibaVideoPlayer.getCacheBitmap());
        playerStateInfo.setDuration(mYkVideoPlayer.getDuration());
        playerStateInfo.setPosition(mYkVideoPlayer.getCurrentPositionWhenPlaying());
        playerStateInfo.setCurrentScreen(mYkVideoPlayer.getCurrentScreen());
        setPlayingStateInfo(playerStateInfo);

        return playerStateInfo;
    }



    /**
     * 添加暂停时的缓存图片
     * @param itemContainer
     * @param cacheBitmap
     */
    protected void addCacheImageView(ViewGroup itemContainer, Bitmap cacheBitmap){

        if (itemContainer == null) {
            return;
        }

        ImageView cacheIV = null;

        //当前itemContainer是否存在cache控件
        View cache = itemContainer.findViewWithTag("cache");

        //如果itemContainer中存在cache，直接使用
        if (cache != null) {
            cacheIV = (ImageView) cache;
        }

        //如果当前itemContainer不存在cache，创建一个添加到itemContainer中
        if (cacheIV == null) {
            cacheIV = new ImageView(context);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            cacheIV.setTag("cache");

//            itemContainer.addView(cacheIV, 0, layoutParams);
        }

        //为cache设置图片
        cacheIV.setImageBitmap(cacheBitmap);

    }

    /**
     * 移出暂停时的缓存图片
     * @param itemContainer
     */
//    protected void removeCacheImageView(ViewGroup itemContainer){
//        if (itemContainer == null) {
//            return;
//        }
//
//        //如果当前itemContainer存在cache，将cache移出
//        View cache = itemContainer.findViewWithTag("cache");
//        if (cache != null) {
//            cache.setTag("");
//            itemContainer.removeView(cache);
//        }
//    }

    /**
     * 获取正在播放的播放器状态
     * @return
     */
    protected abstract PlayerStateInfo getPlayingStateInfo();

    /**
     * 保存正在播放的播放器状态
     * @param playerStateInfo
     */
    protected abstract void setPlayingStateInfo(PlayerStateInfo playerStateInfo);

    /**
     * 根据索引来获取播放信息
     * @param targetIndex
     * @return
     */
    protected abstract PlayerStateInfo getPlayingStateInfoByIndex(Object targetIndex);

    /**
     * 目标索引是否是正在播放的索引
     * @param targetIndex
     * @return true 是目标索引是正在播放的索引
     */
    protected abstract boolean isPlayingIndex(Object targetIndex);

    /**
     * 当前正在播放的索引是否为空，还没有播放过任何视频
     * @return true 当前正在播放的索引为空
     */
    protected abstract boolean isPlayingIndexNull();

    /**
     * 设置目标索引
     * @param targetIndex
     * @return
     */
    protected abstract void setPlayingIndex(Object targetIndex);

    /**
     * 释放资源
     */
    public abstract void release();

}

