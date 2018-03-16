package com.jingyue.lygame.model;

import com.jingyue.lygame.bean.ImageCodeBean;
import com.jingyue.lygame.model.internal.DBRepository;

import java.lang.reflect.Type;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-05 16:43
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class ImageCodeRp extends DBRepository<ImageCodeBean>{

    @Override
    public Type getTClass() {
        return ImageCodeBean.class;
    }

    @Override
    public Class<ImageCodeBean> getTableClass() {
        return ImageCodeBean.class;
    }
}
