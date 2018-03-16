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
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

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
 * Create time  : 2017-08-23 15:15
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * <p>
 * 数据库缓存类
 * <p>
 * {
 * msg   : "",
 * code  : "",
 * data  : {
 * count : 2,
 * <p>
 * ...
 * <p>
 * listdata : [
 * {jsonObject}
 * ]
 * }
 * }
 */
public abstract class DBFlatListRepository<BeanContainer, DBBean extends BaseAdapterModel> extends IDBFlowRespository<BeanContainer, DBBean> {

    public static final boolean DEBUG = BuildConfig.DEBUG;

    public abstract List<DBBean> mapContainer(BeanContainer beanContainer);

    public abstract BeanContainer mapTableBean(List<DBBean> beanList);

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
        List<DBBean> beanList = SQLite.select().from(getTableClass())
                .where(property.like(String.format("?%s?",url)))
                .queryList();
        if (beanList != null && !beanList.isEmpty()) {
            return new BaseResponse<BeanContainer>().setData(mapTableBean(beanList));
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

        //from net
        Observable<BaseResponse<BeanContainer>> fromNet = getEntryFromNet(url, queryMap, needCache).map(new Function<BaseResponse<BeanContainer>, BaseResponse<BeanContainer>>() {
            @Override
            public BaseResponse<BeanContainer> apply(@NonNull BaseResponse<BeanContainer> tBaseResponse) throws Exception {
                //save cache
                if (needCache) saveCache(key, tBaseResponse);
                return tBaseResponse;
            }
        });

        if (forceRefresh) {
            return fromNet.subscribeOn(Schedulers.io())
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
        final List<DBBean> beanList = mapContainer(container);
        if (beanList != null && !beanList.isEmpty()) {
            FlowManager.getDatabase(AppDataBase.class)
                    .beginTransactionAsync(new ITransaction() {
                        @Override
                        public void execute(DatabaseWrapper databaseWrapper) {
                            for (DBBean bean : beanList) {
                                /*if (bean.exists()) {
                                    bean.loadKey();
                                }*/
                                if (!TextUtils.isEmpty(bean.keyUrl)) {
                                    bean.keyUrl.concat("&&").concat(url);
                                } else {
                                    bean.keyUrl = url;
                                }
                                bean.save();
                            }
                        }
                    }).success(new Transaction.Success() {
                @Override
                public void onSuccess(@android.support.annotation.NonNull Transaction transaction) {
                    LogUtils.d(getTableClass().getSimpleName() + " save uploadGuideInfoSucess!");
                }
            });
        }
    }

    @Override
    public Observable<BaseResponse<BeanContainer>> getEntryFromNet(String url, Map<String, String> queryMap, boolean needCache) {
        Map<String, String> headMap = getHeader();
        return HttpRequestFactory.retrofit().create(ApiService.class)
                .executeGet(url, headMap, queryMap)
                .map(new Function<ResponseBody, BaseResponse<BeanContainer>>() {
                    @Override
                    public BaseResponse<BeanContainer> apply(@NonNull ResponseBody responseBody) throws Exception {
                        return GsonUtils.getInstance().fromJson(responseBody.string(), GsonUtils.type(BaseResponse.class, getTClass()));
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
