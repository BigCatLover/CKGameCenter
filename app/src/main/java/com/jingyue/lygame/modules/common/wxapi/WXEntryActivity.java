/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package com.jingyue.lygame.modules.common.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.R;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信客户端回调activity示例
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    private String mAppId;
    private String mAppSceret;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != api) {
            setIntent(intent);
            api.handleIntent(intent, this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getKey();
        registerApp();
        try {
            api.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isWXAppSupport()) {
            ToastUtils.showShort(R.string.a_0231);
            this.finish();
        }
    }

    /**
     * 判断当前手机是否装微信或者微信版本是否支持
     *
     * @return true 可用
     */
    private boolean isWXAppSupport() {
        if (null != api) {
            return api.isWXAppInstalled() && api.isWXAppSupportAPI();
        }
        return false;
    }

    private void getKey() {
        mAppId = BuildConfig.WECHAT_APP_ID;
        mAppSceret = BuildConfig.WECHAT_APP_SECRET;
    }

    /**
     * 注册微信
     */
    private void registerApp() {
        api = WXAPIFactory.createWXAPI(this, mAppId, false);
        api.registerApp(mAppId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterApp();
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    /**
     * 注销微信
     */
    private void unRegisterApp() {
        if (null != api) {
            api.unregisterApp();
            api = null;
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
        switch (baseReq.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX://得到消息指令
//                goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX://显示消息内容
//                goToShowMsg((ShowMessageFromWX.Req) req);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp baseResp) {
        int result = 0;
        if (baseResp instanceof SendAuth.Resp) {

        } else {
            //分享
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK://成功
                    result = R.string.ssdk_oks_share_completed;
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL://发送取消
                    result = R.string.ssdk_oks_share_canceled;
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED://发送被拒绝
                    result = R.string.ssdk_oks_share_failed;
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT://不支持错误
                    result = R.string.ssdk_oks_share_failed;
                    break;
                default:
                    //发送返回
                    result = R.string.ssdk_oks_share_failed;
                    break;
            }
        }
        if (result != 0) {
            ToastUtils.showShort(getResources().getString(result));
            this.finish();
        }
    }
}
