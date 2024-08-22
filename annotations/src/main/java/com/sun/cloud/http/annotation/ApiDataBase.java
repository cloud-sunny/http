package com.sun.cloud.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created on 2021/4/29
 * <p>
 * 数据库fetch策略
 * 目前只能用于返回LiveData类型的接口方法注解
 * 此注解仅暴露提供数据库数据的liveData，不参与DB数据存储与访问
 * <p>
 * 同一个java文件中，只能配合 {@link ApiRepository} 使用
 *
 * @author sunxiaoyun
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface ApiDataBase {

    /**
     * 数据库策略，默认先从数据库获取，再从服务器获取
     *
     * @return 数据库获取策略
     */
    Strategy value() default Strategy.DB_AND_SERVER;

    enum Strategy {
        /**
         * 先从数据库获取，再从服务器获取
         */
        DB_AND_SERVER,
        /**
         * 先从数据库获取，如果数据库数据为空，则从服务器获取
         */
        DB_EMPTY_FETCH_SERVER,
        /**
         * 仅从数据库获取
         */
        DB_ONLY
    }
}
