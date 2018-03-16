package com.jingyue.lygame.modules.find.present;

import com.jingyue.lygame.bean.SubjectListBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.SubjectListRp;
import com.jingyue.lygame.modules.find.view.SubjectListView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.RxMap;
import com.laoyuegou.android.lib.utils.Utils;

import java.util.List;

/**
 * Created by zhanglei on 2017/9/21.
 */
public class SubjectListPresent extends BaseMvpPresenter<SubjectListView> {
    private SubjectListRp subjectListRp;

    public SubjectListPresent(SubjectListView mvpView, BaseImpl base, SubjectListRp respository) {
        super(mvpView, base);
        subjectListRp = respository;
    }

    public void loadData(boolean needProgress) {
        subjectListRp.getEntry(new UrlConstant.Builder(false).floatMoblieV2().subjectlist(),
                new RxMap().put("limit", "100").build(), true, NetworkUtils.hasNetwork(Utils.getContext()))
                .subscribe(new ProgressObserver<BaseResponse<List<SubjectListBean>>>(baseImpl, needProgress) {
                    @Override
                    public void onSuccess(BaseResponse<List<SubjectListBean>> response) {
                        mView.setDataOnMain(response.realData);
                    }

                    @Override
                    public boolean onFail(BaseResponse<List<SubjectListBean>> response) {
                        if (baseImpl != null) {
                            baseImpl.dismissProgress();
                        }
                        if (mView != null && response == null) {
                            mView.setNoMore();
                        }
                        return true;
                    }
                });
    }

}
