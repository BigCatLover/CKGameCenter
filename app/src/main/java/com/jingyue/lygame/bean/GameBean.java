package com.jingyue.lygame.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.jingyue.lygame.bean.internal.BaseItem;
import com.jingyue.lygame.bean.internal.Client;
import com.jingyue.lygame.bean.internal.IgnorePesist;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.modules.rcmd.adapter.GameVH;
import com.jingyue.lygame.modules.rcmd.adapter.TagGameVH;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ManyToMany;
import com.raizlabs.android.dbflow.annotation.MultipleManyToMany;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-04 15:23
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * <p>
 * 游戏详情页
 */
@Table(database = AppDataBase.class, name = "GameInfo")
@MultipleManyToMany({@ManyToMany(referencedTable = GameBean.Tag.class, generateAutoIncrement = false),
        @ManyToMany(referencedTable = GameBean.class, generateAutoIncrement = false)})
public class GameBean extends BaseAdapterModel implements Parcelable {

    /**
     * relatedGame : [{"icon":"https://s3-test-statics-game.gank.tv/upload/20170519/591e8ee975ec0.png","name":"永恒纪元","id":362635,"avg_score":"0.0"},{"icon":"https://s3-test-statics-game.gank.tv/upload/20170519/591e9b04f11c9.png","name":"神之荣耀","id":362644,"avg_score":"0.0"},{"icon":"https://s3-test-statics-game.gank.tv/upload/20170519/591ec36ba0e1a.png","name":"火影忍者","id":362677,"avg_score":"0.0"},{"icon":"https://s3-test-statics-game.gank.tv/upload/20170519/591ec443668bb.png","name":"御龙在天手游","id":362678,"avg_score":"0.0"},{"icon":"https://s3-test-statics-game.gank.tv/upload/20170524/5924f84ec6be7.png","name":"大主宰-新版","id":362679,"avg_score":"0.0"}]
     * down_cnt : 142344
     * down_cnt_rate1 : 142335
     * status_ext : 2
     * status : 5
     * avg_score : 8.7
     */

    @SerializedName("down_cnt_rate1")
    public String downCntRate1;
    @SerializedName("status_ext")
    public String statusExt;
    @SerializedName("status")
    public String status;

    /**
     * relatedGame : [{"icon":"https://s3-test-statics-game.gank.tv/upload/20170519/591e9e3ed84a0.png","name":"妖怪百姬","id":362889,"avg_score":0},{"icon":"https://s3-test-statics-game.gank.tv/upload/20170519/591e9ea524511.png","name":"御灵录","id":362893,"avg_score":100}]
     * avg_score : 100
     * comment_num : 89
     */
    public GameBean() {
        super(AppConstants.ItemType.GAME_DETAIL);
    }

    public GameBean(int itemType) {
        super(itemType);
    }

    @Override
    public int getLayout() {
        switch (itemType) {
            case AppConstants.ItemType.USERLIKE_GAME_DETAIL:
            case AppConstants.ItemType.SEARCH_GAME_LIST:
            case AppConstants.ItemType.GAME_TAG_LIST:
                return R.layout.rcmd_view_tag_list_item;
            case AppConstants.ItemType.GAME_DETAIL:
            default:
                return R.layout.rcmd_view_relate_game_item;

        }
    }

    @Override
    public BaseItem.BVH onCreateViewHolder(View parent, int viewType) {
        switch (viewType) {
            case AppConstants.ItemType.GAME_TAG_LIST:
                return new TagGameVH(parent,viewType);
            case AppConstants.ItemType.USERLIKE_GAME_DETAIL:
                return new TagGameVH(parent,viewType);
            case AppConstants.ItemType.SEARCH_GAME_LIST:
                return new TagGameVH(parent,viewType);
            case AppConstants.ItemType.GAME_DETAIL:
            default:
                return new GameVH(parent);
        }
    }

