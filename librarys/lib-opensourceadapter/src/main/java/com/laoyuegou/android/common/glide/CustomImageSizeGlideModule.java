package com.laoyuegou.android.common.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

/**
 * Created by wang on 16/3/26.
 * glide 初始化组件
 */
public class CustomImageSizeGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        //获取默认的内存使用计算函数
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();

        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);

        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));//设置内存缓存大小
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));//设置BitmapPool缓存内存大小

        //glide默认值是250M
        //final int diskCacheSize = 1024 * 1024 * 300;//最多可以缓存多少字节的数据

        //设置磁盘缓存路径和大小
        //存放在外置文件浏览器
        //builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, "lygame", diskCacheSize));
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context));
        //builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }
}
