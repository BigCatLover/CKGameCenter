package com.jingyue.lygame.bean;

import android.view.View;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.jingyue.lygame.bean.internal.BaseItem;
import com.jingyue.lygame.bean.typeconvert.StringTypeConverter;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.modules.rcmd.adapter.Shop2ImageVH;
import com.jingyue.lygame.modules.rcmd.adapter.Shop3ImageVH;
import com.jingyue.lygame.modules.rcmd.adapter.ShopImageVH;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-15 17:44
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
@Table(name = "shopImage", database = AppDataBase.class, allFields = true)
public class ShopImageBean extends BaseAdapterModel {

    public ShopImageBean() {
        super(AppConstants.ItemType.SHOP_IMAGE_DEFAULT);
    }

    public ShopImageBean(int itemType) {
        super(itemType);
    }

    @Override
    public BaseItem.BVH onCreateViewHolder(View parent, int viewType) {
        return new ShopImageVH(parent);
    }

    public String gameId;

    @Override
    public int getLayout() {
        return R.layout.rcmd_view_shop_image;
    }

    @PrimaryKey(autoincrement = true)
    public int id;
    /**
     * type : 1
     * list : ["https://imgx.gank.tv/cai/img/300/hero/155.jpg"]
     */
    @SerializedName("is_landscape")
    public int type;

    /**
     * Type token 源码中显示
     * List<String> 是不能作为反序列化对象的
     * 这里在反射调用construct的初始化空对象的时候一直出现InvocationTargetException
     * <p>
     * {@code TypeToken<List<String>> list = new TypeToken<List<String>>() {};}
     * <p>
     * <p>This syntax cannot be used to create type literals that have wildcard
     * parameters, such as {@code Class<?>} or {@code List<? extends CharSequence>}.
     */
    @SerializedName("list")
    @Column(typeConverter = StringTypeConverter.class)
    public List<String> list;
    @SerializedName("size")
    public int size;

    public static final class Shop2ImageBean extends ShopImageBean {

        /**
         * for register
         */
        public Shop2ImageBean() {
            super(AppConstants.ItemType.SHOP_IMAGE_2);
        }

        public Shop2ImageBean(ShopImageBean bean) {
            super(AppConstants.ItemType.SHOP_IMAGE_2);
            size = bean.size;
            list = bean.list;
            type = bean.type;
            gameId = bean.gameId;
        }

        @Override
        public int getLayout() {
            return R.layout.rcmd_view_shop_2image;
        }

        @Override
        public BaseItem.BVH onCreateViewHolder(View parent, int viewType) {
            return new Shop2ImageVH(parent);
        }
    }

    public static final class Shop3ImageBean extends ShopImageBean {

        public Shop3ImageBean() {
            super(AppConstants.ItemType.SHOP_IMAGE_3);
        }

        public Shop3ImageBean(ShopImageBean bean) {
            super(AppConstants.ItemType.SHOP_IMAGE_3);
            size = bean.size;
            list = bean.list;
            type = bean.type;
            gameId = bean.gameId;
        }

        @Override
        public int getLayout() {
            return R.layout.rcmd_view_shop_3image;
        }

        @Override
        public BaseItem.BVH onCreateViewHolder(View parent, int viewType) {
            return new Shop3ImageVH(parent);
        }
    }
}