    /**
     * relatedGame : [{"icon":"https://s3-test-statics-game.gank.tv","name":"yck游戏测试1","id":362948,"avg_score":0}]
     * down_cnt : 188
     * version : 6.0.6
     * tags : []
     * id : 362948
     * name : yck游戏测试1
     * company : 腾讯
     * type : 67,68
     * icon : https://s3-test-statics-game.gank.tv
     * size : 1.87 MB
     * score :
     * video_id :
     * video_img :
     * avg_score : 0
     * lang : 中文
     * adxt :
     * game_tag :
     * androidurl : https://env-test-down-game.gank.tv/sdkgame/yckyxcs1and_362948/yckyxcs1and_362948.apk
     * publicity : 哈哈哈哈
     * description : 哈哈哈哈
     * upinfo :
     */
    @Column
    @SerializedName("comment_num")
    public String commentNum;
    @Column
    @PrimaryKey
    @SerializedName("id")
    public int id;
    @SerializedName("is_like")
    public boolean isLike;
    @Column
    @SerializedName("down_cnt")
    public String downCnt;
    @Column
    @SerializedName("version")
    public String version;
    @Column
    @SerializedName("name")
    public String name;
    @Column
    @SerializedName("company")
    public String company;
    @Column
    @SerializedName("type")
    public String type;
    @Column
    @SerializedName("icon")
    public String icon;
    @Column
    @SerializedName("size")
    public String size;
    @Column
    @SerializedName("score")
    public String score;
    @Column
    @SerializedName("video_id")
    public String videoId;
    @Column
    @SerializedName("video_img")
    public String videoImg;
    @Column
    @SerializedName("avg_score")
    public float avgScore;
    @Column
    @SerializedName("lang")
    public String lang;
    @Column
    @SerializedName("adxt")
    public String adxt;
    @Column
    @SerializedName("game_tag")
    public String gameTag;
    @Column
    @SerializedName("androidurl")
    public String androidurl;
    @Column
    @SerializedName("publicity")
    public String publicity;
    @Column
    @SerializedName("description")
    public String description;
    @Column
    @SerializedName("upinfo")
    public String upinfo;
    @SerializedName("relatedGame")
    public List<GameBean> relatedGame;
    @SerializedName("tags")
    public List<Tag> tags;

    @SerializedName("tag")
    @ColumnIgnore
    public List<String> tag;

    @Column
    @SerializedName("sub_cnt")
    public String subCnt;
    /**
     * 1 可下载 2 可预约 3 已经预约
     */
    @Column
    @SerializedName("sub_status")
    public int subStatus;
    /**
     * 是否为联合运营
     */
    @Column
    @SerializedName("is_transport")
    public boolean isTransport;
    /**
     * 是否为活动
     */
    @Column
    @SerializedName("is_active")
    public boolean isActive;

