package com.jingyue.lygame.modules.rcmd;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.jingyue.lygame.BaseFragment;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.RcmdGameListBean;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.events.GameCollectEvent;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.modules.common.MainActivity;
import com.jingyue.lygame.modules.comment.GameScoreActivity;
import com.jingyue.lygame.modules.comment.present.GameCollectPresent;
import com.jingyue.lygame.modules.comment.view.UserActionView;
import com.jingyue.lygame.modules.personal.LoginActivity;
import com.jingyue.lygame.utils.SettingUtil;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.youkuPlayer.YKVideoPlayer;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.PlayerPagerEventCallback;
import com.jingyue.lygame.utils.youkuPlayer.utils.YKListUtil;
import com.jingyue.lygame.widget.CircleImageView;
import com.laoyuegou.android.lib.utils.AnimationUtils;
import com.lygame.libadapter.ImageLoader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;


public class GameItemFragment extends BaseFragment implements UserActionView {

    @BindView(R.id.baseview_ll)
    FrameLayout baseviewLl;
    @BindView(R.id.cache_image)
    ImageView cacheImage;
    @BindView(R.id.fullscreen_click)
    ImageView fullscreenClick;
    @BindView(R.id.tvVideoPlayerTotalTime)
    TextView tvVideoPlayerTotalTime;
    @BindView(R.id.tvVideoPlayerCurrentTime)
    TextView tvVideoPlayerCurrentTime;
    @BindView(R.id.sound)
    ImageView sound;
    @BindView(R.id.sbVideoPlayer)
    SeekBar sbVideoPlayer;
    @BindView(R.id.ivVideoPlayerPauseResume)
    ImageView ivVideoPlayerPauseResume;
    @BindView(R.id.rlVideoPlayerControlFrame)
    RelativeLayout rlVideoPlayerControlFrame;
    //    @BindView(R.id.ivVideoPlayerLoading)
//    ImageView ivVideoPlayerLoading;
    @BindView(R.id.no_connect_text)
    TextView noConnectText;
    @BindView(R.id.gamename)
    TextView gamename;
    @BindView(R.id.like)
    ImageView like;
    @BindView(R.id.rcmd_type)
    TextView rcmdType;
    @BindView(R.id.game_info)
    RelativeLayout gameInfo;
    @BindView(R.id.score)
    TextView score;
    @BindView(R.id.chart)
    RadarChart chart;
    @BindView(R.id.game_score)
    RelativeLayout gameScore;
    @BindView(R.id.ic_ver)
    ImageView icVer;
    @BindView(R.id.comment_title)
    RelativeLayout commentTitle;
    @BindView(R.id.comment_content)
    TextView commentContent;
    @BindView(R.id.author_type)
    ImageView authorType;
    @BindView(R.id.author)
    TextView author;
    @BindView(R.id.author_avater)
    CircleImageView authorAvater;
    @BindView(R.id.author_info)
    RelativeLayout authorInfo;
    @BindView(R.id.comment_ll)
    RelativeLayout commentLl;
    @BindView(R.id.nocomment_ll)
    RelativeLayout nocommentLl;
    @BindView(R.id.seekbarFrame)
    RelativeLayout seekbarFrame;

    private RcmdGameListBean gameBean;
    private int mIndex;
    private MainActivity.PlayerFullScreenEventCallback mFScreenEventCallback;
    private String[] mParties;
    private String videoVid = "";
    private boolean mIsVisibleToUser = false;
    private boolean hasChartShow = false;
    private YKListUtil mYklistUtil;
    private PlayerPagerEventCallback mEventCallback;
    private GameCollectPresent collectPresent;
    private AudioManager mAudioManager;
    private int max;
    private Handler handler;

