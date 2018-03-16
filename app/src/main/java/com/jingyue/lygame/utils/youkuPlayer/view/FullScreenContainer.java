package com.jingyue.lygame.utils.youkuPlayer.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.events.VoiceChangeEvent;
import com.jingyue.lygame.modules.search.SearchActivity;
import com.jingyue.lygame.utils.SettingUtil;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.youkuPlayer.YKMediaManager;
import com.jingyue.lygame.utils.youkuPlayer.YKVideoPlayer;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.YKPlayerActionEventCallback;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.YKPlayerEventCallback;
import com.jingyue.lygame.widget.CommonDialog;
import com.laoyuegou.android.lib.utils.EBus;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.lygame.libadapter.ImageLoader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullScreenContainer extends FrameLayout {

    public static final String TAG = FullScreenContainer.class.getSimpleName();
    @BindView(R.id.baseview_ll)
    FrameLayout flVideoPlayer;
    @BindView(R.id.usercenter)
    ImageView usercenter;
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.share)
    ImageView share;
    @BindView(R.id.video_title)
    RelativeLayout videoTitle;
    @BindView(R.id.cache_image)
    ImageView cacheImage;
    @BindView(R.id.fullscreen_click)
    ImageView fullscreen;
    @BindView(R.id.tvVideoPlayerTotalTime)
    TextView tvVideoPlayerTotalTime;
    @BindView(R.id.tvVideoPlayerCurrentTime)
    TextView tvVideoPlayerCurrentTime;
    @BindView(R.id.sound)
    ImageView sound;
    @BindView(R.id.sbVideoPlayer)
    SeekBar sbVideoPlayer;
    @BindView(R.id.seekbarFrame)
    RelativeLayout seekbarFrame;
    @BindView(R.id.ivVideoPlayerPauseResume)
    ImageView play;
    @BindView(R.id.rlVideoPlayerControlFrame)
    RelativeLayout controlFrame;
    //    @BindView(R.id.ivVideoPlayerLoading)
//    ImageView loadingPB;
    @BindView(R.id.no_connect_text)
    TextView noConnectText;

    private boolean isLoading = false;
    private boolean isYoukuComplete = false;
    private static final int MSG_HIDE_CONTROL_VIEW = 0x0001;
    private static final int MSG_SHOW_ERROR_TIP = 0x0002;

    private int errorCode;
    private boolean isYoukuAd = false;
    private boolean isError;
    private YKVideoPlayer mXibaVideoPlayer;
    private ImageButton fScreenBackToNormalIB;
    private HyperHandler mHandle;
    private boolean isTrackingTouchSeekBar = false;     //是否正在控制SeekBar
    private FullScreenEventCallback mFullScreenEventCallback;
    private Context mContext;
    private int type;//0 不显示自定义title 1 显示
    private AudioManager mAudioManager;
    //    private AnimationDrawable animLoading;
    private String videoImg;

    public FullScreenContainer(Context context, int type) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.ykpalyer_fullscreen_container_layout, this);
        ButterKnife.bind(this);
        EBus.getDefault().register(this);
        mContext = context;
        this.type = type;
        if (type == 0) {
            videoTitle.setVisibility(GONE);
        } else {
            videoTitle.setVisibility(VISIBLE);
        }
