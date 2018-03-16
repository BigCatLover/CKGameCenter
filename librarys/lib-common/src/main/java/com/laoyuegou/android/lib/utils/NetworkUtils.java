/*
Copyright 2015 shizhefei（LuckyJayce）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.laoyuegou.android.lib.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

	public static final int NETWORK_TYPE_NONE = -1; //无网络
	public static final int NETWORK_TYPE_WIFI = 0x01;//wifi
	public static final int NETWORK_TYPE_MOBILE = 0x02;//移动网络

	/**
	 * 判断图片是否来自网络
	 * 如果是Http,https，ftp开头都是网络图片
	 */
	public static boolean isImgFromNet(String url){
		return null != url && (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("ftp://"));
	}


	/**
	 * 是否有网络连接
	 * 
	 * @param paramContext
	 * @return
	 */
	public static boolean hasNetwork(Context paramContext) {
		try {
			ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
			if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
				return true;
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
		}
		return false;
	}

	/**
	 * {@link android.Manifest.permission#ACCESS_NETWORK_STATE}.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if (info == null)
			return false;
		return info.getType() == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * 获取当期连接的网络类型
	 *
	 * @param context
	 * @return
	 */
	public static int getNetworkType(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.
				getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == connectivityManager) {
			return NETWORK_TYPE_NONE;
		}
		NetworkInfo mobileNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (null != wifiNetworkInfo && wifiNetworkInfo.isConnected()) {
			return NETWORK_TYPE_WIFI;
		} else if (null != mobileNetworkInfo && mobileNetworkInfo.isConnected()) {
			return NETWORK_TYPE_MOBILE;
		} else {
			return NETWORK_TYPE_NONE;
		}
	}

}
