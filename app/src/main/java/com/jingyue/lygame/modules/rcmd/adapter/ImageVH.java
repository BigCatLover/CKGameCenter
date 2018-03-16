package com.jingyue.lygame.modules.rcmd.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;

import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.jingyue.lygame.bean.internal.BaseItem;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.modules.rcmd.BigImageGalleryActivity;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-16 11:25
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class ImageVH<T extends BaseAdapterModel> extends BaseItem.BVH<T> {

    public ImageVH(View itemView) {
        super(itemView);
    }

    @Override
    public void onBindViewHolder(T itemInfo, int position) {

    }



    public void imageClick(ImageView view) {
        if(view == null) return;
        final Context context = view.getContext();
        Intent intent = new Intent(context, BigImageGalleryActivity.class);
        // 创建一个 rect 对象来存储共享元素位置信息
        Rect rect = new Rect();
        // 获取元素位置信息
        view.getGlobalVisibleRect(rect);
        // 将位置信息附加到 intent 上
        intent.setSourceBounds(rect);
    }
}
