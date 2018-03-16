package com.jingyue.lygame.modules.common;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.VersionInfoBean;
import com.jingyue.lygame.clickaction.ScreenAction;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.events.LoginEvent;
import com.jingyue.lygame.events.LoginStatusChangeEvent;
import com.jingyue.lygame.events.VideoContainerChangeEvent;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.model.SearchWordsListRp;
import com.jingyue.lygame.modules.comment.CommentMainFragment;
import com.jingyue.lygame.modules.find.FindMainFragment;
import com.jingyue.lygame.modules.personal.present.VersionCheckPresenter;
import com.jingyue.lygame.modules.personal.view.VersionCheckView;
import com.jingyue.lygame.modules.rcmd.GameItemFragment;
import com.jingyue.lygame.modules.rcmd.RecommendMainFragment;
import com.jingyue.lygame.utils.SettingUtil;
import com.jingyue.lygame.utils.download.TasksManager;
import com.jingyue.lygame.utils.ToolUtils;
import com.jingyue.lygame.utils.update.InstallDialog;
import com.jingyue.lygame.utils.update.NewVersionUtil;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.PlayerPagerEventCallback;
import com.jingyue.lygame.utils.youkuPlayer.eventcallback.YKFullScreenEventCallback;
import com.jingyue.lygame.utils.youkuPlayer.utils.YKListUtil;
import com.jingyue.lygame.utils.youkuPlayer.view.FullScreenContainer;
import com.laoyuegou.android.lib.utils.AnimationUtils;
import com.laoyuegou.android.lib.utils.EBus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements VersionCheckView {

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @BindView(R.id.lygame_content)
    FrameLayout mlygameContent;
    @BindView(R.id.tabcontent)
    FrameLayout mTabcontent;
    @BindView(R.id.tabhost)
    FragmentTabHost mTabHost;
    @BindView(R.id.activity_main)
    RelativeLayout mActivityMain;

    private String[] mTitles = null;
    //fragment集合
    private Class fragmentArray[] = {RecommendMainFragment.class, FindMainFragment.class, CommentMainFragment.class};
    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.slct_rcmd, R.drawable.slct_discover, R.drawable.slct_game_comment};
    //视频相关
    private YKListUtil mYklistUtil;
    private FullScreenContainer mFullScreenContainer;
    private PlayerFullScreenEventCallback mFScreenCallback;
    private PlayerPagerEventCallback eventCallback;
    private AudioManager mAudioManager;
    private VersionCheckPresenter mVersionCheckPresenter;
    private boolean needUpdate = false;
    public static final int MSG_PRELOAD = 0x1;
    public static final int MSG_CHECK_NEW_VERSION = 0x2;
    public static final int MSG_SET_NEED_UPDATE = 0x3;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PRELOAD:
                    SearchWordsListRp.getInstance().preLoadData();
                    break;
                case MSG_CHECK_NEW_VERSION:
                    if (mVersionCheckPresenter != null && mVersionCheckPresenter.needCheck()) {
                        mVersionCheckPresenter.startVersionCheck(false, false);
                    }
                    break;
                case MSG_SET_NEED_UPDATE:
                    //首页下载暂时
                    needUpdate = true;
                    break;
            }
        }
    };

    private Dialog mUpdateVersionDialog;

    @Override
    public void onCheckVersion(VersionInfoBean bean, boolean needDownload) {
        if (mUpdateVersionDialog == null) {
            mUpdateVersionDialog = new InstallDialog(this);
        }
        if (needDownload) {
            NewVersionUtil.checkNewVersionAndInstall(bean.url, bean.lastVersionCode, false);
        } else {
            mUpdateVersionDialog.show();
        }
    }

    @Override
    public void onVersionCheckFailure() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        enableEventBus();
        super.onCreate(savedInstanceState);
        mTitles = getResources().getStringArray(R.array.main_title);
        initView();

        add(mVersionCheckPresenter = new VersionCheckPresenter(this, this));
        mHandler.sendEmptyMessageDelayed(MSG_CHECK_NEW_VERSION, 500);
        mHandler.sendEmptyMessageDelayed(MSG_PRELOAD, 500);

        recordPageEvent(new ScreenAction().a3().recommond());
        TasksManager.getImpl().init();
    }

    @Override
    protected void onDestroy() {
        mYklistUtil.release();
        super.onDestroy();

    }

    public YKListUtil getYKListUtil() {
        return mYklistUtil;
    }

    public PlayerPagerEventCallback getEventCallback() {
        return eventCallback;
    }

    public PlayerFullScreenEventCallback getFScreenEventCallback() {
        return mFScreenCallback;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onBackPressed() {
        if (mYklistUtil.onBackPress()) {
            return;
        }
        exitApp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (loginStatusChange) {
            loginStatusChange = false;
            EBus.getDefault().post(new LoginStatusChangeEvent(LoginStatusChangeEvent.LOGIN_STATUS_CAHNGE));
        }
    }

    private boolean loginStatusChange = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoUpdate(LoginEvent event) {
        if (event.loginEvent == LoginEvent.LOGIN) {
            String last = SettingUtil.readString(this, KeyConstant.KEY_LAST_LOGINUSER, "");
            String newLogin = "";
            if (LoginManager.getInstance().getLoginInfo() != null && LoginManager.getInstance().getLoginInfo().id != null) {
                newLogin = LoginManager.getInstance().getLoginInfo().id;
            }
            if (last.isEmpty()) {
                if (LoginManager.getInstance().getLoginInfo() != null) {
                    SettingUtil.write(this, KeyConstant.KEY_LAST_LOGINUSER, newLogin);
                }
            } else {
                if (last.equals(newLogin)) {
                    return;
                }
            }
            loginStatusChange = true;
        } else if (event.loginEvent == LoginEvent.LOGOUT) {
            SettingUtil.write(this, KeyConstant.KEY_LAST_LOGINUSER, "");
            loginStatusChange = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loginStatusChange) {
            loginStatusChange = false;
            EBus.getDefault().post(new LoginStatusChangeEvent(LoginStatusChangeEvent.LOGIN_STATUS_CAHNGE));
        }
        if (needUpdate) {
            mHandler.sendEmptyMessage(MSG_CHECK_NEW_VERSION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mYklistUtil != null) {
            mYklistUtil.onBackPress();
            mYklistUtil.getYKVideoPlayer().showCache();
        }
    }

    protected void initView() {

        mAudioManager = (AudioManager) getContext().getSystemService(getContext().AUDIO_SERVICE);
        int currentVolum = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        SettingUtil.write(getContext(), KeyConstant.KEY_VIDEO_VOICE_VALUE, currentVolum);

        mYklistUtil = new YKListUtil(this);
        eventCallback = new PlayerPagerEventCallback(this, mYklistUtil);
        mFScreenCallback = new PlayerFullScreenEventCallback();

        //实例化TabHost对象，得到TabHost
        mTabHost.setup(this, getSupportFragmentManager(), R.id.lygame_content);

        //得到fragment的个数
        final int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTitles[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }
        addCommentsClick();
        //设置Tab按钮的背景
        mTabHost.getTabWidget().setDividerDrawable(android.R.color.transparent);
        mTabHost.getTabWidget().getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        mTabHost.getTabWidget().setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, getTheme()));

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals(getString(R.string.a_0178))) {
                    recordPageEvent(new ScreenAction().a3().find());
                } else if (tabId.equals(getString(R.string.a_0177))) {
                    recordPageEvent(new ScreenAction().a3().recommond());
                }
                final int index = mTabHost.getCurrentTab();
                setTitle(mTitles[index]);
            }
        });
    }

    //游戏谷tab单独处理，双击游戏谷跳转到游戏谷recycleview的第一个item
    private Fragment fg;

    private void addCommentsClick() {
        mTabHost.getTabWidget().getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTabHost.getCurrentTab() != 2) {
                    recordPageEvent(new ScreenAction().a3().comments());
                    mTabHost.setCurrentTab(2);
                } else {
                    fg = getSupportFragmentManager().findFragmentByTag(getString(R.string.a_0179));
                    if (fg instanceof CommentMainFragment) {
                        ((CommentMainFragment) fg).gotoTop();
                    }
                }
            }
        });
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void VideoContainerChange(VideoContainerChangeEvent event) {
        if (event.from == VideoContainerChangeEvent.TYPE_NEED_CREATE) {
            mYklistUtil = new YKListUtil(this);
            eventCallback = new PlayerPagerEventCallback(this, mYklistUtil);
        }
    }


    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(final int index) {
        View view = getLayoutInflater().inflate(R.layout.view_tab_item, null);

        final AppCompatImageView imageView = (AppCompatImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTitles[index]);
        view.setTag(index);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationUtils.upAndDownAni(imageView, true, false, 0.5f).start();
            }
        });
        return view;
    }

    /**
     * 全屏事件监听
     */
    public class PlayerFullScreenEventCallback implements YKFullScreenEventCallback {

        private GameItemFragment playerFragment;

        public void bindingPlayerUI(GameItemFragment playerFragment) {
            this.playerFragment = playerFragment;
        }

        @Override
        public ViewGroup onEnterFullScreen(boolean showcache, String url) {

            mFullScreenContainer = new FullScreenContainer(MainActivity.this, 1);
            mFullScreenContainer.showCache(showcache, url);

            //初始化全屏控件
            mFullScreenContainer.initUI(mYklistUtil.getYKVideoPlayer());

            mYklistUtil.setEventCallback(mFullScreenContainer.getFullScreenEventCallback());
            mYklistUtil.setPlayerActionEventCallback(mFullScreenContainer.getFullScreenEventCallback());

            //全屏状态下，水平滑动改变位置的总量为屏幕的 1/4
            mYklistUtil.setHorizontalSlopInfluenceValue(4);
            return mFullScreenContainer;
        }

        @Override
        public void onQuitFullScreen(boolean showcache, String url) {
            mFullScreenContainer.releaseFullScreenUI();

            //重新设置UI
            playerFragment.resetUI(showcache, url);

            //绑定List的eventCallback
            mYklistUtil.setEventCallback(eventCallback);
            mYklistUtil.setPlayerActionEventCallback(null);
        }
    }
}
