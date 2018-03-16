package com.jingyue.lygame.bean.internal;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-23 18:21
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class BaseResponse<T> {

    public static final int INVLIDE_CODE = Integer.MIN_VALUE;
    public static final int NULL_VALUE_CODE = -3;
    /**
     * 服务端返回登录失效
     */
    public static final int UN_LOGIN = 1;
    public static final int SUCESS_CDDE = 0;

    @SerializedName("code")
    public int errCode = INVLIDE_CODE;

    @SerializedName("msg")
    public String errMsg;

    @SerializedName("data")
    @Nullable
    public T realData;

    /**
     * 請求結果是否來自緩存
     */
    public boolean fromCache = false;

    public BaseResponse<T> setData(T data) {
        realData = data;
        return this;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "errCode='" + errCode + '\'' +
                ", errMsg='" + errMsg + '\'' +
                ", data=" + realData +
                '}';
    }


}
