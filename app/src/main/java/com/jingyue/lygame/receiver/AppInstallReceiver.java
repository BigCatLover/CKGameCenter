package com.jingyue.lygame.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * author janecer
 * 2014年4月18日上午11:58:45
 */
public class AppInstallReceiver extends BroadcastReceiver {
	private static final String TAG = "AppInstallReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
		}else if(intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")){//应用删除
		}
	}
}
