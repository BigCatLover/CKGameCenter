package com.jingyue.lygame.model.internal;

import android.content.Context;
import android.util.SparseArray;

import com.jingyue.lygame.bean.AppDataBase;
import com.jingyue.lygame.utils.rxJava.BaseObserver;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-11-06 23:26
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class DBRpManager {

    private static final AtomicReference<DBRpManager> INSTANCE = new AtomicReference<DBRpManager>();

    private DBRpManager() {
    }

    private Context mContext;

    public static DBRpManager getInstance() {
        for (; ; ) {
            DBRpManager current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new DBRpManager();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    private List<IDBFlowRespository> mIDBFlowRpSparseArray;

    public void addManager(IDBFlowRespository dbFlowRespository) {
        if (dbFlowRespository == null) return;
        if (mIDBFlowRpSparseArray == null) {
            mIDBFlowRpSparseArray = new ArrayList<>();
        }
        mIDBFlowRpSparseArray.add(dbFlowRespository);
    }

    public Observable<Boolean> cleanCache() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter e) throws Exception {
                FlowManager.getDatabase(AppDataBase.class)
                        .executeTransaction(new ITransaction() {
                            @Override
                            public void execute(DatabaseWrapper databaseWrapper) {
                                for (final IDBFlowRespository idbFlowRespository : mIDBFlowRpSparseArray) {
                                    idbFlowRespository.clearCache();
                                }
                                e.onNext(true);
                            }
                        });
            }
        });
    }
}
