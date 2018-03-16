package com.jingyue.lygame.modules.comment.present;

import com.jingyue.lygame.bean.CommentDetialBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.CommentDetailRp;
import com.jingyue.lygame.modules.comment.view.CommentDetailView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.RxMap;

/**
 * Created by zhanglei on 2017/9/16.
 */
public class CommentDetailPresent extends BaseMvpPresenter<CommentDetailView> {

    private CommentDetailRp commentDetailRp;

    public CommentDetailPresent(CommentDetailView mvpView, BaseImpl base, CommentDetailRp respository) {
        super(mvpView,base);
        commentDetailRp = respository;
    }

    public void loadData(final boolean isRefresh, String comment_id, int page, int limit,boolean needProgress){
        commentDetailRp.getEntry(new UrlConstant.Builder(false).floatMoblieV2().commentDetail(),
                new RxMap().put("page",String.valueOf(page))
                        .put("limit",String.valueOf(limit))
                        .put("comment_id",comment_id).build(),false,false)
                .subscribe(new ProgressObserver<BaseResponse<CommentDetialBean>>(baseImpl,needProgress) {
                    @Override
                    public void onSuccess(BaseResponse<CommentDetialBean> response) {
                        mView.setDataOnMain(response.realData,isRefresh);
                    }

                    @Override
                    public boolean onFail(BaseResponse<CommentDetialBean> response) {
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
