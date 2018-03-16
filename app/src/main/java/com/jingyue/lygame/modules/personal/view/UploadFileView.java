package com.jingyue.lygame.modules.personal.view;

import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpView;

/**
 * Created by zhanglei on 2017/9/21.
 */
public interface UploadFileView extends BaseMvpView {
    void uploadSuccess(String data);
    void uploadProgress(int progress);
    void uploadFailure();
}
