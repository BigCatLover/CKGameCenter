package com.jingyue.lygame.modules.comment.present;

import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.modules.comment.view.UserActionView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.lygame.libadapter.HttpRequestFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhanglei on 2017/9/16.
 */
public class ReportActPresent extends BaseMvpPresenter<UserActionView> {
    private BaseImpl mBase;
    public ReportActPresent(UserActionView mvpView, BaseImpl base) {
        super(mvpView,base);
        mBase = base;
    }

    public void loadData(final String app_id, String content, String phone_info, String score){
        HttpRequestFactory.retrofit().create(ApiService.class)
                .commitComment(LoginManager.getInstance().getLoginInfo().h5Token,app_id,content,phone_info ,score).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ProgressObserver<BaseResponse>(mBase, mBase != null) {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        mView.success(UserActionView.ACTION_REPORT,app_id);
                    }
                });
    }
}
