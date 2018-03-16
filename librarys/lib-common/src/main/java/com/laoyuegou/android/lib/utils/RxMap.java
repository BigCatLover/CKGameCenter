package com.laoyuegou.android.lib.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-23 16:00
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class RxMap<T,R>{

    Map<T,R> map;

    public static RxMap<String,String> newInstance(){
        return new RxMap<>();
    }

    public RxMap() {
        this.map = new HashMap<>();
    }

    public RxMap(Map<T,R> map) {
        this.map = map;
    }

    public RxMap<T,R> put(T t, R r){
        map.put(t,r);
        return this;
    }

    public Map<T,R> build(){
        return map;
    }
}
