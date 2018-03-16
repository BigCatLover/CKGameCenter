package com.lygame.libadapter;

import android.content.Context;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-21 10:03
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 *
 * 推送初始化集合类
 */
public class OpenIMPushFactory {

    private static final AtomicReference<OpenIMPushFactory> INSTANCE = new AtomicReference<OpenIMPushFactory>();

    private OpenIMPushFactory(){}
    private static Context mContext;
    public static void init(Context context) {
        mContext = context;
    }

    public static OpenIMPushFactory getInstance() {
       for (; ; ) {
          OpenIMPushFactory current = INSTANCE.get();
             if (current != null) {
                return current;
             }
             current = new OpenIMPushFactory();
             if (INSTANCE.compareAndSet(null, current)) {
                 return current;
             }
        }
    }
}
