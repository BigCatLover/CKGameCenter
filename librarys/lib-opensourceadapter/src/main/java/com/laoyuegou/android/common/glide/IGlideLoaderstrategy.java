package com.laoyuegou.android.common.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.laoyuegou.android.lib.widget.RingProgressBar;

import java.io.File;

/**
 * Created by luy on 2017/7/17.
 */

public interface IGlideLoaderstrategy extends IImageLoaderstrategy {
    /**
     * 显示带加载进度条的图片
     */
    void showImage(ImageLoaderOptions options, final RingProgressBar pb);

    /**
     * 显示带数字的进度条的图片
     */
    void showImage(ImageLoaderOptions options, final TextView textView, final ProgressBar progressBar);

    /**
     * 加载带loading资源的图片
     */
    void showImage(ImageLoaderOptions options, final ImageView loadingImageView, @DrawableRes int loadingRes);


    /**
     * 下载图片
     */
    void downloadImage(Context context, final String imageUrl, final IDownloadCallback iDownloadCallback);

    /**
     * 通过url获取bitmap，仅限在线程中使用
     * @param url
     * @return
     */
    Bitmap getBitmap(Context context, String url);

    /**
     * 通过url获取文件，仅限在线程中使用
     * @param url
     * @return
     */
    File getFile(Context context, String url);

    /**
     * 暂停加载
     */
    void pause(Context context);

    /**
     * 恢复加载
     */
    void resume(Context context);

    interface IDownloadCallback {
        void onDownloadSuccess(File file);

        void onDownFailed();
    }

}
