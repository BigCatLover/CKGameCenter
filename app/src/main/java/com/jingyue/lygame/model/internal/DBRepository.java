package com.jingyue.lygame.model.internal;

import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.interfaces.ApiService;
import com.laoyuegou.android.lib.utils.GsonUtils;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.lygame.libadapter.HttpRequestFactory;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.Operator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-30 18:37
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * {
 * msg   : "",
 * code  : "",
 * data  : {
 * count : 2,
 * <p>
 * ...
 * }
 * }
 */
public abstract class DBRepository<BeanContainer extends BaseAdapterModel> extends IDBFlowRespository<BeanContainer, BeanContainer> {

    public static final boolean DEBUG = BuildConfig.DEBUG;

    public String getCacheKey(String url, Map<String, String> queryMap) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (queryMap != null && !queryMap.isEmpty()) {
            Set<String> keys = queryMap.keySet();
            sb.append("?");
            for (String key : keys) {
                sb.append(key).append("=").append(queryMap.get(key));
            }
        }
        return sb.toString();
    }

    @Override
    public BaseResponse<BeanContainer> getCache(String url) throws Exception {
        BeanContainer beanList = SQLite.select().from(getTableClass())
                .where(property.eq(url))
                .querySingle();
        if (beanList != null) {
            return new BaseResponse<BeanContainer>().setData(beanList);
        }
        return null;
    }

    @Override
    public Observable<BaseResponse<BeanContainer>> getEntry(final String url, Map<String, String> queryMap, final boolean needCache, boolean forceRefresh) {

        final String key = getCacheKey(url, queryMap);

        //get cache
        Observable<BaseResponse<BeanContainer>> fromCache = Observable.create(new ObservableOnSubscribe<BaseResponse<BeanContainer>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<BaseResponse<BeanContainer>> e) throws Exception {
                final BaseResponse<BeanContainer> cacheResponce = getCache(key);
                if (cacheResponce != null) {
                    LogUtils.e("Cache hint  | key = " + key);
                    cacheResponce.fromCache = true;
                    e.onNext(cacheResponce);
                } else {
                    e.onComplete();
                }
            }
        });

        //save cache
        Observable<BaseResponse<BeanContainer>> fromNet = getEntryFromNet(url, queryMap, needCache)
                .map(new Function<BaseResponse<BeanContainer>, BaseResponse<BeanContainer>>() {
                    @Override
                    public BaseResponse<BeanContainer> apply(@NonNull BaseResponse<BeanContainer> tBaseResponse) throws Exception {
                        if (needCache) saveCache(key, tBaseResponse);
                        return tBaseResponse;
                    }
                });

        if (forceRefresh) {
            return fromNet
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return Observable.concat(fromCache, fromNet)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<BaseResponse<BeanContainer>> getEntry(String url, Map<String, String> queryMap) {
        return getEntry(url, queryMap, true, false);
    }

    @Override
    public void saveCache(final String url, BaseResponse<BeanContainer> tBean) {
        final BeanContainer container = tBean.realData;
        if (container == null) return;
        container.keyUrl = url;
        container.async().save();
    }

    @Override
    public Observable<BaseResponse<BeanContainer>> getEntryFromNet(String url, Map<String, String> queryMap, boolean needCache) {
        Map<String, String> headMap = getHeader();
        return HttpRequestFactory.retrofit().create(ApiService.class)
                .executeGet(url, headMap, queryMap).map(new Function<ResponseBody, BaseResponse<BeanContainer>>() {
                    @Override
                    public BaseResponse<BeanContainer> apply(@NonNull ResponseBody responseBody) throws Exception {
                        try {
                            return GsonUtils.getInstance().fromJson(responseBody.string(), GsonUtils.type(BaseResponse.class, getTClass()));
                        }catch (Exception e){
                            return null;
                        }
                    }
                });
    }

    @Override
    public void clearCache() {
        SQLite.delete(getTableClass()).where(
                Operator.op(NameAlias.builder(BaseAdapterModel.KEY).distinct().build())
                        .isNotNull()).execute();
    }
}
