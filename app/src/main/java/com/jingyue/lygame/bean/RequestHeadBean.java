package com.jingyue.lygame.bean;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by zhanglei on 2017/9/28.
 */
@Table(database = AppDataBase.class, name = "requestHead",allFields = true)
public class RequestHeadBean extends BaseAdapterModel {
    @Column
    @PrimaryKey(autoincrement = true)
    @SerializedName("id")
    public int id;
    @Column
    @SerializedName("download_id")
    public long download_id;
    @Column
    @SerializedName("header")
    public String header;
    @Column
    @SerializedName("value")
    public String value;
}
