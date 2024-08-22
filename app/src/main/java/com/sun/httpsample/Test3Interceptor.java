package com.sun.httpsample;

import com.sun.cloud.http.interceptor.IArgcInterceptor;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created on 2021/4/15
 * <p>
 *
 * @author sunxiaoyun
 */
public class Test3Interceptor implements IArgcInterceptor {


    public Test3Interceptor() {
    }

    @Override
    public Request beforeProceed(Interceptor.Chain chain, Request request) throws IOException {
        Headers headers = request.headers();
        Headers.Builder builder = headers.newBuilder();
        builder
                .set("step3", "Test3Interceptor");
        return request.newBuilder().headers(builder.build()).build();
    }

    @Override
    public Response afterProceed(Interceptor.Chain chain, Response response) throws IOException {
        return IArgcInterceptor.super.afterProceed(chain, response);
    }
}

