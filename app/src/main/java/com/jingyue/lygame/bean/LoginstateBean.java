package com.jingyue.lygame.bean;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-31 14:23
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
@Table(database = AppDataBase.class, name = "LoginState")
public class LoginstateBean extends BaseAdapterModel {

    /**
     * id : 77859
     * username : alex123
     * icon : http://statics.game.lygou.cc/upload/logo/touxiang.jpg
     */
    @Column
    @PrimaryKey
    @SerializedName("id")
    public String id;
    @Column
    @SerializedName("username")
    public String username;
    @Column
    @SerializedName("icon")
    public String icon;
}
