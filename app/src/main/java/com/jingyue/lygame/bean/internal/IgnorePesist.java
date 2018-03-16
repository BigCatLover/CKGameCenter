package com.jingyue.lygame.bean.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-30 10:17
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 *
 * 非持久化对象
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface IgnorePesist {
}
