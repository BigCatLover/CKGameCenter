package com.laoyuegou.android.common.glide.module;

import android.content.Context;

import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

/**
 * Created by luy on 2017/8/8.
 */

public class CustomImageSizeUrlLoader extends BaseGlideUrlLoader<CustomImageSizeModel> {

    public CustomImageSizeUrlLoader(Context context) {
        super( context );
    }

    @Override
    protected String getUrl(CustomImageSizeModel model, int width, int height) {
        return model.requestCustomSizeUrl(width, height);
    }
}
