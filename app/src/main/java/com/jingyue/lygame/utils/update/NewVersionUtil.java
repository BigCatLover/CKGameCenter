package com.jingyue.lygame.utils.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.R;
import com.jingyue.lygame.events.EventDownloadStart;
import com.jingyue.lygame.utils.SettingUtil;
import com.jingyue.lygame.utils.ToolUtils;
import com.laoyuegou.android.lib.utils.EBus;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

import io.reactivex.Observer;
import okhttp3.ResponseBody;

/**
 * Created by liukun on 2017/6/15.
 */

public class NewVersionUtil {

    public static final String NEW_VERSION_NAME = "NEW_VERSION_NAME";
    public static final String NEW_VERSION_SIZE = "NEW_VERSION_SIZE";

    public static final String INSTALL_NEW_VERSION = "INSTALL_NEW_VERSION";
    public static final String FORCE_UPDATE = "FORCE_UPDATE";
    public static final String UPDATE_LOG = "UPDATE_LOG";
    public static final String UPDATE_TITLE = "UPDATE_TITLE";
    public static final String UPDATE_SIZE = "UPDATE_SIZE";
    public static final String UPDATE_URL = "UPDATE_URL";
    public static final String UPDATE_SKIP = "UPDATE_SKIP";

    public static final String DIR_NAME = "apk";

    public static final String NEW_VERSION_INSTALLED = "NEW_VERSION_INSTALLED"; //新版本已经安装

    public static boolean checkNewVersionFile(Context context) {
        //版本是否比当前版本大
        clearNewVersionInfo(context);

        String fileName = SettingUtil.readString(Utils.getContext(), NEW_VERSION_NAME, "");
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }

        File f = new File(getNewVersioinFilePath(context) + fileName);
        if (f.exists()) {
            long NEW_VERSION_SIZE = SettingUtil.readLong(Utils.getContext(), "NEW_VERSION_SIZE", 0);
            if (f.length() == NEW_VERSION_SIZE) {
                return true;
            } else {
                //删除重新下
                f.delete();
                return false;
            }
        }

