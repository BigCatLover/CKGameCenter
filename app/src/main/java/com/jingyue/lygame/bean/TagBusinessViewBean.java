package com.jingyue.lygame.bean;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.jingyue.lygame.bean.typeconvert.StringTypeConverter;
import com.laoyuegou.android.lib.utils.GsonUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.converter.TypeConverter;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-12 11:41
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
@Table(database = AppDataBase.class, allFields = true)
public class TagBusinessViewBean extends BaseAdapterModel {
    /**
     * game : {"id":"362893","type":"92,91,84,109","name":"御灵录"}
     * default : [{"id":"84","name":"二次元","is_selected":0},{"id":"91","name":"剧情","is_selected":0},{"id":"92","name":"养成","is_selected":0},{"id":"109","name":"动漫","is_selected":0}]
     * user : [{"id":"10","name":"","is_selected":0},{"id":"11","name":"123123","is_selected":0},{"id":"13","name":"123123123","is_selected":0},{"id":"12","name":"12312312321","is_selected":0},{"id":"14","name":"12323","is_selected":0},{"id":"9","name":"erere","is_selected":0},{"id":"5","name":"qqqq","is_selected":0},{"id":"15","name":"sdfsdf","is_selected":0},{"id":"16","name":"sdfsfsfd","is_selected":0}]
     * tags : ["二次元","剧情","养成","动漫","","123123","123123123","12312312321","12323","erere","qqqq","sdfsdf","sdfsfsfd"]
     */
    @PrimaryKey(autoincrement = true)
    public int id;

    @SerializedName("game")
    @ColumnIgnore
    @Column(typeConverter = MyTypeConvert.class)
    public GameBean game;
    @SerializedName("default")
    @ColumnIgnore
    public List<com.jingyue.lygame.bean.GameBean.Tag> defaultX;
    @SerializedName("user")
    @ColumnIgnore
    public List<com.jingyue.lygame.bean.GameBean.Tag> user;
    @SerializedName("tags")
    @Column(typeConverter = StringTypeConverter.class)
    public List<String> tags;

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.LOAD}, variableName = "relatedGame")
    public List<GameBean.Tag> getRelatedGame() {
        if (user == null ) {
            user = SQLite.select().from(GameBean.Tag.class)
                    .leftOuterJoin(GameBean_Tag.class)
                    .on(Tag_Table.id.eq(GameBean_Tag_Table.tag_id))
                    .where(Tag_Table.isUserCustom.is(true),GameBean_Tag_Table.gameBean_id.eq(game.id))
                    .queryList();
        } else {//when save
            GameBean_Tag gameBeanTag = null;
            for (GameBean.Tag tag : user) {
                gameBeanTag = new GameBean_Tag();
                gameBeanTag.setGameBean(game);
                gameBeanTag.setTag(tag);
                gameBeanTag.save();
                tag.isUserCustom = true;
                tag.save();
                //tag.gameId = game.id;
            }
        }
        return user;
    }

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.LOAD}, variableName = "defaultX")
    public List<GameBean.Tag> getDefaultX() {
        if (defaultX == null) {
            defaultX = SQLite.select().from(GameBean.Tag.class)
                    .leftOuterJoin(GameBean_Tag.class)
                    .on(Tag_Table.id.eq(GameBean_Tag_Table.tag_id))
                    .where(Tag_Table.isUserCustom.is(false),GameBean_Tag_Table.gameBean_id.eq(game.id))
                    .queryList();
        } else {//when save
            GameBean_Tag gameBeanTag = null;
            for (GameBean.Tag tag : defaultX) {
                gameBeanTag = new GameBean_Tag();
                tag.setIsDefault(true);
                gameBeanTag.setGameBean(game);
                gameBeanTag.setTag(tag);
                tag.save();
            }
        }
        return defaultX;
    }

    public static class MyTypeConvert extends TypeConverter<String, GameBean> {
        @Override
        public String getDBValue(GameBean model) {
            return GsonUtils.getInstance().toJson(model);
        }

        @Override
        public GameBean getModelValue(String data) {
            return GsonUtils.getInstance().fromJson(data, GameBean.class);
        }
    }

}
