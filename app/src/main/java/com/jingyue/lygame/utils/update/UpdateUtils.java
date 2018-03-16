package com.jingyue.lygame.utils.update;

import android.os.Environment;
import android.os.StatFs;

import com.jingyue.lygame.utils.StringUtils;

import java.io.File;

/**
 */
public class UpdateUtils {
    /**
     * 根据路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileNameByPath(String filePath) {
        if (StringUtils.isEmptyOrNull(filePath)) {
            return "";
        }
        int length = filePath.length();
        String fileName;
        try {
            int index = filePath.lastIndexOf("/");
            if (index > 0 && index < length - 1) {
                fileName = filePath.substring(index + 1, length);
            } else {
                if (length < 10) {
                    fileName = filePath;
                } else {
                    fileName = filePath.substring(length - 10, length);
                }
            }
        } catch (Exception e) {
            fileName = filePath;
        }
        return fileName;
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

}
