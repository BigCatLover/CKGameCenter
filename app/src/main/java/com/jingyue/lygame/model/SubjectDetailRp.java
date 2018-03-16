package com.jingyue.lygame.model;

import com.jingyue.lygame.bean.SubjectDetailBean;
import com.jingyue.lygame.model.internal.DBRepository;

import java.lang.reflect.Type;

/**
 * Created by zhanglei on 2017/9/21.
 */
public class SubjectDetailRp extends DBRepository<SubjectDetailBean> {
    @Override
    public Type getTClass() {
        return SubjectDetailBean.class;
    }

    @Override
    public Class<SubjectDetailBean> getTableClass() {
        return SubjectDetailBean.class;
    }
}
