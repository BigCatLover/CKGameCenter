package com.jingyue.lygame.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.DownloadBean;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.SubjectDetailBean;
import com.jingyue.lygame.clickaction.ButtonAction;
import com.jingyue.lygame.events.DownStatusChangeEvent;
import com.jingyue.lygame.events.GameSubEvent;
import com.jingyue.lygame.modules.comment.present.GameCollectPresent;
import com.jingyue.lygame.modules.comment.present.GameSubPresent;
import com.jingyue.lygame.modules.comment.view.UserActionView;
import com.jingyue.lygame.utils.IGameLayout;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.ToolUtils;
import com.jingyue.lygame.utils.download.GameViewUtil;
import com.jingyue.lygame.utils.download.TasksManager;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.utils.EBus;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglei on 2017/12/5.
 */
public class DownloadProgessView extends FrameLayout implements UserActionView, IGameLayout {
    @BindView(R.id.pb_download)
    RoundProgressBar pbDownload;
    @BindView(R.id.iv_center)
    ImageView ivCenter;
    @BindView(R.id.download_click)
    RelativeLayout downloadClick;
    @BindView(R.id.gray_btn)
    TextView grayBtn;
    @BindView(R.id.green_btn)
    TextView greenBtn;

    private GameBean gameBean;
    private Context context;
    private DownloadBean tasksManagerModel;
    private boolean isLikeGameList = false;
    private FileDownloadListener mDownloadListener;
    private String gameId;
    private BaseImpl mBase;
    private GameSubPresent subPresent;
    private GameCollectPresent collectPresent;

    public DownloadProgessView(Context context) {
        this(context, null);
    }

