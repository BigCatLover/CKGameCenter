package com.jingyue.lygame.modules.search.present;

import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.SearchRp;
import com.jingyue.lygame.modules.search.view.SearchView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.RxMap;

import java.util.List;

/**
 * Created by zhanglei on 2017/9/18.
 */
public class SearchPresent extends BaseMvpPresenter<SearchView> {
    private SearchRp searchRp;

    public SearchPresent(SearchView mvpView, BaseImpl base, SearchRp respository) {
        super(mvpView,base);
        searchRp = respository;
    }

    public void loadData(String keyword,int page,int limit,final boolean isRefresh,boolean needProgress){
        if(!isRefresh){
            searchRp.clearCache();
        }
        searchRp.getEntry(new UrlConstant.Builder(false).floatMoblieV2().search(),
                new RxMap().put("keyword",keyword)
                        .put("page",String.valueOf(page))
                        .put("limit",String.valueOf(limit)).build(),false,false)
                .subscribe(new ProgressObserver<BaseResponse<List<GameBean>>>(baseImpl,needProgress) {
                    @Override
                    public void onSuccess(BaseResponse<List<GameBean>> response) {
                        if(response.realData!=null){
                            for (final GameBean gameBean : response.realData) {
                                gameBean.itemType = AppConstants.ItemType.SEARCH_GAME_LIST;
                            }
                            mView.setDataOnMain(response.realData,isRefresh);
                        }

                    }

                    @Override
                    public boolean onFail(BaseResponse<List<GameBean>> response) {
                        if (baseImpl != null) {
                            baseImpl.dismissProgress();
                        }
                        if(mView!=null){
                            mView.noMore();
                        }
                        return false;
                    }
                });
    }
}
