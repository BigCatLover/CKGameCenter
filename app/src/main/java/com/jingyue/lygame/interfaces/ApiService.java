package com.jingyue.lygame.interfaces;

import com.jingyue.lygame.bean.CodeinfoBean;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.LoginInfoBean;
import com.jingyue.lygame.bean.LoginstateBean;
import com.jingyue.lygame.bean.ShopImageBean;
import com.jingyue.lygame.bean.VersionInfoBean;
import com.jingyue.lygame.bean.internal.BaseResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-23 15:48
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public interface ApiService {

    @GET("{url}")
    Observable<ResponseBody> executeGet(
            @Path("url") String url,
            @HeaderMap Map<String, String> hMaps,
            @QueryMap Map<String, String> maps);

    @GET("{url}")
    Observable<ResponseBody> executeGet(
            @Path("url") String url,
            @QueryMap Map<String, String> maps);

    @POST("{url}")
    Observable<ResponseBody> executePost(
            @Path("url") String url,
            @QueryMap Map<String, String> maps);

    @POST("{url}")
    Observable<ResponseBody> executeCachePost(
            @Path("url") String url,
            @QueryMap Map<String, String> maps);

    @POST("{url}")
    Observable<ResponseBody> uploadFiles(
            @Path("url") String url,
            @Path("headers") Map<String, String> headers,
            @Part("filename") String description,
            @PartMap() Map<String, RequestBody> maps);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Header("Range") String range, @Url String fileUrl);

    /**
     * 登录接口
     *
     * @param userName
     * @return
     */
    @GET("/mobile.php/AppApiDev2/login")
    Observable<BaseResponse<LoginInfoBean>> login(
            @Query("user_name") String userName,
            @Query("password") String passWord);

    /**
     * 登出接口
     *
     * @return
     */
    @GET("/mobile.php/AppApiDev2/logout")
    Observable<BaseResponse> loginOut(
            @Header("token") String token);

    /**
     * 提交评论接口
     *
     * @return
     */
    @GET("mobile.php/AppApiDev2/comment")
    Observable<BaseResponse> commitComment(@Header("token") String token, @Query("app_id") String app_id,
                                           @Query("content") String content, @Query("phone_info") String phone_info,
                                           @Query("score") String score);

    /**
     * 取消收藏/关注接口
     *
     * @return
     */
    @GET("mobile.php/AppApiDev2/unlike")
    Observable<BaseResponse> cancelCollect(@Header("token") String token, @Query("app_id") String app_id);

    /**
     * 收藏/关注接口
     *
     * @return
     */
    @GET("mobile.php/AppApiDev2/like")
    Observable<BaseResponse> collect(@Header("token") String token, @Query("app_id") String app_id);


    /**
     * 预约/取消预约
     *
     * @return
     */
    @GET("mobile.php/AppApiDev2/sub")
    Observable<BaseResponse> sub(@Header("token") String token, @Query("app_id") String app_id, @Query("action") String action);


    /**
     * 回复评论接口
     *
     * @return
     */
    @GET("mobile.php/AppApiDev2/reply")
    Observable<BaseResponse> reply(@Header("token") String token, @Query("app_id") String app_id,
                                   @Query("content") String content, @Query("phone_info") String phone_info,
                                   @Query("parent_id") String parent_id);

    /**
     * 评论点赞或者踩
     *
     * @return
     */
    @GET("mobile.php/AppApiDev2/commentLike")
    Observable<BaseResponse> commentLike(@Header("token") String token, @Query("comment_id") String comment_id,
                                         @Query("action") String action);

    /**
     * 专题点赞
     *
     * @return
     */
    @GET("mobile.php/AppApiDev2/likeSubject")
    Observable<BaseResponse> likeSubject(@Header("token") String token, @Query("id") String subject_id);

    /**
     * 专题取消点赞
     *
     * @return
     */
    @GET("mobile.php/AppApiDev2/unlikeSubject")
    Observable<BaseResponse> unlikeSubject(@Header("token") String token, @Query("id") String subject_id);

    /**
     * 引导页提交
     *
     * @return
     */
    @GET("mobile.php/AppApiDev2/guide")
    Observable<BaseResponse> guide(@Query("tag") String tag);


    /**
     * 检查登录状态
     *
     * @param token
     * @return
     */
    @GET("/mobile.php/AppApiDev2/loginState")
    Observable<BaseResponse<LoginstateBean>> loginState(
            @Header("token") String token
    );

    /**
     * 商店图
     *
     * @param appId
     * @return
     */
    @GET("/mobile.php/AppApiDev2/shopImg")
    Observable<BaseResponse<List<ShopImageBean>>> obtainShopImage(
            @Query("app_id") String appId
    );

    /**
     * 添加标签
     *
     * @param tag
     * @return
     */
    @GET("/mobile.php/AppApiDev2/addTag")
    Observable<BaseResponse<GameBean.Tag>> addTag(@Header("token") String token,
                                                  @Query("tag") String tag);

    /**
     * 选择标签
     *
     * @return
     */
    @GET("/mobile.php/AppApiDev2/selectTags")
    Observable<BaseResponse> selectTag(@Query("default") String defaultValue,
                                       @Query("user") String user,
                                       @Query("app_id") String appId);

    /**
     * 上传头像
     *
     * @return
     */
    @Multipart
    @POST("/mobile.php/AppApiDev2/uploadAvatar")
    Observable<BaseResponse> uploadAvatar(@Part("description") RequestBody description,
                                          @Part MultipartBody.Part file, @Header("token") String token);

    /**
     * 上传头像
     *
     * @return
     */
    @Multipart
    @POST("/mobile.php/AppApiDev2/uploadShopImg")
    Observable<BaseResponse<String>> uploadloadShopImage(@Header("token") String token,
                                                         @Query("app_id") String id,
                                                         //@Part("description") RequestBody description,
                                                         @Part MultipartBody.Part file);

    /**
     * 游戏标签页
     *
     * @return
     */
    @GET("/mobile.php/AppApiDev2/tagGameList")
    Observable<BaseResponse<GameBean.TagList>> obtainTagList(@Query("tag_id") String tagId);

    /**
     * 更新用户信息
     *
     * @param token
     * @return
     */
    @GET("/mobile.php/AppApiDev2/userinfo")
    Observable<BaseResponse<LoginInfoBean>> updateUserInfo(@Header("token") String token);


    /**
     * 发送手机验证码
     *
     * @param mobile
     * @return
     */
    @GET("/mobile.php/AppApiDev2/sendMobileCodeForBindMobile")
    Observable<BaseResponse<CodeinfoBean>> sendMobileCodeForRegister(@Query("mobile") String mobile);

    /**
     * 手机注册
     *
     * @param mobileCode
     * @return
     */
    @GET("/mobile.php/AppApiDev2/regByMobile")
    Observable<BaseResponse<LoginInfoBean>> regByMobile(
            @Query("mobile_code") String mobileCode,
            @Query("mobile") String mobile,
            @Query("password") String password,
            @Query("id") String id);

    /**
     * 用户名注册
     *
     * @param userName
     * @param password
     * @param imageCode
     * @return
     */
    @GET("/mobile.php/AppApiDev2/regByName")
    Observable<BaseResponse<LoginInfoBean>> regByName(
            @Query("id") String id,
            @Query("user_name") String userName,
            @Query("password") String password,
            @Query("img_verify_code") String imageCode);

    /**
     * 上传引导信息
     *
     * @param tag
     * @return
     */
    @GET("/mobile.php/AppApiDev2/guide")
    Observable<BaseResponse> uploadGuide(@Query("tag") String tag);

    /**
     * 请求图片验证码信息
     */
    @GET("/mobile.php/AppApiDev2/codeForApp")
    Observable<BaseResponse<CodeinfoBean>> getCodeInfo();

    /**
     * 请求版本信息
     * @return
     */
    @GET("/mobile.php/AppApiDev2/version")
    Observable<BaseResponse<VersionInfoBean>> versionCheck(@Query("versionCode") int versionCode);

    /**
     * 下载更新下载量
     * @return
     */
    @GET("/mobile.php/AppApiDev2/downGame")
    Observable<BaseResponse> downGame(@Query("app_id") String appid);
}
