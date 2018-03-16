package com.lygame.libadapter.utils;

import com.laoyuegou.android.common.glide.DebugConstant;
import com.laoyuegou.android.lib.utils.LogUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-22 11:27
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class LoggingInterceptor implements Interceptor {

    private boolean debugMode = DebugConstant.isDebug;

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!debugMode) {
            return chain.proceed(chain.request());
        }
        //这个chain里面包含了request和response，所以你要什么都可以从这里拿
        Request request = chain.request();

        long t1 = System.nanoTime();//请求发起的时间
        final RequestBody body = chain.request().body();//chain.request().body().contentType().type();
        StringBuilder partStr = new StringBuilder();
        if (body != null) {
            if (body instanceof MultipartBody) {
                List<MultipartBody.Part> parts = ((MultipartBody) body).parts();
                partStr.append("part size : " + parts != null ? parts.size() : 0).append("\n");
                partStr.append(body.contentType()).append("\n");
                if(parts.size() > 0){
                    partStr.append("==========\n");
                }
                for (final MultipartBody.Part part : parts) {
                    partStr.append("Head:")
                            .append(part.headers()).append("\n")
                            .append("Body - contentType:").append(part.body().contentType()).append("\n")
                            .append("     - contentLength: ").append(part.body().contentLength()).append("\n")
                            .append("\n");
                }

            }
        }
        LogUtils.e(String.format("" +
                        "发送请求 %s " +
                        "\nBodyStr:  %s" +
                        "\nHeaders:  %s",
                request.url(),
                partStr.toString(),
                request.headers()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();//收到响应的时间

        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        ResponseBody responseBody = response.peekBody(1024 * 1024);

        LogUtils.e(String.format("接收响应: [%s]" +
                        "\n %n返回json:【%50s】 " +
                        "\n请求执行时间%.1fms" +
                        "\n%n%s",
                response.request().url(),
                responseBody.string(),
                (t2 - t1) / 1e6d,
                response.headers()));
        return response;
    }

}
