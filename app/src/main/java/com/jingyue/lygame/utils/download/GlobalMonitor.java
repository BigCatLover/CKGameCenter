/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jingyue.lygame.utils.download;

import android.app.NotificationManager;

import com.jingyue.lygame.bean.DownloadBean;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.events.DownStatusChangeEvent;
import com.jingyue.lygame.utils.ToolUtils;
import com.laoyuegou.android.lib.utils.AppUtils;
import com.laoyuegou.android.lib.utils.EBus;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadMonitor;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import java.io.File;

/**
 * Created by Jacksgong on 1/19/16.
 */
public class GlobalMonitor implements FileDownloadMonitor.IMonitor {
    public final static int TAG_GAME_ID = 0;
    public final static int TAG_GAME_DOWNLOAD_COUNT = 1;
    private volatile int markStart;
    private volatile int markOver;

    private final static class HolderClass {
        private final static GlobalMonitor INSTANCE = new GlobalMonitor();
    }

    public static GlobalMonitor getImpl() {
        return HolderClass.INSTANCE;
    }

    private final static String TAG = "GlobalMonitor";

    @Override
    public void onRequestStart(int count, boolean serial, FileDownloadListener lis) {
        markStart = 0;
        markOver = 0;
        LogUtils.d(String.format("on request start %d %B", count, serial));
    }

    @Override
    public void onRequestStart(BaseDownloadTask task) {
    }

    @Override
    public void onTaskBegin(BaseDownloadTask task) {
        markStart++;
        Object tagId = task.getTag(TAG_GAME_ID);
        Object tagCount = task.getTag(TAG_GAME_DOWNLOAD_COUNT);
        if (tagId != null && tagId instanceof String && tagCount != null && tagCount instanceof String) {
            EBus.getDefault().post(new DownStatusChangeEvent(task.getId(), (String) tagId));
        }
    }

    @Override
    public void onTaskStarted(BaseDownloadTask task) {
        Object tagId = task.getTag(TAG_GAME_ID);
        Object tagCount = task.getTag(TAG_GAME_DOWNLOAD_COUNT);
        if (tagId != null && tagId instanceof String && tagCount != null && tagCount instanceof String) {
            EBus.getDefault().post(new DownStatusChangeEvent(task.getId(), (String) tagId));
        }
    }

    @Override
    public void onTaskOver(BaseDownloadTask task) {
        markOver++;
        String tagId = (String) task.getTag(TAG_GAME_ID);
        if (tagId != null) {
            byte status = task.getStatus();
            if (status == FileDownloadStatus.completed) {
                if (tagId.endsWith(AppUtils.getMetaData(Utils.getContext(), "HS_APPID"))) {
                    NotificationManager manager = (NotificationManager) Utils.getContext().
                            getSystemService(Utils.getContext().NOTIFICATION_SERVICE);
                    manager.cancel(AppConstants.APP_UPDATE_NOTIFICATIONID);
                    ToolUtils.installApk(Utils.getContext(), new File(task.getPath()));
                } else {
                    DownloadBean taskModelById = TasksManager.getImpl().getTaskModelById(task.getId());
                    if (taskModelById != null) {
                        taskModelById.setStatus(status);
                        TasksManager.getImpl().updateTask(taskModelById);
                    }
                    ToolUtils.installApk(Utils.getContext(), new File(taskModelById.getPath()));
                }
            }
            EBus.getDefault().post(new DownStatusChangeEvent(task.getId(), tagId));
        }
    }

    public int getMarkStart() {
        return markStart;
    }

    public int getMarkOver() {
        return markOver;
    }
}
