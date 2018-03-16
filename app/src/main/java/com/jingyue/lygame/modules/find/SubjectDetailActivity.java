package com.jingyue.lygame.modules.find;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.ShareEntityBean;
import com.jingyue.lygame.bean.SubjectDetailBean;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.events.VideoContainerChangeEvent;
import com.jingyue.lygame.model.SubjectDetailRp;
import com.jingyue.lygame.modules.common.MainActivity;
import com.jingyue.lygame.modules.find.present.SubjectActionPresent;
import com.jingyue.lygame.modules.find.present.SubjectDetailPresent;
import com.jingyue.lygame.modules.find.view.SubjectDetailView;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.ToolUtils;
import com.jingyue.lygame.utils.youkuPlayer.YKMediaManager;
import com.jingyue.lygame.utils.youkuPlayer.YKVideoPlayer;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.YKFullScreenEventCallback;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.YKPlayerEventCallback;
import com.jingyue.lygame.utils.youkuPlayer.utils.YKBaseListUtil;
import com.jingyue.lygame.utils.youkuPlayer.utils.YKListUtil;
import com.jingyue.lygame.utils.youkuPlayer.view.FullScreenContainer;
import com.jingyue.lygame.widget.BaseLinearLayoutManager;
import com.jingyue.lygame.widget.CommonDialog;
import com.laoyuegou.android.lib.utils.BarUtils;
import com.laoyuegou.android.lib.utils.EBus;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.lygame.libadapter.ImageLoader;

