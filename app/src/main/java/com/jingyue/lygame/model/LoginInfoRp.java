package com.jingyue.lygame.model;

import com.jingyue.lygame.bean.LoginInfoBean;
import com.jingyue.lygame.model.internal.DBRepository;

import java.lang.reflect.Type;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-05 11:57
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class LoginInfoRp extends DBRepository<LoginInfoBean> {
    @Override
    public Type getTClass() {
        return LoginInfoBean.class;
    }

    @Override
    public Class<LoginInfoBean> getTableClass() {
        return LoginInfoBean.class;
    }
}
