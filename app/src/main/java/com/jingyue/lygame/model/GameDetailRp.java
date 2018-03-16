package com.jingyue.lygame.model;

import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.GameBean_Table;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.model.internal.DBRepository;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.lang.reflect.Type;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-05 19:44
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class GameDetailRp extends DBRepository<GameBean> {

    @Override
    public Type getTClass() {
        return GameBean.class;
    }

    @Override
    public Class<GameBean> getTableClass() {
        return GameBean.class;
    }

    private int appId;

    public GameDetailRp(int appId) {
        this.appId = appId;
    }

    @Override
    public BaseResponse<GameBean> getCache(String url) throws Exception {
        BaseResponse<GameBean> result = super.getCache(url);
        GameBean bean;
        if (result == null) {
            bean = SQLite.select().from(GameBean.class).where(GameBean_Table.id.is(appId)).querySingle();
            if (bean != null) {
                result = new BaseResponse<GameBean>().setData(bean);
            }
        }
        result.realData.getRelatedGame();
        return result;
    }
}
