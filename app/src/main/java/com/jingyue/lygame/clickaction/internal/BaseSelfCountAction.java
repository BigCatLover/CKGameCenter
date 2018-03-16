package com.jingyue.lygame.clickaction.internal;

import android.content.Context;
import android.util.Log;

import com.jingyue.lygame.utils.StringUtils;
import com.laoyuegou.android.lib.utils.LogUtils;

/**
 * Created by wang on 15/5/20.
 * 只走自己接口上传数据的计数事件
 */
public abstract class BaseSelfCountAction extends BaseAction {

    public static final String A1 = "a1";
    public static final String A2 = "a2";
    public static final String A3 = "a3";
    public static final String A4 = "a4";
    public static final String A5 = "a5";

    public BaseSelfCountAction(Context context) {
        super(context);
    }

    @Override
    public void onRecord() {
        LogUtils.e("MD",getClass().getSimpleName() + " : 埋点！");
        LogUtils.e(params);
        String eventId = this.getEventId();
        if (context != null && !StringUtils.isEmptyOrNull(eventId)) {
            BiManager.getInstance().logEvent(eventId, params);
        }
    }
}