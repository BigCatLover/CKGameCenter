package com.jingyue.lygame.utils.youkuPlayer;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.jingyue.lygame.R;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.events.NetConnectEvent;
import com.jingyue.lygame.events.VoiceChangeEvent;
import com.jingyue.lygame.utils.SettingUtil;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.YKFullScreenEventCallback;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.YKPlayerActionEventCallback;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.YKPlayerEventCallback;
import com.jingyue.lygame.utils.youkuPlayer.listener.YKMediaListener;
import com.jingyue.lygame.widget.CommonDialog;
import com.laoyuegou.android.lib.utils.DisplayUtils;
import com.laoyuegou.android.lib.utils.EBus;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

public class YKVideoPlayer extends FrameLayout implements YKMediaListener {

    public static final String TAG = YKVideoPlayer.class.getSimpleName();
    public static final String NAMESPACE = "http://schemas.android.com/apk/res/android";
    /**
     * 屏幕状态
     */
    public static final int SCREEN_NORMAL = 0;              //正常
    public static final int SCREEN_WINDOW_FULLSCREEN = 1;   //全屏
    public static final int SCREEN_LIST = 3;                //列表
    /**
     * 播放状态
     */
    public static final int STATE_NORMAL = 0;                   //正常
    public static final int STATE_PREPARING = 1;               //准备中
    public static final int STATE_PLAYING = 2;                  //播放中
    public static final int STATE_PLAYING_BUFFERING_START = 3;  //开始缓冲
    public static final int STATE_PAUSE = 4;                    //暂停
    public static final int STATE_COMPLETE = 5;                    //
    public static final int STATE_AUTO_COMPLETE = 6;            //自动播放结束
    public static final int STATE_ERROR = 7;                    //错误状态

    /**
     * 水平滑动时，什么都不做
     */
    public static final int SLIDING_HORIZONTAL_NONE = 0;
    /**
     * 水平滑动时，改变播放位置
     */
    public static final int SLIDING_HORIZONTAL_CHANGE_POSITION = 1;

    public static final int TOUCH_SLOP = 20;   //判定滑动的最小距离
    public static final int CLICK_SLOP = 500;
    public static final int CHANGING_POSITION = 1;       //正在改变播放进度
    public static final int CHANGING_VOLUME = 2;         //正在改变音量

    private int mCurrentState = -1;                       //当前的播放状态
    private int mLastState = -1;                       //退出前的播放状态
    private int fullScreenContainerID = NO_ID;
    private long mCurrentPosition;

    private int mCurrentScreen = -1;                         //当前屏幕状态
    private int mSetUpScreen = -1;                          //初始时的屏幕状态

    private int mNormalScreenHorizontalFeature = SLIDING_HORIZONTAL_NONE;    //普通屏幕下水平滑动的功能
    private int mFullScreenHorizontalFeature = SLIDING_HORIZONTAL_NONE;      //全屏下水平滑动的功能
    protected YKPlayerActionEventCallback mActionEventCallback;    //动作相关事件回调接口
    /**
     * 水平滑动的影响值，用来影响滑动屏幕时，改变的进度
     */
    private int mHorizontalSlopInfluenceValue = 1;

    private GestureDetector mGestureDetector;
    private XibaOnGestureListener mXibaOnGestureListener;
    private int progress = 0;
    private YKFullScreenEventCallback mFScreenEventCallback;    //全屏相关事件回调接口
    private AudioManager mAudioManager;
    private YKPlayerEventCallback eventCallback;   //播放器事件回调接口
    private String url;
    private static Timer UPDATE_PROGRESS_TIMER;             //刷新播放进度的timer
    //    private ProgressTimerTask progressTimerTask;            //TimerTask
    private int mCurrentBufferPercentage;                   //当前缓冲百分比
    private Handler mHandler;                               //主线程handler
    private int mScreenWidth;   //屏幕宽度
    private int mScreenHeight;  //屏幕高度
    private float mDownX;       //触摸的X坐标
    private float mDownY;       //触摸的Y坐标
    private long mDownPosition;  //手指放下时的播放位置
    private long mSeekTimePosition; //滑动改变播放位置
    private int mDownVolumn; //手指放下时音量的大小
    private long startTime = 0;
    private int mTouchCurrentFeature = 0;      //touch事件当前的功能

