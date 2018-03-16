package com.jingyue.lygame.utils.update;

import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.utils.rxJava.BaseObserver;
import com.lygame.libadapter.HttpRequestFactory;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-23 18:59
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class UpdateModel extends BaseObserver {


    public UpdateModel() {
        super(UpdateModel.class.getSimpleName());
    }

    /**
     * 下载新版本
     * @param url
     * @param dir
     * @param newVersion
     * @param observer
     */
    public void downloadNewVersion(String url, final String dir, final String newVersion, long lastDownloadSize, Observer<Boolean> observer) {
        if (lastDownloadSize < 0) {
            lastDownloadSize = 0;
        }
        String range = "bytes=" + lastDownloadSize + "-";
        Observable observable = HttpRequestFactory.retrofit().create(ApiService.class)
                .downloadFile(range, url).map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(ResponseBody responseBody) throws Exception {
                        return NewVersionUtil.downloadNewVersion(responseBody, dir, newVersion);
                    }
                });
        makeSubscribe(observable, observer);
    }

    /**
     * 下载新版本
     *  @param url
     * @param dir
     * @param newVersion
     * @param observer
     */
    public void downloadNewVersionIOThread(String url,
                                           final String dir,
                                           final String newVersion,
                                           long lastDownloadSize,
                                           final UpdatePresenter observer) {
        if (lastDownloadSize < 0) {
            lastDownloadSize = 0;
        }
        String range = "bytes=" + lastDownloadSize + "-";
        Observable observable = HttpRequestFactory.retrofit().create(ApiService.class)
                .downloadFile(range, url).map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(ResponseBody responseBody) throws Exception {
                        return NewVersionUtil.downloadNewVersion(responseBody, dir, newVersion);
                    }
                });
        makeSubscribe(observable, observer);
    }
}

