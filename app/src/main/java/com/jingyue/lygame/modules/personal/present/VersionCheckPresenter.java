package com.jingyue.lygame.modules.personal.present;


import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.bean.VersionInfoBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.modules.personal.view.VersionCheckView;
import com.jingyue.lygame.utils.SettingUtil;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.jingyue.lygame.utils.update.NewVersionUtil;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.Utils;
import com.lygame.libadapter.HttpRequestFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-24 18:32
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class VersionCheckPresenter extends BaseMvpPresenter<VersionCheckView> {
    /**
     * activity fragment不能和view进行耦合
     * 他们是 1..n 的关系
     * BaseImpl是activity的抽象
     *
     * @param mView
     * @param baseImpl
     */
    public VersionCheckPresenter(VersionCheckView mView, BaseImpl baseImpl) {
        super(mView, baseImpl);
    }

    public void startVersionCheck(boolean needProgress, final boolean forceShowRefresh) {
        HttpRequestFactory.retrofit().create(ApiService.class)
                .versionCheck(BuildConfig.VERSION_CODE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressObserver<BaseResponse<VersionInfoBean>>(baseImpl, needProgress) {
                    @Override
                    public void onSuccess(BaseResponse<VersionInfoBean> response) {
                        if (response.realData.update) {
                            final String newVersion = response.realData.lastVersionCode;
                            final boolean needSkip = SettingUtil.readBoolean(Utils.getContext(), NewVersionUtil.UPDATE_SKIP + newVersion, false);
                            if (needSkip && !forceShowRefresh) {
                                return;
                            }else{
                                SettingUtil.write(Utils.getContext(), NewVersionUtil.INSTALL_NEW_VERSION,
                                        newVersion);
                                SettingUtil.write(Utils.getContext(), newVersion +
                                        NewVersionUtil.FORCE_UPDATE, response.realData.forceUpdate ? 1 : 0);
                                SettingUtil.write(Utils.getContext(), newVersion +
                                        NewVersionUtil.UPDATE_URL, response.realData.url);
                                SettingUtil.write(Utils.getContext(), NewVersionUtil.NEW_VERSION_NAME,
                                        NewVersionUtil.getNewVersionFileName(String.valueOf(newVersion)));
                            }

                            if (NewVersionUtil.isNewVersionFileExist(baseImpl.getContext(), newVersion)) {
                                mView.onCheckVersion(response.realData,false);
                            }else{
                                mView.onCheckVersion(response.realData,true);
                            }
                        }else{
                            mView.onVersionCheckFailure();
                        }
                    }
                });
    }

    public boolean needCheck() {
        return true;
    }

}
