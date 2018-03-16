package com.jingyue.lygame.model;

import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.GameBean_Tag;
import com.jingyue.lygame.bean.GameBean_Tag_Table;
import com.jingyue.lygame.bean.TagBusinessViewBean;
import com.jingyue.lygame.bean.Tag_Table;
import com.jingyue.lygame.model.internal.DBRepository;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-18 20:44
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class TagRp extends DBRepository<TagBusinessViewBean> {

    @Override
    public Type getTClass() {
        return TagBusinessViewBean.class;
    }

    @Override
    public Class<TagBusinessViewBean> getTableClass() {
        return TagBusinessViewBean.class;
    }

    /**
     * do this in the
     *
     * @param appId
     */
    @WorkerThread
    public List<GameBean.Tag> loadTag(int appId) {
        return SQLite.select().from(GameBean.Tag.class)
                .leftOuterJoin(GameBean_Tag.class)
                .on(GameBean_Tag_Table.tag_id.eq(Tag_Table.id))
                .where(GameBean_Tag_Table.gameBean_id.is(appId))
                .queryList();
    }

    /**
     * 删除对应游戏先关的tag
     *
     * @param appId
     */
    public void clearCache(String appId) {
        try {
            if (!TextUtils.isEmpty(appId)) {
                SQLite.delete().from(GameBean_Tag.class)
                        .where(GameBean_Tag_Table.gameBean_id.is(Integer.valueOf(appId)))
                        .execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
