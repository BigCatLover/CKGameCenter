package com.jingyue.lygame.bean;

import android.os.Parcel;
import android.os.Parcelable;

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
 * Created by zhanglei on 2017/9/14.
 */
@Table(database = AppDataBase.class, name = "CommentList")
public class CommentListBean extends BaseAdapterModel implements Parcelable {
    /**
     * comment : {"id":5,"app_id":362893,"mem_id":77691,"content":"5555555","is_add":false,"create_time":1503912562,"is_amazing":true,"like_num":"34341",
     * "dislike_num":"34340","phone_info":"HUAWEi","reply_num":"34341","avg_score":"100.00","username":"ceshi123",
     * "avatar":"https://s3dev-test-statics-game.gank.tv/app/newwappc/static/img/misc/user.png","is_like":false,"is_dislike":false,"is_reply":false}
     * add : [{"id":2,"app_id":362893,"mem_id":77691,"content":"123","is_add":true,"create_time":1503912562,
     * "is_amazing":false,"like_num":"12340","phone_info":"HUAWEi","dislike_num":"450","reply_num":"4650",
     * "avg_score":"100.00","username":"ceshi123","avatar":"https://s3dev-test-statics-game.gank.tv/app/newwappc/static/img/misc/user.png",
     * "is_like":false,"is_dislike":false,"is_reply":false}]
     */
    @Column
    @PrimaryKey(autoincrement = true)
    @SerializedName("id")
    public int id = 1;
    @Column
    @ForeignKey(saveForeignKeyModel = true)
    public Comment comment;
    @SerializedName("add")
    public List<Comment> add;

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.LOAD}, variableName = "add")
    public List<Comment> getAdd() {
        //查询
        if (add == null) {
            add = SQLite.select()
                    .from(Comment.class)
                    .where(Comment_Table.parentId.eq(comment.comment_id), Comment_Table.is_add.eq(true), Comment_Table.app_id.eq(comment.app_id))
                    .queryList();
        } else {
            //save
            for (Comment comment1 : add) {
                comment1.parentId = comment.comment_id;
            }
        }

        return add;
    }

    public void setAdd(List<Comment> add) {
        this.add = add;
    }

    @Table(database = AppDataBase.class, allFields = true)
    public static class Comment extends BaseAdapterModel implements Parcelable {
        /**
         * comment_id : 5
         * app_id : 362893
         * mem_id : 77691
         * content : 5555555
         * is_add : false
         * create_time : 1503912562
         * is_amazing : true
         * like_num : 34341
         * dislike_num : 34340
         * phone_info : HUAWEi
         * reply_num : 34341
         * avg_score : 100.00
         * username : ceshi123
         * avatar : https://s3dev-test-statics-game.gank.tv/app/newwappc/static/img/misc/user.png
         * is_like : false
         * is_dislike : false
         * is_reply : false
         */
        public int parentId;

        @Column
        @PrimaryKey
        @SerializedName("comment_id")
        public int comment_id;
        @Column
        @SerializedName("app_id")
        public int app_id;
        @Column
        @SerializedName("mem_id")
        public String mem_id;
        @Column
        @SerializedName("content")
        public String content;
        @Column
        @SerializedName("is_add")
        public boolean is_add;
        @Column
        @SerializedName("is_add2")
        public boolean is_add2;
        @Column
        @SerializedName("create_time")
        public int create_time;
        @Column
        @SerializedName("is_amazing")
        public boolean is_amazing;
        @Column
        @SerializedName("like_num")
        public String like_num;
        @Column
        @SerializedName("dislike_num")
        public String dislike_num;
        @Column
        @SerializedName("phone_info")
        public String phone_info;
        @Column
        @SerializedName("reply_num")
        public String reply_num;
        @Column
        @SerializedName("avg_score")
        public String avg_score;
        @Column
        @SerializedName("username")
        public String username;
        @Column
        @SerializedName("avatar")
        public String avatar;
        @Column
        @SerializedName("is_like")
        public boolean is_like;
        @Column
        @SerializedName("is_dislike")
        public boolean is_dislike;
        @Column
        @SerializedName("is_reply")
        public boolean is_reply;
        @Column
        @ForeignKey(saveForeignKeyModel = true)
        public Honer comment_honer;


        public Comment() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.comment_id);
            dest.writeInt(this.app_id);
            dest.writeString(this.mem_id);
            dest.writeString(this.content);
            dest.writeInt(this.is_add ? 1 : 0);
            dest.writeInt(this.is_add2 ? 1 : 0);
            dest.writeInt(this.create_time);
            dest.writeInt(this.is_amazing ? 1 : 0);
            dest.writeString(this.like_num);
            dest.writeString(this.dislike_num);
            dest.writeString(this.phone_info);
            dest.writeString(this.reply_num);
            dest.writeString(this.avg_score);
            dest.writeString(this.username);
            dest.writeString(this.avatar);
            dest.writeInt(this.is_like ? 1 : 0);
            dest.writeInt(this.is_dislike ? 1 : 0);
            dest.writeInt(this.is_reply ? 1 : 0);
            dest.writeParcelable(this.comment_honer, flags);
        }

        protected Comment(Parcel in) {
            this.comment_id = in.readInt();
            this.app_id = in.readInt();
            this.mem_id = in.readString();
            this.content = in.readString();
            this.is_add = in.readInt() == 1;
            this.is_add2 = in.readInt() == 1;
            this.create_time = in.readInt();
            this.is_amazing = in.readInt() == 1;
            this.like_num = in.readString();
            this.dislike_num = in.readString();
            this.phone_info = in.readString();
            this.reply_num = in.readString();
            this.avg_score = in.readString();
            this.username = in.readString();
            this.avatar = in.readString();
            this.is_like = in.readInt() == 1;
            this.is_dislike = in.readInt() == 1;
            this.is_reply = in.readInt() == 1;
            this.comment_honer = in.readParcelable(Honer.class.getClassLoader());
        }

        public static final Creator<Comment> CREATOR = new Creator<Comment>() {
            @Override
            public Comment createFromParcel(Parcel source) {
                return new Comment(source);
            }

            @Override
            public Comment[] newArray(int size) {
                return new Comment[size];
            }

        };
    }

    @Table(database = AppDataBase.class, allFields = true)
    public static class Honer extends BaseAdapterModel implements Parcelable {
        @Column
        @PrimaryKey
        @SerializedName("level")
        public int level;
        public String icon;
        public String text;

        public Honer() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.level);
            dest.writeString(this.icon);
            dest.writeString(this.text);
        }

        protected Honer(Parcel in) {
            this.level = in.readInt();
            this.icon = in.readString();
            this.text = in.readString();
        }

        public static final Creator<Honer> CREATOR = new Creator<Honer>() {
            @Override
            public Honer createFromParcel(Parcel source) {
                return new Honer(source);
            }

            @Override
            public Honer[] newArray(int size) {
                return new Honer[size];
            }

        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeParcelable(this.comment, flags);
        dest.writeTypedList(this.add);
    }

    protected CommentListBean(Parcel in) {
        this.id = in.readInt();
        this.comment = in.readParcelable(Comment.class.getClassLoader());
        in.readTypedList(this.add, Comment.CREATOR);
    }

    public static final Creator<CommentListBean> CREATOR = new Creator<CommentListBean>() {
        @Override
        public CommentListBean createFromParcel(Parcel source) {
            return new CommentListBean(source);
        }

        @Override
        public CommentListBean[] newArray(int size) {
            return new CommentListBean[size];
        }

    };

    public CommentListBean() {
    }

}
