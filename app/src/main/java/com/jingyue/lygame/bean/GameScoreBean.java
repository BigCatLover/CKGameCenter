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
 * Created by zhanglei on 2017/9/13.
 */

@Table(database = AppDataBase.class, name = "GameScore")
public class GameScoreBean  extends BaseAdapterModel {
    /**
     * score : 0,0,0,0,0,0
     * avg_score:
     * comment : [{"app_id":"362893","mem_id":"0","content":"天空","is_add":"0","create_time":"1505297110","score":"0,0,0,0,0,0","is_amazing":"0","like_num":"0","dislike_num":"0","reply_num":"0","avg_score":"0.00","mid":null,"username":null,"avatar":"https://s3-test-statics-game.gank.tv"},{"app_id":"362893","mem_id":"0","content":"来咯","is_add":"1","create_time":"1505299372","score":"0,0,0,0,0,0","is_amazing":"0","like_num":"0","dislike_num":"0","reply_num":"0","avg_score":"0.00","mid":null,"username":null,"avatar":"https://s3-test-statics-game.gank.tv"},{"app_id":"362893","mem_id":"0","content":"户口","is_add":"1","create_time":"1505299661","score":"3,0,4,3,0,0","is_amazing":"0","like_num":"0","dislike_num":"0","reply_num":"0","avg_score":"1.67","mid":null,"username":null,"avatar":"https://s3-test-statics-game.gank.tv"}]
     * scoreText : [{"text":"创世神作","min":9,"max":10},{"text":"时代精品","min":8,"max":9},{"text":"业界顶尖","min":7,"max":8},{"text":"同类标准","min":6,"max":7},{"text":"值得一试","min":5,"max":6},{"text":"平均以下","min":4,"max":5},{"text":"扭头就走","min":0,"max":4}]
     */

    @Column
    @PrimaryKey(autoincrement = true)
    @SerializedName("id")
    public int id =1;
    @Column
    @SerializedName("score")
    public String score;
    @Column
    @SerializedName("avg_score")
    public String avg_score;
    @Column
    @SerializedName("app_id")
    public String appid;
    @Column
    @SerializedName("memid")
    public String memid;

    public List<CommentListBean.Comment> comment;

    @ForeignKey(saveForeignKeyModel = true)
    public LastBean last;

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.LOAD}, variableName = "comment")
    public List<CommentListBean.Comment> getComment() {
        // 查询
        if (comment == null ) {
            if(memid == null){
                comment = null;
            }else {
                comment = SQLite.select()
                        .from(CommentListBean.Comment.class)
                        .where(Comment_Table.app_id.eq(Integer.valueOf(appid)),Comment_Table.mem_id.eq(memid))
                        .queryList();
            }
        }else {
            //save
            for (CommentListBean.Comment comment1 : comment) {
                comment1.parentId = id;
            }
        }
        return comment;
    }

    @Table(database = AppDataBase.class,allFields = true)
    public static class LastBean extends BaseAdapterModel{

        @Column
        @PrimaryKey(autoincrement = true)
        @SerializedName("id")
        public int id =1;
        public String avg_score;
        public String score;
    }
}
