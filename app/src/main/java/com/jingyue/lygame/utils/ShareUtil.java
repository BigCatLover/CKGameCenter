package com.jingyue.lygame.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.ClipboardManager;
import android.view.View;

import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.ShareEntityBean;
import com.jingyue.lygame.modules.onekeyshare.OnekeyShare;
import com.jingyue.lygame.modules.onekeyshare.OnekeyShareTheme;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.lygame.libadapter.ImageLoader;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;

/**
 * Created by zhanglei on 2017/10/29.
 */
public class ShareUtil {
    /**
     * 分享微信好友
     */
    public static final String SHARE_WECHAT = "share_platform_wechat";
    /**
     * 分享微信朋友圈
     */
    public static final String SHARE_WECHATMOMENTS = "share_platform_wechatmoments";


    public static void showShare(final Context context, final ShareEntityBean config, final String platform) {
        if (!StringUtils.isEmptyOrNull(platform) && (platform.equals(SHARE_WECHAT) || platform.equals(SHARE_WECHATMOMENTS))) {
            //从shareSdk提炼出来的微信好友和朋友圈单独分享
            shareWechat(context, config, platform, true);
        } else {
            OnekeyShare oks = new OnekeyShare();
            oks.setTitleUrl(config.getUrl());
            oks.setUrl(config.getUrl());
            oks.setSite(context.getString(R.string.app_name));
            oks.setSiteUrl(config.getUrl());
            oks.setSilent(false);
            oks.setTheme(OnekeyShareTheme.CLASSIC);

            //qq
            oks.setImageUrl_qq(config.getImageUrl());
            oks.setTitle_qq(config.getTitle());
            oks.setShareContent_qq(config.getContent());
            oks.setShareUrl_qq(config.getUrl());
            oks.setJsParam_qq(null);
            //qzone
            oks.setImageUrl_qzone(config.getImageUrl());
            oks.setTitle_qzone(config.getTitle());
            oks.setShareContent_qzone(config.getContent());
            oks.setShareUrl_qzone(config.getUrl());
            //新浪微博
            oks.setImageUrl_sina(config.getImageUrl());//需要高级权限 新浪微博
            oks.setTitle_sina(config.getTitle());
            oks.setShareContent_sina(config.getContent());
            oks.setShareUrl_sina(config.getUrl());

            //微信好友
            Bitmap wechatEnableLogo = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ssdk_oks_classic_wechat);
            final String wechatLabel = context.getResources().getString(R.string.a_0230);
            View.OnClickListener wechatListener = new View.OnClickListener() {
                public void onClick(View v) {
                    shareWechat(context, config, SHARE_WECHAT, true);
                }
            };
            oks.setCustomerLogo(4, wechatEnableLogo, wechatLabel, wechatListener);

            //微信朋友圈
            Bitmap wechatmomentsLogo = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ssdk_oks_classic_wechatmoments);
            final String wechatmomentsLabel = context.getResources().getString(R.string.a_0229);
            View.OnClickListener wechatmomentsListener = new View.OnClickListener() {
                public void onClick(View v) {
                    shareWechat(context, config, SHARE_WECHATMOMENTS, true);
                }
            };
            oks.setCustomerLogo(5, wechatmomentsLogo, wechatmomentsLabel,
                    wechatmomentsListener);

            Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_copy);
            String label = context.getString(R.string.a_0220);
            View.OnClickListener listener = new View.OnClickListener() {
                public void onClick(View v) {
                    ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setText(config.getUrl()); // 复制
                    ToastUtils.showShort(context.getResources().getString(R.string.a_0221));
                }
            };
            oks.setCustomerLogo(5, logo, label, listener);
            if (!StringUtils.isEmptyOrNull(platform)) {
                oks.setPlatform(platform);
            }
            // 启动分享GUI
            oks.show(context);
        }

    }

    /**
     * 微信分享api初始化
     */
    private static IWXAPI mIwxapi;

    /**
     * 注册微信
     */
    public static void registerWechat(Context context) {
        mIwxapi = WXAPIFactory.createWXAPI(context, BuildConfig.WECHAT_APP_ID, false);
//        mIwxapi.registerApp(BuildConfig.WECHAT_APP_ID);
    }

    /**
     * 判断当前手机是否装微信或者微信版本是否支持
     *
     * @return true 可用
     */
    private static boolean isWXAppSupport() {
        if (null != mIwxapi) {
            return mIwxapi.isWXAppInstalled() && mIwxapi.isWXAppSupportAPI();
        }
        return false;
    }

    /**
     * 注销微信
     */
    public static void unRegisterWechat() {
        if (null != mIwxapi) {
            mIwxapi.unregisterApp();
            mIwxapi = null;
        }
    }

    /**
     * 微信分享
     *
     * @param context
     * @param platform 平台
     */
    private static void shareWechat(final Context context, final ShareEntityBean config, final String platform, boolean shareWeb) {
        registerWechat(context);
        if (!isWXAppSupport()) {
            ToastUtils.showShort(R.string.a_0231);
            unRegisterWechat();
            return;
        }
        if (shareWeb) {//分享网页
            showShareLoadingDialog(context);
            ImageLoader.getInstance()
                    .downloadImage(context, config.getImageUrl(), new ImageLoader.IDownloadCallback() {
                        @Override
                        public void onDownloadSuccess(File file) {
                            Bitmap bmp = ImageUtils.fileToBitmap(file);
                            byte[] thumbData = ImageUtils.getShareThumbData(bmp, 150, 150, 32);
                            if (null == mIwxapi) {
                                registerWechat(context);
                            }
                            shareWechatWeb(thumbData, platform, config);
                            hideShareLoadingDialog(context);
                        }

                        @Override
                        public void onDownFailed() {
//                                    ToastUtil.show(context.getResources().getString(R.string.ssdk_oks_share_failed));
//                                    unRegisterWechat();
                            if (null == mIwxapi) {
                                registerWechat(context);
                            }
                            shareWechatWeb(null, platform, config);

                            hideShareLoadingDialog(context);
                        }
                    });
        } else {
            //分享图片
            showShareLoadingDialog(context);
            sharePicDownload(context, 0, platform, config);
        }
    }

    /**
     * 微信分享网页
     */
    private static void shareWechatWeb(byte[] thumbData, String platform, ShareEntityBean config) {
        WXWebpageObject webpage = new WXWebpageObject();
        //分享的url
        webpage.webpageUrl = config.getUrl();

        WXMediaMessage msg = new WXMediaMessage(webpage);
//        msg.mediaObject = webpage;
        //分享的标题
        msg.title = config.getTitle();
        //分享的内容
        msg.description = config.getContent();
        if (null != thumbData && thumbData.length > 0) {
            //分享的缩略图
            msg.thumbData = thumbData;
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = getWechatScene(platform);
        mIwxapi.sendReq(req);

    }

    /**
     * 分享 图片最大下载次数
     */
    private final static int MAX_DOWNLOAD_NUM = 10;

    /**
     * 下载分享的图片
     *
     * @param count    当前下载次数，下载大图统计大图次数 下载小图统计小图次数
     * @param platform 分享的平台
     */
    private static void sharePicDownload(final Context context, final int count, final String platform, final ShareEntityBean config) {
        ImageLoader.getInstance().downloadImage(context, config.getImageUrl(), new ImageLoader.IDownloadCallback() {
            @Override
            public void onDownloadSuccess(File file) {
                if (null != file && file.exists()) {
                    if (null == mIwxapi) {
                        registerWechat(context);
                    }
                    shareWechatPic(file, platform);
                    hideShareLoadingDialog(context);
                } else {
                    onDownFailed();
                }
            }

            @Override
            public void onDownFailed() {
                if (count < MAX_DOWNLOAD_NUM) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int index = count + 1;
                            sharePicDownload(context, index, platform, config);
                        }
                    }, 100);
                } else {
                    ToastUtils.showShort(R.string.ssdk_oks_share_failed);
                    unRegisterWechat();
                }
            }
        });
    }

    /**
     * 微信分享图片
     */
    private static void shareWechatPic(File file, String platform) {
        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(file.toString());

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        Bitmap bmp = ImageUtils.fileToBitmap(file);
        byte[] thumbData = ImageUtils.getShareThumbData(bmp, 150, 150, 32);
        if (null != thumbData && thumbData.length > 0) {
            //分享的缩略图
            msg.thumbData = thumbData;
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = getWechatScene(platform);
        mIwxapi.sendReq(req);

    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private static int getWechatScene(String platform) {
        if (platform.equals(SHARE_WECHAT)) {//好友
            return SendMessageToWX.Req.WXSceneSession;
        } else if (platform.equals(SHARE_WECHATMOMENTS)) {//朋友圈
            return SendMessageToWX.Req.WXSceneTimeline;
        }
        return SendMessageToWX.Req.WXSceneTimeline;
    }

    /**
     * 分享 弹出第三方前显示loading框
     *
     * @param context
     */
    private static void showShareLoadingDialog(Context context) {
        if (context instanceof Activity) {
            if (context instanceof BaseActivity) {
                BaseActivity activity = (BaseActivity) context;
                activity.showLoadingDialog();
            } else if (context instanceof BaseActivity) {
                BaseActivity activity = (BaseActivity) context;
                activity.showLoadingDialog();
            }
        }
    }

    /**
     * 分享 弹出第三方后隐藏loadding
     *
     * @param context
     */
    private static void hideShareLoadingDialog(Context context) {
        if (context instanceof Activity) {
            if (context instanceof BaseActivity) {
                BaseActivity activity = (BaseActivity) context;
                activity.dismissLoadingDialog();
            } else if (context instanceof BaseActivity) {
                BaseActivity activity = (BaseActivity) context;
                activity.dismissLoadingDialog();
            }
        }
    }
}
