package com.sun.cloud.http.netcheck.exception;

import com.sun.cloud.http.netcheck.NetExceptionHandler;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created on 2019/10/23
 *
 * @author sunxiaoyun
 */
public class ExceptionInterceptor implements Interceptor {

    public ExceptionInterceptor(HandlerConfig config) {
        NetExceptionHandler.get().config(config);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        try {
            Response response = chain.proceed(request);
            if (!response.isSuccessful()) {
                NetExceptionHandler.get().handException(request.url().toString(), response.message());
            }
            return response;
        } catch (IOException e) {
            NetExceptionHandler.get().handException(request.url().toString(), e);
            throw e;
        }
    }
}
