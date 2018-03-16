package com.jingyue.lygame.bean;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-05 16:45
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
@Table(database = AppDataBase.class,allFields = true)
public class ImageCodeBean extends BaseAdapterModel {
    @PrimaryKey(autoincrement = true)
    public int id;
    @SerializedName("width")
    public String width;
    @SerializedName("height")
    public String height;
}
