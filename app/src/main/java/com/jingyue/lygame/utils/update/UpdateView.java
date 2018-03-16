package com.jingyue.lygame.utils.update;

import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-29 16:45
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public interface UpdateView extends BaseMvpView{
    void onDownloadFinished();
    void onPublish(int percent);
}
