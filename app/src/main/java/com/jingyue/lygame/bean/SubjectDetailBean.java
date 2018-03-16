package com.jingyue.lygame.bean;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Created by zhanglei on 2017/9/20.
 */
@Table(database = AppDataBase.class, name = "subjectDetail")
public class SubjectDetailBean extends BaseAdapterModel{

    /**
     * subject : {"id":"1","name":"aaaa","description"：""，content":"100","img":"/upload/20170519/591e9ea524511.png","like_num":"100","updated_time":"0","is_like":false}
     * list : [{"id":"100","name":"APP","icon":"https://s3-test-statics-game.gank.tv/upload/20170415/58f19cd24a0cb.png","description":null,"bigimage":null,"androidurl":null,"video_img":null,"avg_score":null}]
     */

    @Column
    @PrimaryKey(autoincrement = true)
    @SerializedName("id")
    public int id;

    @Column
    @SerializedName("subject")
    @ForeignKey(saveForeignKeyModel = true)
    public Subject subject;

    @SerializedName("description")
    public String description;

    public List<ListBean> list;

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.LOAD}, variableName = "list")
    public List<ListBean> getList() {
        //查询
        if (list == null ) {
            list = SQLite.select()
                    .from(ListBean.class)
                    .where(ListBean_Table.parentId.eq(id))
                    .queryList();
        }else {
            //save
            for (ListBean bean : list) {
                bean.parentId = id;
            }
        }
        return list;
    }


    @Table(database = AppDataBase.class,allFields = true)
    public static class Subject extends BaseAdapterModel{
        /**
         * id : 12
         * name : 唤醒回忆的像素风
         * content : 362794,362796,362626
         * description : 唤醒童年回忆的萌萌哒像素风
         * img : https://s3-test-statics-game.gank.tv/upload/20170920/59c2569c4f60a.png
         * big_img : https://s3-test-statics-game.gank.tv/upload/20170920/59c2585789d4a.jpg
         * like_num : 1
         * updated_time : 1505918624
         * is_like : false
         */
        @Column
        @PrimaryKey
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("content")
        public String content;
        @SerializedName("description")
        public String description;
        @SerializedName("img")
        public String img;
        @SerializedName("big_img")
        public String bigImg;
        @SerializedName("like_num")
        public String likeNum;
        @SerializedName("updated_time")
        public String updatedTime;
        @SerializedName("is_like")
        public boolean isLike;
    }

    @Table(database = AppDataBase.class,allFields = true)
    public static class ListBean extends BaseAdapterModel{
        /**
         * id : 362796
         * name : 僵尸突击队4
         * status_ext : 0
         * is_transport : false
         * icon : https://s3-test-statics-game.gank.tv/upload/20170519/591eaff45e045.png
         * description : 病毒爆发，大批僵尸入侵威胁着人类的生存
         * bigimage : 1495440962.png
         * androidurl : https://env-test-down-game.gank.tv/sdkgame/jstjd4and_362796/jstjd4and_362796.apk
         * video_id : XMzAwOTA1MzQ0NA==
         * video_img : https://imgx.gank.tv/cai/img/game_center/2.jpg
         * avg_score : 0.0
         * is_active : false
         */
        public int parentId;

        @Column
        @PrimaryKey
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("status_ext")
        public String statusExt;
        @SerializedName("is_transport")
        public boolean isTransport;
        @SerializedName("icon")
        public String icon;
        @SerializedName("description")
        public String description;
        @SerializedName("bigimage")
        public String bigimage;
        @SerializedName("androidurl")
        public String androidurl;
        @SerializedName("video_id")
        public String videoId;
        @SerializedName("video_img")
        public String videoImg;
        @SerializedName("avg_score")
        public String avgScore;
        @SerializedName("is_active")
        public boolean isActive;
        @SerializedName("sub_status")
        public int subStatus;
        @SerializedName("packagename")
        public String packagename;
    }
}
