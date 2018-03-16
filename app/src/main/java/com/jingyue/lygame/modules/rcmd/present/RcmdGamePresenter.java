package com.jingyue.lygame.modules.rcmd.present;

import com.jingyue.lygame.bean.RcmdGameListBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.RcmdGameRp;
import com.jingyue.lygame.modules.rcmd.view.GameListView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.RxMap;
import com.laoyuegou.android.lib.utils.Utils;

import java.util.List;


/**
 * Created by zhanglei on 2017/9/7.
 */
public class RcmdGamePresenter extends BaseMvpPresenter<GameListView> {

    private RcmdGameRp mRcmdGameRp;

    public RcmdGamePresenter(GameListView mvpView, BaseImpl base, RcmdGameRp respository) {
        super(mvpView, base);
        mRcmdGameRp = respository;
    }

    public void loadData(final int page, int limit, final boolean needProgress) {
        if (!needProgress) {
            mRcmdGameRp.clearCache();
        }
        mRcmdGameRp.getEntry(new UrlConstant.Builder(false).floatMoblieV2().recommend(),
                new RxMap().put("page", String.valueOf(page))
                        .put("limit", String.valueOf(limit))
                        .build(), true, NetworkUtils.hasNetwork(Utils.getContext()))
                .subscribe(new ProgressObserver<BaseResponse<List<RcmdGameListBean>>>(baseImpl, needProgress) {
                    @Override
                    public void onSuccess(BaseResponse<List<RcmdGameListBean>> response) {
                        mView.setDataOnMain(response.realData, needProgress);
                    }

                    @Override
                    public boolean onFail(BaseResponse<List<RcmdGameListBean>> response) {
                        if (mView != null && response == null) {
                            mView.noData();
                        }
                        if (baseImpl != null) {
                            baseImpl.dismissProgress();
                        }
                        return true;
                    }
                });
    }
}
