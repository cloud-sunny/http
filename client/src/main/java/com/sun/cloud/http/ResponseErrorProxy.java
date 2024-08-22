package com.sun.cloud.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sun.cloud.http.base.IResponse;
import com.sun.cloud.http.error.DefaultResponseErrorHandler;
import com.sun.cloud.http.error.IErrorHandler;

/**
 * Created on 2021/4/14
 * <p>
 * 异常转化为Response/Message代理类
 *
 * @author sunxiaoyun
 */
public class ResponseErrorProxy {
    static IErrorHandler defaultErrorHandler;

    public static void setProxy(IErrorHandler handler) {
        defaultErrorHandler = handler;
    }

    public static String errorToMessage(Throwable throwable, boolean nullable) {
        if (defaultErrorHandler == null) {
            defaultErrorHandler = new DefaultResponseErrorHandler();
        }
        return defaultErrorHandler.errorToMessage(throwable, nullable);
    }

    @Nullable
    public static String errorToNullableMessage(Throwable throwable) {
        return errorToMessage(throwable, true);
    }

    @NonNull
    public static String errorToNoNullMessage(Throwable throwable) {
        return errorToMessage(throwable, false);
    }

    public static <T, R extends IResponse<T>> R errorToResponse(Throwable throwable) {
        if (defaultErrorHandler == null) {
            defaultErrorHandler = new DefaultResponseErrorHandler();
        }
        return defaultErrorHandler.errorToResponse(throwable);
    }
}
