package com.jingyue.lygame.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebView;

import com.jingyue.lygame.bean.DeviceBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created by liu hong liang on 2016/11/11.
 */
public class DeviceUtil {

    public static DeviceBean getDeviceBean(Context context) {
        DeviceBean deviceBean = new DeviceBean();
        deviceBean.setUserua(getUserUa(context));
        deviceBean.setLocal_ip(getHostIP());
        getNetIp(deviceBean);
        String deviceId = getDeviceId(context);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = "null";
        }
        deviceBean.setDeviceId(deviceId);
        String mac = getMac();
        if (TextUtils.isEmpty(mac)) {
            mac = "null";
        }
        deviceBean.setMac(mac);
        deviceBean.setDeviceId(deviceId);
        deviceBean.setUserua(getUserUa(context));
        StringBuffer deviceInfoSb = new StringBuffer();
        deviceInfoSb.append(getPhoneNum(context)).append("||android").
                append(Build.VERSION.RELEASE).append("||").
                append(mac).append("||").
                append(deviceId).append("||").
                append(getPhoneModel()).append("||").
                append(getOperators(context));
        deviceBean.setDeviceinfo(deviceInfoSb.toString());
        return deviceBean;
    }

    /**
     * 获取SIM卡运营商
     *
     * @param context
     * @return
     */
    public static String getOperators(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String operator = null;
        String IMSI = null;
        try {
            IMSI = tm.getSubscriberId();
        } catch (SecurityException e) {
        }
        if (IMSI == null || IMSI.equals("")) {
            return operator;
        }
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            operator = "中国移动";
        } else if (IMSI.startsWith("46001")) {
            operator = "中国联通";
        } else if (IMSI.startsWith("46003")) {
            operator = "中国电信";
        }
        if (TextUtils.isEmpty(operator)) {
            return "null";
        } else {
            return operator;
        }
    }

    /**
     * 手机型号
     *
     * @return
     */
    public static String getPhoneModel() {
        if (TextUtils.isEmpty(Build.MODEL)) {
            return "null";
        } else {
            return Build.MODEL;
        }
    }

    public static String getPhoneNum(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNum = "null";
        try {
            phoneNum = telephonyManager.getLine1Number();
        } catch (SecurityException e) {
        }
        if (TextUtils.isEmpty(phoneNum)) {
            return "null";
        } else {
            return phoneNum;
        }
    }

    //获取外网IP
    public static void getNetIp(final DeviceBean deviceBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL infoUrl = null;
                try {
//                    Log.e("zl","ip in");
                    infoUrl = new URL("http://city.ip138.com/ip2city.asp");
                    BufferedReader br = new BufferedReader(new InputStreamReader(infoUrl.openStream()));
                    StringBuilder strber = new StringBuilder();
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        strber.append(line + "\n");
                    }
                    br.close();
                    //从反馈的结果中提取出IP地址
                    line = strber.toString();
                    int start = line.indexOf("[");
                    int end = line.indexOf("]");
                    // 修复
                    // java.lang.StringIndexOutOfBoundsException:  length=218;  regionStart=0;  regionLength=-1
                    if (start > end && start != -1 && end != -1) {
                        line = line.substring(start + 1, 1 + end);
                    } else {
                        line = null;
                    }
                    if (line == null) {
                        deviceBean.setLogin_ip("");
                    } else {
                        deviceBean.setLogin_ip(line);
                    }
                } catch (MalformedURLException e) {
                    deviceBean.setLogin_ip("");
                    e.printStackTrace();
                } catch (IOException e) {
                    deviceBean.setLogin_ip("");
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 获取ua信息
     *
     * @throws UnsupportedEncodingException
     */
    public static String getUserUa(Context context) {
        WebView webview = new WebView(context);
        webview.layout(0, 0, 0, 0);
        String str = webview.getSettings().getUserAgentString();
        return str;
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
    }

    // IMEI码
    private static String getIMIEStatus(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        return deviceId;
    }

    // Mac地址
    private static String getLocalMac(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    private static String getMac() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macSerial;
    }

    // Android Id
    private static String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static boolean isPhone(Context context) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int type = telephony.getPhoneType();
        return type != 0;
    }

    /**
     * 获取设备ID
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        String deviceId = "";
        if (isPhone(context)) {//是通信设备使用设备id
            TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                deviceId = telephony.getDeviceId();
            } catch (SecurityException e) {
            }
        } else {//使用android_id
            deviceId = Settings.Secure.getString(context.getContentResolver(), "android_id");
        }
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        //使用mac地址
        try {
            deviceId = getLocalMac(context).replace(":", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        //使用UUID
        UUID uuid = UUID.randomUUID();
        deviceId = uuid.toString().replace("-", "");
        return deviceId;
    }

    /**
     * 获取当前应用程序的版本号
     *
     * @return
     * @author wangjie
     */
    public static int getAppVersionCode(Context context) {
        int version = 1;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 判断是魅族操作系统
     * <h3>Version</h3> 1.0
     * <h3>CreateTime</h3> 2016/6/18,9:43
     * <h3>UpdateTime</h3> 2016/6/18,9:43
     * <h3>CreateAuthor</h3> vera
     * <h3>UpdateAuthor</h3>
     * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
     *
     * @return true 为魅族系统 否则不是
     */
    public static boolean isMeizuFlymeOS() {
/* 获取魅族系统操作版本标识*/
        String meizuFlymeOSFlag = getSystemProperty("ro.build.display.id", "");
        if (TextUtils.isEmpty(meizuFlymeOSFlag)) {
            return false;
        } else
            return meizuFlymeOSFlag.contains("flyme") || meizuFlymeOSFlag.toLowerCase().contains("flyme");
    }

    /**
     * 获取系统属性
     * <h3>Version</h3> 1.0
     * <h3>CreateTime</h3> 2016/6/18,9:35
     * <h3>UpdateTime</h3> 2016/6/18,9:35
     * <h3>CreateAuthor</h3> vera
     * <h3>UpdateAuthor</h3>
     * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
     *
     * @param key          ro.build.display.id
     * @param defaultValue 默认值
     * @return 系统操作版本标识
     */
    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
