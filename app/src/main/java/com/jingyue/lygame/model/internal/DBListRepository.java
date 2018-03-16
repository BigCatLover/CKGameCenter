package com.jingyue.lygame.model.internal;

import android.text.TextUtils;

import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.bean.AppDataBase;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.interfaces.ApiService;
import com.laoyuegou.android.lib.utils.GsonUtils;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.lygame.libadapter.HttpRequestFactory;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.Operator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.List;
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
 * data  : [
 * {
 * JsonObject
 * },
 * {
 * JsonObject
 * }
 * }
 */
public abstract class DBListRepository<BeanContainer extends BaseAdapterModel> extends IDBFlowRespository<List<BeanContainer>, BeanContainer> {

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
    public BaseResponse<List<BeanContainer>> getCache(String url) throws Exception {
        List<BeanContainer> beanList = SQLite.select().from(getTableClass())
                .where(property.eq(url))
                .queryList();
        if (beanList != null && !beanList.isEmpty()) {
            return new BaseResponse<List<BeanContainer>>().setData(beanList);
        }
        return null;
    }

    @Override
    public Observable<BaseResponse<List<BeanContainer>>> getEntry(final String url, Map<String, String> queryMap, final boolean needCache, boolean forceRefresh) {

        final String key = getCacheKey(url, queryMap);

        //get cache
        Observable<BaseResponse<List<BeanContainer>>> fromCache = Observable.create(new ObservableOnSubscribe<BaseResponse<List<BeanContainer>>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<BaseResponse<List<BeanContainer>>> e) throws Exception {
                final BaseResponse<List<BeanContainer>> cacheResponce = getCache(key);
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
        Observable<BaseResponse<List<BeanContainer>>> fromNet = getEntryFromNet(url, queryMap, needCache)
                .map(new Function<BaseResponse<List<BeanContainer>>, BaseResponse<List<BeanContainer>>>() {
                    @Override
                    public BaseResponse<List<BeanContainer>> apply(@NonNull BaseResponse<List<BeanContainer>> tBaseResponse) throws Exception {
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
    public Observable<BaseResponse<List<BeanContainer>>> getEntry(String url, Map<String, String> queryMap) {
        return getEntry(url, queryMap, true, false);
    }

    @Override
    public void saveCache(final String url, BaseResponse<List<BeanContainer>> tBean) {
        final List<BeanContainer> container = tBean.realData;
        if (container == null) return;
        FlowManager.getDatabase(AppDataBase.class)
                .beginTransactionAsync(new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        for (BeanContainer beanContainer : container) {
                            beanContainer.keyUrl = url;
                            beanContainer.save();
                        }
                    }
                }).execute();
    }

    @Override
    public Observable<BaseResponse<List<BeanContainer>>> getEntryFromNet(String url, Map<String, String> queryMap, boolean needCache) {
        Map<String, String> headMap = getHeader();
        return HttpRequestFactory.retrofit().create(ApiService.class)
                .executeGet(url, headMap, queryMap).map(new Function<ResponseBody, BaseResponse<List<BeanContainer>>>() {
                    @Override
                    public BaseResponse<List<BeanContainer>> apply(@NonNull ResponseBody responseBody) throws Exception {
                        return GsonUtils.getInstance().fromJson(responseBody.string(), GsonUtils.type(BaseResponse.class, getTClass()));
                    }
                });

    }

    @Override
    public void clearCache() {
        SQLite.delete(getTableClass()).where(
                Operator.op(NameAlias.builder(BaseAdapterModel.KEY).distinct().build())
                        .isNotNull()).async().execute();
    }
}
