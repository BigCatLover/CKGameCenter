package com.jingyue.lygame.modules.comment.view;

import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

/**
 * Created by zhanglei on 2017/9/16.
 */
public interface UserActionView extends BaseMvpView {
    int ACTION_LIKE = 0;
    int ACTION_UNLIKE = 1;
    int ACTION_REPLY = 2;
    int ACTION_REPORT = 3;
    int ACTION_SUB = 4;
    int ACTION_UNSUB = 5;
    void success(int action,String id);
}
