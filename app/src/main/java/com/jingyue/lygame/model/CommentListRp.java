package com.jingyue.lygame.model;

import com.jingyue.lygame.bean.CommentListBean;
import com.jingyue.lygame.model.internal.DBListRepository;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by zhanglei on 2017/9/15.
 */
public class CommentListRp extends DBListRepository<CommentListBean> {
    @Override
    public Type getTClass() {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{CommentListBean.class};
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

    @Override
    public Class<CommentListBean> getTableClass() {
        return CommentListBean.class;
    }
}
