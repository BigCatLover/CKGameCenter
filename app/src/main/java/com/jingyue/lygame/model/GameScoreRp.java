package com.jingyue.lygame.model;

import com.jingyue.lygame.bean.GameScoreBean;
import com.jingyue.lygame.model.internal.DBRepository;

import java.lang.reflect.Type;

/**
 * Created by zhanglei on 2017/9/16.
 */
public class GameScoreRp extends DBRepository<GameScoreBean> {
    @Override
    public Type getTClass() {
        return GameScoreBean.class;
    }

    @Override
    public Class<GameScoreBean> getTableClass() {
        return GameScoreBean.class;
    }
}