    public DownloadProgessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initUI();
    }

    private void initUI() {
        LayoutInflater.from(getContext()).inflate(R.layout.include_download_progress_layout, this, true);
        ButterKnife.bind(this);
    }

    @Override
    public void setGameBean(GameBean gameBean) {
        this.gameBean = gameBean;
        updateUI(null);
    }

    @Override
    public GameBean getGameBean() {
        if (gameBean == null) {
            return new GameBean();
        }
        return gameBean;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDwonFileStatusChange(DownStatusChangeEvent change) {
        if (gameBean == null) return;
        if (String.valueOf(gameBean.id).equals(change.gameId)) {
            updateUI(change);
        }
    }

    public void setGameBeans(SubjectDetailBean.ListBean bean) {
        GameBean bean1 = new GameBean();
        bean1.androidurl = bean.androidurl;
        bean1.name = bean.name;
        bean1.packagename = bean.packagename;
        bean1.subStatus = bean.subStatus;
        bean1.id = Integer.valueOf(bean.id);
        setGameBeans(bean1, false);
    }


    /**
     * @param gameBean
     * @param isLikeGameList 用于区分是否是游戏收藏列表，如果是游戏收藏列表，右侧按钮只可以取消收藏，不可以进行下载
     */
    public void setGameBeans(GameBean gameBean, boolean isLikeGameList) {
        this.isLikeGameList = isLikeGameList;
        setGameBean(gameBean);
        restoreDownloadProgressListener();
    }

    /**
     * @param gameId 该方法仅在下载中心中使用
     */
    public void setGameId(String gameId, FileDownloadListener downloadListener) {
        this.isLikeGameList = false;
        this.gameId = gameId;
        this.mDownloadListener = downloadListener;
        tasksManagerModel = TasksManager.getImpl().getTaskModelByGameId(gameId);
        if (tasksManagerModel != null) {
            GameBean bean = new GameBean();
            bean.packagename = tasksManagerModel.getPackageName();
            bean.androidurl = tasksManagerModel.getUrl();
            bean.id = Integer.valueOf(tasksManagerModel.getGameId());
            this.gameBean = bean;
            setGameBean(gameBean);
        }

        restoreDownloadProgressListener();
    }


    private void restoreDownloadProgressListener() {
        if (tasksManagerModel != null) {//没有下载过，不用恢复
            byte status = FileDownloader.getImpl().getStatus(tasksManagerModel.getUrl(), tasksManagerModel.getPath());
            if (pbDownload != null) {
                pbDownload.setProgress(TasksManager.getImpl().getProgress(tasksManagerModel.getId()));
            }
            switch (status) {
                case FileDownloadStatus.pending://等待
                case FileDownloadStatus.started://下载中
                case FileDownloadStatus.connected://下载中
                case FileDownloadStatus.progress://下载中
                case FileDownloadStatus.warn://已经在下载或者在队列中了，丢失下载进度和状态记录，进行重新连接
                    FileDownloader.getImpl().replaceListener(tasksManagerModel.getId(),
                            mDownloadListener == null ? downloadListener : mDownloadListener);
                    break;
            }
        }
    }

    private void updateUI(DownStatusChangeEvent change) {
        if (gameBean == null && gameId == null) {
            return;
        }
        if (isLikeGameList) {
            greenBtn.setVisibility(INVISIBLE);
            grayBtn.setVisibility(VISIBLE);
            grayBtn.setText(R.string.a_0204);
            return;
        }
        greenBtn.setVisibility(INVISIBLE);
        grayBtn.setVisibility(INVISIBLE);
        downloadClick.setVisibility(GONE);


        if (gameId != null) {
            updateWithDownload(gameId);
            return;
        }
        if (gameBean != null) {
            //针对非联运游戏（即 没有接入我们SDK的游戏）本地有安装的haul直接打开
            if (!StringUtils.isEmptyOrNull(gameBean.packagename) && ToolUtils.isPkgInstalled(context, gameBean.packagename)) {
                greenBtn.setVisibility(VISIBLE);
                greenBtn.setText(R.string.a_0193);
                return;
            }
            if (gameBean.subStatus == 1) {
                updateWithDownload(String.valueOf(gameBean.id));
            } else if (gameBean.subStatus == 2) {
                greenBtn.setText(R.string.a_0194);
            } else if (gameBean.subStatus == 3) {
                greenBtn.setVisibility(INVISIBLE);
                grayBtn.setVisibility(VISIBLE);
                grayBtn.setText(R.string.a_0195);
            }
        }
    }

    private void updateWithDownload(String gameId) {
        tasksManagerModel = TasksManager.getImpl().getTaskModelByGameId(gameId);
        //设置下载按钮
        if (tasksManagerModel == null) {//没有下载过
            greenBtn.setVisibility(VISIBLE);
            grayBtn.setVisibility(INVISIBLE);
            downloadClick.setVisibility(GONE);
            greenBtn.setText(R.string.a_0023);
            return;
        }

        if (tasksManagerModel.getStatus() == FileDownloadStatus.completed) {//标记安装过了
            greenBtn.setVisibility(VISIBLE);
            grayBtn.setVisibility(INVISIBLE);
            downloadClick.setVisibility(GONE);
            if (ToolUtils.isPkgInstalled(getContext(), tasksManagerModel.getPackageName())) {//手机中还存在，可以直接启动
                greenBtn.setText(R.string.a_0193);
            } else {
                File file = new File(tasksManagerModel.getPath());
                if (!file.exists()) {
                    greenBtn.setText(R.string.a_0023);
                } else {
                    greenBtn.setText(R.string.a_0197);
                }
            }
            return;
        }
        byte status = FileDownloader.getImpl().getStatus(tasksManagerModel.getUrl(), tasksManagerModel.getPath());
        switch (status) {
            case FileDownloadStatus.pending://等待
                greenBtn.setText(R.string.a_0198);
                break;
            case FileDownloadStatus.INVALID_STATUS://初始
                greenBtn.setText(R.string.a_0023);
                break;
            case FileDownloadStatus.started://下载中
            case FileDownloadStatus.connected://下载中
            case FileDownloadStatus.progress://下载中
                downloadClick.setVisibility(VISIBLE);
                greenBtn.setVisibility(INVISIBLE);
                grayBtn.setVisibility(INVISIBLE);
                pbDownload.setProgress(TasksManager.getImpl().getProgress(tasksManagerModel.getId()));
                ivCenter.setImageResource(R.mipmap.ic_download_ing);
                break;
            case FileDownloadStatus.paused://暂停
                ivCenter.setImageResource(R.mipmap.ic_down_stop);
                downloadClick.setVisibility(VISIBLE);
                greenBtn.setVisibility(INVISIBLE);
                grayBtn.setVisibility(INVISIBLE);
                break;
            case FileDownloadStatus.completed://安装
            case FileDownloadStatus.blockComplete://安装
                //判断是启动还是安装
                greenBtn.setVisibility(VISIBLE);
                //判断是启动还是安装
                if (ToolUtils.isPkgInstalled(Utils.getContext(), tasksManagerModel.getPackageName()) && tasksManagerModel.getStatus()
                        == FileDownloadStatus.completed) {
                    greenBtn.setText(R.string.a_0193);
                } else {
                    greenBtn.setText(R.string.a_0197);
                }
                break;
            case FileDownloadStatus.retry://重试
            case FileDownloadStatus.error://重试
                greenBtn.setVisibility(VISIBLE);
                greenBtn.setText(R.string.a_0196);
                break;
            case FileDownloadStatus.warn://已经在下载或者在队列中了，丢失下载进度和状态记录，进行重新连接
                greenBtn.setVisibility(INVISIBLE);
                downloadClick.setVisibility(VISIBLE);
                break;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EBus.getDefault().register(this);
        //重新显示到窗口，更新ui
        updateUI(null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (EBus.getDefault().isRegistered(this)) {
            EBus.getDefault().unregister(this);
        }
        if (tasksManagerModel != null) {
            //移除监听器
            FileDownloader.getImpl().replaceListener(tasksManagerModel.getId(), null);
        }
    }

    private boolean hasWindowFocusChange = false;


    //用户进行安装、卸载或者删除安装包等操作时及时更新布局
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            hasWindowFocusChange = true;
        } else {
            if (hasWindowFocusChange) {
                hasWindowFocusChange = false;
                if (gameBean == null || tasksManagerModel == null) return;
                if (tasksManagerModel.getStatus() == FileDownloadStatus.completed) {
                    File file = new File(tasksManagerModel.getPath());
                    if (!file.exists()) {
                        TasksManager.getImpl().deleteTaskByModel(tasksManagerModel);
                    }
                    EBus.getDefault().post(new DownStatusChangeEvent(tasksManagerModel.getId(), tasksManagerModel.getGameId()));
                    return;
                } else {
                    File file = new File(tasksManagerModel.getPath() + ".temp");
                    if (!file.exists()) {//安装包还存在，可以安装
                        TasksManager.getImpl().deleteTaskByModel(tasksManagerModel);
                        //通知刷新
                        EBus.getDefault().post(new DownStatusChangeEvent(tasksManagerModel.getId(), tasksManagerModel.getGameId()));
                    }
                }
            }
        }
    }

    @OnClick(R.id.download_ll)
    public void onViewClicked() {
        //取消预约游戏
        if (gameBean == null && gameId == null) {
            return;
        }
        if (gameId != null) {
            tasksManagerModel = TasksManager.getImpl().getTaskModelByGameId(gameId);
            if (StringUtils.isEmptyOrNull(tasksManagerModel.getUrl())) {
                return;
            }
            GameViewUtil.clickDownload(tasksManagerModel, this, mDownloadListener == null ? downloadListener : mDownloadListener);
            return;
        }
        if (gameBean != null) {
            if (isLikeGameList) {
                if (collectPresent == null) {
                    collectPresent = new GameCollectPresent(this, mBase);
                }
                collectPresent.doAction(String.valueOf(gameBean.id), true);
                return;
            }
            if (gameBean.subStatus == 2) {
                if (gameBean != null) {
                    new ButtonAction(String.valueOf(gameBean.id)).a3().sub().onRecord();
                }
                if (subPresent == null) {
                    subPresent = new GameSubPresent(this, mBase);
                }
                subPresent.doAction(String.valueOf(gameBean.id), false);
            } else if (gameBean.subStatus == 3) {//已预约
                if (subPresent == null) {
                    subPresent = new GameSubPresent(this, mBase);
                }
                subPresent.doAction(String.valueOf(gameBean.id), true);
            } else {
                if (StringUtils.isEmptyOrNull(gameBean.androidurl)) {
                    return;
                }
                tasksManagerModel = TasksManager.getImpl().getTaskModelByGameId(String.valueOf(gameBean.id));
                GameViewUtil.clickDownload(tasksManagerModel, this, mDownloadListener == null ? downloadListener : mDownloadListener);
            }
        }
    }

    public void updateProgress(int soFarBytes, int totalBytes, boolean isPause) {
        if (!isPause) {
            downloadClick.setVisibility(VISIBLE);
            greenBtn.setVisibility(INVISIBLE);
            grayBtn.setVisibility(INVISIBLE);
            ivCenter.setImageResource(R.mipmap.ic_download_ing);
            if (soFarBytes >= totalBytes) {
                downloadClick.setVisibility(GONE);
                greenBtn.setVisibility(VISIBLE);
                greenBtn.setText(R.string.a_0197);
            } else {
                if (pbDownload != null) {
                    pbDownload.setProgress(StringUtils.getProgress(totalBytes, soFarBytes));
                }
            }
        } else {
            ivCenter.setImageResource(R.mipmap.ic_down_stop);
            if (pbDownload != null) {
                pbDownload.setProgress(StringUtils.getProgress(totalBytes, soFarBytes));
            }
        }

    }

    /**
     * 下载进度监听器
     */
    FileDownloadListener downloadListener = new FileDownloadSampleListener() {
        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            downloadClick.setVisibility(VISIBLE);
            greenBtn.setVisibility(INVISIBLE);
            grayBtn.setVisibility(INVISIBLE);
            ivCenter.setImageResource(R.mipmap.ic_download_ing);
            if (soFarBytes >= totalBytes) {
                downloadClick.setVisibility(GONE);
                greenBtn.setVisibility(VISIBLE);
                greenBtn.setText(R.string.a_0197);
            } else {
                if (pbDownload != null) {
                    pbDownload.setProgress(StringUtils.getProgress(totalBytes, soFarBytes));
                }
            }
            LogUtils.e("下载中：" + task.getId() + "  进度：" + soFarBytes + "-->速度 " + task.getSpeed());
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            ivCenter.setImageResource(R.mipmap.ic_down_stop);
            if (pbDownload != null) {
                pbDownload.setProgress(StringUtils.getProgress(totalBytes, soFarBytes));
            }
        }
    };

    @Override
    public void showProgress(String msg) {

    }

    @Override
    public void dismissProgress() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameSubChange(GameSubEvent event) {
        if (event.currentStatus == GameSubEvent.TYPE_SUB && event.appid.equals(String.valueOf(gameBean.id))) {
            gameBean.subStatus = 3;
            downloadClick.setVisibility(INVISIBLE);
            greenBtn.setVisibility(INVISIBLE);
            grayBtn.setVisibility(VISIBLE);
            grayBtn.setText(R.string.a_0195);
        } else if (event.currentStatus == GameSubEvent.TYPE_UNSUB && event.appid.equals(String.valueOf(gameBean.id))) {
            gameBean.subStatus = 2;
            downloadClick.setVisibility(INVISIBLE);
            greenBtn.setText(R.string.a_0194);
        }
    }

    @Override
    public void success(int action, String id) {
        if (action == UserActionView.ACTION_UNSUB) {
            grayBtn.setVisibility(INVISIBLE);
            greenBtn.setVisibility(VISIBLE);
            greenBtn.setText(R.string.a_0194);
            ToastUtils.showShort(R.string.a_0203);
        } else if (action == UserActionView.ACTION_SUB) {
            greenBtn.setVisibility(INVISIBLE);
            grayBtn.setVisibility(VISIBLE);
            grayBtn.setText(R.string.a_0195);
            ToastUtils.showShort(R.string.a_0200);
        } else if (action == UserActionView.ACTION_UNLIKE) {
            ToastUtils.showShort(R.string.a_0201);
        }
    }
}