    private ViewGroup mParent;      //播放器父容器
    private int mIndexInParent = 0;  //在父容器中的索引
    private ViewGroup.LayoutParams mLayoutParams;   //播放器布局参数
    private int mBackgroundColor = Color.TRANSPARENT; //播放器背景色
    //    private Bitmap mCacheBitmap;    //用于暂停时，切换屏幕状态用
    private Context mContext;
    private boolean mIsBuffering = false;   //是否正在加载
    private long mLastPosition = 0;
    private boolean mIsLoading = false;
    private boolean isRealStart = false;
    private boolean cacheHasShow = false;
    private boolean showCache = false;//全屏时是否显示暂停图片；
    private String cacheUrl;//暂停图片地址
    private CommonDialog noWifiDialog;
    private CommonDialog noNetDialog;
    private boolean nowifiplay = false;
//    private OrientationUtils mOrientationUtils;

    /**
     * 监听是否有外部其他多媒体开始播放
     */
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:

                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (YKMediaManager.getInstance(mContext).getMediaPlayer().isPlaying()) {
                        YKMediaManager.getInstance(mContext).getMediaPlayer().pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        }
    };

    public YKVideoPlayer(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public YKVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        String background = attrs.getAttributeValue(NAMESPACE, "background");
        if (background != null) {
            mBackgroundColor = Color.parseColor(background);
        }

        init();
    }

    /**
     * 初始化
     */
    private Activity activity;

    private void init() {
        EBus.getDefault().register(this);
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        mHandler = new Handler();
        mCurrentState = -1;
        mScreenWidth = DisplayUtils.getScreenWidthInPx(getContext());
        mScreenHeight = DisplayUtils.getScreenHeightInPx(getContext());
        mXibaOnGestureListener = new XibaOnGestureListener();
        mGestureDetector = new GestureDetector(getContext(), mXibaOnGestureListener);
//        mOrientationUtils = new OrientationUtils((Activity) getContext(), this);
//        mOrientationUtils.setOrientation();
        activity = (Activity) mContext;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        addTexture();
    }

    /**
     * 设置视频源
     *
     * @param url    视频地址
     * @param screen 屏幕类型
     * @return true为设置成功，false为设置失败
     */
    public boolean setUp(String url, int screen) {
        if (TextUtils.isEmpty(url) && TextUtils.equals(this.url, url)) {
            return false;
        }

        mCurrentState = STATE_NORMAL;

        this.url = url;
        this.mCurrentScreen = screen;
        this.mCurrentPosition = 0;

        this.mSetUpScreen = screen;

        this.mIsLoading = false;

        return true;
    }

    /**
     * 设置视频源
     *
     * @param url      播放地址
     * @param screen   屏幕类型
     * @param position 播放位置
     *                 //     * @param cacheBitmap  起始播放时显示的图片，主要用于List中，从暂停状态重新播放时使用
     * @return true为设置成功，false为设置失败
     */
    public boolean setUp(String url, int screen, long position) {
        if (TextUtils.isEmpty(url) && TextUtils.equals(this.url, url)) {
            return false;
        }
        mCurrentState = STATE_NORMAL;

        this.url = url;
        this.mCurrentScreen = screen;
        this.mCurrentPosition = position;
        this.mSetUpScreen = screen;
        this.mIsLoading = false;

        return true;
    }

    /**
     * 设置播放器回调
     *
     * @param eventCallback 播放器事件回调接口实现类
     */
    public void setEventCallback(YKPlayerEventCallback eventCallback) {
        this.eventCallback = eventCallback;
    }


    @Override
    public void onVideoSizeChanged(int width, int height) {

    }


    @Override
    public void playPrepare() {
        setUiWithStateAndScreen(STATE_PLAYING);
//        if (mCurrentPosition > 0) {
//            YKMediaManager.getInstance(mContext).getMediaPlayer().seekTo((int)(mCurrentPosition));
//            YKMediaManager.getInstance(mContext).startPlayer();
//        }
    }

    /**
     * 添加texture
     */
    private void addTexture() {
        removeTexture();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        if (this.indexOfChild(YKMediaManager.getInstance(mContext).getMediaPlayer()) != -1) {
            return;
        }
        ViewGroup parent = (ViewGroup) YKMediaManager.getInstance(mContext).getMediaPlayer().getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        YKMediaManager.getInstance(mContext).getMediaPlayer().hideControllerView();
        this.addView(YKMediaManager.getInstance(mContext).getMediaPlayer(), lp);
    }


    /**
     * 移出texture
     */
    private void removeTexture() {
        if (this.getChildCount() > 0) {
            this.removeAllViews();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void VoicerChange(VoiceChangeEvent event) {
        int last = SettingUtil.readInt(getContext(), KeyConstant.KEY_VIDEO_VOICE_VALUE, 0);
        if (event.value > 0 && last == 0) {
            if (eventCallback != null) {
                eventCallback.onVoiceChange(true);
            }
        } else if (event.value == 0 && last > 0) {
            if (eventCallback != null) {
                eventCallback.onVoiceChange(false);
            }
        }
        SettingUtil.write(getContext(), KeyConstant.KEY_VIDEO_VOICE_VALUE, event.value);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetConnectEvent(NetConnectEvent event) {
        if (event.type == NetConnectEvent.TYPE_WIFI_DISCONNECTED) {
            if (getCurrentState() == STATE_PLAYING) {
                YKMediaManager.getInstance(mContext).pausePlayer();
                showNoWifiDialog();
            }
        } else if (event.type == NetConnectEvent.TYPE_NETWORK_DISCONNECTED) {
            if (getCurrentState() == STATE_PLAYING) {
                YKMediaManager.getInstance(mContext).pausePlayer();
                showNoNetDialog();
            }
        }
    }

    /**
     * 释放资源
     */
    @Override
    protected void onDetachedFromWindow() {
        if (mCurrentScreen == SCREEN_NORMAL) {
            release();
        }
        super.onDetachedFromWindow();
    }


    //**********↓↓↓↓↓↓↓↓↓↓ --播放相关的方法 start-- ↓↓↓↓↓↓↓↓↓↓**********

    /**
     * 准备播放
     */
    public void prepareVideo() {
        //设置播放器监听
        YKMediaManager.getInstance(mContext).setListener(this);
//        addTexture();
//        //屏幕常亮
//        ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        //准备播放视频
        YKMediaManager.getInstance(mContext).prepare(url);

        //启动刷新播放进度的timer
        startProgressTimer();
    }

    /**
     * 播放按钮逻辑
     * 切换播放器的播放暂停状态
     * 发送事件给监听器
     *
     * @return true 正常播放；false 播放需要流量
     */
    public boolean togglePlayPause() {
        if (StringUtils.isEmptyOrNull(url)) {
            return false;
        }

        //如果不是本地播放，同时网络状态又不是WIFI
        if (!NetworkUtils.hasNetwork(getContext())) {
            ToastUtils.showShort("请先链接网络~");
            return false;
        }
        if (mCurrentState != STATE_PLAYING) {
            if (NetworkUtils.getNetworkType(getContext()) == NetworkUtils.NETWORK_TYPE_MOBILE && !nowifiplay) {
                showNoWifiDialog();
                return false;
            }
        }

        nowifiplay = false;
        cacheHasShow = false;

        //如果当前是普通状态 或者 错误状态 -> 初始化播放视频
        if (mCurrentState == STATE_NORMAL || mCurrentState == STATE_ERROR) {

            if (mCurrentState == STATE_ERROR) {
                YKMediaManager.getInstance(mContext).releaseMediaPlayer();
            }
            //准备初始化播放
            prepareVideo();

        } else if (mCurrentState == STATE_PLAYING) {                    //如果当前是播放状态 -> 暂停播放
            setUiWithStateAndScreen(STATE_PAUSE);
            YKMediaManager.getInstance(mContext).pausePlayer();
        } else if (mCurrentState == STATE_PAUSE) {                      //如果当前是暂停状态 -> 继续播放
            setUiWithStateAndScreen(STATE_PLAYING);
            YKMediaManager.getInstance(mContext).startPlayer();
        } else if (mCurrentState == STATE_AUTO_COMPLETE
                || mCurrentState == STATE_COMPLETE) {              //如果当前是自动播放完成状态 -> 从头开始播放
            //准备初始化播放
            prepareVideo();
        }
        return true;
    }

    private void showNoWifiDialog() {
        noWifiDialog = new CommonDialog.Builder(mContext)
                .setContent(mContext.getString(R.string.a_0208))
                .setRightButtonInterface(mContext.getString(R.string.a_0205),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                noWifiDialog.dismiss();
                                noWifiDialog = null;
                                nowifiplay = true;
                                togglePlayPause();
                            }
                        }).setLeftButtonInterface(mContext.getString(R.string.a_0075),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                noWifiDialog.dismiss();
                            }
                        }).show();
        noWifiDialog.setCancelable(false);
    }

    private void showNoNetDialog() {
        noNetDialog = new CommonDialog.Builder(mContext)
                .setContent(mContext.getString(R.string.a_0210))
                .setOneButtonInterface(mContext.getString(R.string.a_0076),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                noNetDialog.dismiss();
                                noNetDialog = null;
                                togglePlayPause();
                            }
                        }).show();
        noNetDialog.setCancelable(false);
    }

    /**
     * 指定位置开始播放
     *
     * @param progress 目标进度
     */
    public void seekTo(int progress) {
        if (YKMediaManager.getInstance(mContext).getMediaPlayer() != null) {
            int seekTime = (int) getDuration() * progress / 100;

            if (YKMediaManager.getInstance(mContext).getMediaPlayer().isPlaying()) {
                YKMediaManager.getInstance(mContext).getMediaPlayer().seekTo(seekTime);
            } else if (mCurrentState == STATE_PAUSE || mCurrentState == STATE_COMPLETE) {
                if (eventCallback != null) {
                    eventCallback.onPlayerResume();    //回调继续播放方法
                }
                YKMediaManager.getInstance(mContext).getMediaPlayer().seekTo(seekTime);
                YKMediaManager.getInstance(mContext).getMediaPlayer().play();
                setUiWithStateAndScreen(STATE_PLAYING);
            }
        }
    }

    /**
     * 获取视频时长
     *
     * @return 视频总时长
     */
    public long getDuration() {
        long duration = 0;
        if (YKMediaManager.getInstance(mContext).getMediaPlayer() != null) {
            duration = YKMediaManager.getInstance(mContext).getMediaPlayer().getDuration();
            return duration;
        }
        return duration;
    }

    //根据播放器状态和屏幕状态设置UI
    public void setUiWithStateAndScreen(int state) {
        mCurrentState = state;
        switch (mCurrentState) {

//            case STATE_COMPLETE:
//                cancelProgressTimer();
//                //屏幕常亮
//                ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                break;
            case STATE_NORMAL:
            case STATE_ERROR:
                release();
                break;
            case STATE_PREPARING:
//                resetProgressAndTime();
                break;
            case STATE_PLAYING:
//            case STATE_PAUSE:
            case STATE_PLAYING_BUFFERING_START:
                startProgressTimer();
                //屏幕常亮
                ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            case STATE_PAUSE:
            case STATE_COMPLETE:
            case STATE_AUTO_COMPLETE:
                cancelProgressTimer();
                //取消屏幕常亮
                ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
        }
    }

    public boolean isPlaying() {
        return (getCurrentState() == 2) && isRealStart;
    }

    /**
     * //     * 释放资源
     * //
     */
    public void release() {
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        YKMediaManager.getInstance(mContext).releaseMediaPlayer();
        EBus.getDefault().unregister(this);
        YKMediaManager.getInstance(mContext).release();
        cancelProgressTimer();
        removeTexture();
        if (eventCallback != null) {
            eventCallback.release();
        }
        //取消屏幕常亮
        ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        activity = null;
    }

    /**
     * 暂停
     */
    public void pausePlayer() {
        mLastState = mCurrentState;
        if (mCurrentState == STATE_PLAYING || mCurrentState == STATE_PLAYING_BUFFERING_START || mCurrentState == STATE_PREPARING) {
            mCurrentState = STATE_PAUSE;
        }
//        if (mCurrentScreen == SCREEN_LIST) {
//            getCacheImageBitmap();
//        }
        YKMediaManager.getInstance(mContext).pausePlayer();
        mCurrentPosition = YKMediaManager.getInstance(mContext).getMediaPlayer().getCurrentPosition();
    }

    /**
     * 恢复
     */
    public void resumePlayer() {

        if (mCurrentState == STATE_PAUSE && mLastState != STATE_PAUSE) {
//            if (cacheImageView.getVisibility() != VISIBLE) {
//                cacheImageView.setVisibility(VISIBLE);
//            }

            if (YKMediaManager.getInstance(mContext).getMediaPlayer() != null) {
                YKMediaManager.getInstance(mContext).startPlayer();
            } else {
                prepareVideo();
            }
        }
        mLastState = -1;
    }

    public void reset() {
        mCurrentState = -1;
        mLastState = -1;
        mCurrentPosition = 0;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                startTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:
//                float x = event.getX();
                //移动X，Y方向的距离
//                float deltaX = x - mDownX;      //右移为正，左移为负
                //移动X，Y方向的绝对值
//                float absDeltaX = Math.abs(deltaX);
//                if (absDeltaX >= TOUCH_SLOP) {
//                    mDownPosition = getCurrentPositionWhenPlaying();//获取当前的播放位置
//                }
//                long totalTimeDuration = getDuration();
//                mSeekTimePosition = (long) (mDownPosition + totalTimeDuration * deltaX / (mScreenWidth * mHorizontalSlopInfluenceValue));
//
//                if (mSeekTimePosition > totalTimeDuration) {
//                    mSeekTimePosition = totalTimeDuration;
//                } else if (mSeekTimePosition < 0) {
//                    mSeekTimePosition = 0;
//                }
//                progress = (int) (mSeekTimePosition * 100 / (totalTimeDuration == 0 ? 1 : totalTimeDuration));
//                if (eventCallback != null) {
//                    eventCallback.onChangingPosition(mDownPosition, mSeekTimePosition, totalTimeDuration);
//                }

                break;

            case MotionEvent.ACTION_UP:
                long currentTime = System.currentTimeMillis();
                long cha = currentTime - startTime;
                if (cha < 500) {
//                    if(eventCallback!=null){
//                        eventCallback.controlFrame(mCurrentState==STATE_PLAYING);
//                    }
//                    if(YKMediaManager.getInstance(mContext).getMediaPlayer().isPlaying()){
//                        YKMediaManager.getInstance(mContext).pausePlayer();
//                    }else {
//                        YKMediaManager.getInstance(mContext).startPlayer();
//                    }
                } else {
                    int seekTime = (int) getDuration() * progress / 100;
                    YKMediaManager.getInstance(mContext).getMediaPlayer().seekTo(seekTime);
                    startProgressTimer();
                }

                break;
        }
        return super.dispatchTouchEvent(event);
    }


    /**
     * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ --处理触摸事件 start-- ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;

                break;

            case MotionEvent.ACTION_MOVE:

                //移动X，Y方向的距离
                float deltaX = x - mDownX;      //右移为正，左移为负
                float deltaY = y - mDownY;      //下移为正，上移为负
                //移动X，Y方向的绝对值
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                long totalTimeDuration = getDuration();

                mSeekTimePosition = (long) (mDownPosition + totalTimeDuration * deltaX / (mScreenWidth * mHorizontalSlopInfluenceValue));

                if (mSeekTimePosition > totalTimeDuration) {
                    mSeekTimePosition = totalTimeDuration;
                } else if (mSeekTimePosition < 0) {
                    mSeekTimePosition = 0;
                }
                int progress = (int) (mSeekTimePosition * 100 / (totalTimeDuration == 0 ? 1 : totalTimeDuration));
                YKMediaManager.getInstance(mContext).getMediaPlayer().seekTo(progress);
                if (eventCallback != null) {
                    eventCallback.onChangingPosition(mDownPosition, mSeekTimePosition, totalTimeDuration);  //回调音量改变方法
                }

                break;

            case MotionEvent.ACTION_UP:
                YKMediaManager.getInstance(mContext).getMediaPlayer().seekTo((int) mSeekTimePosition);
                startProgressTimer();
                break;
        }
        mGestureDetector.onTouchEvent(event);

        return true;
    }


    /**
     * 第一次进入水平滑动，分配垂直滑动功能
     */
    private void firstHorizontalSlide() {

        int screenHorizontalFeature = 0;

        if (mCurrentScreen == SCREEN_NORMAL) {  //普通屏幕
            screenHorizontalFeature = mNormalScreenHorizontalFeature;
        } else if (mCurrentScreen == SCREEN_WINDOW_FULLSCREEN) {    //全屏模式
            screenHorizontalFeature = mFullScreenHorizontalFeature;
        }

        confirmHorizontalFeature(screenHorizontalFeature);
    }

    /**
     * 确认水平滑动的功能
     *
     * @param screenHorizontalFeature
     */
    private void confirmHorizontalFeature(int screenHorizontalFeature) {
        if (screenHorizontalFeature == SLIDING_HORIZONTAL_CHANGE_POSITION) {
            //改变播放进度
            mTouchCurrentFeature = CHANGING_POSITION;
            mDownPosition = getCurrentPositionWhenPlaying();//获取当前的播放位置
            cancelProgressTimer();
        }
    }

    public void onResume() {
        YKMediaManager.getInstance(mContext).startPlayer();
    }

    public void onPause() {
        YKMediaManager.getInstance(mContext).pausePlayer();
    }

    public void showCache() {
        if (mCurrentState == STATE_PLAYING) {
            YKMediaManager.getInstance(mContext).getMediaPlayer().onPause();
            mCurrentState = STATE_PAUSE;
        }
        if (eventCallback != null) {
            eventCallback.showCache();
            cacheHasShow = true;
        }
    }

    /**
     * 获得当前状态
     *
     * @return 返回当前播放器的状态
     */
    public int getCurrentState() {
        return mCurrentState;
    }

    /**
     * 获得当前屏幕状态
     *
     * @return 返回当前屏幕的状态
     */
    public int getCurrentScreen() {
        return mCurrentScreen;
    }
