package com.laoyuegou.android.common.glide;

import android.content.Context;
import android.support.annotation.NonNull;


/**
 * Created by luy on 2017/7/14
 */

public interface IImageLoaderstrategy {
    void showImage(@NonNull ImageLoaderOptions options);
    void cleanMemory();
    // 在application的oncreate中初始化
    void init(Context context);
    void onTrimMemory(int level);
}
