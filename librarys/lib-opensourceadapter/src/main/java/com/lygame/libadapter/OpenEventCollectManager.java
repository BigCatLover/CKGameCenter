package com.lygame.libadapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.lygame.libadapter.internal.IAnalysisHelper;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-11-02 18:04
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class OpenEventCollectManager implements IAnalysisHelper {

    private static final AtomicReference<OpenEventCollectManager> INSTANCE = new AtomicReference<OpenEventCollectManager>();

    private OpenEventCollectManager() {
        init(Utils.getContext());
    }

    private Context mContext;

    public static OpenEventCollectManager getInstance() {
        for (; ; ) {
            OpenEventCollectManager current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new OpenEventCollectManager();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        //添加时间统计
        eventers.add(new UmengAnalysisFactory().createAnalysis());
        LogUtils.e(getDeviceInfo(context));
    }


    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                result = rest == PackageManager.PERMISSION_GRANTED;
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            String mac = null;
            FileReader fstream = null;
            try {
                fstream = new FileReader("/sys/class/net/wlan0/address");
            } catch (FileNotFoundException e) {
                fstream = new FileReader("/sys/class/net/eth0/address");
            }
            BufferedReader in = null;
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    mac = in.readLine();
                } catch (IOException e) {
                } finally {
                    if (fstream != null) {
                        try {
                            fstream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private final List<IAnalysisHelper> eventers = new ArrayList<>();


    @Override
    public void onPause(Context context) {
        for (final IAnalysisHelper eventer : eventers) {
            eventer.onPause(context);
        }
    }

    @Override
    public void onResume(Context context) {
        for (final IAnalysisHelper eventer : eventers) {
            eventer.onResume(context);
        }
    }

    @Override
    public void onPageStart(String name) {
        for (final IAnalysisHelper eventer : eventers) {
            eventer.onPageStart(name);
        }
    }

    @Override
    public void onPageEnd(String name) {
        for (final IAnalysisHelper eventer : eventers) {
            eventer.onPageEnd(name);
        }
    }

    @Override
    public void onKillProcess(Context context) {
        for (final IAnalysisHelper eventer : eventers) {
            eventer.onKillProcess(context);
        }
    }

    @Override
    public void onEvent(Context context, String eventId) {
        for (final IAnalysisHelper eventer : eventers) {
            eventer.onEvent(context, eventId);
        }
    }

    @Override
    public void onEvent(Context context, String eventId, String label) {
        for (final IAnalysisHelper eventer : eventers) {
            eventer.onEvent(context, eventId, label);
        }
    }

    @Override
    public void onEvent(Context context, String eventId, Map<String, String> params) {
        for (final IAnalysisHelper eventer : eventers) {
            eventer.onEvent(context, eventId, params);
        }
    }

    @Override
    public void onEvent(Context context, String eventId, Map<String, String> params, int counter) {
        for (final IAnalysisHelper eventer : eventers) {
            eventer.onEvent(context, eventId, params, counter);
        }
    }

    @Override
    public void reportError(Context context, Throwable e) {
        for (final IAnalysisHelper eventer : eventers) {
            eventer.reportError(context, e);
        }
    }

    /**
     * umeng事件统计
     */
    public class UmengAnalysisFactory implements Factory {

        @Override
        public IAnalysisHelper createAnalysis() {
            return new IAnalysisHelper() {
                @Override
                public void init(Context context) {
                    // SDK在统计Fragment时，需要关闭Activity自带的页面统计，
                    // 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
                    MobclickAgent.openActivityDurationTrack(false);
                    MobclickAgent.setScenarioType(mContext, MobclickAgent.EScenarioType.E_UM_NORMAL);
                }

                @Override
                public void onPause(Context context) {
                    MobclickAgent.onPause(context);
                }

                @Override
                public void onResume(Context context) {
                    MobclickAgent.onResume(context);
                }

                @Override
                public void onPageStart(String name) {
                    MobclickAgent.onPageStart(name);
                }

                @Override
                public void onPageEnd(String name) {
                    MobclickAgent.onPageEnd(name);
                }

                @Override
                public void onKillProcess(Context context) {
                    MobclickAgent.onKillProcess(context);
                }

                @Override
                public void onEvent(Context context, String eventId) {
                    MobclickAgent.onEvent(context, eventId);
                }

                @Override
                public void onEvent(Context context, String eventId, String label) {
                    MobclickAgent.onEvent(context, eventId, label);
                }

                @Override
                public void onEvent(Context context, String eventId, Map<String, String> params) {
                    MobclickAgent.onEvent(context, eventId, params);
                }

                @Override
                public void onEvent(Context context, String eventId, Map<String, String> params, int counter) {
                    MobclickAgent.onEventValue(context, eventId, params, counter);
                }

                @Override
                public void reportError(Context context, Throwable e) {
                    MobclickAgent.reportError(context, e);
                }

            };
        }
    }
}
