package com.jingyue.lygame.bean;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.io.Serializable;

/**
 * Created by zhanglei on 2017/9/7.
 */
@Table(database = AppDataBase.class, name = "RcmdGameList")
public class RcmdGameListBean extends BaseAdapterModel implements Serializable {


    /**
     * id : 362626
     * name : 街机金蟾捕鱼
     * video_id : XMzAwOTAzMTM5Mg==
     * video_img :
     * score : 0,0,0,0,0,0
     * avg_score : 0
     * game_tag : 用户鉴赏
     * is_like : false
     * sub_status:( 1 可下载 2 可预约 3 已经预约)
     * icon : https://s3-test-statics-game.gank.tv/upload/20170906/59af9635c7c49.png
     * comment : {"id":64,"app_id":362626,"mem_id":77859,"content":"good ","is_add":false,"create_time":1504667571,"is_amazing":false,"like_num":"0","dislike_num":"0","phone_info":"","reply_num":"0","avg_score":"0.00","username":"alex123","avatar":"https://s3-test-statics-game.gank.tv/app/newwappc/static/img/misc/user.png"}
     */
    @Column
    @PrimaryKey
    @SerializedName("id")
    public int id;

    @Column
    @SerializedName("name")
    public String name;

    @Column
    @SerializedName("video_id")
    public String video_id;

    @Column
    @SerializedName("video_img")
    public String video_img;

    @Column
    @SerializedName("score")
    public String score;

    @Column
    @SerializedName("sub_status")
    public int sub_status;


    @Column
    @SerializedName("avg_score")
    public String avg_score="";

    @Column
    @SerializedName("game_tag")
    public String game_tag;
    @Column
    @SerializedName("is_like")
    public boolean is_like;

    @Column
    @SerializedName("icon")
    public String icon;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    public CommentListBean.Comment comment;

}
