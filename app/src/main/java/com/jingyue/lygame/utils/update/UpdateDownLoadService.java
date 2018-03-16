package com.jingyue.lygame.utils.update;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.jingyue.lygame.BaseService;
import com.jingyue.lygame.R;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.events.EventDownloadEnd;
import com.jingyue.lygame.utils.SettingUtil;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.download.GlobalMonitor;
import com.jingyue.lygame.utils.download.TasksManager;
import com.laoyuegou.android.lib.utils.AppUtils;
import com.laoyuegou.android.lib.utils.EBus;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadList;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;

import static com.jingyue.lygame.utils.update.NewVersionUtil.NEW_VERSION_INSTALLED;

/**
 * 升级下载
 */
public class UpdateDownLoadService extends BaseService {

    private UpdatePresenter mCommonPresenter;

    private String newVersion;

    @Override
    public IBinder onBind(Intent intent) {
        return new UpdateDownloadBinder();
    }

    public class UpdateDownloadBinder extends Binder {
        UpdateDownLoadService getService() {
            return UpdateDownLoadService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            return Service.START_NOT_STICKY;
        }
        String url = intent.getStringExtra("url");
        newVersion = intent.getStringExtra("newVersion");
        startUpgradeApkDownload(url, newVersion);
        return Service.START_NOT_STICKY;
    }

    private void startUpgradeApkDownload(String url, String newVersion) {
        createNotificaation();
        createDownTask(url, newVersion).start();
    }

    private NotificationCompat.Builder builder;
    private NotificationManager manager;

    private void createNotificaation() {
        builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.lyg_logo);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(AppConstants.APP_UPDATE_NOTIFICATIONID, builder.build());
        builder.setProgress(100, 0, false);
    }

    private BaseDownloadTask createDownTask(String url, String newVersion) {
        String appid = AppUtils.getMetaData(Utils.getContext(), "HS_APPID");
        int downloadId = TasksManager.getImpl().getDownloadId(url);
        BaseDownloadTask.IRunningTask task = FileDownloadList.getImpl().get(downloadId);
        BaseDownloadTask baseDownloadTask = null;
        if (task != null && task instanceof BaseDownloadTask) {//如果已经在队列中，取出来
            baseDownloadTask = (BaseDownloadTask) task;
        } else {
            baseDownloadTask = FileDownloader.getImpl().create(url);
            baseDownloadTask.setCallbackProgressTimes(200);
            baseDownloadTask.setMinIntervalUpdateSpeed(400);
            baseDownloadTask.setTag(GlobalMonitor.TAG_GAME_ID, appid);
            //设置下载文件放置路径
            baseDownloadTask.setPath(NewVersionUtil.getInstallNewVersionFile(Utils.getContext(), newVersion).getPath());
        }
        baseDownloadTask.setListener(downloadListener);
        return baseDownloadTask;
    }

    FileDownloadListener downloadListener = new FileDownloadSampleListener() {
        @Override
        protected void progress(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
            if (soFarBytes >= totalBytes) {
                SettingUtil.write(Utils.getContext(), NEW_VERSION_INSTALLED, newVersion);
                EBus.getDefault().post(new EventDownloadEnd());
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    builder.setProgress(100, StringUtils.getProgress(totalBytes, soFarBytes), false);
                    manager.notify(AppConstants.APP_UPDATE_NOTIFICATIONID, builder.build());
                    //下载进度提示
                    builder.setContentTitle(getContext().getString(R.string.a_0304, StringUtils.getProgress(totalBytes, soFarBytes)) + "%");
                    builder.setContentText(getContext().getString(R.string.a_0305, StringUtils.formateSpeed(task.getSpeed()),
                            StringUtils.getRemainTime(totalBytes, soFarBytes, task.getSpeed())));
                }
            }).start();
            LogUtils.e("下载进度 = " + StringUtils.getProgress(totalBytes, soFarBytes));
        }
    };

}
