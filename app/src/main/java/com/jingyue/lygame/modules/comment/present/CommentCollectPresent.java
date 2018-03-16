package com.jingyue.lygame.modules.comment.present;

import com.jingyue.lygame.bean.CommentCollectBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.CommentCollectRp;
import com.jingyue.lygame.modules.comment.view.CommentCollectView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.RxMap;
import com.laoyuegou.android.lib.utils.Utils;

import java.util.List;

/**
 * Created by zhanglei on 2017/9/17.
 */
public class CommentCollectPresent extends BaseMvpPresenter<CommentCollectView> {
    private CommentCollectRp commentCollectRp;

    public CommentCollectPresent(CommentCollectView mvpView, BaseImpl base, CommentCollectRp respository) {
        super(mvpView, base);
        commentCollectRp = respository;
    }

    public void loadData(final boolean isRefresh, final int page, int limit, boolean needProgress) {
        if (!isRefresh) {
            commentCollectRp.clearCache();
        }
        commentCollectRp.getEntry(new UrlConstant.Builder(false).floatMoblieV2().commentsCollect(),
                new RxMap().put("page", String.valueOf(page))
                        .put("limit", String.valueOf(limit)).build(), true, NetworkUtils.hasNetwork(Utils.getContext()))
                .subscribe(new ProgressObserver<BaseResponse<List<CommentCollectBean>>>(baseImpl, needProgress) {
                    @Override
                    public void onSuccess(BaseResponse<List<CommentCollectBean>> response) {
                        mView.setDataOnMain(response.realData, isRefresh);
                    }

                    @Override
                    public boolean onFail(BaseResponse<List<CommentCollectBean>> response) {
                        if (baseImpl != null) {
                            baseImpl.dismissProgress();
                        }
                        if (mView != null && response == null) {
                            mView.noMore();
                        }
                        return true;
                    }
                });
    }
}
