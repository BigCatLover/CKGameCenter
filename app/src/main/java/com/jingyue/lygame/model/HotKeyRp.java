package com.jingyue.lygame.model;

import com.jingyue.lygame.bean.SearchHotKeyBean;
import com.jingyue.lygame.bean.SearchLocalWordsBean;
import com.jingyue.lygame.model.internal.DBListRepository;
import com.jingyue.lygame.model.internal.DBRepository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by zhanglei on 2017/9/18.
 */
public class HotKeyRp extends DBRepository<SearchHotKeyBean> {
    @Override
    public Type getTClass() {
        return SearchHotKeyBean.class;
    }

    @Override
    public Class<SearchHotKeyBean> getTableClass() {
        return SearchHotKeyBean.class;
    }
}
