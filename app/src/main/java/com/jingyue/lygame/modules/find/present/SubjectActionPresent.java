package com.jingyue.lygame.modules.find.present;

import android.support.annotation.StringDef;

import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.modules.find.view.SubjectActionView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.lygame.libadapter.HttpRequestFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhanglei on 2017/9/21.
 */
public class SubjectActionPresent extends BaseMvpPresenter<SubjectActionView> {

    public static final String ACTION_DISLIKE = "dislike";
    public static final String ACTION_LIKE = "like";

    @StringDef({ACTION_LIKE,ACTION_DISLIKE})
    @interface LikeAction{}

    private BaseImpl mBase;
    public SubjectActionPresent(SubjectActionView mvpView, BaseImpl base) {
        super(mvpView,base);
        mBase = base;
    }

    public void doAction(String subject_id,@SubjectActionPresent.LikeAction final String action){
        if(action.equals(ACTION_DISLIKE)){
            HttpRequestFactory.retrofit().create(ApiService.class)
                    .unlikeSubject(LoginManager.getInstance().getLoginInfo().h5Token,subject_id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new ProgressObserver<BaseResponse>(mBase, mBase != null) {
                        @Override
                        public void onSuccess(BaseResponse response) {
                            mView.ActionCallback(ACTION_DISLIKE);
                        }
                    });
        }else {
            HttpRequestFactory.retrofit().create(ApiService.class)
                    .likeSubject(LoginManager.getInstance().getLoginInfo().h5Token,subject_id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new ProgressObserver<BaseResponse>(mBase, mBase != null) {
                        @Override
                        public void onSuccess(BaseResponse response) {
                            mView.ActionCallback(ACTION_LIKE);
                        }
                    });
        }
    }
}
