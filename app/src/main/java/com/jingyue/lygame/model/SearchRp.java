package com.jingyue.lygame.model;

import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.model.internal.DBListRepository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by zhanglei on 2017/9/18.
 */
public class SearchRp extends DBListRepository<GameBean> {
    @Override
    public Type getTClass() {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{GameBean.class};
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
    public Class<GameBean> getTableClass() {
        return GameBean.class;
    }

    @Override
    public Observable<BaseResponse<List<GameBean>>> getEntryFromNet(String url, Map<String, String> queryMap, boolean needCache) {
        return super.getEntryFromNet(url, queryMap, needCache);
    }
}
