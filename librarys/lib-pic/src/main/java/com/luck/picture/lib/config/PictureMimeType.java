package com.luck.picture.lib.config;


import android.text.TextUtils;

import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DebugUtil;

import java.io.File;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.config
 * email：893855882@qq.com
 * data：2017/5/24
 */

public final class PictureMimeType {

    public static int ofImage() {
        return PictureConfig.TYPE_IMAGE;
    }

    public static int isPictureType(String pictureType) {
        switch (pictureType) {
            case "image/png":
            case "image/PNG":
            case "image/jpeg":
            case "image/JPEG":
            case "image/webp":
            case "image/WEBP":
            case "image/gif":
            case "image/GIF":
            case "imagex-ms-bmp":
                return PictureConfig.TYPE_IMAGE;
        }
        return PictureConfig.TYPE_IMAGE;
    }

    /**
     * 是否是gif
     *
     * @param pictureType
     * @return
     */
    public static boolean isGif(String pictureType) {
        switch (pictureType) {
            case "image/gif":
            case "image/GIF":
                return true;
        }
        return false;
    }

    /**
     * 是否是gif
     *
     * @param path
     * @return
     */
    public static boolean isImageGif(String path) {
        if (!TextUtils.isEmpty(path)) {
            int lastIndex = path.lastIndexOf(".");
            String pictureType = path.substring(lastIndex, path.length());
            return pictureType.startsWith(".gif")
                    || pictureType.startsWith(".GIF");
        }
        return false;
    }

    /**
     * 是否是视频
     *
     * @param pictureType
     * @return
     */
    public static boolean isVideo(String pictureType) {
        switch (pictureType) {
            case "video/3gp":
            case "video/3gpp":
            case "video/3gpp2":
            case "video/avi":
            case "video/mp4":
            case "video/quicktime":
            case "video/x-msvideo":
            case "video/x-matroska":
            case "video/mpeg":
            case "video/webm":
            case "video/mp2ts":
                return true;
        }
        return false;
    }

    /**
     * 是否是网络图片
     *
     * @param path
     * @return
     */
    public static boolean isHttp(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("http")
                    || path.startsWith("https")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断文件类型是图片还是视频
     *
     * @param file
     * @return
     */
    public static String fileToType(File file) {
        if (file != null) {
            String name = file.getName();
            DebugUtil.i("**** fileToType:", name);
            if (name.endsWith(".mp4") || name.endsWith(".avi")
                    || name.endsWith(".3gpp") || name.endsWith(".3gp") || name.startsWith(".mov")) {
                return "video/mp4";
            } else if (name.endsWith(".PNG") || name.endsWith(".png") || name.endsWith(".jpeg")
                    || name.endsWith(".gif") || name.endsWith(".GIF") || name.endsWith(".jpg")
                    || name.endsWith(".webp") || name.endsWith(".WEBP") || name.endsWith(".JPEG")) {
                return "image/jpeg";
            } else if (name.endsWith(".mp3") || name.endsWith(".amr")
                    || name.endsWith(".aac") || name.endsWith(".war")
                    || name.endsWith(".flac")) {
                return "audio/mpeg";
            }
        }
        return "image/jpeg";
    }

    /**
     * is type Equal
     *
     * @param p1
     * @param p2
     * @return
     */
    public static boolean mimeToEqual(String p1, String p2) {
        return isPictureType(p1) == isPictureType(p2);
    }

    public static String createImageType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int last = fileName.lastIndexOf(".") + 1;
                String temp = fileName.substring(last, fileName.length());
                return "image/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "image/jpeg";
        }
        return "image/jpeg";
    }

    public static String createVideoType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int last = fileName.lastIndexOf(".") + 1;
                String temp = fileName.substring(last, fileName.length());
                return "video/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "video/mp4";
        }
        return "video/mp4";
    }

    /**
     * 是否是长图
     *
     * @param media
     * @return true 是 or false 不是
     */
    public static boolean isLongImg(LocalMedia media) {
        if (null != media) {
            int width = media.getWidth();
            int height = media.getHeight();
            int h = width * 3;
            return height > h;
        }
        return false;
    }

    /**
     * 获取图片后缀
     *
     * @param path
     * @return
     */
    public static String getLastImgType(String path) {
        try {
            int index = path.lastIndexOf(".");
            if (index > 0) {
                String imageType = path.substring(index, path.length());
                switch (imageType) {
                    case ".png":
                    case ".PNG":
                    case ".jpg":
                    case ".jpeg":
                    case ".JPEG":
                    case ".WEBP":
                    case ".webp":
                        return imageType;
                    default:
                        return ".png";
                }
            } else {
                return ".png";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ".png";
        }
    }
}
