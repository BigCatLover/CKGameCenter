package com.jingyue.lygame.bean.typeconvert;

import java.util.ArrayList;
import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-04 16:29
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class StringTypeConverter extends com.raizlabs.android.dbflow.converter.TypeConverter<String, List<String>> {

    public static final String SPLITE = "&&";

    @Override
    public String getDBValue(List<String> models) {
        StringBuilder sb = new StringBuilder();
        for (int i =0;i< models.size();i++) {
            if(i>0){
                sb.append(SPLITE);
            }
            sb.append(models.get(i));
        }
        return sb.toString();
    }

    @Override
    public List<String> getModelValue(String data) {
        String[] datas = data.split("&&");
        if (datas == null || (datas != null && datas.length == 0))
            return null;
        List<String> dataList = new ArrayList<>();
        for (String dataStr : datas) {
            dataList.add(dataStr);
        }
        return dataList;
    }
}
