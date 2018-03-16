package com.jingyue.lygame.clickaction;

import android.content.Context;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-11-06 11:24
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class ClickActionUtils {

    private static final AtomicReference<ClickActionUtils> INSTANCE = new AtomicReference<ClickActionUtils>();

    private ClickActionUtils(){}

    private Context mContext;

    public static ClickActionUtils getInstance() {
       for (; ; ) {
          ClickActionUtils current = INSTANCE.get();
             if (current != null) {
                return current;
             }
             current = new ClickActionUtils();
             if (INSTANCE.compareAndSet(null, current)) {
                 return current;
             }
        }
    }

    public void init(Context context){
        mContext = context.getApplicationContext();
    }
}
