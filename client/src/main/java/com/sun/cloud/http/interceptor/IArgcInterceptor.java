package com.sun.cloud.http.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created on 2019/7/4
 *
 * @author WingHawk
 */
public interface IArgcInterceptor extends Comparable<IArgcInterceptor> {

    /**
     * 请求前。
     *
     * @param chain   请求拦截链
     * @param request 请求
     * @return 请求。该方法返回 null 不会发出请求。
     * @throws IOException 请求异常
     */
    default Request beforeProceed(Interceptor.Chain chain, Request request) throws IOException {
        return request;
    }

    /**
     * 响应后
     *
     * @param response 响应
     * @return 响应。
     * @throws IOException 请求异常
     * @deprecated please use {@link #afterProceed(Interceptor.Chain, Response)} instead
     */
    default Response afterProceed(Response response) throws IOException {
        return response;
    }

    /**
     * 响应后
     *
     * @param chain    请求链
     * @param response 响应
     * @return 响应。
     * @throws IOException 请求异常
     */
    default Response afterProceed(Interceptor.Chain chain, Response response) throws IOException {
        return afterProceed(response);
    }

    /**
     * 该拦截器是否为终止拦截器。如果该方法返回 true，在其之后配置的其它拦截器将不会执行。
     *
     * @return true 终止，false 非终止
     */
    default boolean isTerminal() {
        return false;
    }

    default short getOrder() {
        return 0;
    }

    @Override
    default int compareTo(IArgcInterceptor o) {
        return this.getOrder() - o.getOrder();
    }

    default Interceptor asOkHttpInterceptor() {
        return chain -> {
            Request request = beforeProceed(chain, chain.request());
            Response response = chain.proceed(request);
            return afterProceed(chain, response);
        };
    }
}
