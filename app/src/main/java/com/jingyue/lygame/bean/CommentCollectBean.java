package com.jingyue.lygame.bean;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by zhanglei on 2017/9/16.
 * 收藏实体
 */
@Table(database = AppDataBase.class, name = "CommentCollect")
public class CommentCollectBean extends BaseAdapterModel {
    /**
     * comment_id : 1
     * app_id : 362893
     * mem_id : 77691
     * content : 123123123123123
     * avg_score : 100.00
     * like_num : 12340
     * username : ceshi123
     * game : {"id":0,"name":"","icon":""}
     */
    @Column
    @PrimaryKey
    @SerializedName("comment_id")
    private String comment_id;
    @Column
    private String app_id;
    @Column
    private String mem_id;
    @Column
    private String content;
    @Column
    private String avg_score;
    @Column
    private String like_num;
    @Column
    private String username;
    @Column
    private boolean is_add;
    @Column
    private boolean is_amazing;
    @Column
    private boolean is_like;
    @Column
    private boolean is_dislike;
    @Column
    private boolean is_reply;

    @ForeignKey(saveForeignKeyModel = true)
    public CollectGameBean game;

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
    public boolean is_add() {
        return is_add;
    }

    public void set_add(boolean is_add) {
        this.is_add = is_add;
    }

    public boolean is_amazing() {
        return is_amazing;
    }

    public void set_amazing(boolean is_amazing) {
        this.is_amazing = is_amazing;
    }

    public boolean is_like() {
        return is_like;
    }

    public void set_like(boolean is_like) {
        this.is_like = is_like;
    }

    public boolean is_dislike() {
        return is_dislike;
    }

    public void set_dislike(boolean is_dislike) {
        this.is_dislike = is_dislike;
    }

    public boolean is_reply() {
        return is_reply;
    }

    public void set_reply(boolean is_reply) {
        this.is_reply = is_reply;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getMem_id() {
        return mem_id;
    }

    public void setMem_id(String mem_id) {
        this.mem_id = mem_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvg_score() {
        return avg_score;
    }

    public void setAvg_score(String avg_score) {
        this.avg_score = avg_score;
    }

    public String getLike_num() {
        return like_num;
    }

    public void setLike_num(String like_num) {
        this.like_num = like_num;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CollectGameBean getGame() {
        return game;
    }

    public void setGame(CollectGameBean game) {
        this.game = game;
    }

    @Table(database = AppDataBase.class,allFields = true)
    public static class CollectGameBean extends BaseAdapterModel{
        /**
         * id : 0
         * name :
         * icon :
         */
        @Column
        @PrimaryKey()
        private int id;
        @Column
        private String name;
        @Column
        private String icon;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
