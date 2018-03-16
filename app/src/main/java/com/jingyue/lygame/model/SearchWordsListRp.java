package com.jingyue.lygame.model;

import com.jingyue.lygame.bean.SearchLocalWordsBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.internal.DBRepository;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.RxMap;
import com.laoyuegou.android.lib.utils.Utils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhanglei on 2017/9/18.
 */
public class SearchWordsListRp extends DBRepository<SearchLocalWordsBean> {
    private static final AtomicReference<SearchWordsListRp> INSTANCE = new AtomicReference<SearchWordsListRp>();

    public static SearchWordsListRp getInstance() {
        for (; ; ) {
            SearchWordsListRp current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new SearchWordsListRp();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    public void preLoadData(){
        getEntry(new UrlConstant.Builder(false).floatMoblieV2().searchWords(),
                new RxMap<String,String>().build(),true, NetworkUtils.hasNetwork(Utils.getContext()))
                .subscribeOn(Schedulers.io())
                .subscribe(new ProgressObserver<BaseResponse<SearchLocalWordsBean>>(null,false) {
                    @Override
                    public void onSuccess(BaseResponse<SearchLocalWordsBean> response) {
                        SearchLocalWordsBean bean = response.realData;
                        SearchLocalWordsBean bean1 = SQLite.select().from(SearchLocalWordsBean.class).querySingle();
                        if(bean1!=null){
                            bean1.delete();
                        }
                        bean.async().save();
                    }
                });
    }

    public SearchLocalWordsBean getData(){
        SearchLocalWordsBean bean = SQLite.select().from(SearchLocalWordsBean.class).querySingle();
        return bean;
    }

    @Override
    public Type getTClass() {
        return SearchLocalWordsBean.class;
    }

    @Override
    public Class<SearchLocalWordsBean> getTableClass() {
        return SearchLocalWordsBean.class;
    }
}