//
//    @Override
//    public void onPortrait() {
//        if (mCurrentScreen == SCREEN_WINDOW_FULLSCREEN) {
//            quitFullScreen();
//        }
//    }
//
//    @Override
//    public void onLandscape() {
//        if (mCurrentScreen == mSetUpScreen) {
//            startFullScreen(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
//    }
//
//    @Override
//    public void onReverseLandscape() {
//        if (mCurrentScreen == mSetUpScreen) {
//            startFullScreen(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
//        }
//    }

    /**
     * 处理单击 双击事件
     */
    private class XibaOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mActionEventCallback != null) {
                mActionEventCallback.onSingleTap();    //单击事件回调
            }
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {

            if (e.getAction() == MotionEvent.ACTION_UP) {
                if (mActionEventCallback != null) {
                    mActionEventCallback.onDoubleTap();    //双击事件回调
                }
                return true;
            }
            return false;
        }
    }


    /**
     * 启动计时器更新播放进度
     */
    private void startProgressTimer() {
        cancelProgressTimer();
        UPDATE_PROGRESS_TIMER = new Timer();
        progressTimerTask = new ProgressTimerTask();
        UPDATE_PROGRESS_TIMER.schedule(progressTimerTask, 0, 200);
    }

    private ProgressTimerTask progressTimerTask;

    /**
     * 取消计时器
     */
    private void cancelProgressTimer() {
        if (UPDATE_PROGRESS_TIMER != null) {
            UPDATE_PROGRESS_TIMER.cancel();
        }
        if (progressTimerTask != null) {
            progressTimerTask.cancel();
        }

        if (mTimerRunnable != null) {
            mHandler.removeCallbacks(mTimerRunnable);
        }


//        //取消屏幕常亮
//        ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 恢复默认
     */
    public void quitFullScreen() {
        if (mCurrentScreen == SCREEN_NORMAL) {
            return;
        }

//        getCacheImageBitmap();  //获取视频截图

        //获取contentView
        ViewGroup contentView = getContentView();

        //获取全屏容器
        ViewGroup fullScreenContainer = (ViewGroup) contentView.findViewById(fullScreenContainerID);

        if (fullScreenContainer != null) {
            fullScreenContainer.removeView(this);           //将播放器从全屏父容器中移出
            contentView.removeView(fullScreenContainer);    //将父容器从ContentView中移出
        }

        if (mParent != null) {
            mParent.addView(this, mIndexInParent, mLayoutParams);   //将播放器添加到原来的容器中
        }
        if (!EBus.getDefault().isRegistered(this)) {
            EBus.getDefault().register(this);
        } else {
            EBus.getDefault().unregister(this);
            EBus.getDefault().register(this);
        }

        this.setBackgroundColor(mBackgroundColor);  //还原背景

        if (mFScreenEventCallback != null) {
            showCache = mCurrentState == STATE_PAUSE || mCurrentState == STATE_NORMAL || mCurrentState == STATE_AUTO_COMPLETE;
            mFScreenEventCallback.onQuitFullScreen(showCache, cacheUrl);   //调用退出全屏回调事件
        }
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        YKMediaManager.getInstance(mContext).getMediaPlayer().goSmallScreen();
        if (mCurrentState == STATE_PLAYING) {
            YKMediaManager.getInstance(mContext).startPlayer();
        }

        //设置屏幕状态
        mCurrentScreen = mSetUpScreen;
    }

    public void setHorizontalSlopInfluenceValue(int horizontalSlopInfluenceValue) {
        this.mHorizontalSlopInfluenceValue = horizontalSlopInfluenceValue <= 0 ? 1 : horizontalSlopInfluenceValue;
    }

    public void setCacheImageUrl(String url) {
        cacheUrl = url;
    }

    /**
     * 设置动作事件相关回调接口
     *
     * @param playerActionEventCallback 动作事件回调接口实现类
     */
    public void setPlayerActionEventCallback(YKPlayerActionEventCallback playerActionEventCallback) {
        this.mActionEventCallback = playerActionEventCallback;
    }

    public void startFullScreen(int orientation) {
        //如果当前在全屏状态，直接返回
        if (mCurrentScreen == SCREEN_WINDOW_FULLSCREEN) {
            return;
        }

        ViewGroup fullScreenContainer = null;
        //进入全屏事件回调

        if (mFScreenEventCallback != null) {
            showCache = mCurrentState == STATE_PAUSE || mCurrentState == STATE_NORMAL || mCurrentState == STATE_AUTO_COMPLETE;
            fullScreenContainer = mFScreenEventCallback.onEnterFullScreen(showCache, cacheUrl);
        }

        if (fullScreenContainer == null) {
            return;
        }

        mParent = ((ViewGroup) this.getParent());    //获取当前父容器
        mIndexInParent = mParent.indexOfChild(this);    //获取在父容器中的索引
        mLayoutParams = this.getLayoutParams();     //获取当前的布局参数
        ((ViewGroup) this.getParent()).removeView(this);     //将播放器从当前容器中移出

        this.setBackgroundColor(Color.TRANSPARENT);   //设置背景色

        //将全屏播放器放到全屏容器之中
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        fullScreenContainer.addView(YKVideoPlayer.this, 0, layoutParams);

        //如果容器没有id，设置一个ID给容器
        fullScreenContainerID = fullScreenContainer.getId();
        if (fullScreenContainerID == NO_ID) {
            fullScreenContainerID = R.id.fullscreen_container_id;
            fullScreenContainer.setId(fullScreenContainerID);
        }

        //将全屏容器添加到contentView中
        ViewGroup contentView = getContentView();
        FrameLayout.LayoutParams contentViewLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        );
        contentView.addView(fullScreenContainer, contentViewLp);

        if (orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
        activity.setRequestedOrientation(orientation);
//        YKMediaManager.getInstance(mContext).getMediaPlayer().goFullScreen();
        if (mCurrentState == STATE_PLAYING) {
            YKMediaManager.getInstance(mContext).startPlayer();
        }
        //设置屏幕状态
        mCurrentScreen = SCREEN_WINDOW_FULLSCREEN;

    }

    public void setAutoRotate(boolean autoRotate) {
//        mOrientationUtils.setAutoRotate(autoRotate);
    }

    /**
     * 设置全屏事件相关回调接口
     *
     * @param fullScreenEventCallback 全屏事件回调接口实现类
     */
    public void setFullScreenEventCallback(YKFullScreenEventCallback fullScreenEventCallback) {
        this.mFScreenEventCallback = fullScreenEventCallback;
    }

    private ViewGroup getContentView() {
        return (ViewGroup) (scanForActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
    }

    private Activity scanForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    /**
     * 自定义计时器任务，回调播放进度更新事件
     */
    private class ProgressTimerTask extends TimerTask {

        @Override
        public void run() {
            if (mCurrentState == STATE_PLAYING
                    || mCurrentState == STATE_PAUSE
                    || mCurrentState == STATE_PLAYING_BUFFERING_START) {

                final long position = getCurrentPositionWhenPlaying();                   //当前播放位置
                final long duration = getDuration();                                     //总时长
                final int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));   //播放进度

//                Log.e(TAG, "ProgressTimerTask: run position=" + position);

                mTimerRunnable.setProgressInfo(progress, position, duration);

                mHandler.post(mTimerRunnable);


            }
        }
    }

    private TimerRunnable mTimerRunnable = new TimerRunnable();

    private class TimerRunnable implements Runnable {

        private int progress;
        private long position;
        private long duration;

        public void setProgressInfo(int progress, long position, long duration) {
            this.progress = progress;
            this.position = position;
            this.duration = duration;
        }

        @Override
        public void run() {
            if (eventCallback != null) {
                if (mLastPosition == position && mIsBuffering) {

                    if (!mIsLoading) {
                        mIsLoading = true;
                        //showLoading
                        eventCallback.onLoading();
                    }

                } else {
                    if (mIsLoading) {
                        mIsLoading = false;
                    }
                    if (position == duration) {

                    } else {
                        eventCallback.onPlayerProgressUpdate(progress, mCurrentBufferPercentage, position, duration);
                    }
                }
            }
            mLastPosition = position;
        }
    }

    /**
     * 获取当前播放位置
     *
     * @return 当前播放位置
     */
    public long getCurrentPositionWhenPlaying() {
        long position = 0;
        if (mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSE) {
            try {
                position = YKMediaManager.getInstance(mContext).getMediaPlayer().getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return position;
            }
        }
        return position;
    }


    @Override
    public void onAdBegin(int index) {
        if (eventCallback != null) {
            eventCallback.onAdStart();
        }
    }

    @Override
    public void onComplete() {
        mCurrentState = STATE_AUTO_COMPLETE;
        if (eventCallback != null) {
            eventCallback.onComplete();
        }
    }

    @Override
    public void onError(int code, String des) {
        mCurrentState = STATE_ERROR;
        if (eventCallback != null) {
            eventCallback.onError(code);
        }
    }

    @Override
    public void controlFrame() {
        if (eventCallback != null) {
            eventCallback.controlFrame(mCurrentState == STATE_PLAYING);
        }
    }

    @Override
    public void onAdEnd(int index) {
        if (eventCallback != null) {
            eventCallback.onAdEnd();
        }
    }

    @Override
    public void showErrorDialog() {
        if (eventCallback != null) {
            eventCallback.showErrorDialog();
        }
    }

    @Override
    public void onLoaded() {
        if (eventCallback != null) {
            eventCallback.onLoad();
        }
    }

    @Override
    public void onLoading() {
        mCurrentState = STATE_PLAYING;
        if (eventCallback != null) {
            eventCallback.onLoading();
        }
    }

    @Override
    public void onRealVideoStart() {
        isRealStart = true;
        if (eventCallback != null) {
            eventCallback.onRealStart();
            cacheHasShow = false;
        }
    }

    @Override
    public void onPlayStart() {
        setUiWithStateAndScreen(STATE_PLAYING);
        if (eventCallback != null) {
            eventCallback.onPlayerResume();
            cacheHasShow = false;
        }
    }


    public boolean hasCacheShow() {
        return cacheHasShow;
    }

    @Override
    public void onBufferingUpdate(int i) {
        mCurrentBufferPercentage = i;
    }

    @Override
    public void onPlayPause() {
        setUiWithStateAndScreen(STATE_PAUSE);
        if (eventCallback != null) {
            eventCallback.onPlayerPause();
        }
    }

}
