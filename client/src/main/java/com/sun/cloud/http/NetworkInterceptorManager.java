package com.sun.cloud.http;


import com.sun.cloud.http.interceptor.InterceptorManager;

/**
 * 加载注册的 interceptors 集合，并在请求和响应的时候调用。
 * 注意：所有的 interceptor 都常驻内存，请勿在其成员变量中持有 activity 相关的引用。
 * <p>
 * Created on 2019/7/3
 *
 * @author WingHawk
 */
final class NetworkInterceptorManager extends InterceptorManager {

    private NetworkInterceptorManager() {
    }

    public static NetworkInterceptorManager getInstance() {
        return NetworkInterceptorManager.InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final NetworkInterceptorManager INSTANCE
                = new NetworkInterceptorManager();
    }
}
