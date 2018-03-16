package com.jingyue.lygame.modules.personal.present;

import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.LikeGameListRp;
import com.jingyue.lygame.modules.personal.LikeGameFragment;
import com.jingyue.lygame.modules.search.view.SearchView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.RxMap;
import java.util.List;

/**
 * Created by zhanglei on 2017/9/21.
 */
public class UserGameListPresent extends BaseMvpPresenter<SearchView> {

    private LikeGameListRp gameListRp;

    public UserGameListPresent(SearchView mvpView, BaseImpl base, LikeGameListRp respository) {
        super(mvpView, base);
        gameListRp = respository;
    }

    public void loadData(final boolean isRefresh,final int type, int page, int limit) {
        if(type == LikeGameFragment.TYPE_COLLECT){
            gameListRp.getEntry(new UrlConstant.Builder(false).floatMoblieV2().likeGameList(),
                    new RxMap().put("page", String.valueOf(page))
                            .put("limit", String.valueOf(limit)).build(), false, false)
                    .subscribe(new ProgressObserver<BaseResponse<List<GameBean>>>(baseImpl, true) {
                        @Override
                        public void onSuccess(BaseResponse<List<GameBean>> response) {
                            if (response.realData != null) {
                                for (final GameBean gameBean : response.realData) {
                                    gameBean.itemType = AppConstants.ItemType.USERLIKE_GAME_DETAIL;
                                }
                                mView.setDataOnMain(response.realData,isRefresh);
                            }
                        }
                    });
        }else {
            gameListRp.getEntry(new UrlConstant.Builder(false).floatMoblieV2().subGameList(),
                    new RxMap().put("page", String.valueOf(page))
                            .put("limit", String.valueOf(limit)).build(), false, false)
                    .subscribe(new ProgressObserver<BaseResponse<List<GameBean>>>(baseImpl, true) {
                        @Override
                        public void onSuccess(BaseResponse<List<GameBean>> response) {
                            if (response.realData != null) {
                                for (final GameBean gameBean : response.realData) {
                                    gameBean.itemType = AppConstants.ItemType.GAME_TAG_LIST;
                                }
                                mView.setDataOnMain(response.realData,isRefresh);
                            }
                        }
                    });
        }

    }
}
