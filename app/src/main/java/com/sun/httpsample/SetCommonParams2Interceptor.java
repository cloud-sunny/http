package com.sun.httpsample;

import com.blankj.utilcode.util.AppUtils;
import com.sun.cloud.http.interceptor.IArgcInterceptor;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created on 2021/4/15
 * <p>
 *
 * @author sunxiaoyun
 */
public class SetCommonParams2Interceptor implements IArgcInterceptor {

    private String znToken;

    public SetCommonParams2Interceptor(String token) {
        this.znToken = token;
    }

    @Override
    public Request beforeProceed(Interceptor.Chain chain, Request request) throws IOException {
        Headers headers = request.headers();
        Headers.Builder builder = headers.newBuilder();
        builder.set("X-Requested-With", "XMLHttpRequest")
                .set("Connection", "Closed")
                .set("version", AppUtils.getAppVersionName())
                .set("from", "Android");
        if (znToken != null) {
            builder.set("gt", znToken);
        }
        return request.newBuilder().headers(builder.build()).build();
    }
}

