package com.jingyue.lygame.utils.rxJava;

import com.laoyuegou.android.lib.utils.GsonUtils;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-04 12:46
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class ResponseBodyFunction<R> implements Function<ResponseBody,R>{

    private Class<R> rClass;

    public ResponseBodyFunction(Class<R> rClass) {
        this.rClass = rClass;
    }

    @Override
    public R apply(@NonNull ResponseBody responseBody) throws Exception {
        return GsonUtils.getInstance().fromJson(responseBody.string(),rClass);
    }
}
