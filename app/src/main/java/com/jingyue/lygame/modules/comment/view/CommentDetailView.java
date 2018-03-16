package com.jingyue.lygame.modules.comment.view;

import com.jingyue.lygame.bean.CommentDetialBean;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;
/**
 * Created by zhanglei on 2017/9/16.
 */
public interface CommentDetailView extends BaseMvpView {
    void setDataOnMain(CommentDetialBean beans,boolean isRefresh);
    void noMore();
}
