package com.sun.cloud.http.base;


/**
 * Created on 2021/4/29
 * <p>
 *
 * @author sunxiaoyun
 */
public interface BooleanFunction<T> {

    /**
     * @return boolean
     */
    boolean apply(T t);
}
