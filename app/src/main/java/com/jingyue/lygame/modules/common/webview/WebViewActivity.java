package com.jingyue.lygame.modules.common.webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.utils.AppJumpUtils;
import com.jingyue.lygame.utils.download.GlobalMonitor;
import com.jingyue.lygame.utils.GlobalParamsUtils;
import com.jingyue.lygame.utils.download.TasksManager;
import com.laoyuegou.android.lib.utils.AppUtils;
import com.laoyuegou.android.lib.utils.DebugUtils;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.UrlEncodeUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadList;
import com.liulishuo.filedownloader.FileDownloader;
import com.lygame.libadapter.utils.SaveCookiesInterceptor;

import java.io.File;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Created by admin on 2016/8/31.
 */
public class WebViewActivity extends BaseActivity {
    public final static String TITLE_NAME = "titleName";
    public static final String JS_SCHAME = "javascript:";
    public final static String URL = "url";
    public static final String TOKEN = "token";
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;

    private String titleName;
    private String url;

    //private CustomProgressDialog dialog;
    private WebSettings webSettings;
    private boolean needParams = true;
    private ActionBarUtil barUtil;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AndroidJSInterfaceForWeb.ACTION_SET_TOKEN:
                    if (webView != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(JS_SCHAME).append("setToken(\"")
                                .append(UrlEncodeUtils.encode(LoginManager.getInstance().getEncryptHsToken()))
                                .append("\")");
                        webView.loadUrl(stringBuilder.toString());
                    }
                    break;
                case AndroidJSInterfaceForWeb.ACTION_FINISH:
                    if (webView != null) {
                        if (!webView.canGoBack()) {
                            onBackPressed();
                        } else {
                            webView.goBack();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_webview;
    }

    private boolean webApp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            titleName = getIntent().getStringExtra(TITLE_NAME);
            url = getIntent().getStringExtra(URL);
            if (AppConstants.WEB_APP.equals(titleName)) {
                titleName = "";
                webApp = true;
            }
        }

        barUtil = ActionBarUtil.inject(this).title(titleName);

        setupUI();
    }


    @Override
    public void onLoginSucess() {
        super.onLoginSucess();
        if (webView != null) {
            loadUrl(webView.getUrl());
        }
    }

    @Override
    public void onBackPressed() {
        if (webApp) {
            if (!webView.canGoBack()) {
                AppJumpUtils.exitApp(this);
            } else {
                webView.goBack();
            }
        } else {
            if (!webView.canGoBack()) {
                finish();
            } else {
                webView.goBack();
            }
        }
//        super.onBackPressed();
    }

    private void setupUI() {
        if (!TextUtils.isEmpty(titleName)) {
            if (titleName.equals(getString(R.string.a_0291)) || titleName.isEmpty()) {
                needParams = false;
            }
        }
        if (TextUtils.isEmpty(url)) {
            ToastUtils.showShort(R.string.a_0290);
        } else {
            initWebClient();
            loadUrl(url);
        }
       /* ptrRefresh.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (!titleName.startsWith("密保") || !titleName.equals("忘记密码")) {
                    webView.reload();
                }
                ptrRefresh.refreshComplete();
            }
        });*/
    }

    private void loadUrl(String url) {
        //url = url.replaceAll("/dao/dist","");
        //此处逻辑可省略
        SaveCookiesInterceptor.syncPhpCookie(this, Uri.parse(UrlConstant.BASE_URL).getHost(), url);
        Uri uri = Uri.parse(url);
        if (!needParams) {
            webView.loadUrl(url);
        } else {
            final Uri.Builder builder = uri.buildUpon();
            builder.clearQuery();
            uri = builder.appendQueryParameter(TOKEN, LoginManager
                    .getInstance()
                    .getEncryptHsToken()
            ).build();
            webView.loadUrl(uri.toString(), GlobalParamsUtils.getInstance().httpParams().headers());
        }
    }

    private void initWebClient() {
        // WebView加载web资源
        webSettings = webView.getSettings();
        webView.setDownloadListener(new MyWebViewDownLoadListener());

        // 设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        // 设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // //支持通过JS打开新窗口
        webView.requestFocusFromTouch();

        webView.addJavascriptInterface(new AndroidJSInterfaceForWeb(this, mHandler), AppConstants.JS_CONTEXT);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                LogUtils.e("onPageFinished url = " + url);
                if (url.contains("wpa")) {//调用QQ
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoading();
            }


            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtils.e("onPageFinished url = " + url);
                if (webView.getContentHeight() > 0) {
                    hideLoading();
                }
                if (DebugUtils.DEBUG) {
                    CookieManager cookieManager = CookieManager.getInstance();
                    String CookieStr = cookieManager.getCookie(url);
                    LogUtils.e("Cookies = " + CookieStr);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                hideLoading();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(titleName)) {
                    barUtil.title(titleName);
                }
            }
        });
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            createDownTask(url).start();
        }
    }

    private BaseDownloadTask createDownTask(String url) {
        String appid = AppUtils.getMetaData(Utils.getContext(), "HS_APPID");
        int downloadId = TasksManager.getImpl().getDownloadId(url);
        BaseDownloadTask.IRunningTask task = FileDownloadList.getImpl().get(downloadId);
        BaseDownloadTask baseDownloadTask = null;
        if (task != null && task instanceof BaseDownloadTask) {//如果已经在队列中，取出来
            baseDownloadTask = (BaseDownloadTask) task;
        } else {
            baseDownloadTask = FileDownloader.getImpl().create(url);
//            baseDownloadTask.setCallbackProgressTimes(200);
//            baseDownloadTask.setMinIntervalUpdateSpeed(400);
            baseDownloadTask.setTag(GlobalMonitor.TAG_GAME_ID, appid);
            //设置下载文件放置路径
            baseDownloadTask.setPath(AppConstants.AVATER_DEFAULT_DIR + File.separator + url);
        }
        baseDownloadTask.setListener(null);
        return baseDownloadTask;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {//防止内存泄漏
            webView.removeAllViews();
            ViewParent parent = webView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.destroy();
            webView = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (webSettings != null) {
            webSettings.setJavaScriptEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webSettings != null) {
            webSettings.setJavaScriptEnabled(true);
        }
    }

    public static void start(Context context, String titleName, String url) {
        if (!NetworkUtils.hasNetwork(context)) {
            ToastUtils.showShort(R.string.a_0289);
            return;
        }
        Intent starter = new Intent(context, WebViewActivity.class);
        starter.putExtra(TITLE_NAME, titleName);
        starter.putExtra(URL, url);
        context.startActivity(starter);
    }
}
