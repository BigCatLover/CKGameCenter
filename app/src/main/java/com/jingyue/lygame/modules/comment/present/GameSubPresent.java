package com.jingyue.lygame.modules.comment.present;

import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.events.GameSubEvent;
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
 * Created by zhanglei on 2017/9/26.
 * 游戏预约与取消预约
 */


public class GameSubPresent extends BaseMvpPresenter<UserActionView> {
    private BaseImpl mBase;
    public GameSubPresent(UserActionView mvpView, BaseImpl base) {
        super(mvpView,base);
        mBase = base;
    }

    /**
     *
     * @param app_id
     * @param isSub 当前是否是预约状态
     */
    public void doAction(final String app_id, boolean isSub){
        if(isSub){
            HttpRequestFactory.retrofit().create(ApiService.class)
                    .sub(LoginManager.getInstance().getLoginInfo().h5Token,app_id,"unsub")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new ProgressObserver<BaseResponse>(mBase, mBase != null) {
                        @Override
                        public void onSuccess(BaseResponse response) {
                            EBus.getDefault().post(new GameSubEvent(GameSubEvent.TYPE_UNSUB,app_id));
                            mView.success(UserActionView.ACTION_UNSUB,app_id);
                        }

                    });
        }else {
            HttpRequestFactory.retrofit().create(ApiService.class)
                    .sub(LoginManager.getInstance().getLoginInfo().h5Token,app_id,"sub")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new ProgressObserver<BaseResponse>(mBase, mBase != null) {
                        @Override
                        public void onSuccess(BaseResponse response) {
                            EBus.getDefault().post(new GameSubEvent(GameSubEvent.TYPE_SUB,app_id));
                            mView.success(UserActionView.ACTION_SUB,app_id);
                        }

                    });
        }

    }
}
