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
public class Test1Interceptor implements IArgcInterceptor {


    public Test1Interceptor() {
    }

    @Override
    public Request beforeProceed(Interceptor.Chain chain, Request request) throws IOException {
        Headers headers = request.headers();
        Headers.Builder builder = headers.newBuilder();
        builder
                .set("step1", "Test1Interceptor");
        return request.newBuilder().headers(builder.build()).build();
    }

    public static int x = 0;

    /**
     * cannot make a new request because the previous response is still open: please call response.close()
     * 07-09 14:09:54.653 27525 27690 E AndroidRuntime:
     * at okhttp3.internal.connection.RealCall.enterNetworkInterceptorExchange(RealCall.kt:229)
     */
    @Override
    public Response afterProceed(Interceptor.Chain chain, Response response) throws IOException {
        synchronized (this) {
            if (x <= 10) {
                x += 1;
                response.close();
                Headers headers = chain.request().headers();
                Headers.Builder builder = headers.newBuilder()
                        .set("step1", "step1")
                        .set("step2", "step2")
                        .set("step3", "step3")
                        .set("step4", "step4")
                        .set("step5", "step5")
                        .set("reset", "sayHi");
                return chain.proceed(chain.request().newBuilder().headers(builder.build()).build());
            }
        }
        return response;
    }
}

