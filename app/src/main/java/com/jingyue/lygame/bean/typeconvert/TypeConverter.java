package com.jingyue.lygame.bean.typeconvert;

import com.laoyuegou.android.lib.utils.GsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-04 16:29
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class TypeConverter<T> extends com.raizlabs.android.dbflow.converter.TypeConverter<String, List<T>> {

    private Class<T> type;

    public TypeConverter(Class<T> type) {
        this.type = type;
    }

    @Override
    public String getDBValue(List<T> models) {
        StringBuilder sb = new StringBuilder();
        for (T model : models) {
            sb.append("&&");
            sb.append(GsonUtils.getInstance().toJson(model));
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    @Override
    public List<T> getModelValue(String data) {
        String[] datas = data.split("&&");
        if (datas == null || (datas != null && datas.length == 0))
            return null;
        List<T> dataList = new ArrayList<>();
        for (String dataStr : datas) {
            dataList.add(GsonUtils.getInstance().fromJson(dataStr,type));
        }
        return dataList;
    }
}
