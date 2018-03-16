package com.jingyue.lygame.modules.comment.present;

import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.clickaction.ButtonAction;
import com.jingyue.lygame.events.GameCollectEvent;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.modules.comment.view.UserActionView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.EBus;
import com.lygame.libadapter.HttpRequestFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhanglei on 2017/9/16.
 * 收藏
 */
public class GameCollectPresent extends BaseMvpPresenter<UserActionView> {
    private BaseImpl mBase;

    public GameCollectPresent(UserActionView mvpView, BaseImpl base) {
        super(mvpView, base);
        mBase = base;
    }

    private String appId;

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void doAction(boolean islike) {
        doAction(appId, islike);
    }

    /**
     * @param appId
     * @param isLike 当前是否是收藏状态
     */
    public void doAction(final String appId, boolean isLike) {
        new ButtonAction(appId).a3().like().onRecord();

        if (isLike) {
            HttpRequestFactory.retrofit().create(ApiService.class)
                    .cancelCollect(LoginManager.getInstance().getLoginInfo().h5Token, appId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new ProgressObserver<BaseResponse>(mBase, mBase != null) {
                        @Override
                        public void onSuccess(BaseResponse response) {
                            EBus.getDefault().post(new GameCollectEvent(GameCollectEvent.TYPE_UNLIKE, appId));
                            mView.success(UserActionView.ACTION_UNLIKE, appId);
                        }
                    });
        } else {
            HttpRequestFactory.retrofit().create(ApiService.class)
                    .collect(LoginManager.getInstance().getLoginInfo().h5Token, appId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new ProgressObserver<BaseResponse>(mBase, mBase != null) {
                        @Override
                        public void onSuccess(BaseResponse response) {

                            EBus.getDefault().post(new GameCollectEvent(GameCollectEvent.TYPE_LIKE, appId));
                            mView.success(UserActionView.ACTION_LIKE, appId);
                        }

                    });
        }

    }
}
