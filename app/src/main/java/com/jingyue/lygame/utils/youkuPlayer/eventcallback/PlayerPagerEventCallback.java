package com.jingyue.lygame.utils.youkuPlayer.eventcallback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.modules.rcmd.GameItemFragment;
import com.jingyue.lygame.utils.SettingUtil;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.youkuPlayer.utils.YKBaseListUtil;
import com.jingyue.lygame.utils.youkuPlayer.utils.YKListUtil;
import com.jingyue.lygame.widget.CommonDialog;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;

public class PlayerPagerEventCallback implements YKPlayerEventCallback {

    private ImageView play;
    private TextView currentTimeTV;
    private TextView totalTimeTV;
    private SeekBar demoSeek;
    private ImageView cacheImage;
    //    private LinearLayout parent;
    private TextView noConnectText;
    //    private ImageView loadingPB;
    private RelativeLayout controlFrame;
    private RelativeLayout seekbarFrame;
    private int errorCode;
    private ImageView sound;
    private boolean isYoukuAd = false;
    // 蜂窝网提示
    private CommonDialog noWifiDialog;
    private boolean isNoWifePlay = false;

    private boolean isYoukuComplete = false;
    private boolean isLoading;
    // 播放完毕
    private CommonDialog completionDialog;

    private static final int MSG_HIDE_CONTROL_VIEW = 0x0001;
    private static final int MSG_SHOW_ERROR_TIP = 0x0002;

    private GameItemFragment playerUI;

    private GameItemFragment nextPlayerUI;

    private YKListUtil mYklistutil;

    private Message mUtilMsg;
    private Context mContext;
    private HyperHandler mHandle;
    //    private AnimationDrawable animLoading;
    private boolean isError;
    private boolean isCacheShow = false;

    public PlayerPagerEventCallback(Context context, YKListUtil mYkListutil) {
        this.mYklistutil = mYkListutil;
        mContext = context;
        initHandle();
        mYklistutil.setPlayingItemPositionChangeImpl(new YKBaseListUtil.PlayingItemPositionChange() {

            @Override
            public void prePlayingItemPositionChange(Message utilMsg) {
                mUtilMsg = utilMsg;
            }

            @Override
            public void prePlayingItemChangeOnPause() {
                if (playerUI != null) {
                    demoSeek.setEnabled(false);

                    //如果loading正在显示，在这里隐藏
//                    if (loadingPB.getVisibility() == View.VISIBLE) {
//                        loadingPB.setVisibility(View.GONE);
//                    }
                    changePlayerUI();
                }
            }
        });
    }

    private void initHandle() {
        mHandle = new HyperHandler();
    }

    public void bindingPlayerUI(GameItemFragment playerFragment, int position) {
        if (this.playerUI == null || mYklistutil.getPlayingIndex() == position) {
            this.playerUI = playerFragment;
            bindingUIItem();
        } else {
            this.nextPlayerUI = playerFragment;
        }
    }

    public void changePlayerUI() {

        if (nextPlayerUI != null) {
            playerUI = nextPlayerUI;
            nextPlayerUI = null;
            bindingUIItem();
        }
    }

    private void bindingUIItem() {
        this.cacheImage = playerUI.getCacheImage();
        this.noConnectText = playerUI.getNoNetTx();
        this.controlFrame = playerUI.getConrolFrame();
        this.play = playerUI.getPlay();
        this.currentTimeTV = playerUI.getCurrentTimeTV();
        this.totalTimeTV = playerUI.getTotalTimeTV();
        this.demoSeek = playerUI.getDemoSeek();
//        this.loadingPB = playerUI.getLoadingPB();
//        this.animLoading = playerUI.getAniDrawable();
        this.seekbarFrame = playerUI.getseekbarFrame();
        this.sound = playerUI.getSound();
    }

