package com.jingyue.lygame.modules.comment.view;

import com.jingyue.lygame.bean.CommentListBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

import java.util.List;

/**
 * Created by zhanglei on 2017/9/15.
 */
public interface CommentListView extends BaseMvpView {
    void setCommentsOnMain(List<CommentListBean> beans,boolean isRefresh);
    void noMore();
}
