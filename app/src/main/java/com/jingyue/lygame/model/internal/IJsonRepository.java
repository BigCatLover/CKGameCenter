package com.jingyue.lygame.model.internal;

import com.jingyue.lygame.bean.internal.BaseResponse;

import java.lang.reflect.Type;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-11-20 18:33
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * <p>
 * 首页数据 和 详情页数据的缓存逻辑是完全不同的
 * <p>
 * 首页数据多半和url相关
 * 详情页数据一般和id相关
 * <p>
 * 而首页数据的首次加载逻辑 ：
 * 有网络时 一般需要先显示缓存在显示网络加载数据
 * 无网络时 一般需要显示缓存数据
 * <p>
 * 所以首页以缓存Url对应的json为主
 * <p>
 * 详情页一般需要显示实时数据。
 */
public interface IJsonRepository<T> {

    Observable<String> getEntryFromNet(String url, Map<String, String> queryMap);

    Observable<T> getEntry(String url, Map<String, String> params, boolean needCache, boolean forceRefresh);

    String getCacheKey(String url, Map<String, String> params);

    void saveCache(String key, String json);

    String getCache(String url) throws Exception;

    Type getTClass();

    void clearCache();
}
