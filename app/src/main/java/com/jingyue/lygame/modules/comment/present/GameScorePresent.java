package com.jingyue.lygame.modules.comment.present;

import com.jingyue.lygame.bean.GameScoreBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.GameScoreRp;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.modules.comment.view.GameScoreView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.RxMap;

/**
 * Created by zhanglei on 2017/9/16.
 */
public class GameScorePresent extends BaseMvpPresenter<GameScoreView> {

    private GameScoreRp gameScoreRp;

    public GameScorePresent(GameScoreView mvpView, BaseImpl base, GameScoreRp respository) {
        super(mvpView,base);
        gameScoreRp = respository;
    }

    public void loadData(final String appid_id){
        gameScoreRp.getEntry(new UrlConstant.Builder(false).floatMoblieV2().commentScoreDetail(),
                new RxMap().put("app_id",appid_id).build(),false,false)
                .subscribe(new ProgressObserver<BaseResponse<GameScoreBean>>(baseImpl,true) {
                    @Override
                    public void onSuccess(BaseResponse<GameScoreBean> response) {
                        GameScoreBean bean = response.realData;
                        bean.memid = LoginManager.getInstance().getLoginInfo().id;
                        bean.appid = appid_id;
                        bean.save();
                        mView.setDataOnMain(bean);
                    }
                });
    }
}
