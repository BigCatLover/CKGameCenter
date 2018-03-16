package com.jingyue.lygame.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.jingyue.lygame.bean.internal.Client;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-05 11:53
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * 登录用户信息
 */
@Table(database = AppDataBase.class, allFields = true)
public class LoginInfoBean extends BaseAdapterModel implements Cloneable, Serializable {
    /**
     * 4   id	        用户id	是	[string]	查看
     * 5	 username	用户名	是	[string]	查看
     * 6	 status	    用户状态	是	[string]	查看
     * 7	 icon	    用户头像	是	[string]	查看
     * 8	 identifier	app验证用	是	[string]	查看
     * 9	 accesstoken	app验证用	是	[string]	查看
     * 10	 expaire_time	app验证用	是	[string]
     * <p>
     * id : 77859
     * username : alex123
     * status : 2
     * icon : https://s3-test-statics-game.gank.tv/app/newwappc/static/img/misc/user.png
     * identifier : 77859597712994699b
     * accesstoken : fZ2p3U2uds8vfA7O9xbR5R4h7Ndvd17ddQbk5l0k7X8i0f7w3y5N4kcHbp3g0bf9
     * expaire_time : 1504772280
     */
    @PrimaryKey
    @NotNull
    @SerializedName("id")
    public String id;
    @SerializedName("username")
    public String username;
    @SerializedName("status")
    public String status;
    @SerializedName("icon")
    public String icon;
    @SerializedName("identifier")
    public String identifier;
    @SerializedName("accesstoken")
    public String accesstoken;
    @SerializedName("expaire_time")
    public int expaireTime;
    @SerializedName("is_guided")
    public boolean isGuided;

    /**
     * reg_time : 1506060641
     * add_score : 0
     * love_types : 146,152,137
     * ip : 58.246.3.50
     */
    @SerializedName("reg_time")
    public String regTime;
    @SerializedName("add_score")
    public int addScore;
    @SerializedName("love_types")
    public String loveTypes;
    @SerializedName("ip")
    public String ip;

    public boolean isScoreValid() {
        return addScore > 0;
    }

    /**
     * 标志用户是否已登录
     */
    @Client
    public boolean isLogin;
    /**
     * 登录时间
     */
    @Client
    public long loginTime;
    @Client
    public String h5Token;

    /**
     * avatar : https://s3-test-statics-game.gank.tv/upload/shopImg/59a7b2cc4
     * score : 2360.00
     * comment_honer : {"level":2,"text":"游戏拓荒者","icon":"aaaa.png"}
     */
    @SerializedName("avatar")
    public String avatar;
    @SerializedName("score")
    public String score;
    @SerializedName("comment_honer")
    @ForeignKey(saveForeignKeyModel = true)
    public CommentHonerBean commentHoner;

    @Table(database = AppDataBase.class, allFields = true)
    public static class CommentHonerBean implements Serializable, Cloneable {
        /**
         * level : 2
         * text : 游戏拓荒者
         * icon : aaaa.png
         */
        @PrimaryKey
        @SerializedName("level")
        public int level;
        @SerializedName("text")
        public String text;
        @SerializedName("icon")
        public String iconX;

        @Override
        protected Object clone() throws CloneNotSupportedException {
            CommentHonerBean bean = new CommentHonerBean();
            bean.iconX = iconX;
            bean.level = level;
            bean.text = text;
            return bean;
        }

        @Override
        public String toString() {
            return "CommentHonerBean{" +
                    "level=" + level +
                    ", text='" + text + '\'' +
                    ", iconX='" + iconX + '\'' +
                    '}';
        }
    }

    /**
     * 深拷贝
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        ObjectOutputStream oo = null;
        ObjectInputStream oi = null;
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            oo = new ObjectOutputStream(bo);
            oo.writeObject(this);
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            oi = new ObjectInputStream(bi);
            return oi.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oo != null) {
                    oo.close();
                }
                if (oi != null) {
                    oi.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.clone();
    }

    @Override
    public String toString() {
        return "LoginInfoBean{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", commentHoner=" + commentHoner +
                '}';
    }

    @Override
    public boolean save() {
        if (TextUtils.isEmpty(id)) {
            LogUtils.e(">>>>>>invalid user info,cancel save!");
            return false;
        }
        return super.save();
    }
}