    public static GameItemFragment newInstance(RcmdGameListBean bean, int position) {
        GameItemFragment instance = new GameItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(KeyConstant.KEY_GAMEBEAN, bean);
        args.putInt(KeyConstant.KEY_INDEX, position);
        instance.setArguments(args);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setEnableEventBus(true);
        Bundle data = getArguments();
        if (data != null) {
            gameBean = (RcmdGameListBean) data.getSerializable(KeyConstant.KEY_GAMEBEAN);
            mIndex = data.getInt(KeyConstant.KEY_INDEX);
            videoVid = gameBean.video_id;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.rcmd_fragment_pageitem;
    }

    @Override
    public void success(int action, String id) {
        if (action == UserActionView.ACTION_UNLIKE) {
            like.setImageResource(R.mipmap.ic_collect_unselect);
            gameBean.is_like = false;
        } else if (action == UserActionView.ACTION_LIKE) {
            like.setImageResource(R.mipmap.ic_collect_select);
            gameBean.is_like = true;
        }
    }

    /**
     * 播放按钮监听
     */
    private class StartListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mEventCallback.bindingPlayerUI(GameItemFragment.this, mIndex);
            mYklistUtil.togglePlay(videoVid, mIndex, baseviewLl, mEventCallback);
        }
    }

    private boolean isCreate = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mYklistUtil = ((MainActivity) getActivity()).getYKListUtil();
        mEventCallback = ((MainActivity) getActivity()).getEventCallback();
        if (!isCreate) {
            isCreate = true;
            if (handler == null) {
                handler = new Handler();
            }
            setupSimpleUI();
            if (mIsVisibleToUser) {
                if (handler != null) {
                    handler.postDelayed(LOAD_DATA, 500);
                }
            }
        }
    }

    private boolean hasCharInit = false;

    private void setupSimpleUI() {
        if (gameBean != null) {
            rcmdType.setText(gameBean.game_tag);
            gamename.setText(gameBean.name);
            score.setText(String.valueOf(gameBean.avg_score));
            nocommentLl.setVisibility(View.VISIBLE);
            commentLl.setVisibility(View.GONE);
            if (gameBean.comment != null) {
                if (!TextUtils.isEmpty(gameBean.comment.content)) {
                    commentContent.setText(gameBean.comment.content);
                    nocommentLl.setVisibility(View.GONE);
                    commentLl.setVisibility(View.VISIBLE);
                }
                if (gameBean.comment.username != null) {
                    author.setText(gameBean.comment.username);
                }
            }
            if (!hasCharInit) {
                initChart();
                hasCharInit = true;
            }
            if (StringUtils.isEmptyOrNull(gameBean.video_id)) {
                ivVideoPlayerPauseResume.setVisibility(View.GONE);
            } else {
                ivVideoPlayerPauseResume.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupUI() {
        if (gameBean == null) {
            return;
        }
        mAudioManager = (AudioManager) getContext().getSystemService(getContext().AUDIO_SERVICE);
        mFScreenEventCallback = ((MainActivity) getActivity()).getFScreenEventCallback();
        if (like == null) {
            mIsVisibleToUser = false;
            isCreate = false;
            return;
        }
        if (gameBean.is_like) {
            like.setImageResource(R.mipmap.ic_collect_select);
        } else {
            like.setImageResource(R.mipmap.ic_collect_unselect);
        }
        max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
            sound.setImageResource(R.mipmap.sound_open);
        } else {
            sound.setImageResource(R.mipmap.sound_close);
        }

        noConnectText.setOnClickListener(new StartListener());
        ivVideoPlayerPauseResume.setOnClickListener(new StartListener());
        cacheImage.setOnClickListener(new StartListener());
        sbVideoPlayer.setOnSeekBarChangeListener(new SeekProgressListener());
        fullscreenClick.setOnClickListener(new FullScreenListener());
        if (!StringUtils.isEmptyOrNull(gameBean.video_img)) {
            ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(cacheImage, gameBean.video_img));
        }
        if (gameBean.comment != null) {
            if (gameBean.comment.comment_honer != null) {
                if (!StringUtils.isEmptyOrNull(gameBean.comment.avatar)) {
                    //TODO 评论头像
                    ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(authorAvater, gameBean.comment.avatar, R.mipmap.userdefault));
                }
                if (!StringUtils.isEmptyOrNull(gameBean.comment.comment_honer.icon)) {
                    ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(authorType, gameBean.comment.comment_honer.icon));
                }
            }
        }

        if (sbVideoPlayer != null) {
            sbVideoPlayer.setMax(100);
            sbVideoPlayer.setProgress(0);
            sbVideoPlayer.setSecondaryProgress(0);
            sbVideoPlayer.setThumbOffset(1);
        }

        if (!hasChartShow) {
            setData(false);
            chart.animateY(AppConstants.DEFAULT_ANIMATION_TIME);
            hasChartShow = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != handler) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    /**
     * 全屏按钮监听
     */
    private class FullScreenListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mFScreenEventCallback.bindingPlayerUI(GameItemFragment.this);
            mEventCallback.bindingPlayerUI(GameItemFragment.this, mIndex);
            mYklistUtil.startFullScreen(videoVid, mIndex, baseviewLl,
                    mEventCallback, mFScreenEventCallback, gameBean.video_img);
        }
    }

    private void initChart() {
        if (chart == null) {
            return;
        }
        mParties = new String[]{
                getString(R.string.a_0067),
                getString(R.string.a_0062),
                getString(R.string.a_0063),
                getString(R.string.a_0064),
                getString(R.string.a_0065),
                getString(R.string.a_0066)
        };

        chart.setDescription("");
        chart.setWebLineWidth(0.75f);
        chart.setWebLineWidthInner(0.75f);
        chart.setWebAlpha(30);
        chart.setRotationAngle(-120f);
        chart.setValueColorInner(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getContext().getTheme()));
        chart.setBgColorInner(ResourcesCompat.getColor(getResources(), R.color.chart_blue, getContext().getTheme()));
        chart.setWebColorInner(ResourcesCompat.getColor(getResources(), R.color.chart_blue, getContext().getTheme()));
        chart.setWebColor(ResourcesCompat.getColor(getResources(), R.color.chart_blue, getContext().getTheme()));
        chart.setTouchEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setEnabled(true);
        YAxis yAxis = chart.getYAxis();
        yAxis.setEnabled(false);
        yAxis.setLabelCount(6, false);
        yAxis.setTextSize(12f);
        yAxis.setStartAtZero(true);
        xAxis.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black_text, baseActivity.getTheme()));
        yAxis.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black_text, baseActivity.getTheme()));
        setData(true);

    }

    public void setData(boolean isFirstShow) {
        RadarDataSet set1;
        String[] scores = new String[]{"0", "0", "0", "0", "0", "0"};
        int cnt = 6;
        if (isFirstShow) {
            ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            for (int i = 0; i < cnt; i++) {
                yVals1.add(new Entry(Float.valueOf(scores[i]), i));
            }
            set1 = new RadarDataSet(yVals1, "");
        } else {
            if (gameBean.score != null && !gameBean.score.isEmpty()) {
                scores = gameBean.score.split(",");
            }
            cnt = scores.length;
            ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            for (int i = 0; i < cnt; i++) {
                yVals1.add(new Entry(Float.valueOf(scores[i]), i));
            }
            set1 = new RadarDataSet(yVals1, "");
        }
        set1.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getContext().getTheme()));
        set1.setFillColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getContext().getTheme()));
        set1.setDrawFilled(true);
        set1.setLineWidth(1f);
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < cnt; i++)
            xVals.add(mParties[i % mParties.length]);

        ArrayList<RadarDataSet> sets = new ArrayList<RadarDataSet>();
        sets.add(set1);

        RadarData data = new RadarData(xVals, sets);
        data.setValueTextSize(10f);
        data.setDrawValues(true);

        chart.setData(data);
        chart.invalidate();
    }

    @OnClick({R.id.like, R.id.sound, R.id.nocomment_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.like:
                if (gameBean.is_like) {
                    doCollect(true);
                } else {
                    doCollect(false);
                }
                break;
            case R.id.sound:
                if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                    if (SettingUtil.readInt(getContext(), KeyConstant.KEY_VIDEO_VOICE_VALUE, 0) > 0) {
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                SettingUtil.readInt(getContext(), KeyConstant.KEY_VIDEO_VOICE_VALUE, 0), AudioManager.STREAM_MUSIC);
                    } else {
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max / 2, AudioManager.STREAM_MUSIC);
                        SettingUtil.write(getContext(), KeyConstant.KEY_VIDEO_VOICE_VALUE, max / 2);
                    }
                    sound.setImageResource(R.mipmap.sound_open);
                } else {
                    sound.setImageResource(R.mipmap.sound_close);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.STREAM_MUSIC);
                }
                break;
            case R.id.nocomment_ll:
                if (gameBean == null) {
                    return;
                }
                GameScoreActivity.start(getContext(), String.valueOf(gameBean.id), gameBean.score);
                break;
        }
    }

    private void doCollect(boolean isAdd) {
        if (LoginManager.getInstance().isLogin()) {
            if (collectPresent == null) {
                collectPresent = new GameCollectPresent(this, this);
                add(collectPresent);
            }
            collectPresent.doAction(String.valueOf(gameBean.id), isAdd);
        } else {
            LoginActivity.open(getContext(), LoginActivity.BACK_BACK_LOGIN_BACK);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ChangeCollectStatusEvent(GameCollectEvent event) {
        if (event.currentStatus == GameCollectEvent.TYPE_UNLIKE && event.appid.equals(String.valueOf(gameBean.id))) {
            like.setImageResource(R.mipmap.ic_collect_unselect);
            gameBean.is_like = false;
        } else if (event.currentStatus == GameCollectEvent.TYPE_LIKE && event.appid.equals(String.valueOf(gameBean.id))) {
            gameBean.is_like = true;
            like.setImageResource(R.mipmap.ic_collect_select);
        }
        AnimationUtils.biggerThenReturnAni(like).start();
        gameBean.save();
    }

    public void resetUI(boolean showcache, String url) {
        if (showcache) {
            cacheImage.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(cacheImage, url));
        } else {
            cacheImage.setVisibility(View.GONE);
        }
        rlVideoPlayerControlFrame.setVisibility(View.VISIBLE);
        //设置播放按钮状态
        if (mYklistUtil.getCurrentState() == YKVideoPlayer.STATE_PLAYING) {
            ivVideoPlayerPauseResume.setImageResource(R.mipmap.stop);
        } else {
            ivVideoPlayerPauseResume.setImageResource(R.mipmap.ic_play);
        }

        //如果视频未加载，进度条不可用
        if (mYklistUtil.getCurrentState() == YKVideoPlayer.STATE_NORMAL
                || mYklistUtil.getCurrentState() == YKVideoPlayer.STATE_ERROR) {

            sbVideoPlayer.setEnabled(false);
        } else {

            sbVideoPlayer.setEnabled(true);

            long totalTimeDuration = mYklistUtil.getDuration();
            long currentTimePosition = mYklistUtil.getCurrentPosition();

            //设置视频总时长和当前播放位置
            tvVideoPlayerCurrentTime.setText(StringUtils.stringForTime(currentTimePosition));
            tvVideoPlayerTotalTime.setText(StringUtils.stringForTime(totalTimeDuration));

            int progress = (int) (currentTimePosition * 100 / (totalTimeDuration == 0 ? 1 : totalTimeDuration));   //播放进度

            //设置进度条位置
            sbVideoPlayer.setProgress(progress);
        }
    }

    /**
     * 进度条监听
     */
    private class SeekProgressListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            mEventCallback.bindingPlayerUI(GameItemFragment.this, mIndex);
            mYklistUtil.seekTo(videoVid, mIndex, baseviewLl,
                    mEventCallback, sbVideoPlayer.getProgress(), sbVideoPlayer.getMax());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        mIsVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            if (isCreate && !hasChartShow) {
                if (handler == null) {
                    handler = new Handler();
                }
                handler.postDelayed(LOAD_DATA, 500);
            }
        } else {
            if (handler != null) {
                handler.removeCallbacks(LOAD_DATA);
            }
            if (mYklistUtil != null) {
                mYklistUtil.getYKVideoPlayer().showCache();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    //延时加载页面数据
    private Runnable LOAD_DATA = new Runnable() {
        @Override
        public void run() {
            setupUI();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public ImageView getPlay() {
        return ivVideoPlayerPauseResume;
    }

    public ImageView getCacheImage() {
        return cacheImage;
    }

    public RelativeLayout getConrolFrame() {
        return rlVideoPlayerControlFrame;
    }

    public TextView getNoNetTx() {
        return noConnectText;
    }

    public TextView getCurrentTimeTV() {
        return tvVideoPlayerCurrentTime;
    }

    public TextView getTotalTimeTV() {
        return tvVideoPlayerTotalTime;
    }

    public ImageView getSound() {
        return sound;
    }

    public SeekBar getDemoSeek() {
        return sbVideoPlayer;
    }

//    public ImageView getLoadingPB() {
//        return ivVideoPlayerLoading;
//    }
//
//    public AnimationDrawable getAniDrawable() {
//        return (AnimationDrawable) ivVideoPlayerLoading.getDrawable();
//    }

    public RelativeLayout getseekbarFrame() {
        return seekbarFrame;
    }
}
