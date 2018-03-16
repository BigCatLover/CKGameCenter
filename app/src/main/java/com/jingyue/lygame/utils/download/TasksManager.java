package com.jingyue.lygame.utils.download;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.jingyue.lygame.bean.DownloadBean;
import com.jingyue.lygame.bean.DownloadBean_Table;
import com.jingyue.lygame.utils.ToolUtils;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloadMonitor;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.util.List;

/**
 * Created by zhanglei on 2017/12/5.
 */
public class TasksManager {
    private static final String TAG = TasksManager.class.getSimpleName();

    private final static class HolderClass {
        private final static TasksManager INSTANCE = new TasksManager();
    }

    private FileDownloadConnectListener listener;

    public static TasksManager getImpl() {
        return HolderClass.INSTANCE;
    }

    private TasksManager() {
    }

    /**
     * 初始化下载服务，不可以在application中调用，可在启动的第一个activity调用
     */
    public void init() {
        if (!FileDownloader.getImpl().isServiceConnected()) {
            FileDownloader.getImpl().bindService();
            if (listener != null) {
                FileDownloader.getImpl().removeServiceConnectListener(listener);
            }
            listener = new FileDownloadConnectListener() {
                @Override
                public void connected() {
                    Log.d(TAG, "下载服务已经连接成功！");
                }

                @Override
                public void disconnected() {
                    Log.d(TAG, "下载服务解除连接！");
                }
            };
            FileDownloader.getImpl().addServiceConnectListener(listener);
        }
        String path = FileDownloadHelper.getAppContext().getExternalFilesDir("Download").getAbsolutePath();

        FileDownloadUtils.setDefaultSaveRootPath(path);
        if (FileDownloadMonitor.getMonitor() == null) {
            FileDownloadMonitor.setGlobalMonitor(GlobalMonitor.getImpl());
        }
    }

    /**
     * 根据文件下载地址返回一个下载id
     *
     * @param url
     * @return
     */
    public int getDownloadId(String url) {
        return FileDownloadUtils.generateId(url, FileDownloadUtils.getDefaultSaveFilePath(url));
    }

    /**
     * 解除服务监听
     */
    private void unregisterServiceConnectionListener() {
        FileDownloader.getImpl().removeServiceConnectListener(listener);
        listener = null;
    }

    /**
     * 下载服务是否已经连接上，只有连接上了，才能获取下载过的文件状态和下载信息
     *
     * @return
     */
    public boolean isReady() {
        return FileDownloader.getImpl().isServiceConnected();
    }

    /**
     * 判断是否下载完成
     *
     * @param status
     * @return
     */
    public boolean isDownloaded(int status) {
        return status == FileDownloadStatus.completed;
    }

    /**
     * 是否是下载成功，并且安装了
     *
     * @param context
     * @param gameId
     * @return
     */
    public boolean isDownOkAndInstalled(Context context, String gameId) {
        DownloadBean taskModelByGame = SQLite.select()
                .from(DownloadBean.class)
                .where(DownloadBean_Table.gameId.eq(gameId)).querySingle();
        int id = taskModelByGame.getId();
        byte status = FileDownloader.getImpl().getStatus(id, taskModelByGame.getPath());
        if (status == FileDownloadStatus.completed) {
            if (ToolUtils.isPkgInstalled(context, taskModelByGame.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isClickedDown(String gameid) {
        DownloadBean taskModelByGame = SQLite.select()
                .from(DownloadBean.class)
                .where(DownloadBean_Table.gameId.eq(gameid)).querySingle();
        if (taskModelByGame != null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDownOk(Context context, String gameId) {
        DownloadBean taskModelByGame = SQLite.select()
                .from(DownloadBean.class)
                .where(DownloadBean_Table.gameId.eq(gameId)).querySingle();
        if (taskModelByGame != null) {
            int id = taskModelByGame.getId();
            byte status = FileDownloader.getImpl().getStatus(id, taskModelByGame.getPath());
            if (status == FileDownloadStatus.completed) {
                return true;
            }
        }

        return false;
    }

    public DownloadBean getTaskModelById(int id) {
        return SQLite.select()
                .from(DownloadBean.class)
                .where(DownloadBean_Table.id.eq(id)).querySingle();
    }

    public DownloadBean addTask(String gameId, String gameName, String gameIcon, String url, String pkgname,String gameSize) {
        DownloadBean model = new DownloadBean();
        String path = FileDownloadUtils.getDefaultSaveFilePath(url);
        final int id = FileDownloadUtils.generateId(url, path);
        model.setId(id);
        model.setUrl(url);
        model.setPath(path);
        model.setGameId(gameId);
        model.setGameName(gameName);
        model.setGameIcon(gameIcon);
        model.setPackageName(pkgname);
        model.setGameSize(gameSize);
        model.setStatus(0);
        model.save();
        return model;
    }

    public DownloadBean getTaskModelByGameId(String gameId) {
        return SQLite.select().from(DownloadBean.class)
                .where(DownloadBean_Table.gameId.eq(gameId)).querySingle();
    }


    /**
     * @return 返回所有未下载完成的游戏
     */
    public List<DownloadBean> getAllTasks() {
        return SQLite.select().from(DownloadBean.class).where(DownloadBean_Table.status.eq(0)).queryList();
    }

    public boolean updateTask(DownloadBean downloadBean) {
        downloadBean.save();
        return true;
    }

    public boolean deleteTaskByModel(DownloadBean downloadBean) {
        FileDownloader.getImpl().clear(downloadBean.getId(), downloadBean.getPath());
        downloadBean.delete();
        return true;
    }

    public boolean deleteDbTaskaById(int id) {
        DownloadBean bean = SQLite.select()
                .from(DownloadBean.class)
                .where(DownloadBean_Table.id.eq(id)).querySingle();
        if (bean != null) {
            bean.delete();
        }
        return true;
    }

    public boolean deleteDbTaskByGameId(String gameId) {
        DownloadBean bean = SQLite.select()
                .from(DownloadBean.class)
                .where(DownloadBean_Table.gameId.eq(gameId)).querySingle();
        if (bean != null) {
            return bean.delete();
        }
        return true;
    }

    public int getStatus(final int id, String path) {
        return FileDownloader.getImpl().getStatus(id, path);
    }

    public long getTotal(final int id) {
        long total = FileDownloader.getImpl().getTotal(id);
        if (total == 0) {
            DownloadBean taskModelById = TasksManager.getImpl().getTaskModelById(id);
            if (taskModelById != null && taskModelById.getGameSize() != null) {
                try {
                    return Long.parseLong(taskModelById.getGameSize());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (taskModelById != null) {
                File file = new File(taskModelById.getPath());
                if (file.exists() && file.isFile()) {
                    return file.length();
                }
            }
        }
        return total;
    }

    public int getProgress(final int id) {
        long total = FileDownloader.getImpl().getTotal(id);
        long soFar = FileDownloader.getImpl().getSoFar(id);
        if (total <= 0) return 0;
        return (int) (soFar * 100. / total);
    }

    public long getSoFar(final int id) {
        return FileDownloader.getImpl().getSoFar(id);
    }

    public String createPath(final String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return FileDownloadUtils.getDefaultSaveFilePath(url);
    }
}
