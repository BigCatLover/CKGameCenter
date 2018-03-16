package com.laoyuegou.android.common.glide;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-11-04 17:42
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class GlobalImageIntercepter implements RequestListener<String,GlideDrawable> {

    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        return false;
    }
}
