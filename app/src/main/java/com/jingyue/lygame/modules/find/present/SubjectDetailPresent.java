package com.jingyue.lygame.modules.find.present;

import com.jingyue.lygame.bean.SubjectDetailBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.SubjectDetailRp;
import com.jingyue.lygame.modules.find.view.SubjectDetailView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.RxMap;

/**
 * Created by zhanglei on 2017/9/21.
 */
public class SubjectDetailPresent extends BaseMvpPresenter<SubjectDetailView> {

    private SubjectDetailRp subjectDetailRp;

    public SubjectDetailPresent(SubjectDetailView mvpView, BaseImpl base, SubjectDetailRp respository) {
        super(mvpView, base);
        subjectDetailRp = respository;
    }

    public void loadData(final boolean isRefresh, String subject_id, boolean needProgress) {
        subjectDetailRp.getEntry(new UrlConstant.Builder(false).floatMoblieV2().subject(),
                new RxMap().put("page", "1")
                        .put("limit", "100")
                        .put("id", subject_id).build(), false, false)
                .subscribe(new ProgressObserver<BaseResponse<SubjectDetailBean>>(baseImpl, needProgress) {
                    @Override
                    public void onSuccess(BaseResponse<SubjectDetailBean> response) {
                        mView.setDataOnMain(response.realData, isRefresh);
                    }

                    @Override
                    public boolean onFail(BaseResponse<SubjectDetailBean> response) {
                        if (baseImpl != null) {
                            baseImpl.dismissProgress();
                        }
                        if (mView != null) {
                            mView.noMore();
                        }
                        return false;
                    }
                });
    }

}
