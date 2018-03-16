package com.jingyue.lygame.interfaces;


import com.jingyue.lygame.bean.internal.BaseResponse;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 事件上传接口
 */
public interface BiService {

    /**
     * 上传事件统计
     * @return
     */
    @FormUrlEncoded
    @POST("/stat/log2")
    Observable<BaseResponse> logEvent(@Header("Auth-Timestamp") String timeStamp,
                                      @Header("Auth-Sign") String authSign,
                                      @Field("log") String log);

}
