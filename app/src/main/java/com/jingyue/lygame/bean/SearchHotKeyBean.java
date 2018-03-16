package com.jingyue.lygame.bean;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.jingyue.lygame.bean.typeconvert.StringTypeConverter;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Created by zhanglei on 2017/9/18.
 */
@Table(database = AppDataBase.class, name = "HotKey")
public class SearchHotKeyBean extends BaseAdapterModel {
    @Column
    @PrimaryKey(autoincrement = true)
    @SerializedName("id")
    public int id;

    @SerializedName("games")
    @Column(typeConverter = StringTypeConverter.class)
    public List<String> games;

    @SerializedName("tags")
    public List<HotKeyTag> tags;

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.LOAD}, variableName = "tags")
    public List<HotKeyTag> getTags() {
        //查询
        if (tags == null) {
            tags = SQLite.select()
                    .from(HotKeyTag.class)
                    .where(HotKeyTag_Table.parentId.eq(id))
                    .queryList();
        } else {
            //save
            for (HotKeyTag tag : tags) {
                tag.parentId = id;
            }
        }

        return tags;

    }

    @Table(database = AppDataBase.class, allFields = true)
    public static class HotKeyTag extends BaseAdapterModel{
        public int parentId;
        @SerializedName("name")
        public String name;
        @PrimaryKey
        @SerializedName("id")
        public String id;
    }
}
