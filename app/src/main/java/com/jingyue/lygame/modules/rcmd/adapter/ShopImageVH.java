package com.jingyue.lygame.modules.rcmd.adapter;

import android.view.View;
import android.widget.ImageView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.ShopImageBean;
import com.jingyue.lygame.bean.internal.BaseItem;
import com.jingyue.lygame.modules.rcmd.BigImageGalleryActivity;
import com.laoyuegou.android.common.glide.ImageLoaderOptions;
import com.lygame.libadapter.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-16 10:29
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class ShopImageVH extends BaseItem.BVH<ShopImageBean>{

    @BindView(R.id.type1_image1)
    ImageView type1Image1;

    public ShopImageVH(View itemView) {
        super(itemView);
        //R.layout.rcmd_view_shop_image
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindViewHolder(ShopImageBean itemInfo, int position) {
        setValue(itemInfo);
    }

    public void setValue(final ShopImageBean value) {
        ImageLoader.getInstance().showImage(
                ImageLoader.defaultOptions(type1Image1,
                        value.list.get(0),
                        R.mipmap.ic_load_err_land)
                        .diskCacheStrategy(ImageLoaderOptions.DiskCacheStrategy.SOURCE)
                        .build());
        type1Image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImageGalleryActivity.open(v.getContext(), value.list.get(0), value.gameId, v, true);
            }
        });
    }
}
