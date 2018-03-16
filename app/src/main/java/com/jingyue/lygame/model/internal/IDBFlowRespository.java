package com.jingyue.lygame.model.internal;

import android.text.TextUtils;

import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.model.LoginManager;
import com.laoyuegou.android.lib.utils.RxMap;
import com.raizlabs.android.dbflow.sql.language.property.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-30 18:42
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public abstract class IDBFlowRespository<BeanContainer, DBBean> implements IRepository<BaseResponse<BeanContainer>> {

    public IDBFlowRespository(){
        DBRpManager.getInstance();
    }

    protected Property<String> property = new Property<>(getTableClass(), BaseAdapterModel.KEY);

    /**
     * 获取model实例的表class
     * instance of table
     *
     * @return
     */
    public abstract Class<DBBean> getTableClass();

    public Map<String, String> getHeader() {
        String h5Token = LoginManager.getInstance().getHsToken();
        if (TextUtils.isEmpty(h5Token)) {
            h5Token = "";
        }
        return new RxMap<String, String>()
                .put(KeyConstant.KEY_TOKEN, h5Token)
                .build();
    }
}
