package com.jingyue.lygame.modules.comment.present;

import com.jingyue.lygame.bean.CommentListBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.CommentListRp;
import com.jingyue.lygame.modules.comment.view.CommentListView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.RxMap;

import java.util.List;

/**
 * Created by zhanglei on 2017/9/15.
 */
public class CommentListPresent extends BaseMvpPresenter<CommentListView> {
    private CommentListRp commentListRp;

    public CommentListPresent(CommentListView mvpView, BaseImpl base, CommentListRp respository) {
        super(mvpView, base);
        commentListRp = respository;
    }

    public void loadData(final boolean isRefresh,String app_id, int page, int limit,boolean needProgress) {
        commentListRp.getEntry(new UrlConstant.Builder(false).floatMoblieV2().commentList(),
                new RxMap().put("page", String.valueOf(page))
                        .put("limit", String.valueOf(limit))
                        .put("app_id", app_id).build(), false, false)
                .subscribe(new ProgressObserver<BaseResponse<List<CommentListBean>>>(baseImpl, needProgress) {
                    @Override
                    public void onSuccess(BaseResponse<List<CommentListBean>> response) {
                        mView.setCommentsOnMain(response.realData,isRefresh);
                    }

                    @Override
                    public boolean onFail(BaseResponse<List<CommentListBean>> response) {
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

    public void loadData(String app_id, int page) {
        loadData(true,app_id, page, 3,false);
    }
}
