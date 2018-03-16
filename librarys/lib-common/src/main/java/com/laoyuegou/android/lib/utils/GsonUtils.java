package com.laoyuegou.android.lib.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 全局Gson单例
 * Author       : yizhihao
 * Create time  : 2016-12-16 下午4:36
 * email        : 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class GsonUtils {

    private Gson sGson;

    private static final AtomicReference<GsonUtils> INSTANCE = new AtomicReference<GsonUtils>();

    private GsonUtils() {
        sGson = new GsonBuilder()
                //.excludeFieldsWithoutExposeAnnotation() //不导出实体中没有用@Expose注解的属性
                .enableComplexMapKeySerialization() //支持Map的key为复杂对象的形式
                .serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")//时间转化为特定格式
                .setLenient()
                //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)//会把字段首字母大写,注:对于实体上使用了@SerializedName注解的不会生效.
                //.setPrettyPrinting() //对json结果格式化.
                //.setVersion(1.0)    //有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
                //@Since(版本号)能完美地实现这个功能.还的字段可能,随着版本的升级而删除,那么
                //@Until(版本号)也能实现这个功能,GsonBuilder.setVersion(double)方法需要调用.
                .create();
    }

    public static GsonUtils getInstance() {
        for (; ; ) {
            GsonUtils current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new GsonUtils();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    public Gson getGson() {
        return sGson;
    }

    public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return sGson.fromJson(json, classOfT);
    }

    public <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        return sGson.fromJson(json, typeOfT);
    }

    public String toJson(Object src) {
        return sGson.toJson(src);
    }

    public static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }
}
