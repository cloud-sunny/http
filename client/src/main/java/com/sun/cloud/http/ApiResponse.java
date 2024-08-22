package com.sun.cloud.http;


import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.sun.cloud.http.base.IResponse;

import javax.annotation.Nullable;

/**
 * Created on 2018/8/22 18:55
 *
 * @author WingHawk
 */
@SuppressWarnings("unused")
public class ApiResponse<T> implements IResponse<T> {
    public T data;
    public String message;
    public String status;
    public String identifier;
    public String uri;

    public static String errorToMessage(@Nullable Throwable throwable) {
        return ResponseErrorProxy.errorToMessage(throwable, true);
    }

    @NonNull
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public boolean isSuccessful() {
        if (status == null) {
            return false;
        }
        try {
            long code = Long.parseLong(status);
            return code == 0 || (code >= 200 && code < 300);
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }
}
