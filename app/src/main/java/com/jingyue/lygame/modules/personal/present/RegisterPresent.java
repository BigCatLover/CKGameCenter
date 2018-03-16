package com.jingyue.lygame.modules.personal.present;

import android.text.TextUtils;

import com.jingyue.lygame.bean.CodeinfoBean;
import com.jingyue.lygame.bean.LoginInfoBean;
import com.jingyue.lygame.bean.internal.BaseResponse;
import com.jingyue.lygame.clickaction.GetIdentifyCodeAction;
import com.jingyue.lygame.clickaction.RegisterByPhoneAction;
import com.jingyue.lygame.clickaction.RegisterByUserNameAction;
import com.jingyue.lygame.interfaces.ApiService;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.modules.personal.view.ImageCodeView;
import com.jingyue.lygame.utils.rxJava.ProgressObserver;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.lygame.libadapter.HttpRequestFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-05 16:14
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class RegisterPresent extends BaseMvpPresenter<ImageCodeView> {

    private String phoneCodeId;

    /**
     * activity 不能和view进行耦合
     * 他么是 1..n 的关系
     * BaseImpl是activity的抽象
     *
     * @param mView
     * @param baseImpl
     */
    public RegisterPresent(ImageCodeView mView, BaseImpl baseImpl) {
        super(mView, baseImpl);
    }

    public void getImageCode() {
        HttpRequestFactory.retrofit().create(ApiService.class)
                .getCodeInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressObserver<BaseResponse<CodeinfoBean>>(baseImpl, false) {
                    @Override
                    public void onSuccess(BaseResponse<CodeinfoBean> response) {
                        mView.onGetCodeInfo(response.realData);
                    }
                });
    }

    public void sendPhoneCode(String mobilePhone) {
        new GetIdentifyCodeAction().onRecord();
        HttpRequestFactory.retrofit().create(ApiService.class)
                .sendMobileCodeForRegister(mobilePhone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressObserver<BaseResponse<CodeinfoBean>>(baseImpl, false) {
                    @Override
                    public void onSuccess(BaseResponse<CodeinfoBean> response) {
                        phoneCodeId = response.realData.id;
                        mView.onGetCodeInfo(response.realData);
                        ToastUtils.showShort("发送验证码成功");
                    }

                    @Override
                    public boolean onFail(BaseResponse<CodeinfoBean> response) {
                        return super.onFail(response);
                    }
                });
    }

    public void regbyPhone(final String mobileCode, String phone, String password) {
        if(TextUtils.isEmpty(phoneCodeId)){

        }
        HttpRequestFactory.retrofit().create(ApiService.class)
                .regByMobile(mobileCode, phone, password,phoneCodeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressObserver<BaseResponse<LoginInfoBean>>(baseImpl, true) {
                    @Override
                    public void onSuccess(BaseResponse<LoginInfoBean> response) {
                        new RegisterByPhoneAction(true).onRecord();
                        response.realData.isGuided = false;
                        response.realData.username = mobileCode;
                        LoginManager.getInstance().saveLoginInfo(response.realData,baseImpl);
                        ToastUtils.showShort("恭喜！注册成功。");
                        mView.onRegByPhoneSucess();
                    }

                    @Override
                    public boolean onFail(BaseResponse<LoginInfoBean> response) {
                        new RegisterByPhoneAction(false).onRecord();
                        super.onFail(response);
                        return true;
                    }

                });
    }

    public void regByName(String id, final String userName, String password, String imgCode) {
        HttpRequestFactory.retrofit().create(ApiService.class)
                .regByName(id, userName, password, imgCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressObserver<BaseResponse<LoginInfoBean>>(baseImpl, true) {
                    @Override
                    public void onSuccess(BaseResponse<LoginInfoBean> response) {
                        response.realData.isGuided = false;
                        response.realData.username = userName;
                        LoginManager.getInstance().saveLoginInfo(response.realData,baseImpl);
                        ToastUtils.showShort("恭喜！注册成功。");
                        new RegisterByUserNameAction(true).onRecord();
                        mView.onRegByUserSucess();
                    }

                    @Override
                    public boolean onFail(BaseResponse<LoginInfoBean> response) {
                        new RegisterByUserNameAction(false).onRecord();
                        super.onFail(response);
                        mView.onRegisterFailure();
                        return true;
                    }
                });
    }
}
