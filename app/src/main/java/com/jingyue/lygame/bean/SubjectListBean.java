package com.jingyue.lygame.bean;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by zhanglei on 2017/9/20.
 */

@Table(database = AppDataBase.class, name = "subjectList",allFields = true)
public class SubjectListBean extends BaseAdapterModel{
    /**
     * id : 1
     * name : aaaa
     * img : https://s3-test-statics-game.gank.tv/upload/20170519/591e9ea524511.png
     * big_img : https://s3-test-statics-game.gank.tv/upload/20170914/59b9fe294d674.jpg
     * updated_time : 0
     */
    @PrimaryKey
    @Column
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("img")
    public String img;
    @SerializedName("big_img")
    public String bigImg;
    @SerializedName("updated_time")
    public String updatedTime;
}