        return false;
    }

    public boolean checkNewVersionFileExist(Context context) {
        String fileName = SettingUtil.readString(Utils.getContext(), NEW_VERSION_NAME, "");
        File f = new File(getNewVersioinFilePath(context) + fileName);
        return f.exists();
    }

    /**
     * 获取本地是否有已经下载好的版本
     *
     * @param context
     * @return case 1: 如果文件存在，并且已经下载完成，返回-1
     * case 2: 如果文件存在，但是下载为完成，返回文件长度
     * case 3: 如果文件不存在，返回0
     */
    public static long checkNewVersionFileSize(Context context) {
        String fileName = SettingUtil.readString(Utils.getContext(), NEW_VERSION_NAME, "");
        String filePathName = getNewVersioinFilePath(context) + fileName;
        File f = new File(filePathName);
        if (f.exists()) {
            long size = SettingUtil.readLong(Utils.getContext(), NEW_VERSION_SIZE, 0);
            //如果文件存在，但是失去了文件大小的记录，删除文件重新下载，因为无法验证文件是否正确
            if (size == -999) {
                f.delete();
                return 0;
            } else if (f.length() == size) {
                if (!checkAPK(filePathName)) {
                    f.delete();
                    return 0;
                }
                return -1;
            } else {
                return f.length();
            }
        }

        return 0;
    }

    /**
     * 获取文件路径
     *
     * @param context
     * @return
     */
    public static String getNewVersioinFilePath(Context context) {
        return new File(context.getExternalCacheDir(), DIR_NAME).getAbsolutePath();
    }

    public static File getInstallNewVersionFile(Context context, String newVersion) {
        return new File(context.getExternalCacheDir(),
                DIR_NAME + File.separator + NewVersionUtil.getNewVersionFileName(newVersion));
    }

    /**
     * 获取文件名
     *
     * @param newVersion
     * @return
     */
    public static String getNewVersionFileName(String newVersion) {
        String fileName = newVersion + "_" + BuildConfig.FLAVOR + ".apk";
        return fileName;
    }

    /**
     * 根据版本号，判断文件是否存在
     */
    public static boolean isNewVersionFileExist(Context context, String newVersion) {
        return getInstallNewVersionFile(context, newVersion).exists();
    }

    /**
     * 下载最新的安装包
     *
     * @param responseBody
     * @param dir
     * @param newVersion
     * @return
     */
    public static boolean downloadNewVersion(ResponseBody responseBody, String dir, String
            newVersion) {
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdir();
        }

        String fileName = getNewVersionFileName(newVersion);
        SettingUtil.write(Utils.getContext(), NEW_VERSION_NAME, fileName);
        try {
            File target = new File(dir + fileName);

            if (!target.exists()) {
                long contentLength = responseBody.contentLength();
                SettingUtil.write(Utils.getContext(), NEW_VERSION_SIZE, contentLength);
            }

            return appendFile(target, responseBody);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查apk的完整性
     *
     * @return
     */
    public static boolean checkAPK(String filePath) {
        PackageManager manager = Utils.getContext().getApplicationContext().getPackageManager();
        PackageInfo info = manager.getPackageArchiveInfo(filePath,
                PackageManager.GET_ACTIVITIES);
        return info != null;
    }

    /**
     * 校验文件长度
     *
     * @param filePathName
     * @param fileLength
     * @return
     */
    public static boolean checkFileLength(String filePathName, long fileLength) {
        File file = new File(filePathName);

        if (file.isFile() && file.exists()) {
            return file.length() == fileLength;
        }
        return false;
    }


    public static boolean appendFile(File target, ResponseBody responseBody) {
        try {
            InputStream inputStream = null;
            RandomAccessFile randomFile = null;

            long fileSizeDownloaded = 0;
            long fileSize = responseBody.contentLength();

            try {
                byte[] fileReader = new byte[4096];

                inputStream = responseBody.byteStream();

                randomFile = new RandomAccessFile(target, "rw");
                long fileLength = randomFile.length();
                randomFile.seek(fileLength);

                int read;

                while ((read = inputStream.read(fileReader)) != -1) {
                    randomFile.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (randomFile != null) {
                    randomFile.close();
                }
                if (responseBody != null) {
                    responseBody.close();
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static long appendFile(File target, ResponseBody responseBody, Observer<Long> observer) {
        try {
            InputStream inputStream = null;
            RandomAccessFile randomFile = null;

            long fileSizeDownloaded = 0;
            long fileSize = responseBody.contentLength();

            try {
                byte[] fileReader = new byte[10240];

                inputStream = responseBody.byteStream();

                randomFile = new RandomAccessFile(target, "rw");
                long fileLength = randomFile.length();
                randomFile.seek(fileLength);

                int read;

                while ((read = inputStream.read(fileReader)) != -1) {

                    randomFile.write(fileReader, 0, read);
                    fileSizeDownloaded += read;

                    if (observer != null) {
                        observer.onNext(fileSizeDownloaded);
                    }
                }
                observer.onComplete();


                return fileSizeDownloaded;
            } catch (IOException e) {
                if (observer != null) {
                    observer.onError(e);
                }
                return -1;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (randomFile != null) {
                    randomFile.close();
                }
                if (responseBody != null) {
                    responseBody.close();
                }
            }
        } catch (Exception e) {
            if (observer != null) {
                observer.onError(e);
            }
            return -1;
        }
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context, String filePath) {
        ToolUtils.installApk(context, new File(filePath));
    }

    /**
     * 清除版本更新相关信息
     *
     * @param context
     */
    public static void clearNewVersionInfo(Context context) {
        String newVersion = SettingUtil.readString(Utils.getContext(), NEW_VERSION_INSTALLED, "");
        if (TextUtils.isEmpty(newVersion)) {
            return;
        }

        if (newVersion.startsWith("v")) {
            newVersion = newVersion.substring(1, newVersion.length());
        }

        //如果 当前版本 与 缓存版本一致，代表更新成功
        if (newVersion.equals(BuildConfig.VERSION_NAME)) {

            //删除文件
            String dir = getNewVersioinFilePath(context);
            String fileName = SettingUtil.readString(Utils.getContext(), NEW_VERSION_NAME, "");
            String filePath = dir + fileName;
            File f = new File(filePath);

            if (f != null && f.exists()) {
                f.delete();
            }

            //删除缓存内容

            SettingUtil.delete(Utils.getContext(), INSTALL_NEW_VERSION);
            SettingUtil.delete(Utils.getContext(), FORCE_UPDATE);
            SettingUtil.delete(Utils.getContext(), UPDATE_LOG);
            SettingUtil.delete(Utils.getContext(), UPDATE_TITLE);
            SettingUtil.delete(Utils.getContext(), UPDATE_SIZE);
            SettingUtil.delete(Utils.getContext(), NEW_VERSION_INSTALLED);
            SettingUtil.delete(Utils.getContext(), NEW_VERSION_NAME);
            SettingUtil.delete(Utils.getContext(), NEW_VERSION_SIZE);


        }
    }

    /**
     * 下载最新的安装包
     *
     * @param responseBody
     * @param dir
     * @return
     */
    public static long downloadFile(ResponseBody responseBody, String dir, String
            fileName, Observer<Long> observer) {
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdir();
        }


        try {
            File target = new File(dir + fileName);

            if (!target.exists()) {
                long contentLength = responseBody.contentLength();
                //记录文件大小
//                CacheManager.getInstance().addSimpleLongCache(fileName + "_SIZE", contentLength);
                SettingUtil.write(Utils.getContext(), fileName + "_SIZE", contentLength);
            }

            return appendFile(target, responseBody, observer);

        } catch (Exception e) {
            if (observer != null) {
                observer.onError(e);
            }
            return -1;
        }
    }

    public static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(
                    FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }


    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 根据下载状态进行下载或安装
     *
     * @param url
     * @param newVersion
     */
    public static void checkNewVersionAndInstall(String url, String newVersion, boolean needToast) {
        long lastDownloadSize = NewVersionUtil.checkNewVersionFileSize(Utils.getContext());

        if (lastDownloadSize > 0) {                                         //如果下载一半，继续下载
            startDownloadService(url, newVersion, lastDownloadSize);
        } else if (NewVersionUtil.isNewVersionFileExist(Utils.getContext(), newVersion)) {      //如果已经下载完成，直接安装

            SettingUtil.write(Utils.getContext(), NEW_VERSION_INSTALLED + newVersion, newVersion);
            //安装新包
            String path = NewVersionUtil.getInstallNewVersionFile(Utils.getContext(), newVersion).getPath();
            NewVersionUtil.installApk(Utils.getContext(), path);
        } else {
            //如果没有下载，去下载
            LogUtils.i(Utils.getContext().getString(R.string.a_0125));
            if (needToast) ToastUtils.showShort(Utils.getContext().getString(R.string.a_0125));
            startDownloadService(url,  newVersion, lastDownloadSize);
        }

    }

    /**
     * 启动下载服务
     *
     * @param url
     * @param newVersion
     * @param lastDownloadSize
     */
    private static void startDownloadService(String url,  String newVersion, long lastDownloadSize) {
        Intent intent = new Intent(Utils.getContext().getApplicationContext(), UpdateDownLoadService.class);

        intent.putExtra("url", url);
        intent.putExtra("newVersion", newVersion);
        intent.putExtra("lastDownloadSize", lastDownloadSize);

        Utils.getContext().startService(intent);
        EBus.getDefault().post(new EventDownloadStart());
    }
}
