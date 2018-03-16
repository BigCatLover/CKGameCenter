package com.jingyue.lygame.model;

import com.jingyue.lygame.bean.RcmdGameListBean;
import com.jingyue.lygame.model.internal.IDBFLowJsonRespsitory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by zhanglei on 2017/9/7.
 */

public class RcmdGameRp extends IDBFLowJsonRespsitory<RcmdGameListBean> {
    @Override
    public Type getTClass() {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{RcmdGameListBean.class};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

}
