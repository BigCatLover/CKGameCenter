package com.jingyue.lygame.model;

import com.jingyue.lygame.bean.CommentDetialBean;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.model.internal.DBRepository;
import java.lang.reflect.Type;

/**
 * Created by zhanglei on 2017/9/16.
 */
public class CommentDetailRp extends DBRepository<CommentDetialBean> {
    @Override
    public Type getTClass() {
        return CommentDetialBean.class;
    }

    @Override
    public Class<CommentDetialBean> getTableClass() {
        return CommentDetialBean.class;
    }
}