//        animLoading = (AnimationDrawable) loadingPB.getDrawable();
        mAudioManager = (AudioManager) getContext().getSystemService(getContext().AUDIO_SERVICE);
        initHandle();

    }

    //初始化UI
    public void initUI(YKVideoPlayer xibaVideoPlayer) {
        this.mXibaVideoPlayer = xibaVideoPlayer;
        if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
            sound.setImageResource(R.mipmap.sound_open);
        } else {
            sound.setImageResource(R.mipmap.sound_close);
        }
        sound.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            SettingUtil.readInt(getContext(), KeyConstant.KEY_VIDEO_VOICE_VALUE, 0), AudioManager.STREAM_MUSIC);
                    sound.setImageResource(R.mipmap.sound_open);
                } else {
                    sound.setImageResource(R.mipmap.sound_close);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.STREAM_MUSIC);
                }
            }
        });

        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mXibaVideoPlayer.togglePlayPause();
            }
        });
        cacheImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mXibaVideoPlayer.togglePlayPause();
            }
        });
        noConnectText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mXibaVideoPlayer.togglePlayPause();
            }
        });
        fullscreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mXibaVideoPlayer.quitFullScreen();
            }
        });
        search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mXibaVideoPlayer.quitFullScreen();
                mXibaVideoPlayer.onPause();
                SearchActivity.start(getContext());
            }
        });

        //进度条监听
        sbVideoPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTrackingTouchSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mXibaVideoPlayer.seekTo(seekBar.getProgress());
                isTrackingTouchSeekBar = false;
            }
        });

        //设置播放按钮状态
        if (mXibaVideoPlayer.getCurrentState() == YKVideoPlayer.STATE_PLAYING) {
            setImageDrawable(R.mipmap.stop, play);      //设置暂停状态icon
        } else {
            setImageDrawable(R.mipmap.ic_play, play);       //设置播放状态icon
        }

        //如果视频未加载，进度条不可用
        if (mXibaVideoPlayer.getCurrentState() == YKVideoPlayer.STATE_NORMAL
                || mXibaVideoPlayer.getCurrentState() == YKVideoPlayer.STATE_ERROR) {

            sbVideoPlayer.setEnabled(false);
        } else {

            sbVideoPlayer.setEnabled(true);

            long totalTimeDuration = mXibaVideoPlayer.getDuration();
            long currentTimePosition = mXibaVideoPlayer.getCurrentPositionWhenPlaying();

            //设置视频总时长和当前播放位置
            tvVideoPlayerCurrentTime.setText(StringUtils.stringForTime(currentTimePosition));
            tvVideoPlayerTotalTime.setText(StringUtils.stringForTime(totalTimeDuration));

            int progress = (int) (currentTimePosition * 100 / (totalTimeDuration == 0 ? 1 : totalTimeDuration));   //播放进度

            //设置进度条位置
            sbVideoPlayer.setProgress(progress);
        }

        mFullScreenEventCallback = new FullScreenEventCallback();

    }

    public void showCache(boolean showcache, String url) {
        videoImg = url;
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(cacheImage, url));
        if (showcache) {
            play.setVisibility(VISIBLE);
            play.setImageResource(R.mipmap.ic_play);
            controlFrame.setVisibility(VISIBLE);
            cacheImage.setVisibility(VISIBLE);
            seekbarFrame.setVisibility(GONE);
        } else {
            cacheImage.setVisibility(GONE);
        }

    }

    /**
     * 显示全部控件
     */
    private void showFullScreenUI() {
        controlFrame.setVisibility(VISIBLE);
    }

    /**
     * 隐藏全部控件
     */
    private void dismissFullScreenUI() {
        controlFrame.setVisibility(GONE);
    }

    /**
     * 显示或隐藏全屏UI控件
     */
    private void toggleShowHideFullScreenUI() {
        if (controlFrame != null) {
            if (controlFrame.getVisibility() == View.VISIBLE) {
                dismissFullScreenUI();
            } else {
                showFullScreenUI();
            }
        }
    }

    private void setImageDrawable(int drawableID, ImageView targetIB) {
        Drawable drawable = getResources().getDrawable(drawableID);

        targetIB.setImageDrawable(drawable);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void VoicerChange(VoiceChangeEvent event) {
        int last = SettingUtil.readInt(getContext(), KeyConstant.KEY_VIDEO_VOICE_VALUE, 0);
        if (event.value > 0 && last == 0) {
            if (mFullScreenEventCallback != null) {
                mFullScreenEventCallback.onVoiceChange(true);
            }
        } else if (event.value == 0 && last > 0) {
            if (mFullScreenEventCallback != null) {
                mFullScreenEventCallback.onVoiceChange(false);
            }
        }
        SettingUtil.write(getContext(), KeyConstant.KEY_VIDEO_VOICE_VALUE, event.value);
    }

    public FullScreenEventCallback getFullScreenEventCallback() {
        return mFullScreenEventCallback;
    }

    /**
     * 释放全屏控件
     */
    public void releaseFullScreenUI() {
        if (play != null) {
            play = null;
        }
        if (fScreenBackToNormalIB != null) {
            fScreenBackToNormalIB = null;
        }
        if (tvVideoPlayerTotalTime != null) {
            tvVideoPlayerTotalTime = null;
        }
        if (tvVideoPlayerCurrentTime != null) {
            tvVideoPlayerCurrentTime = null;
        }
//        if (animLoading != null) {
//            animLoading.stop();
//            animLoading = null;
//        }
        if (share != null) {
            share = null;
        }
        if (sbVideoPlayer != null) {
            sbVideoPlayer = null;
        }

        if (mFullScreenEventCallback != null) {
            mFullScreenEventCallback = null;
        }
        EBus.getDefault().unregister(this);
    }

    private void initHandle() {
        mHandle = new HyperHandler();
    }

    private class FullScreenEventCallback implements YKPlayerEventCallback, YKPlayerActionEventCallback {

        @Override
        public void onPlayerProgressUpdate(int progress, int secProgress, long currentTime, long totalTime) {

            tvVideoPlayerCurrentTime.setText(StringUtils.stringForTime(currentTime));
            tvVideoPlayerTotalTime.setText(StringUtils.stringForTime(totalTime));

            if (!isTrackingTouchSeekBar) {
                sbVideoPlayer.setProgress(progress);
            }

            sbVideoPlayer.setSecondaryProgress(secProgress);
            if (!sbVideoPlayer.isEnabled()) {
                sbVideoPlayer.setEnabled(true);
            }

            //如果loading正在显示，在这里隐藏
//            if (loadingPB.getVisibility() == View.VISIBLE) {
//                loadingPB.setVisibility(View.GONE);
//            }
        }

        @Override
        public void onPlayerPause() {
            if (controlFrame.getVisibility() == View.GONE) {
                controlFrame.setVisibility(View.VISIBLE);
                if (play != null && play.getVisibility() == View.GONE) {
                    play.setVisibility(View.VISIBLE);
                }
                if (seekbarFrame.getVisibility() == View.GONE) {
                    seekbarFrame.setVisibility(View.VISIBLE);
                }
            }
            if (play != null) {
                play.setImageResource(R.mipmap.ic_play);
            }
        }

        @Override
        public void onPlayerResume() {
            if (cacheImage.getVisibility() == View.VISIBLE) {
                cacheImage.setVisibility(View.GONE);
            }
            play.setImageResource(R.mipmap.stop);
            if (controlFrame.getVisibility() == View.VISIBLE) {
                controlFrame.setVisibility(View.GONE);
            }

            if (null != mHandle) {
                mHandle.removeMessages(MSG_HIDE_CONTROL_VIEW);
                mHandle.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_VIEW, 2000);
            }
        }

        @Override
        public void release() {
            releaseFullScreenUI();
        }

        @Override
        public void onVoiceChange(boolean isOpen) {
            if (isOpen) {
                sound.setImageResource(R.mipmap.sound_open);
            } else {
                sound.setImageResource(R.mipmap.sound_close);
            }
        }


        @Override
        public void onLoad() {
//            if (null != loadingPB && loadingPB.getVisibility() == VISIBLE) {
//                loadingPB.setVisibility(View.GONE);
//            }
            isLoading = false;
            isYoukuComplete = false;
        }

        @Override
        public void onLoading() {
            isYoukuAd = false;
            if (controlFrame != null && controlFrame.getVisibility() == VISIBLE) {
                controlFrame.setVisibility(GONE);
            }
            isLoading = true;
//            if (null != loadingPB && loadingPB.getVisibility() == GONE) {
//                loadingPB.setVisibility(VISIBLE);
//                animLoading.start();
//            }
            if (cacheImage != null && cacheImage.getVisibility() == View.VISIBLE) {
                cacheImage.setVisibility(View.GONE);
            }
            noConnectText.setVisibility(GONE);

        }

        @Override
        public void onAdEnd() {
            errorCode = 0;
            isYoukuAd = false;
        }

        @Override
        public void onError(int code) {
            String error = "";
            switch (code) {
                case 1005:   // 网络连接失败
                    if (null != YKMediaManager.getInstance(mContext).getMediaPlayer()) {
                        TextView yprTv = (TextView) YKMediaManager.getInstance(mContext).getMediaPlayer().findViewById(R.id.btn_player_replay);
                        if (yprTv != null && (yprTv.getVisibility() == View.GONE || yprTv.getVisibility() == View.INVISIBLE)) {
                            yprTv.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
                case 1008:   // 网络出错
                    error = mContext.getString(R.string.a_0237);
                    break;
                case 1009:   // 搜索出错
                    error = mContext.getString(R.string.a_0236);
                    break;
                case 1007:   // 播放器准备失败
//                            error = getString(R.string.a_1449);
//                            break;
                case 1010:   // 准备超时
//                            error = getString(R.string.a_1452);
//                            break;
                case 1110:   // 广告出现4xx错误
//                            error = getString(R.string.a_1453);
//                            break;
                case 1111:   // 正片出现4xx错误
//                            error = getString(R.string.a_1454);
                    error = mContext.getString(R.string.a_0240);
                    break;
                case 2004:   // 超时
                    error = mContext.getString(R.string.a_0238);
                    break;
                case -2:     // videoId为空
                case 1:      // 其他错误
                case 700:    // 媒体轨道滞后
                case 800:    // 媒体信息出错
                case 801:    // 不支持seek
                case 1002:   // 播放错误
                case 2005:   // 播放广告时播放器准备超时
                case 2200:   // 中插广告播放器准备出错
                case 2201:   // 中插广告数据源错误
                case 2205:   // 播放前贴广告时播放器准备出错
                case 3001:   // 无版权
                case 3002:   // 被禁止播放
                    if (!NetworkUtils.hasNetwork(mContext)) {
                        ToastUtils.showShort(R.string.a_0241);
                    }
                    break;
                case 1006:   // 数据源错误
                    error = mContext.getString(R.string.a_0239);
                    break;
                default:     // 未知错误
                    error = mContext.getString(R.string.a_0240);
                    break;
            }
            if (error.isEmpty()) {
                return;
            } else {
                if (null != mHandle) {
                    mHandle.removeMessages(MSG_SHOW_ERROR_TIP);
                    mHandle.obtainMessage(MSG_SHOW_ERROR_TIP, error).sendToTarget();
                }
            }

//            if (null != loadingPB && loadingPB.getVisibility() == View.VISIBLE) {
//                loadingPB.setVisibility(View.GONE);
//            }
        }

        @Override
        public void showErrorDialog() {
        }

        @Override
        public void onAdStart() {
            isError = false;
            isYoukuAd = true;
            if (cacheImage != null && cacheImage.getVisibility() == View.VISIBLE) {
                cacheImage.setVisibility(View.GONE);
            }
//            if (loadingPB != null && loadingPB.getVisibility() == View.VISIBLE) {
//                loadingPB.setVisibility(View.GONE);
//            }
        }

        @Override
        public void onRealStart() {
            isError = false;
            play.setImageResource(R.mipmap.stop);
        }

        @Override
        public void onComplete() {
            isYoukuComplete = true;
            showCache();
        }

        @Override
        public void onChangingPosition(long originPosition, long seekTimePosition, long totalTimeDuration) {
            int progress = (int) (seekTimePosition * 100 / (totalTimeDuration == 0 ? 1 : totalTimeDuration));   //播放进度
            //设置进度条
            sbVideoPlayer.setProgress(progress);
        }

        @Override
        public void showCache() {
            if (null != mHandle) {
                mHandle.removeMessages(MSG_HIDE_CONTROL_VIEW);
            }
            cacheImage.setVisibility(VISIBLE);
            if (controlFrame.getVisibility() == View.GONE) {
                controlFrame.setVisibility(View.VISIBLE);
            }
            if (play.getVisibility() == View.GONE) {
                play.setVisibility(View.VISIBLE);
            }
            play.setImageResource(R.mipmap.ic_play);
            if (seekbarFrame.getVisibility() == View.VISIBLE) {
                seekbarFrame.setVisibility(View.GONE);
            }
            seekbarFrame.setVisibility(View.GONE);
//            if (loadingPB != null && loadingPB.getVisibility() == View.VISIBLE) {
//                loadingPB.setVisibility(View.GONE);
//            }
            noConnectText.setVisibility(View.GONE);
        }

        @Override
        public void controlFrame(boolean isPlaying) {
            controlFrameOnClick();
        }

        @Override
        public void onSingleTap() {
            toggleShowHideFullScreenUI();
            mXibaVideoPlayer.togglePlayPause();
        }

        @Override
        public void onDoubleTap() {
            mXibaVideoPlayer.quitFullScreen();
        }

    }

    /**
     * 显示或隐藏控制面板
     */
    private void controlFrameOnClick() {
        if (isYoukuAd) {
            return;
        }

        if (cacheImage != null && cacheImage.getVisibility() == View.VISIBLE) {
            return;
        }

        if (!NetworkUtils.hasNetwork(mContext)) {
            ToastUtils.showShort("请先链接网络~");
            return;
        }

        if (isLoading) {
            return;
        }

        if (errorCode == 3002 || errorCode == 2005 || isYoukuComplete) {
            return;
        }
        if (null != controlFrame) {
            if (controlFrame.getVisibility() == View.VISIBLE) {
                controlFrame.setVisibility(View.GONE);
            } else {
                controlFrame.setVisibility(View.VISIBLE);
                if (play.getVisibility() == View.GONE) {
                    play.setVisibility(View.VISIBLE);
                }
                if (seekbarFrame.getVisibility() == View.GONE) {
                    seekbarFrame.setVisibility(View.VISIBLE);
                }
                if (null != mHandle) {
                    mHandle.removeMessages(MSG_HIDE_CONTROL_VIEW);
                    mHandle.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_VIEW, 2000);
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class HyperHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null == msg) {
                return;
            }
            switch (msg.what) {
                case MSG_SHOW_ERROR_TIP:
                    if (null != msg.obj && !StringUtils.isEmptyOrNull(msg.obj.toString())) {
                        String s = (String) msg.obj;
                        showErrorDialog(s);
                    }
                    break;
                case MSG_HIDE_CONTROL_VIEW:
                    if (null != controlFrame) {
                        controlFrame.setVisibility(View.GONE);
                    }
                default:
                    break;
            }
        }
    }

    private CommonDialog errorDialog;

    private void showErrorDialog(String error) {
        if (null != errorDialog && errorDialog.isShowing()) {
            return;
        }
        errorDialog = new CommonDialog.Builder(getContext())
                .setContent(error)
                .setOneButtonInterface(getContext().getString(R.string.a_0076),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                errorDialog.dismiss();
                                errorDialog = null;
                            }
                        }).show();
        errorDialog.setCancelable(true);
    }

}