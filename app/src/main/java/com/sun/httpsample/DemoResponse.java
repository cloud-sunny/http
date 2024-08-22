package com.sun.httpsample;

import com.alibaba.fastjson.annotation.JSONField;
import com.sun.cloud.http.base.IResponse;

/**
 * Created on 2021/4/15
 * <p>
 *
 * @author sunxiaoyun
 */
public class DemoResponse<T> implements IResponse<T> {

    @JSONField(name = "status")
    public int code;

    public String message;

    public T data;

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
        return code == 200;
    }
}