    @Override
    public void onLoad() {
        isYoukuComplete = false;
        isLoading = false;

//        if(loadingPB!=null&&loadingPB.getVisibility() == View.VISIBLE){
//            loadingPB.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onLoading() {
        isCacheShow = false;
        isLoading = true;
        isYoukuAd = false;
        if (null != controlFrame && controlFrame.getVisibility() == View.VISIBLE) {
            controlFrame.setVisibility(View.GONE);
        }
//        if (null != loadingPB && loadingPB.getVisibility() == View.GONE) {
//            loadingPB.setVisibility(View.VISIBLE);
//        }
//        animLoading.start();
        if (cacheImage != null && cacheImage.getVisibility() == View.VISIBLE) {
            cacheImage.setVisibility(View.GONE);
        }
        noConnectText.setVisibility(View.GONE);
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
                error = mContext.getString(R.string.a_0240);
                break;
            case 2004:   // 超时
                isError = true;
                error = "";
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
                if (NetworkUtils.hasNetwork(mContext)) {
                    ToastUtils.showShortSafe(R.string.a_0059);
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
        if (null != controlFrame && controlFrame.getVisibility() == View.VISIBLE) {
            controlFrame.setVisibility(View.GONE);
        }
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
//        if(loadingPB!=null&&loadingPB.getVisibility() == View.VISIBLE){
//            loadingPB.setVisibility(View.GONE);
//        }
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
        demoSeek.setProgress(progress);
    }

    @Override
    public void showCache() {
        isCacheShow = true;
        if (null != mHandle) {
            mHandle.removeMessages(MSG_HIDE_CONTROL_VIEW);
        }
        cacheImage.setVisibility(View.VISIBLE);

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
//        if (loadingPB != null && loadingPB.getVisibility() == View.VISIBLE) {
//            loadingPB.setVisibility(View.GONE);
//        }
        noConnectText.setVisibility(View.GONE);
        currentTimeTV.setText(R.string.a_0139);
        totalTimeTV.setText(R.string.a_0139);
    }

    @Override
    public void controlFrame(boolean isPlaying) {
        controlFrameOnClick();
    }

    @Override
    public void onPlayerProgressUpdate(int progress, int secProgress, long currentTime, long totalTime) {
        currentTimeTV.setText(StringUtils.stringForTime(currentTime));
        totalTimeTV.setText(StringUtils.stringForTime(totalTime));
        demoSeek.setProgress(progress);
        demoSeek.setSecondaryProgress(secProgress);
        if (!demoSeek.isEnabled()) {
            demoSeek.setEnabled(true);
        }

    }

    @Override
    public void onPlayerPause() {
        if (null != mHandle) {
            mHandle.removeMessages(MSG_HIDE_CONTROL_VIEW);
        }
        if (controlFrame.getVisibility() == View.GONE) {
            controlFrame.setVisibility(View.VISIBLE);
            if (play.getVisibility() == View.GONE) {
                play.setVisibility(View.VISIBLE);
            }
            if (seekbarFrame.getVisibility() == View.GONE) {
                seekbarFrame.setVisibility(View.VISIBLE);
            }
            if (SettingUtil.readInt(mContext, KeyConstant.KEY_VIDEO_VOICE_VALUE, 0) > 0) {
                sound.setImageResource(R.mipmap.sound_open);
            } else {
                sound.setImageResource(R.mipmap.sound_close);
            }
        }
        play.setImageResource(R.mipmap.ic_play);
        if (mUtilMsg != null) {
            demoSeek.setEnabled(false);

            //如果loading正在显示，在这里隐藏
//            if (loadingPB.getVisibility() == View.VISIBLE) {
//                loadingPB.setVisibility(View.GONE);
//            }

            //在这里解除对Holder的绑定，否则loading会出现在上一个Item中
            changePlayerUI();

            mUtilMsg.sendToTarget();

            mUtilMsg = null;
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
                    mHandle.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_VIEW, 5000);
                }
            }
        }
    }

    @Override
    public void onPlayerResume() {
        isCacheShow = false;
        if (cacheImage.getVisibility() == View.VISIBLE) {
            cacheImage.setVisibility(View.GONE);
        }
        if (isYoukuComplete) {
            return;
        }
        play.setImageResource(R.mipmap.stop);
        if (controlFrame.getVisibility() == View.VISIBLE) {
            controlFrame.setVisibility(View.GONE);
        }
    }

    @Override
    public void release() {
        if (null != mHandle) {
            mHandle.removeCallbacksAndMessages(null);
            mHandle = null;
        }
        if (demoSeek != null) {
            currentTimeTV.setText(R.string.a_0139);
            totalTimeTV.setText(R.string.a_0139);
            demoSeek.setSecondaryProgress(0);
            demoSeek.setProgress(0);
        }
        if (playerUI != null) {
            playerUI = null;
        }

        isNoWifePlay = false;

        if (null != noWifiDialog) {
            if (noWifiDialog.isShowing()) {
                noWifiDialog.dismiss();
            }
            noWifiDialog = null;
        }

        if (null != completionDialog) {
            if (completionDialog.isShowing()) {
                completionDialog.dismiss();
            }
            completionDialog = null;
        }
    }

    @Override
    public void onVoiceChange(boolean isOpen) {
        if (isOpen) {
            sound.setImageResource(R.mipmap.sound_open);
        } else {
            sound.setImageResource(R.mipmap.sound_close);
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
        errorDialog = new CommonDialog.Builder(mContext)
                .setContent(error)
                .setOneButtonInterface(mContext.getString(R.string.a_0076),
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
