package com.jingyue.lygame.modules.personal.present;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.jingyue.lygame.bean.LoginInfoBean;
import com.jingyue.lygame.bean.LoginInfoBean_Table;
import com.jingyue.lygame.bean.LoginstateBean_Table;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.modules.personal.view.LoginView;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.mvp.basemvps.BaseMvpPresenter;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-04 11:39
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class LoginPresent extends BaseMvpPresenter<LoginView> {

    private LoginManager mLoginManager;
    private List<LoginInfoBean> mLoginInfoList;

    /**
     * activity 不能和view进行耦合
     * 他么是 1..n 的关系
     * BaseImpl是activity的抽象
     *
     * @param mView
     * @param baseImpl
     */
    public LoginPresent(LoginView mView, BaseImpl baseImpl) {
        super(mView, baseImpl);
        mLoginManager = LoginManager.getInstance();
    }


    public void startLogin(String userName,String passWord) {
        mLoginManager.login(baseImpl,userName,passWord);
    }


    public boolean isLogin() {
        return mLoginManager.isLogin();
    }

    public void loadLoginHistory() {
        //查询
        SQLite.select().from(LoginInfoBean.class)
                .orderBy(LoginInfoBean_Table.loginTime,false)
                .async().queryListResultCallback(new QueryTransaction.QueryResultListCallback<LoginInfoBean>() {
            @Override
            public void onListQueryResult(QueryTransaction transaction, @NonNull final List<LoginInfoBean> tResult) {
                mLoginInfoList = tResult;
                List<LoginInfoBean> dels = new ArrayList<LoginInfoBean>();
                for (final LoginInfoBean bean : mLoginInfoList) {
                    if(TextUtils.isEmpty(bean.id)){
                        dels.add(bean);
                    }
                }
                if(dels.size() > 0){
                    mLoginInfoList.removeAll(dels);
                }
                final int size = mLoginInfoList.size();
                if(size > AppConstants.MAX_LOGIN_HISTORY_SIZE){
                    for (int i = size - 1; i >= AppConstants.MAX_LOGIN_HISTORY_SIZE; i--) {
                        mLoginInfoList.remove(i);
                    }
                }
                mView.onGetLoginHistory(mLoginInfoList);
                LogUtils.e(mLoginInfoList);
            }
        }).execute();
    }

    public boolean needShowUserNamePop(){
        return mLoginInfoList != null && !mLoginInfoList.isEmpty();
    }

    public List<LoginInfoBean> getLoginInfoList(){
        return mLoginInfoList;
    }
}


