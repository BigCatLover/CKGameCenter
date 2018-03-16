package com.jingyue.lygame.bean;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-24 18:35
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
@Table(allFields = true, name = "version", database = AppDataBase.class)
public class VersionInfoBean extends BaseAdapterModel {

    /**
     * update : false
     * url :
     * lastVersionCode : 1
     */
    @SerializedName("update")
    public boolean update;
    @SerializedName("url")
    public String url;
    @PrimaryKey
    @SerializedName("lastVersionCode")
    public String lastVersionCode;
    @SerializedName("forceUpdate")
    public boolean forceUpdate;
}
