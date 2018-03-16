/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jingyue.lygame.utils.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.DownloadBean;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.clickaction.ButtonAction;
import com.jingyue.lygame.model.DownloadRp;
import com.jingyue.lygame.utils.IGameLayout;
import com.jingyue.lygame.utils.ToolUtils;
import com.jingyue.lygame.widget.CommonDialog;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadList;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by liu hong liang on 2016/9/29.
 */

public class GameViewUtil {

    private static CommonDialog downHintDialog;

    private static void confirmDown(final IGameLayout iGameLayout, final FileDownloadListener downloadListener) {
        if (!NetworkUtils.isWifi(Utils.getContext())) {//弹出提示是否下载
            if (iGameLayout instanceof View) {
                Context context = ((View) iGameLayout).getContext();
                downHintDialog = new CommonDialog.Builder(context)
                        .setContent(context.getString(R.string.a_0303))
                        .setRightButtonInterface(context.getString(R.string.a_0076),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        start(iGameLayout, downloadListener);
                                    }
                                }).setLeftButtonInterface(context.getString(R.string.a_0075),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        downHintDialog.dismiss();
                                        downHintDialog = null;
                                    }
                                }).show();
                downHintDialog.setCancelable(false);
            }
        } else {
            start(iGameLayout, downloadListener);
        }
    }

    /**
     * 下载按钮点击
     */
    public static synchronized void clickDownload(DownloadBean tasksManagerModel, IGameLayout iGameLayout, FileDownloadListener downloadListener) {
        //判断当前状态：
        if (tasksManagerModel == null) {//没有下载过，去下载
            if (!NetworkUtils.hasNetwork(Utils.getContext())) {
                ToastUtils.showShort(R.string.a_0289);
            } else {
                confirmDown(iGameLayout, downloadListener);
            }
            return;
        }
        if (tasksManagerModel.getStatus() == FileDownloadStatus.completed) {//标记安装过了
            if (ToolUtils.isPkgInstalled(Utils.getContext(), tasksManagerModel.getPackageName())) {//手机中还存在，可以直接启动
                installOrOpen(tasksManagerModel);
            } else {//被卸载了
                File file = new File(tasksManagerModel.getPath());
                if (file.exists()) {//安装包还存在，可以安装
                    installOrOpen(tasksManagerModel);
                } else {//安装包被删除了，删除数据库记录，重新下载
                    confirmDown(iGameLayout, downloadListener);
                }
            }
            return;
        }

        byte status = FileDownloader.getImpl().getStatus(tasksManagerModel.getId(), tasksManagerModel.getPath());
        switch (status) {
            case FileDownloadStatus.pending://等待
//                start(iGameLayout,downloadListener);
                confirmDown(iGameLayout, downloadListener);
                break;
            case FileDownloadStatus.started://下载中
                pause(tasksManagerModel.getId());
                break;
            case FileDownloadStatus.connected://下载中
                pause(tasksManagerModel.getId());
                break;
            case FileDownloadStatus.progress://下载中
                pause(tasksManagerModel.getId());
                break;
            case FileDownloadStatus.paused://暂停
                confirmDown(iGameLayout, downloadListener);
//                start(iGameLayout,downloadListener);
                break;
            case FileDownloadStatus.completed://安装
                installOrOpen(tasksManagerModel);
                break;
            case FileDownloadStatus.blockComplete://安装
                installOrOpen(tasksManagerModel);
                break;
            case FileDownloadStatus.retry://重试
//                start(iGameLayout,downloadListener);
                confirmDown(iGameLayout, downloadListener);
                break;
            case FileDownloadStatus.error://重试
                confirmDown(iGameLayout, downloadListener);
//                start(iGameLayout,downloadListener);
                break;
            case FileDownloadStatus.warn://已经在下载或者在队列中了，丢失下载进度和状态记录，进行重新连接
//                start(iGameLayout,downloadListener);
                confirmDown(iGameLayout, downloadListener);
                break;
            case FileDownloadStatus.INVALID_STATUS:
                confirmDown(iGameLayout, downloadListener);
//                start(iGameLayout,downloadListener);
                break;
        }
    }

    public static void uninstallApk(Context context, DownloadBean tasksManagerModel) {
        String packageNameByApkFile = tasksManagerModel.getPackageName();
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageNameByApkFile));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public static void uninstallApk(Context context, String pkgname) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + pkgname));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    private static void installOrOpen(DownloadBean tasksManagerModel) {
        //判断是启动还是安装
        if (ToolUtils.isPkgInstalled(Utils.getContext(), tasksManagerModel.getPackageName())
                && tasksManagerModel.getStatus() == FileDownloadStatus.completed) {
            ToolUtils.openAppByPackageName(Utils.getContext(), tasksManagerModel.getPackageName());
        } else {
            //进行安装
            ToolUtils.installApk(Utils.getContext(), new File(tasksManagerModel.getPath()));
        }
    }

    private static void pause(int id) {
        FileDownloader.getImpl().pause(id);
    }

    public static synchronized void start(IGameLayout iGameLayout, FileDownloadListener downloadListener) {
        try {
            DownloadBean taskModelByGameId = TasksManager.getImpl().getTaskModelByGameId(String.valueOf(iGameLayout.getGameBean().id));
            if (taskModelByGameId != null) {//已经下载过了
                if (taskModelByGameId.getUrl() == null) {//url不空
                    getUrlAndDownload(iGameLayout.getGameBean(), downloadListener);
                } else {
                    createDownTask(iGameLayout.getGameBean(), downloadListener).start();
                }
            } else {//没有下载过，或者已经被删除了
                getUrlAndDownload(iGameLayout.getGameBean(), downloadListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建一个下载任务
     *
     * @param gameBean
     * @return
     */
    private static BaseDownloadTask createDownTask(GameBean gameBean, FileDownloadListener downloadListener) {

        int downloadId = TasksManager.getImpl().getDownloadId(gameBean.androidurl);
        BaseDownloadTask.IRunningTask task = FileDownloadList.getImpl().get(downloadId);
        BaseDownloadTask baseDownloadTask = null;
        if (task != null && task instanceof BaseDownloadTask) {//如果已经在队列中，取出来
            baseDownloadTask = (BaseDownloadTask) task;
        } else {
            baseDownloadTask = FileDownloader.getImpl().create(gameBean.androidurl);
            baseDownloadTask.setCallbackProgressTimes(200);
            baseDownloadTask.setMinIntervalUpdateSpeed(400);
            baseDownloadTask.setTag(GlobalMonitor.TAG_GAME_ID, String.valueOf(gameBean.id));
            //设置下载文件放置路径
            baseDownloadTask.setPath(FileDownloadUtils.getDefaultSaveFilePath(gameBean.androidurl));
//            if (TextUtils.isEmpty(gameBean.downCnt)) {
//                gameBean.downCnt = "0";
//            }
        }
        if (downloadListener != null) {
            baseDownloadTask.setListener(downloadListener);
        }
        return baseDownloadTask;
    }

    private static void getUrlAndDownload(GameBean gameBean, FileDownloadListener downloadListener) {
        final WeakReference<FileDownloadListener> listenerWk = new WeakReference<FileDownloadListener>(downloadListener);
        final WeakReference<GameBean> gameBeanWk = new WeakReference<GameBean>(gameBean);
        //埋点
        if (gameBeanWk.get() != null) {
            new ButtonAction(String.valueOf(gameBeanWk.get().id)).a3().download().onRecord();
        }

        //统计下载次数
        DownloadRp.getInstance().doAction(gameBeanWk.get().id, gameBean.downCnt);

        DownloadBean tasksManagerModel = TasksManager.getImpl().addTask(String.valueOf(gameBeanWk.get().id), gameBeanWk.get().name,
                gameBeanWk.get().icon, gameBeanWk.get().androidurl, gameBeanWk.get().packagename, gameBeanWk.get().size);
        if (tasksManagerModel != null) {
            createDownTask(gameBeanWk.get(), listenerWk.get()).start();
        }
    }
}
