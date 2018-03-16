package com.jingyue.lygame.clickaction.internal;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;

import com.jingyue.lygame.bean.AppDataBase;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-26 15:40
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
@Table(allFields = true, database = AppDataBase.class, name = "event")
public class BiAction extends BaseAdapterModel{

    public static final String EUID = "euid";

    public static final String EID = "eid";

    public static final String RT = "rt";

    public static final String LOG = "log";

    public int count;

    public String userId;

    public String event;

    @PrimaryKey
    public String eventId;

    @Status
    @ColumnIgnore
    public int status = STATUS_NORNAL;

    @IntDef({
            STATUS_NORNAL,STATUS_UPLOADING
    })
    @interface Status{

    }
    public static final int STATUS_UPLOADING = 0x1;
    public static final int STATUS_NORNAL =0x0;
}
