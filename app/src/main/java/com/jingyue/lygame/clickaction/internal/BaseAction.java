package com.jingyue.lygame.clickaction.internal;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wang on 15/5/20.
 */
public abstract class BaseAction {

    protected Context context;
    protected Map<String, String> params;

    public BaseAction(Context context) {
        this.context = context;
        params = new HashMap<>();
    }

    public Map<String, String> getParams() {
        return params;
    }

    public abstract void onRecord() throws Exception;

    public abstract String getEventId();

}
