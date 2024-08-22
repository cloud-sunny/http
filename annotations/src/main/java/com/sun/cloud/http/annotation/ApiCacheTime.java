package com.sun.cloud.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 2021/4/29
 * <p>
 * 接口缓存时间配置
 * <p>
 * 同一个java文件中，只能配合 {@link ApiRepository} 使用
 *
 * @author sunxiaoyun
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface ApiCacheTime {

    /**
     * 接口数据缓存配置，默认不缓存
     *
     * @return cacheTimeInSec 数据缓存时间，单位秒
     */
    int value() default 0;

    interface CacheTime {
        /**
         * 一分钟
         */
        int ONE_MIN = 60;
        /**
         * 半小时
         */
        int HALF_HOUR = 30 * ONE_MIN;
        /**
         * 一小时
         */
        int ONE_HOUR = 2 * HALF_HOUR;
        /**
         * 半天
         */
        int HALF_DAY = 12 * ONE_HOUR;
        /**
         * 一天
         */
        int ONE_DAY = 24 * ONE_HOUR;
        /**
         * 三天
         */
        int THREE_DAY = 3 * ONE_DAY;
        /**
         * 七天
         */
        int SEVEN_DAY = 7 * ONE_DAY;
        /**
         * 半个月
         */
        int HALF_MONTH = 15 * ONE_DAY;
        /**
         * 一个月
         */
        int ONE_MONTH = 30 * ONE_DAY;
    }
}
