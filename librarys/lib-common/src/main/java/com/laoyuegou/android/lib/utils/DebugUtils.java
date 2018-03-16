package com.laoyuegou.android.lib.utils;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-07-01 15:14
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class DebugUtils {

    //TODO PUbLISH
    public static final boolean DEBUG = false;

    public static void mockTakeTime(){
        if(DEBUG){
            try{
                Thread.sleep(2000);
            }catch (InterruptedException err){
            }
        }
    }

}
