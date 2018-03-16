package com.jingyue.lygame.model;

import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.events.DownloadEvent;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.utils.EBus;
import com.lygame.libadapter.HttpRequestFactory;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhanglei on 2017/10/30.
 */
public class DownloadRp {
    private static final AtomicReference<DownloadRp> INSTANCE = new AtomicReference<DownloadRp>();

    public static DownloadRp getInstance() {
        for (; ; ) {
            DownloadRp current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new DownloadRp();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    public void doAction(final int app_id, final String downcnt) {
        HttpRequestFactory.retrofit().create(ApiService.class)
                .downGame(String.valueOf(app_id))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ProgressObserver<BaseResponse>(null, false) {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if (!StringUtils.isEmptyOrNull(downcnt)) {
                            int cnt = Integer.valueOf(downcnt) + 1;
                            EBus.getDefault().post(new DownloadEvent(cnt));
                        }
                    }
                });
    }
}
