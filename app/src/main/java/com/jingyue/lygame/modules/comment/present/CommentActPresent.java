package com.jingyue.lygame.modules.comment.present;

import android.support.annotation.StringDef;

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

/**@
 * Created by zhanglei on 2017/9/16.
 */
public class CommentActPresent extends BaseMvpPresenter<UserActionView> {

    public static final String ACTION_DISLIKE = "dislike";
    public static final String ACTION_LIKE = "like";

    @StringDef({ACTION_LIKE,ACTION_DISLIKE})
    @interface LikeAction{}

    private BaseImpl mBase;
    public CommentActPresent(UserActionView mvpView, BaseImpl base) {
        super(mvpView,base);
        mBase = base;
    }

    public void doAction(final String comment_id, @LikeAction final String action){
        HttpRequestFactory.retrofit().create(ApiService.class)
                .commentLike(LoginManager.getInstance().getLoginInfo().h5Token,comment_id,action)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ProgressObserver<BaseResponse>(mBase, mBase != null) {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if(action.equals(ACTION_DISLIKE)){
                            mView.success(UserActionView.ACTION_UNLIKE,comment_id);
                        }else {
                            mView.success(UserActionView.ACTION_LIKE,comment_id);
                        }

                    }
                });
    }
}
