package com.jingyue.lygame.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.jingyue.lygame.constant.AppConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by wang on 15/1/29.
 */
public class FileUtils {

    /**
     * 打开文件
     * 当手机中没有一个app可以打开file时会抛ActivityNotFoundException
     * @param context     activity
     * @param file        File
     * @param contentType 文件类型如：文本（text/html）
     */
    public static void startActionFile(Context context, File file, String contentType) throws ActivityNotFoundException {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(getUriForFile(context, file), contentType);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 打开相机
     *
     * @param activity    Activity
     * @param file        File
     * @param requestCode result requestCode
     */
    public static void startActionCapture(Activity activity, File file, int requestCode) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(activity, file));
        activity.startActivityForResult(intent, requestCode);
    }

    public static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() +".fileProvider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    /**
     * 获取工作目录
     *
     * @param context
     * @return
     */
    public static String getRootStoragePath(Context context) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            String savePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + AppConstants.PRO_DIR + File.separator;
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            return savePath;
        } else {
            String tmp = context.getFilesDir().getAbsolutePath();
            if (tmp.endsWith(File.separator)) {
                return tmp;
            } else {
                return tmp + File.separator;
            }
        }
    }

    public static String getFilePathFromUrl(Context context, String fileUrl) {
        if (context == null || StringUtils.isEmptyOrNull(fileUrl)) {
            return null;
        }
        File cacheDir = new File(context.getCacheDir(), "volley");
        int firstHalfLength = fileUrl.length() / 2;
        String localFilename = String.valueOf(fileUrl.substring(0, firstHalfLength).hashCode());
        localFilename += String.valueOf(fileUrl.substring(firstHalfLength).hashCode());
        File file = new File(cacheDir, localFilename);
        return file.getAbsolutePath();
    }

    /**
     * 删除文件夹
     *
     * @param filepsth
     */
    public static void deleteDir(String filepsth) {
        File file = new File(filepsth);
        if (null != file && file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            int size=   childFiles.length;
            for (int i = 0; i < size; i++) {
                deleteDir(childFiles[i].getPath());
            }
            file.delete();
        }
    }

    /**
     * 删除文件
     *
     * @param filepsth
     */
    public static void deleteFile(String filepsth) {
        File file = new File(filepsth);
        if (null != file && file.isFile()) {
            file.delete();
            return;
        }
    }

    /**
     * 创建文件夹
     *
     * @param destDirName
     * @return
     */
    public static boolean createDir(String destDirName) {
        if (destDirName == null) {
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        File dir = new File(destDirName);
        if (dir.exists()) {
            return false;
        }
        // 创建单个目录
        return dir.mkdirs();
    }

    public static String getDCIMPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
    }

    public static void copyfile(File fromFile, File toFile, Boolean rewrite) {
        if (!fromFile.exists()) {
            return;
        }
        if (!fromFile.isFile()) {
            return;
        }
        if (!fromFile.canRead()) {
            return;
        }
        if (null == toFile || null == toFile.getParentFile()) {
            return;
        }

        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }

        if (toFile.exists() && rewrite) {
            toFile.delete();
        }

        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.close();
        } catch (Exception ex) {
            Log.e("readfile", ex.getMessage());
        }
    }

    /**
     * 下载一个文件
     *
     * @param context 上下文
     * @param fileUrl 下载url
     */
    public static boolean downloadFile(Context context, String fileUrl, String path) {
        if (StringUtils.isEmptyOrNull(fileUrl)) {
            return false;
        }
        if (StringUtils.isEmptyOrNull(path)) {
            return false;
        }
        URL url = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            //构建图片的url地址
            url = new URL(fileUrl);
            //开启连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时的时间，45000毫秒即45秒
            conn.setConnectTimeout(45000);
            //设置获取图片的方式为GET
            conn.setRequestMethod("GET");
            //响应码为200，则访问成功
            if (conn.getResponseCode() == 200) {
                //获取连接的输入流，这个输入流就是图片的输入流
                is = conn.getInputStream();

                File file = new File(path);
                fos = new FileOutputStream(file);
                int len = 0;
                byte[] buffer = new byte[1024];
                //将输入流写入到我们定义好的文件中
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                //将缓冲刷入文件
                fos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            //在最后，将各种流关闭
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 下载一个文件
     *
     * @param context 上下文
     * @param fileUrl 下载url
     */
    public static void downloadSplashFile(Context context, String fileUrl) {
        if (StringUtils.isEmptyOrNull(fileUrl)) {
            return;
        }
        //判断文件是否已经存在
        String path = getSplashFilePathByUrl(context, fileUrl, true);
        if (!StringUtils.isEmptyOrNull(path)) {
            return;
        }
        String fileKey = getSplashFileKey(fileUrl);
        SettingUtil.write(context, fileKey, false);
        URL url = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            //构建图片的url地址
            url = new URL(fileUrl);
            //开启连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时的时间，5000毫秒即5秒
            conn.setConnectTimeout(5000);
            //设置获取图片的方式为GET
            conn.setRequestMethod("GET");
            //响应码为200，则访问成功
            if (conn.getResponseCode() == 200) {
                //获取连接的输入流，这个输入流就是图片的输入流
                is = conn.getInputStream();
                //构建一个file对象用于存储图片
                String localFile = getSplashFilePathByUrl(context, fileUrl, false);
                if (StringUtils.isEmptyOrNull(localFile)) {
                    return;
                }
                File file = new File(localFile);
                fos = new FileOutputStream(file);
                int len = 0;
                byte[] buffer = new byte[1024];
                //将输入流写入到我们定义好的文件中
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                //将缓冲刷入文件
                fos.flush();
                SettingUtil.write(context, fileKey, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //在最后，将各种流关闭
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取本地路径
     *
     * @param context
     * @param url     下载地址
     * @param isCheck 是否通过校验标志判断
     * @return 本地路径, 如果为空 则不存在
     */
    public static String getSplashFilePathByUrl(Context context, String url, boolean isCheck) {
        if (StringUtils.isEmptyOrNull(url)) {
            return "";
        }
        String dirPath = context.getFilesDir() + File.separator + "splash";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileKey = getSplashFileKey(url);
        String filePath = dirPath + File.separator + fileKey + ".file";
        if (!SettingUtil.readBoolean(context, fileKey, false)) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
        if (new File(filePath).exists() || !isCheck) {
            return filePath;
        } else {
            return "";
        }
    }

    /**
     * 清除垃圾的splash文件
     *
     * @param urls 有效的URL,清除时会跳过
     */
    public static void clearSplashFiles(Context context, ArrayList<String> urls) {
        String dirPath = context.getFilesDir() + File.separator + "splash";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return;
        }

        ArrayList<String> enableFiles = new ArrayList<String>();
        if (urls != null) {
            int size = urls.size();
            for (int i = 0; i < size; i++) {
                String url = urls.get(i);
                if (StringUtils.isEmptyOrNull(url)) {
                    continue;
                }
                String fileKey = getSplashFileKey(url);
                String filePath = dirPath + File.separator + fileKey + ".file";
                enableFiles.add(filePath);
            }
        }
        File[] files = dir.listFiles();
        int length = files.length;
        for (int i = 0; i < length; i++) {
            if (enableFiles.contains(files[i].getAbsolutePath())) {
                continue;
            }
            files[i].delete();
        }
    }

    private static String getSplashFileKey(String url) {
        if (StringUtils.isEmptyOrNull(url)) {
            return "";
        }
        return url.hashCode() + "";
    }


    private static final String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

    public static String getMIMEType(String fName) {

        String type = "*/*";

        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
    /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        int size=   MIME_MapTable.length;
        for (int i = 0; i <size; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }


    //写数据到SD中的文件
    public static void writeFileSdcardFile(String fileName, String write_str) {
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            byte[] bytes = write_str.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
