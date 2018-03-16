package com.lygame.libadapter.internal;

import android.content.Context;

import java.util.Map;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-11-02 18:05
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 *  事件统计
 */
public interface IAnalysisHelper {

    /**
     * 初始化事件统计
     * @param context
     */
    void init(Context context);

    /**
     * 当前页被覆盖或者终结
     * @param context
     */
    void onPause(Context context);

    /**
     * 进入当前activity
     * @param context
     */
    void onResume(Context context);

    /**
     * fragment进入页面时
     * @param name
     */
    void onPageStart(String name);

    /**
     * fragment出页面时调用
     * @param name
     */
    void onPageEnd(String name);

    void onKillProcess(Context context);

    void onEvent(Context context,String eventId);

    void onEvent(Context context,String eventId,String label);

    void onEvent(Context context,String eventId,Map<String,String> params);

    void onEvent(Context context,String eventId,Map<String,String> params,int counter);

    void reportError(Context context,Throwable e);

    interface Factory{

        IAnalysisHelper createAnalysis();

    }
}
