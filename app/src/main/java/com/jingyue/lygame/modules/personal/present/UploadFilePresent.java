package com.jingyue.lygame.modules.personal.present;

import android.text.TextUtils;

import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.modules.personal.view.UploadFileView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.lygame.libadapter.HttpRequestFactory;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by zhanglei on 2017/9/21.
 * modify by yizhihao on 2017/9/25.
 */
public class UploadFilePresent extends BaseMvpPresenter<UploadFileView> {

    private BaseImpl mBase;

    public UploadFilePresent(UploadFileView mvpView, BaseImpl base) {
        super(mvpView, base);
        mBase = base;
    }


    public void upload(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        String descriptionString = file.getName();
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
        HttpRequestFactory.retrofit().create(ApiService.class)
                .uploadAvatar(description, body, LoginManager.getInstance().getLoginInfo().h5Token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ProgressObserver<BaseResponse>(mBase, mBase != null) {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        mView.uploadSuccess("success");
                    }

                });
    }


    /**
     * @param business
     * @param fileStrs
     */
    public void uploadFiles(String business, List<String> fileStrs, String appId) {
        if (fileStrs == null || fileStrs.isEmpty() || TextUtils.isEmpty(appId)) {
            LogUtils.e("file or appId can't be null!");
            return;
        }

        Observable<BaseResponse<String>> observableResult = null;
        Observable<BaseResponse<String>> observableTemp = null;

        for (final String fileStr : fileStrs) {
            File file = new File(fileStr);
            if (!file.exists()) continue;
            final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            /*final RequestBody appBody = RequestBody.create(MediaType.parse("multipart/form-data"), appId);
            MultipartBody.Part appIdPart = MultipartBody.Part.createFormData("app_id", null, appBody);*/

            observableTemp = HttpRequestFactory.retrofit().create(ApiService.class)
                    .uploadloadShopImage(LoginManager.getInstance().getEncryptHsToken(),
                            appId,
                            body);

            if (observableResult != null) {
                observableResult = Observable.concat(observableResult, observableTemp);
            } else {
                observableResult = observableTemp;
            }
        }
        observableResult.flatMap(new Function<BaseResponse<String>, ObservableSource<BaseResponse<String>>>() {
            @Override
            public ObservableSource<BaseResponse<String>> apply(@NonNull BaseResponse<String> baseResponse) throws Exception {
                if (baseResponse.errCode == BaseResponse.SUCESS_CDDE) {
                    mView.uploadProgress(progress);
                } else {
                    mView.uploadFailure();
                }
                progress++;
                return Observable.just(baseResponse);
            }

            private int progress = 0;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressObserver<BaseResponse<String>>(baseImpl, true) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        mView.uploadSuccess(response.realData);
                    }
                });


    }
}
