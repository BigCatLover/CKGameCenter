package com.jingyue.lygame.model;

import com.jingyue.lygame.bean.CommentCollectBean;
import com.jingyue.lygame.model.internal.IDBFLowJsonRespsitory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by zhanglei on 2017/9/17.
 */
public class CommentCollectRp extends IDBFLowJsonRespsitory<CommentCollectBean> {
    @Override
    public Type getTClass() {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{CommentCollectBean.class};
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
