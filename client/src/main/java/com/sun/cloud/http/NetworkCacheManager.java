package com.sun.cloud.http;

import androidx.annotation.NonNull;

import com.sun.cloud.http.interceptor.IArgcInterceptor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * okhttp 仅会对 GET 请求进行缓存，此类利用一些操作欺骗 okhttp，使 POST 请求也可以缓存。
 * 大致流程为在 okhttp 获取缓存前将请求方式更改为 GET, 在 okhttp 开始缓存前，将响应中的请求方式也更改为 GET。
 * <p>
 * 1. 在网络请求前将请求方式统一设置为 GET（NetworkCacheInterceptor#beforeProceed）
 * 2. 根据请求体生成唯一 okhttp-cache-key 并将它添加到请求的 url 上（okhttp 缓存的 key 是根据 url 计算的）（NetworkCacheInterceptor#beforeProceed）
 * 3. okhttp 获取缓存
 * 4. 如果没有缓存，将请求方式还原（CacheInterceptor#beforeProceed）进行网络请求
 * 5. 将响应中的请求方式统一设置为 GET，并设置缓存时间 （CacheInterceptor#afterProceed）
 * 6. okhttp 缓存响应数据
 * 7. 将响应中的请求方式还原
 * <p>
 * <p>
 * Created on 2020/5/22
 *
 * @author WingHawk
 */
class NetworkCacheManager {

    private Map<String, ICacheTimeProvider> mCachePaths = new HashMap<>();
    private short iOffset = 1;

    public NetworkCacheManager setupOffset(short iOffset) {
        this.iOffset = iOffset;
        return this;
    }

    private NetworkCacheManager() {
    }

    void init() {
        ApiCenter.getInstance()
                .addInterceptor(new CacheInterceptor())
                .addNetworkInterceptor(new NetworkCacheInterceptor());
    }

    /**
     * 添加需要缓存的接口路径。假如一个接口地址如：http://uatgw.mapfarm.com/m/farm/queryFarmList?userId=123,
     * 其中 host 参数代表的是 uatgw.mapfarm.com，而 path 参数代表的是 /m/farm/queryFarmList。
     * 必须完全按照该格式添加。
     *
     * @param host     域名地址，比如 uatgw.mapfarm.com
     * @param path     接口路径。比如 /m/farm/queryFarmList，必须以 "/" 开头。
     * @param provider 接口缓存时间提供对象
     */
    void addCachePath(@NonNull String host, @NonNull String path, ICacheTimeProvider provider) {
        String key = host + path;
        mCachePaths.put(key, provider);
    }

    /**
     * 添加需要缓存的接口路径。假如一个接口地址如：http://uatgw.mapfarm.com/m/farm/queryFarmList?userId=123,
     * 其中 host 参数代表的是 uatgw.mapfarm.com，而 path 参数代表的是 /m/farm/queryFarmList。
     * 必须完全按照该格式添加。
     *
     * @param host           域名地址，比如 uatgw.mapfarm.com
     * @param path           接口路径。比如 /m/farm/queryFarmList，必须以 "/" 开头
     * @param cacheTimeInSec 缓存时间，单位：秒
     */
    void addCachePath(@NonNull String host, @NonNull String path, int cacheTimeInSec) {
        addCachePath(host, path, (h, p) -> cacheTimeInSec);
    }

    static NetworkCacheManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static NetworkCacheManager INSTANCE = new NetworkCacheManager();
    }

    private boolean shouldCache(Request request) {
        String method = request.method();
        if (!"GET".equalsIgnoreCase(method) && !"POST".equalsIgnoreCase(method)) {
            return false;
        }
        String host = request.url().host();
        String path = request.url().encodedPath();
        String key = host + path;
        ICacheTimeProvider provider = mCachePaths.get(key);
        return provider != null && provider.getCacheTimeInSec(host, path) > 0;
    }

    private class CacheInterceptor implements IArgcInterceptor {
        @Override
        public Request beforeProceed(Interceptor.Chain chain, Request request) throws IOException {
            if (shouldCache(request)) {
                RequestBody body = request.body();
                if (body != null) {
                    Buffer buffer = new Buffer();
                    body.writeTo(buffer);
                    String cacheKey = buffer.md5().hex();
                    HttpUrl url = request.url()
                            .newBuilder()
                            .addQueryParameter("okhttp-cache-key", cacheKey)
                            .build();
                    Request.Builder builder = request.newBuilder()
                            .header("Origin-Request-Method", request.method())
                            .method("GET", null)
                            .url(url);
                    try {
                        Field bodyField = Request.Builder.class.getDeclaredField("body");
                        bodyField.setAccessible(true);
                        bodyField.set(builder, body);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    request = builder.build();
                }
            }
            return request;
        }

        @Override
        public Response afterProceed(Interceptor.Chain chain, Response response) throws IOException {
            Request request = response.request();
            String requestMethod = request.header("Origin-Request-Method");
            if (requestMethod != null) {
                HttpUrl url = request.url()
                        .newBuilder()
                        .removeAllQueryParameters("okhttp-cache-key")
                        .build();
                request = request.newBuilder()
                        .url(url)
                        .method(requestMethod, request.body())
                        .removeHeader("Origin-Request-Method")
                        .build();
                response = response.newBuilder().request(request).build();
            }
            return response;
        }

        @Override
        public short getOrder() {
            return Short.MAX_VALUE;
        }
    }

    private class NetworkCacheInterceptor implements IArgcInterceptor {
        @Override
        public Request beforeProceed(Interceptor.Chain chain, Request request) throws IOException {
            String requestMethod = request.header("Origin-Request-Method");
            if (requestMethod != null) {
                request = request
                        .newBuilder()
                        .method(requestMethod, request.body())
                        .build();
            }
            return request;
        }

        @Override
        public Response afterProceed(Interceptor.Chain chain, Response response) throws IOException {
            Request request = response.request();
            RequestBody requestBody = request.body();
            if (shouldCache(request)) {
                String host = request.url().host();
                String path = request.url().encodedPath();
                String key = host + path;
                ICacheTimeProvider provider = mCachePaths.get(key);
                long cacheTime = provider == null ? 0 : provider.getCacheTimeInSec(host, path);
                String cacheControl = new CacheControl.Builder()
                        .maxAge(((int) cacheTime), TimeUnit.SECONDS)
                        .build()
                        .toString();
                Request.Builder builder = request.newBuilder().method("GET", null);
                try {
                    Field bodyField = Request.Builder.class.getDeclaredField("body");
                    bodyField.setAccessible(true);
                    bodyField.set(builder, requestBody);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response.newBuilder()
                        .request(builder.build())
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            }
            return response;
        }

        @Override
        public short getOrder() {
            return (short) (Short.MIN_VALUE + iOffset);
        }
    }
}
