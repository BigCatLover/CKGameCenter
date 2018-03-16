package com.jingyue.lygame.bean;

import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-11-20 17:02
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
@Table(name = "JsonData",database = AppDataBase.class,allFields = true)
public class JsonDataBean extends BaseAdapterModel{

    public String json;

    @PrimaryKey
    @Column
    public String url;

}
