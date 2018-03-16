package com.laoyuegou.android.common.glide.module;


/**
 * Created by luy on 2017/8/8.
 */

public class CustomImageSizeModelFutureStudio implements CustomImageSizeModel {
    String baseImageUrl;

    public CustomImageSizeModelFutureStudio(String baseImageUrl) {
        this.baseImageUrl = baseImageUrl;
    }

    @Override
    public String requestCustomSizeUrl(int width, int height) {
        return baseImageUrl;
    }
}
