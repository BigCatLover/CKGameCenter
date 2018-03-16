package com.jingyue.lygame.modules.rcmd.adapter;

import android.view.View;
import android.widget.ImageView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.ShopImageBean;
import com.jingyue.lygame.bean.internal.BaseItem;
import com.jingyue.lygame.modules.rcmd.BigImageGalleryActivity;
import com.lygame.libadapter.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-16 10:29
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class Shop3ImageVH extends BaseItem.BVH<ShopImageBean> implements View.OnClickListener {

    @BindView(R.id.type3_image1)
    ImageView type3Image1;
    @BindView(R.id.type3_image2)
    ImageView type3Image2;
    @BindView(R.id.type3_image3)
    ImageView type3Image3;
    
    public Shop3ImageVH(View itemView) {
        super(itemView);
        //R.layout.rcmd_view_shop_image
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindViewHolder(ShopImageBean itemInfo, int position) {
        setValue(itemInfo);
    }


    public void setValue(final ShopImageBean value) {
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultSourceOptions(type3Image1, value.list.get(0),R.mipmap.ic_load_err_land));
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultSourceOptions(type3Image2, value.list.get(1),R.mipmap.ic_load_err_land));
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultSourceOptions(type3Image3, value.list.get(2),R.mipmap.ic_load_err_land));
        type3Image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImageGalleryActivity.open(v.getContext(), value.list.get(0), value.gameId, v,true);
            }
        });
        type3Image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImageGalleryActivity.open(v.getContext(), value.list.get(1), value.gameId, v,true);
            }
        });
        type3Image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImageGalleryActivity.open(v.getContext(), value.list.get(2), value.gameId, v,true);
            }
        });
    }


    @Override
    public void onClick(View v) {
        final String url = String.valueOf(v.getTag());
        BigImageGalleryActivity.open(v.getContext(), url, null, v,true);
    }
}
