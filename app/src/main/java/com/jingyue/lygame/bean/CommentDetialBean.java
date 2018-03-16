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
 * Created by zhanglei on 2017/9/15.
 */
@Table(database = AppDataBase.class, name = "commentDetail")
public class CommentDetialBean extends BaseAdapterModel {
    /**
     * comment : {"comment_id":2,"app_id":"362893","mem_id":"77691","content":"123123123123123","is_add":"0",
     * "create_time":"1503912562","is_amazing":"0","like_num":"12340","dislike_num":"340","reply_num":"60","avg_score":"100.00","mid":"77691","username":"ceshi123","avatar":""}
     * add : [{"comment_id":"12","app_id":"362893","mem_id":"77691","content":"123","is_add":"1","create_time":"1503912562","is_amazing":"0","like_num":"12340","dislike_num":"450","reply_num":"4650","avg_score":"100.00","mid":"77691","username":"ceshi123","avatar":""}]
     * reply : [{"comment_id":"3","app_id":"362893","mem_id":"77694","content":"4444444","is_add":"0","create_time":"1503912562","is_amazing":"0","like_num":"12340","dislike_num":"12340","reply_num":"1232340","avg_score":"100.00","mid":"77694","username":"143760","avatar":""}]
     */
    @Column
    @PrimaryKey(autoincrement = true)
    @SerializedName("id")
    public int id = 1;
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    public CommentListBean.Comment comment;
    public List<CommentListBean.Comment> add;
    public List<CommentListBean.Comment> reply;

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.LOAD}, variableName = "add")
    public List<CommentListBean.Comment> getAdd() {
        // 查询
        if (add == null ) {
            add = SQLite.select()
                    .from(CommentListBean.Comment.class)
                    .where(Comment_Table.parentId.eq(id), Comment_Table.is_add.is(true))
                    .queryList();
        } else {
            //save
            for (CommentListBean.Comment comment1 : add) {
                comment1.parentId = comment.comment_id;
            }
        }

        return add;
    }

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.LOAD}, variableName = "reply")
    public List<CommentListBean.Comment> getReply() {
        //查询
        if (reply == null ) {
            reply = SQLite.select()
                    .from(CommentListBean.Comment.class)
                    .where(Comment_Table.parentId.eq(comment.comment_id), Comment_Table.is_reply.is(true))
                    .queryList();
        } else {
            //save
            for (CommentListBean.Comment comment1 : reply) {
                comment1.is_reply = true;
                comment1.parentId = comment.comment_id;
            }
        }
        return reply;
    }

}
