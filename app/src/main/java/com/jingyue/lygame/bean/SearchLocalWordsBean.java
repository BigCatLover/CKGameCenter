package com.jingyue.lygame.bean;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.jingyue.lygame.bean.typeconvert.StringTypeConverter;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;

/**
 * Created by zhanglei on 2017/9/18.
 */

@Table(database = AppDataBase.class, name = "LocalSearchWords",allFields = true)
public class SearchLocalWordsBean extends BaseAdapterModel {
    @Column
    @PrimaryKey(autoincrement = true)
    @SerializedName("id")
    public int id;
    @SerializedName("company_name")
    @Column(typeConverter = StringTypeConverter.class)
    public List<String> companyName;
    @SerializedName("keywords_name")
    @Column(typeConverter = StringTypeConverter.class)
    public List<String> keywordsName;
    @SerializedName("game_name")
    @Column(typeConverter = StringTypeConverter.class)
    public List<String> gameName;
    @SerializedName("type_name")
    @Column(typeConverter = StringTypeConverter.class)
    public List<String> typeName;
}
