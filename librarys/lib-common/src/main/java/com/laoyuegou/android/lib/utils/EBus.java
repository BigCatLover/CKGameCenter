package com.laoyuegou.android.lib.utils;

import org.greenrobot.eventbus.EventBus;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-12 20:08
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class EBus {

    private static EventBus eventBus = EventBus.getDefault();

    public static EventBus getDefault() {
        return eventBus;
    }
}
