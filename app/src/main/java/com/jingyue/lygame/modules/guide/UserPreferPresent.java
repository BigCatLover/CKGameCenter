package com.jingyue.lygame.modules.guide;

import com.jingyue.lygame.bean.LoginInfoBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.utils.WebDataControl;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.lygame.libadapter.HttpRequestFactory;

import java.util.List;

import io.reactivex.schedulers.Schedulers;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-26 17:26
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class UserPreferPresent extends BaseMvpPresenter<UserPreferView> {

    /**
     * activity fragment不能和view进行耦合
     * 他们是 1..n 的关系
     * BaseImpl是activity的抽象
     *
     * @param baseImpl
     */
    public UserPreferPresent(UserPreferView view, BaseImpl baseImpl) {
        super(view, baseImpl);
    }

    public void uploadGuide() {
        List<String> tag = WebDataControl.getUserLabel();
        StringBuilder stringBuilder = new StringBuilder(tag.size() * 5);
        if (tag != null) {
            int index = 0;
            for (final String s : tag) {
                stringBuilder.append(s);
                index++;
                if (index <= tag.size() - 1) {
                    stringBuilder.append(",");
                }
            }
        }
        LogUtils.e("guide info = " + stringBuilder.toString());
        HttpRequestFactory.retrofit().create(ApiService.class)
                .uploadGuide(stringBuilder.toString())
                .subscribeOn(Schedulers.io())
                .subscribe(new ProgressObserver<BaseResponse>(baseImpl, true) {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        LoginInfoBean bean = LoginManager.getInstance().getLoginInfo();
                        bean.isGuided = true;
                        bean.async().save();
                        mView.uploadGuideInfoSucess();
                    }
                });
    }
}