import butterknife.BindView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class SubjectDetailActivity extends BaseActivity implements SubjectDetailView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    @BindView(R.id.retry_ll)
    RelativeLayout retryLl;
    @BindView(R.id.paddingview)
    View paddingview;

    private SubjectDetailPresent subjectDetailPresent;
    private SubjectActionPresent actionPresent;
    private SubjectDetailBean.Subject subject;
    private String subject_id;
    private YKListUtil ykListUtil;
    private ListEventCallback mEventCallback;
    private ListFullScreenEventCallback mFScreenEventCallback;
    //全屏容器
    private FullScreenContainer mFullScreenContainer;

    private Message mUtilMsg;
    private ActionBarUtil mBar;
    private SubjectDetailAdapter adapter;
    //    private AnimationDrawable animLoading;
    private AudioManager mAudioManager;
    private int scrollY = 0;
    private int itemHeight = 0;
    private int headHeight = 0;
    private int scrollExtent = 0;//当前屏幕显示的区域高度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBar = ActionBarUtil.inject(this).title("").showSubject().hideCollect();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup.LayoutParams params1 = paddingview.getLayoutParams();
            params1.height = BarUtils.getStatusBarHeight(getContext());
            paddingview.setLayoutParams(params1);
        }
        add(subjectDetailPresent = new SubjectDetailPresent(this, this, new SubjectDetailRp()));
        add(actionPresent = new SubjectActionPresent(mBar, this));
        mBar.setSubActionPresent(actionPresent);
        subject_id = getIntent().getStringExtra(KeyConstant.KEY_SUBJECT_ID);
        if (!StringUtils.isEmptyOrNull(subject_id)) {
            ShareEntityBean config = new ShareEntityBean();
            final StringBuilder s = new StringBuilder();
            s.append(UrlConstant.BASE_H5_URL).append(UrlConstant.SHARE_URL_SUFFIX).append(AppConstants.SHARE_FROM.GAMETHEME_PAGE).append("/").append(subject_id);
            config.setUrl(s.toString());
            mBar.setShare(config);
        }
        mAudioManager = (AudioManager) getSystemService(getContext().AUDIO_SERVICE);
        ptrRefresh.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                ptrRefresh.refreshComplete();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                loadData(true, false);
            }
        });
        recyclerView.setLayoutManager(new BaseLinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollY -= dy;

                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                if (manager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) manager;
                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                    int firstItemPosition = linearManager.findFirstVisibleItemPosition();

                    if (scrollExtent == 0) {
                        scrollExtent = recyclerView.computeVerticalScrollExtent();
                    }
                    if (headHeight == 0 && firstItemPosition == 0) {
                        headHeight = recyclerView.getChildAt(0).getHeight();
                    }
                    if (itemHeight == 0 && firstItemPosition <= 1 && lastItemPosition >= 1) {
                        itemHeight = recyclerView.getChildAt(1).getHeight();
                    }
                    if (ykListUtil == null) {
                        return;
                    }
                    int index = ykListUtil.getPlayingIndex();
                    if (index < 1) {
                        return;
                    }

                    if (Math.abs(scrollY) > (headHeight + 430 + itemHeight * (index - 1))
                            || (scrollExtent + Math.abs(scrollY)) < (headHeight + 240 + itemHeight * (index - 1))) {
                        if (ykListUtil.getCurrentState() == YKVideoPlayer.STATE_PLAYING || ykListUtil.getCurrentState() == YKVideoPlayer.STATE_PAUSE) {
                            ykListUtil.getYKVideoPlayer().showCache();
                        }
                    }
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        ykListUtil = new YKListUtil(this);
        mEventCallback = new ListEventCallback();
        mFScreenEventCallback = new ListFullScreenEventCallback();

        ykListUtil.setPlayingItemPositionChangeImpl(new YKBaseListUtil.PlayingItemPositionChange() {
            @Override
            public void prePlayingItemPositionChange(Message utilMsg) {
                mUtilMsg = utilMsg;
            }

            @Override
            public void prePlayingItemChangeOnPause() {
                if (mEventCallback != null && mEventCallback.getHolder() != null) {
                    mEventCallback.getHolder().sbVideoPlayer.setEnabled(false);

                    //如果loading正在显示，在这里隐藏
//                    if (mEventCallback.getHolder().ivLoading.getVisibility() == View.VISIBLE) {
//                        mEventCallback.getHolder().ivLoading.setVisibility(View.GONE);
//                    }
                    mEventCallback.changeHolder();
                }
            }
        });
        loadData(true, true);
    }

    private void loadData(boolean isRefresh, boolean needProgress) {
        if (ykListUtil.getYKVideoPlayer().getCurrentState() == YKVideoPlayer.STATE_PLAYING) {
            ykListUtil.getYKVideoPlayer().showCache();
        }
        subjectDetailPresent.loadData(isRefresh, subject_id, needProgress);
    }

    @Override
    protected void onDestroy() {
        if (ykListUtil.getYKVideoPlayer().isPlaying()) {
            ykListUtil.getYKVideoPlayer().onPause();
        }
        ykListUtil.release();
        EBus.getDefault().post(new VideoContainerChangeEvent(VideoContainerChangeEvent.TYPE_NEED_CREATE));
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                ToolUtils.controlVolumeAndNotify(mAudioManager, true);
                return false;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                ToolUtils.controlVolumeAndNotify(mAudioManager, false);
                return false;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_subject_detail;
    }

    public static void start(Context context, String id) {
        ((MainActivity) context).getYKListUtil().release();
        Intent starter = new Intent(context, SubjectDetailActivity.class);
        starter.putExtra(KeyConstant.KEY_SUBJECT_ID, id);
        context.startActivity(starter);
    }

    private void updateTitle() {
        mBar.title(subject.name).setSubjectId(subject.id);
        mBar.setIsSublike(subject.isLike);
        mBar.setSubLikeNum(subject.likeNum);
    }

    @Override
    public void setDataOnMain(SubjectDetailBean beans, boolean isRefresh) {
        ptrRefresh.refreshComplete();
        subject = beans.subject;
        if (adapter == null) {
            adapter = new SubjectDetailAdapter(this, ykListUtil, mEventCallback, mFScreenEventCallback, subject);
            recyclerView.setAdapter(adapter);
        }
        updateTitle();
        adapter.notifyDataChanged(beans.getList());
    }


    @Override
    public void noMore() {
        if (subject == null) {
            retryLl.setVisibility(View.VISIBLE);
        } else {
            if (ptrRefresh != null) {
                ptrRefresh.refreshComplete();
            }
        }
    }

    @OnClick(R.id.retry_ll)
    public void onViewClicked() {
        loadData(true, true);
        retryLl.setVisibility(View.GONE);
    }

    public class ListEventCallback implements YKPlayerEventCallback {
        private boolean isYoukuComplete = false;
        private boolean isYoukuAd = false;
        private boolean isCacheShow = false;
        private boolean isLoading = false;
        private boolean isError;
        private int errorCode;
        private SubjectDetailAdapter.VideoViewHolder holder;
        private static final int MSG_HIDE_CONTROL_VIEW = 0x0001;
        private static final int MSG_SHOW_ERROR_TIP = 0x0002;

        private SubjectDetailAdapter.VideoViewHolder nextHolder;
        private HyperHandler mHandle = new HyperHandler();

        public void bindHolder(SubjectDetailAdapter.VideoViewHolder holder, int position) {
            if (this.holder == null || ykListUtil.getPlayingIndex() == position) {
                this.holder = holder;
            } else {
                this.nextHolder = holder;
            }
        }

        public void changeHolder() {
            if (nextHolder != null) {
                holder = nextHolder;
                nextHolder = null;
            }
        }

        public SubjectDetailAdapter.VideoViewHolder getHolder() {
            return holder;
        }

        @Override
        public void onLoad() {
            isYoukuComplete = false;
            isLoading = false;
//            if (holder.ivLoading != null && holder.ivLoading.getVisibility() == View.VISIBLE) {
//                holder.ivLoading.setVisibility(View.GONE);
//            }
        }

        @Override
        public void onLoading() {
            isYoukuAd = false;
            isCacheShow = false;
            isLoading = true;
            if (null != holder.controlFrame && holder.controlFrame.getVisibility() == View.VISIBLE) {
                holder.controlFrame.setVisibility(View.GONE);
            }

//            if (holder.ivLoading.getVisibility() == View.GONE) {
//                holder.ivLoading.setVisibility(View.VISIBLE);
//            }
//            animLoading = (AnimationDrawable) holder.ivLoading.getDrawable();
//            animLoading.start();
            if (holder.cacheImage != null && holder.cacheImage.getVisibility() == View.VISIBLE) {
                holder.cacheImage.setVisibility(View.GONE);
            }
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
                    error = getString(R.string.a_0237);
                    break;
                case 1009:   // 搜索出错
                    error = getString(R.string.a_0236);
                    break;
                case 1007:   // 播放器准备失败
                    error = getString(R.string.a_0235);
                    break;
                case 1010:   // 准备超时
                    error = getString(R.string.a_0234);
                    break;
                case 1110:   // 广告出现4xx错误
//                            error = getString(R.string.a_1453);
//                            break;
                case 1111:   // 正片出现4xx错误
//                            error = getString(R.string.a_1454);
                case 2004:   // 超时
                    isError = true;
                    error = getString(R.string.a_0238);
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
                    if (!NetworkUtils.hasNetwork(Utils.getContext())) {
                        ToastUtils.showShort(R.string.a_0241);
                    }
                    break;
                case 1006:   // 数据源错误
                    error = getString(R.string.a_0239);
                    break;
                default:     // 未知错误
                    error = getString(R.string.a_0240);
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
        }

        @Override
        public void showErrorDialog() {

        }

        @Override
        public void onAdStart() {
            isError = false;
            isYoukuAd = true;
            if (holder.cacheImage != null && holder.cacheImage.getVisibility() == View.VISIBLE) {
                holder.cacheImage.setVisibility(View.GONE);
            }
//            if (holder.ivLoading != null && holder.ivLoading.getVisibility() == View.VISIBLE) {
//                holder.ivLoading.setVisibility(View.GONE);
//            }
            if (holder.controlFrame != null && holder.controlFrame.getVisibility() == View.VISIBLE) {
                holder.controlFrame.setVisibility(View.GONE);
            }
        }

        @Override
        public void onRealStart() {
            isError = false;
            if (isCacheShow) {
                YKMediaManager.getInstance(getContext()).releaseMediaPlayer();
            } else {
                holder.ivPlay.setImageResource(R.mipmap.stop);
            }

        }

        @Override
        public void onComplete() {
            isYoukuComplete = true;
            showCache();
        }

        @Override
        public void onChangingPosition(long originPosition, long seekTimePosition, long totalTimeDuration) {
            int progress = (int) (seekTimePosition * 100 / (totalTimeDuration == 0 ? 1 : totalTimeDuration));   //播放进度
            holder.sbVideoPlayer.setProgress(progress);
        }

        @Override
        public void showCache() {
            isCacheShow = true;
            if (null != mHandle) {
                mHandle.removeMessages(MSG_HIDE_CONTROL_VIEW);
            }
            if (holder.ivPlay.getVisibility() == View.GONE) {
                holder.ivPlay.setVisibility(View.VISIBLE);
            }
            holder.ivPlay.setImageResource(R.mipmap.ic_play);
            if (holder.cacheImage.getVisibility() == View.GONE) {
                holder.cacheImage.setVisibility(View.VISIBLE);
            }
            if (holder.controlFrame.getVisibility() == View.GONE) {
                holder.controlFrame.setVisibility(View.VISIBLE);
            }
            if (holder.seekbarFrame.getVisibility() == View.VISIBLE) {
                holder.seekbarFrame.setVisibility(View.GONE);
            }

            holder.noConnectText.setVisibility(View.GONE);
            holder.tvCurrentTime.setText(R.string.a_0139);
            holder.tvTotalTime.setText(R.string.a_0139);
        }

        @Override
        public void controlFrame(boolean isPlaying) {
            controlFrameOnClick();
        }

        @Override
        public void onPlayerProgressUpdate(int progress, int secProgress, long currentTime, long totalTime) {

            holder.tvCurrentTime.setText(StringUtils.stringForTime(currentTime));
            holder.tvTotalTime.setText(StringUtils.stringForTime(totalTime));

            holder.sbVideoPlayer.setProgress(progress);

            holder.sbVideoPlayer.setSecondaryProgress(secProgress);
            if (!holder.sbVideoPlayer.isEnabled()) {
                holder.sbVideoPlayer.setEnabled(true);
            }

            //如果loading正在显示，在这里隐藏
//            if (holder.ivLoading.getVisibility() == View.VISIBLE) {
//                holder.ivLoading.setVisibility(View.GONE);
//            }

        }

        @Override
        public void onPlayerPause() {
            if (nextHolder != null) {
                showCache();
            }
            if (holder.controlFrame.getVisibility() == View.GONE) {
                holder.controlFrame.setVisibility(View.VISIBLE);
            }
            if (holder.ivPlay.getVisibility() == View.GONE) {
                holder.ivPlay.setVisibility(View.VISIBLE);
            }
            if (holder.seekbarFrame.getVisibility() == View.GONE) {
                holder.seekbarFrame.setVisibility(View.VISIBLE);
            }
            holder.ivPlay.setImageResource(R.mipmap.ic_play);
            if (mUtilMsg != null) {
                holder.sbVideoPlayer.setEnabled(false);

                //如果loading正在显示，在这里隐藏
//                if (holder.ivLoading.getVisibility() == View.VISIBLE) {
//                    holder.ivLoading.setVisibility(View.GONE);
//                }

                //在这里解除对Holder的绑定，否则loading会出现在上一个Item中
                showCache();
                mEventCallback.changeHolder();

                mUtilMsg.sendToTarget();

                mUtilMsg = null;
            }
        }

        @Override
        public void onPlayerResume() {
            if (holder.cacheImage.getVisibility() == View.VISIBLE) {
                holder.cacheImage.setVisibility(View.GONE);
            }
            isCacheShow = false;
            if (isYoukuComplete) {
                return;
            }
            holder.ivPlay.setImageResource(R.mipmap.stop);
            if (holder.controlFrame.getVisibility() == View.VISIBLE) {
                holder.controlFrame.setVisibility(View.GONE);
            }
            if (null != mHandle) {
                mHandle.removeMessages(MSG_HIDE_CONTROL_VIEW);
                mHandle.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_VIEW, 2000);
            }
        }

        @Override
        public void release() {

        }

        @Override
        public void onVoiceChange(boolean isOpen) {
            if (isOpen) {
                holder.sound.setImageResource(R.mipmap.sound_open);
            } else {
                holder.sound.setImageResource(R.mipmap.sound_close);
            }
        }

        /**
         * 显示或隐藏控制面板
         */
        private void controlFrameOnClick() {
            if (isYoukuAd) {
                return;
            }
            if (holder.cacheImage != null && holder.cacheImage.getVisibility() == View.VISIBLE) {
                return;
            }

            if (!NetworkUtils.hasNetwork(Utils.getContext())) {
                return;
            }

            if (isLoading) {
                return;
            }

            if (errorCode == 3002 || errorCode == 2005 || isYoukuComplete) {
                return;
            }
            if (null != holder.controlFrame) {
                if (holder.controlFrame.getVisibility() == View.GONE) {
                    holder.controlFrame.setVisibility(View.VISIBLE);
                    if (holder.ivPlay.getVisibility() == View.GONE) {
                        holder.ivPlay.setVisibility(View.VISIBLE);
                    }
                    if (holder.seekbarFrame.getVisibility() == View.GONE) {
                        holder.seekbarFrame.setVisibility(View.VISIBLE);
                    }
                    if (null != mHandle) {
                        mHandle.removeMessages(MSG_HIDE_CONTROL_VIEW);
                        mHandle.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_VIEW, 2000);
                    }
                } else {
                    holder.controlFrame.setVisibility(View.GONE);
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
                    case MSG_HIDE_CONTROL_VIEW:
                        if (null != holder.controlFrame) {
                            holder.controlFrame.setVisibility(View.GONE);
                        }
                        break;
                    case MSG_SHOW_ERROR_TIP:
                        if (null != msg.obj && !StringUtils.isEmptyOrNull(msg.obj.toString())) {
                            String s = (String) msg.obj;
                            showErrorDialog(s);
                        }
                        break;
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


    public class ListFullScreenEventCallback implements YKFullScreenEventCallback {

        private SubjectDetailAdapter.VideoViewHolder holder;

        public RecyclerView.ViewHolder getHolder() {
            return holder;
        }

        public void setHolder(SubjectDetailAdapter.VideoViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public ViewGroup onEnterFullScreen(boolean showcache, String url) {

            mFullScreenContainer = new FullScreenContainer(SubjectDetailActivity.this, 0);
            if (showcache) {
                mFullScreenContainer.showCache(showcache, url);
            }
            //初始化全屏控件
//            initFullScreenUI();
            mFullScreenContainer.initUI(ykListUtil.getYKVideoPlayer());

            ykListUtil.setEventCallback(mFullScreenContainer.getFullScreenEventCallback());
            ykListUtil.setPlayerActionEventCallback(mFullScreenContainer.getFullScreenEventCallback());

            return mFullScreenContainer;
        }

        @Override
        public void onQuitFullScreen(boolean showcache, String url) {

            mFullScreenContainer.releaseFullScreenUI();


            //重新设置item的UI状态
            //这里不能像ListView中使用保存播放状态的方式处理
            if (holder != null) {
                resetUI(holder, showcache, url);
            }
            //绑定List的eventCallback
            ykListUtil.setEventCallback(mEventCallback);
            ykListUtil.setPlayerActionEventCallback(null);

        }

    }

    private void resetUI(SubjectDetailAdapter.VideoViewHolder holder, boolean showcache, String url) {
        //设置播放按钮状态
        if (ykListUtil.getCurrentState() == YKVideoPlayer.STATE_PLAYING) {
            holder.ivPlay.setImageResource(R.mipmap.stop);
        } else {
            holder.ivPlay.setImageResource(R.mipmap.ic_play);
        }
        if (showcache) {
            holder.cacheImage.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(holder.cacheImage, url));
            if (holder.seekbarFrame.getVisibility() == View.VISIBLE) {
                holder.seekbarFrame.setVisibility(View.GONE);
            }
        } else {
            holder.cacheImage.setVisibility(View.GONE);
        }


    }

    @Override
    public void onBackPressed() {
        if (ykListUtil.onBackPress()) {
            return;
        }
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ykListUtil != null) {
            ykListUtil.onBackPress();
            ykListUtil.getYKVideoPlayer().showCache();
        }
    }

}
