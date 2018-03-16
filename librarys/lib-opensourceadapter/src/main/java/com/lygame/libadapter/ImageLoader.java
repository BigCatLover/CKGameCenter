package com.lygame.libadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.laoyuegou.android.common.glide.GlideImageLoader;
import com.laoyuegou.android.common.glide.IGlideLoaderstrategy;
import com.laoyuegou.android.common.glide.IImageLoaderstrategy;
import com.laoyuegou.android.common.glide.ImageLoaderOptions;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.widget.RingProgressBar;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by luy on 2017/7/14
 */
public class ImageLoader implements IGlideLoaderstrategy {

    private static final AtomicReference<ImageLoader> INSTANCE = new AtomicReference<ImageLoader>();

    private ImageLoader(){}

    /**
     * 在application的oncreate中初始化
     */
    public void init(Context context){
        loaderstrategy = new GlideImageLoader();
        loaderstrategy.init(context);
    }

    @Override
    public void onTrimMemory(int level) {
        if(loaderstrategy!=null){
            loaderstrategy.onTrimMemory(level);
        }
    }

    public static ImageLoader getInstance() {
       for (; ; ) {
          ImageLoader current = INSTANCE.get();
             if (current != null) {
                return current;
             }
             current = new ImageLoader();
             if (INSTANCE.compareAndSet(null, current)) {
                 return current;
             }
        }
    }

    private IImageLoaderstrategy loaderstrategy;

    public void setImageLoaderStrategy(IImageLoaderstrategy strategy) {
        loaderstrategy = strategy;
    }

    /*
     *  可创建默认的Options设置，假如不需要使用ImageView ，
     *  请自行new一个Imageview传入即可
     *  内部只需要获取Context
     */

    public static ImageLoaderOptions getDefaultOptions(@NonNull View container, @NonNull String url) {
        return new ImageLoaderOptions.Builder(container, url).isCrossFade(true).build();
    }

    public static ImageLoaderOptions getDefaultOptions(@NonNull View container, @NonNull String url,@NonNull int resid) {
        return new ImageLoaderOptions.Builder(container, url).isCrossFade(true).error(resid).build();
    }
    public static ImageLoaderOptions getDefaultOptions(@NonNull View container, @NonNull String url,@NonNull int placeholder,@NonNull int resid) {
        return new ImageLoaderOptions.Builder(container, url).isCrossFade(true).placeholder(placeholder).error(resid).build();
    }

    public static ImageLoaderOptions getDefaultSourceOptions(@NonNull View container, @NonNull String url,@NonNull int resid) {
        return new ImageLoaderOptions.Builder(container, url)
                .isCrossFade(true).placeholder(resid)
                .autoSizeUrl(true)
                .diskCacheStrategy(ImageLoaderOptions.DiskCacheStrategy.SOURCE)
                .build();
    }


    public static ImageLoaderOptions.Builder defaultOptions(@NonNull View container, @NonNull String url,@NonNull int resid) {
        return new ImageLoaderOptions.Builder(container, url).placeholder(resid);
    }

    public static ImageLoaderOptions getFileOptions(@NonNull View container, @NonNull File path,@NonNull int resid) {
        return new ImageLoaderOptions.Builder(container, path).isCrossFade(true).placeholder(resid).error(resid).build();
    }

    @Override
    public void showImage(@NonNull ImageLoaderOptions options) {
        if (loaderstrategy != null) {
            loaderstrategy.showImage(options);
        }
    }

    public void showImage(@NonNull ImageLoaderOptions options, BaseImpl base) {
        if (loaderstrategy != null) {
            loaderstrategy.showImage(options);
        }
    }


    @Override
    public void cleanMemory() {
        loaderstrategy.cleanMemory();
    }

    /**
     * 显示带加载进度条的图片
     */
    @Override
    public void showImage(ImageLoaderOptions options, RingProgressBar pb) {
        if (loaderstrategy instanceof IGlideLoaderstrategy) {
            ((IGlideLoaderstrategy) loaderstrategy).showImage(options, pb);
        }
    }

    /**
     * 显示带数字的进度条的图片
     */
    @Override
    public void showImage(ImageLoaderOptions options, TextView textView, ProgressBar progressBar) {
        if (loaderstrategy instanceof IGlideLoaderstrategy) {
            ((IGlideLoaderstrategy) loaderstrategy).showImage(options, textView, progressBar);
        }
    }

    /**
     * 下载图片
     *
     * @param imageUrl：图片url
     * @param iDownloadCallback:下载成功和失败的回调
     */
    @Override
    public void downloadImage(Context context, String imageUrl, IDownloadCallback iDownloadCallback) {
        if (loaderstrategy instanceof IGlideLoaderstrategy) {
            ((IGlideLoaderstrategy) loaderstrategy).downloadImage(context, imageUrl, iDownloadCallback);
        }
    }

    /**
     * 加载带loading资源的图片
     */
    @Override
    public void showImage(ImageLoaderOptions options, ImageView loadingImageView, @DrawableRes int loadingRes) {
        if (loaderstrategy instanceof IGlideLoaderstrategy) {
            ((IGlideLoaderstrategy) loaderstrategy).showImage(options, loadingImageView, loadingRes);
        }
    }

    @Override
    public Bitmap getBitmap(Context context, String url) {
        if (loaderstrategy instanceof IGlideLoaderstrategy) {
            return ((IGlideLoaderstrategy) loaderstrategy).getBitmap(context, url);
        }
        return null;
    }

    @Override
    public File getFile(Context context, String url) {
        if (loaderstrategy instanceof IGlideLoaderstrategy) {
            return ((IGlideLoaderstrategy) loaderstrategy).getFile(context, url);
        }
        return null;
    }

    /**
     * 暂停加载
     */
    @Override
    public void pause(Context context) {
        if (loaderstrategy instanceof IGlideLoaderstrategy) {
            ((IGlideLoaderstrategy) loaderstrategy).pause(context);
        }
    }

    /**
     * 恢复加载
     */
    @Override
    public void resume(Context context) {
        if (loaderstrategy instanceof IGlideLoaderstrategy) {
            ((IGlideLoaderstrategy) loaderstrategy).resume(context);
        }
    }
}