    /**
     * 包名用来安装和启动
     */
    @Column
    @SerializedName("packagename")
    public String packagename;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.commentNum);
        dest.writeInt(this.id);
        dest.writeInt(this.isLike?1:0);
        dest.writeString(this.downCnt);
        dest.writeString(this.version);
        dest.writeString(this.name);
        dest.writeString(this.company);
        dest.writeString(this.type);
        dest.writeString(this.icon);
        dest.writeString(this.size);
        dest.writeString(this.score);
        dest.writeString(this.videoId);
        dest.writeString(this.videoImg);
        dest.writeFloat(this.avgScore);
        dest.writeString(this.lang);
        dest.writeString(this.adxt);
        dest.writeString(this.gameTag);
        dest.writeString(this.androidurl);
        dest.writeString(this.publicity);
        dest.writeString(this.description);
        dest.writeString(this.upinfo);
        dest.writeTypedList(this.relatedGame);
        dest.writeTypedList(this.tags);
        dest.writeList(this.tag);
        dest.writeString(this.subCnt);
        dest.writeInt(this.subStatus);
        dest.writeInt(this.isTransport?1:0);
        dest.writeInt(this.isActive?1:0);
        dest.writeString(this.packagename);
    }
    protected GameBean(Parcel in) {
        this.commentNum = in.readString();
        this.id = in.readInt();
        this.isLike = in.readInt()==1;
        this.downCnt = in.readString();
        this.version = in.readString();
        this.name = in.readString();
        this.company = in.readString();
        this.type = in.readString();
        this.icon = in.readString();
        this.size = in.readString();
        this.score = in.readString();
        this.videoId = in.readString();
        this.videoImg = in.readString();
        this.avgScore = in.readFloat();
        this.lang = in.readString();
        this.adxt = in.readString();
        this.gameTag = in.readString();
        this.androidurl = in.readString();
        this.publicity = in.readString();
        this.description = in.readString();
        this.upinfo = in.readString();
        if(this.relatedGame == null){
            this.relatedGame = new ArrayList<>();
        }
        in.readTypedList(this.relatedGame, GameBean.CREATOR);
        if(this.tags == null){
            this.tags = new ArrayList<>();
        }
        in.readTypedList(this.tags, Tag.CREATOR);
        if(this.tag == null){
            this.tag = new ArrayList<>();
        }
        in.readStringList(this.tag);
        this.subCnt = in.readString();
        this.subStatus = in.readInt();
        this.isTransport = in.readInt()==1;
        this.isActive = in.readInt()==1;
        this.packagename = in.readString();
    }

    public static final Parcelable.Creator<GameBean> CREATOR = new Parcelable.Creator<GameBean>() {
        @Override
        public GameBean createFromParcel(Parcel source) {
            return new GameBean(source);
        }

        @Override
        public GameBean[] newArray(int size) {
            return new GameBean[size];
        }

    };

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.LOAD}, variableName = "tags")
    public List<Tag> getTags() {
        if (tags == null ) {
            tags = SQLite.select().
                    from(Tag.class)
                    .leftOuterJoin(GameBean_Tag.class)
                    .on(GameBean_Tag_Table.tag_id.is(Tag_Table.id))
                    .where(GameBean_Tag_Table.gameBean_id.is(id))
                    .queryList();

        } else {
            GameBean_Tag gameBeanTag;
            for (Tag tag : tags) {
                tag.setIsDefault(true);
                gameBeanTag = new GameBean_Tag();
                gameBeanTag.setTag(tag);
                gameBeanTag.setGameBean(this);
                gameBeanTag.save();
                tag.save();
            }
        }
        return tags;
    }

    /**
     * relatedGame : [{"icon":"https://s3-test-statics-game.gank.tv","name":"yck游戏测试1","id":362948,"avg_score":0}]
     *
     * @return
     */
    @OneToMany(methods = {OneToMany.Method.SAVE}, variableName = "relatedGame")
    public List<GameBean> getRelatedGame() {
        LogUtils.e("getRelatedGame");
        if (relatedGame == null) {
            relatedGame = SQLite.select(GameBean_Table.id, GameBean_Table.name, GameBean_Table.avgScore, GameBean_Table.icon).from(GameBean.class)
                    .leftOuterJoin(GameBean_GameBean.class)
                    .on(GameBean_Table.id.is(GameBean_GameBean_Table.gameBean1_id))
                    .where(GameBean_GameBean_Table.gameBean0_id.is(id))
                    .queryList();
        } else {//when save
            GameBean_GameBean beanGameBean;
            for (GameBean simpleGameInfoBean : relatedGame) {
                beanGameBean = new GameBean_GameBean();
                beanGameBean.setGameBean0(this);
                beanGameBean.setGameBean1(simpleGameInfoBean);
                beanGameBean.save();
            }
        }
        return relatedGame;
    }

    @Override
    public String toString() {
        return "GameBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj instanceof GameBean) {
            if (id == 0 || ((GameBean) obj).id == 0) return false;
            if (id == ((GameBean) obj).id) return true;
        }
        return false;
    }


    /**
     * 游戏标签表
     * 与游戏是 n....n关系
     */
    @Table(database = AppDataBase.class, name = "tag", allFields = true)
    public static class Tag extends BaseAdapterModel implements Parcelable, Comparable<Tag> {

        @Override
        public int compareTo(@NonNull Tag o) {
            return isSelected > o.isSelected ?
                    (isUserCustom ? -2 : -1 ) : (isSelected == o.isSelected ?
                    (isUserCustom ? -1 : 0  ) : 1);
        }

        /**
         * 详情页 + 按钮
         */
        public static class AddTag extends Tag {
            public String gameName;
        }

        @SerializedName("name")
        public String name;
        @PrimaryKey
        @SerializedName("id")
        public String id;
        @SerializedName("is_selected")
        public int isSelected;
        @SerializedName("is_default")
        public int isDefault;

        public boolean isDefault() {
            return isDefault == 1;
        }

        public void setIsDefault(boolean isDefault) {
            this.isDefault = isDefault ? 1 : 0;
        }

        public void setIsSelected(boolean selected) {
            isSelected = selected ? 1 : 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj == this) return true;
            if (obj instanceof Tag) {
                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(((Tag) obj).id)) return false;
                if (id.equals(((Tag) obj).id)) return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Tag{" +
                    "name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    "， isSelected " + isSelected() +
                    ",  isDefault " + isDefault() +
                    '}';
        }

        /**
         * 用于表示是否用户添加字段
         */
        @Client
        public boolean isUserCustom;

        public boolean isSelected() {
            return isSelected == 1;
        }

        public Tag() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.name);
            dest.writeInt(this.isSelected);
            dest.writeInt(this.isDefault);
        }
        protected Tag(Parcel in) {
            this.id = in.readString();
            this.name = in.readString();
            this.isSelected = in.readInt();
            this.isDefault = in.readInt();
        }

        public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
            @Override
            public Tag createFromParcel(Parcel source) {
                return new Tag(source);
            }

            @Override
            public Tag[] newArray(int size) {
                return new Tag[size];
            }

        };
    }


    @IgnorePesist
    public static class TagList extends BaseAdapterModel {

        /**
         * list : [{"id":"100","type":"67,68,69","icon":"https://s3-test-statics-game.gank.tv/upload/20170415/58f19cd24a0cb.png","name":"APP","avg_score":null,"tags":[{"id":"67","name":"动作","is_default":1},{"id":"68","name":"三国","is_default":1},{"id":"69","name":"休闲","is_default":1}]},{"id":"362788","type":"72,77,67","icon":"https://s3-test-statics-game.gank.tv/upload/20170519/591eb37e40da8.png","name":"孤狼","avg_score":"0.00","tags":[{"id":"72","name":"角色","is_default":1},{"id":"77","name":"射击","is_default":1},{"id":"67","name":"动作","is_default":1}]},{"id":"362948","type":"67,68","icon":"https://s3-test-statics-game.gank.tv","name":"yck游戏测试1","avg_score":"0.00","tags":[{"id":"67","name":"动作","is_default":1},{"id":"68","name":"三国","is_default":1}]},{"id":"362950","type":"67,68,69,72","icon":"https://s3-test-statics-game.gank.tv/upload/20170804/59840f52c8d35.png","name":"胡莱三国2","avg_score":"0.00","tags":[{"id":"67","name":"动作","is_default":1},{"id":"68","name":"三国","is_default":1},{"id":"69","name":"休闲","is_default":1},{"id":"72","name":"角色","is_default":1}]}]
         * tag : {"id":"67","name":"动作"}
         */
        @SerializedName("tag")
        @ForeignKey
        public Tag tag;
        @SerializedName("list")
        public List<GameBean> list;
    }

}
