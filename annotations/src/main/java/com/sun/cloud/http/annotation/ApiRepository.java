package com.sun.cloud.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 2019/10/31
 * <p>
 * 注解请求ApiService接口，用于生成请求Repository
 *
 * @author sunxiaoyun
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ApiRepository {
    /**
     * 直接配置url
     */
    String value() default "";

    /**
     * 通过枚举类型配置当前url映射
     */
    HttpURL baseUrl() default HttpURL.APP;

    /**
     * 通过自定义key配置url映射
     */
    String urlKey() default "";

    /**
     * 通过自定义key构造okttp系列
     */
    String key() default "default";
}