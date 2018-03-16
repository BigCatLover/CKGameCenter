package com.jingyue.lygame.modules.comment.view;

import com.jingyue.lygame.bean.CommentCollectBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

import java.util.List;

/**
 * Created by zhanglei on 2017/9/17.
 * 游评汇
 */
public interface CommentCollectView extends BaseMvpView {
    void setDataOnMain(List<CommentCollectBean> beans,boolean isRefresh);
    void noMore();
}
