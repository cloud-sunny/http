package com.sun.cloud.http.error;

import com.sun.cloud.http.base.IResponse;

import javax.annotation.Nullable;

/**
 * Created on 2021/4/15
 * <p>
 *
 * @author sunxiaoyun
 */
public interface IErrorHandler {
    /**
     * 异常转化为提示消息
     *
     * @param throwable 异常
     * @param nullable  返回是否为空
     * @return message
     */
    @Nullable
    String errorToMessage(@Nullable Throwable throwable, boolean nullable);

    /**
     * 异常转化为response
     *
     * @param throwable 异常
     * @param <T>       数据类型
     * @param <R>       自定义类型
     * @return response
     */
    <T, R extends IResponse<T>> R errorToResponse(@Nullable Throwable throwable);
}
