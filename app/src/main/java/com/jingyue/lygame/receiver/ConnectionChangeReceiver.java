package com.jingyue.lygame.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.jingyue.lygame.events.NetConnectEvent;
import com.laoyuegou.android.lib.utils.EBus;
import com.laoyuegou.android.lib.utils.NetworkUtils;

/**
 * @author Javen
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
    private static final String TAG = ConnectionChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean flag = false;
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {//在此监听wifi有无
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    if (NetworkUtils.getNetworkType(context) == NetworkUtils.NETWORK_TYPE_MOBILE) {
                        EBus.getDefault().post(new NetConnectEvent(NetConnectEvent.TYPE_WIFI_DISCONNECTED));
                        flag = true;
                    }
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    break;
            }
        }
        if (!flag) {
            if (NetworkUtils.getNetworkType(context) == NetworkUtils.NETWORK_TYPE_NONE) {
                EBus.getDefault().post(new NetConnectEvent(NetConnectEvent.TYPE_NETWORK_DISCONNECTED));
            }
        }

    }
}