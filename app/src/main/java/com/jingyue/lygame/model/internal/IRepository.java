package com.jingyue.lygame.model.internal;

import java.lang.reflect.Type;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-23 17:49
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 *
 *  主要用于二级页面缓存
 *
 */
public interface IRepository<T> {

    /**
     * 用于gson解析
     * data的类型
     * @return
     */
    Type getTClass();

    /**
     * 获取数据
     * @param url baseurl + {url = url前缀 + 业务url}
     * @param queryMap 参数
     * @param needCache  如果网络加载是否需要缓存
     * @param forceRefresh 强制从网络获取
     * @return
     */
    Observable<T> getEntry(final String url, Map<String, String> queryMap, final boolean needCache, boolean forceRefresh);

    Observable<T> getEntry(final String url, Map<String, String> queryMap);

    T getCache(String url) throws Exception;

    Observable<T> getEntryFromNet(String url, Map<String, String> queryMap, boolean needCache);

    void saveCache(String url, T baseBeanList);

    String getCacheKey(String url, Map<String, String> queryMap);

    void clearCache();

}
