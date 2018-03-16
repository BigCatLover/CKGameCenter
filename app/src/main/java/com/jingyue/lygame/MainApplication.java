package com.jingyue.lygame;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;

import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.utils.AppJumpUtils;
import com.jingyue.lygame.utils.GlobalParamsUtils;
import com.laoyuegou.android.lib.utils.CrashUtils;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.lygame.libadapter.HttpRequestFactory;
import com.lygame.libadapter.ImageLoader;
import com.lygame.libadapter.OpenEventCollectManager;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;
import com.youku.cloud.player.YoukuPlayerConfig;

import java.lang.ref.WeakReference;
import java.net.Proxy;

//import android.support.multidex.MultiDex;


/**
 * Custom Application
 * Created by TonyChen on 15/4/23.
 */
public class MainApplication extends Application implements CrashUtils.CrashAppListener {

    private static WeakReference<Activity> mCurrentActivity;

    public static void setCurrentActivity(Activity mCurrentActivity) {
        MainApplication.mCurrentActivity = new WeakReference<Activity>(mCurrentActivity);
    }

    public static boolean isTopActivity(Activity activity) {
        return activity == mCurrentActivity.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        initConfig();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(base);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        FlowManager.destroy();
        mCurrentActivity = null;
        OpenEventCollectManager.getInstance().onKillProcess(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ImageLoader.getInstance().cleanMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        ImageLoader.getInstance().onTrimMemory(level);
    }

    private Runnable mInitTask;
    private Handler mInitHandler;

    private void initConfig() {
        long begin = System.currentTimeMillis();
        //utils上下文环境初始化
        Utils.init(this);
        //初始化log
        initLog();
        //异常处理工具初始化
        CrashUtils.init();
        CrashUtils.setCrashListener(this);
        //初始化请求工具类
        HttpRequestFactory.init(this, BuildConfig.BASE_URL);
        //数据库初始化
        FlowManager.init(new FlowConfig.Builder(this).build());
        GlobalParamsUtils.getInstance();
        //图片加载工具
        ImageLoader.getInstance().init(this);
        if (mInitTask == null) {
            mInitTask = new InitTask(this);
        }
        if (mInitHandler == null) {
            HandlerThread thread = new HandlerThread("initThread");
            thread.start();
            mInitHandler = new Handler(thread.getLooper());
        }
        mInitHandler.post(mInitTask);
        LogUtils.e("启动初始化耗时：" + (System.currentTimeMillis() - begin) + "ms");
    }

    private void initLog() {
        LogUtils.Builder builder = new LogUtils.Builder()
                .setLogSwitch(BuildConfig.DEBUG)// 设置log总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLogHeadSwitch(true)// 设置log头信息开关，默认为开
                .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
                .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setConsoleFilter(LogUtils.V)// log的控制台过滤器，和logcat过滤器同理，默认Verbose
                .setFileFilter(LogUtils.V);// log文件过滤器，和logcat过滤器同理，默认Verbose
        LogUtils.d(builder.toString());
    }

    /**
     * 当app退出时如何处理
     * 回調來自crashandler
     */
    @Override
    public void onFinishApp(final Thread t, final Throwable e) {
        OpenEventCollectManager.getInstance().reportError(this, e);
        AppJumpUtils.exitApp(getApplicationContext());
    }

    /**
     * 初始化任务
     */
    private class InitTask implements Runnable {

        private Context context;

        InitTask(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            if (AppConstants.isInited) {
                sendBroadcast(new Intent(AppConstants.ACTION_INITED));
                return;
            }
            OpenEventCollectManager.getInstance().init(context);
            LoginManager.getInstance();
            FileDownloader.setupOnApplicationOnCreate(MainApplication.this)
                    .connectionCreator(new FileDownloadUrlConnection
                            .Creator(new FileDownloadUrlConnection.Configuration()
                            .connectTimeout(15_000) // set connection timeout.
                            .readTimeout(15_000) // set read timeout.
                            .proxy(Proxy.NO_PROXY) // set proxy
                    ))
                    .commit();
            FileDownloader.getImpl().setMaxNetworkThreadCount(3);
            YoukuPlayerConfig.setClientIdAndSecret(BuildConfig.YK_CLIENTID, BuildConfig.YK_SECRETKEY);
            YoukuPlayerConfig.onInitial((Application) context);
            YoukuPlayerConfig.setLog(false);
            AppConstants.isInited = true;
            //初始化完成
            sendBroadcast(new Intent(AppConstants.ACTION_INITED));
        }
    }
}
