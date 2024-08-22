package com.sun.cloud.http.error;

import com.blankj.utilcode.util.StringUtils;
import com.sun.cloud.http.ApiResponse;
import com.sun.cloud.http.base.IResponse;
import com.sun.cloud.http.exception.ApiException;
import com.sun.http.R;

import org.json.JSONException;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import javax.annotation.Nullable;

import retrofit2.HttpException;

/**
 * Created on 2021/4/14
 * <p>
 *
 * @author sunxiaoyun
 */
public class DefaultResponseErrorHandler implements IErrorHandler {

    protected String defaultMessageHint;

    public DefaultResponseErrorHandler() {
        defaultMessageHint = StringUtils.getString(com.sun.http.R.string.argclib_http_server_error_hint);
    }

    @Override
    @Nullable
    public String errorToMessage(@Nullable Throwable throwable, boolean nullable) {
        if (throwable == null && nullable) {
            return null;
        }
        if (throwable == null && !nullable) {
            return defaultMessageHint;
        }
        if (throwable instanceof ApiException) {
            return throwable.getMessage();
        } else if (throwable instanceof HttpException) {
            // http请求异常，有错误码
            return StringUtils.getString(R.string.argclib_http_server_error_hint);
        } else if ((throwable instanceof com.alibaba.fastjson.JSONException
                || throwable instanceof JSONException
                || throwable instanceof ParseException)) {
            // 数据解析异常
            return StringUtils.getString(R.string.argclib_http_server_error_hint);
        } else if (throwable instanceof SocketException) {
            // 网络连接错误
            return StringUtils.getString(R.string.argclib_http_net_error_hint);
        } else if (throwable instanceof UnknownHostException) {
            // 找不到服务器，一般为未联网
            return StringUtils.getString(R.string.argclib_http_net_error_hint);
        } else if (throwable instanceof SocketTimeoutException) {
            // 连接超时
            return StringUtils.getString(R.string.argclib_http_net_error_hint);
        } else if (throwable instanceof NullPointerException) {
            return StringUtils.getString(R.string.argclib_http_null_hint);
        }
        return StringUtils.getString(R.string.argclib_http_other_hint);
    }

    @Override
    public IResponse errorToResponse(@Nullable Throwable throwable) {
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.status = "500";
        apiResponse.message = errorToMessage(throwable, false);
        return apiResponse;
    }
}
