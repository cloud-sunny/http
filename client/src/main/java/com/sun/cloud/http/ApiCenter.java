package com.sun.cloud.http;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.collection.LruCache;
import androidx.lifecycle.LiveData;

import com.sun.cloud.http.annotation.HttpURL;
import com.sun.cloud.http.base.ExceptionHandler;
import com.sun.cloud.http.base.Function;
import com.sun.cloud.http.interceptor.IArgcInterceptor;
import com.sun.cloud.http.interceptor.InterceptorManager;
import com.sun.cloud.http.utils.RequestUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class ApiCenter {

    public static final String TAG = "ApiCenter";
    public static boolean debug = false;
    private static final int SERVICE_CACHE_MAX_SIZE = 8;

    protected LruCache<String, Object> cacheServices = new LruCache<>(SERVICE_CACHE_MAX_SIZE);
    protected final Map<String, Retrofit.Builder> mBuilders = new HashMap<>();
    protected final List<OkHttpClientFactory> mAlternateOkHttpClientFactorys = new ArrayList<>();
    protected String baseUrl = "";

    protected Map<String, String> mHttpUrls = new HashMap<>();
    private final String KEY_UUID = java.util.UUID.randomUUID().toString();
    private OkHttpClientFactory okHttpClientFactory;

    /**
     * 添加替补okHttpClientFactory [重写hash equals维护去重复]
     *
     * @param okHttpClientFactory
     * @return
     */
    public ApiCenter addAlternateOkHttpClientFactory(OkHttpClientFactory okHttpClientFactory) {
        if (!mAlternateOkHttpClientFactorys.contains(okHttpClientFactory)) {
            mAlternateOkHttpClientFactorys.add(okHttpClientFactory);
        }
        return this;
    }

    private ApiCenter() {
    }

    public ApiCenter init(Context context) {
        if (okHttpClientFactory == null) {
            okHttpClientFactory = new DefaultOkHttpClientFactory(context);
        }
        OkHttpClient okHttpClient = okHttpClientFactory.createOkHttpClient();
        NetworkCacheManager.getInstance().init();
        Retrofit.Builder builder = okHttpClientFactory.createRetrofitBuilder(okHttpClient);
        mBuilders.put(RetrofitKey.DEF, builder);
        return this;
    }

    public void setOkHttpClientFactory(OkHttpClientFactory okHttpClientFactory) {
        this.okHttpClientFactory = okHttpClientFactory;
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
    public void addCachePath(@NonNull String host, @NonNull String path, ICacheTimeProvider provider) {
        NetworkCacheManager.getInstance().addCachePath(host, path, provider);
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
    public void addCachePath(@NonNull String host, @NonNull String path, int cacheTimeInSec) {
        NetworkCacheManager.getInstance().addCachePath(host, path, cacheTimeInSec);
    }

    public ApiCenter baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public ApiCenter configUrl(HttpURL urlType, String url) {
        mHttpUrls.put(getKeyFromHttpURL(urlType), url);
        return this;
    }

    public ApiCenter configUrl(String urlKey, String url) {
        mHttpUrls.put(urlKey, url);
        return this;
    }

    private String getKeyFromHttpURL(HttpURL urlType) {
        return KEY_UUID + urlType.name();
    }

    public String getUrl(HttpURL urlType) {
        return mHttpUrls.get(getKeyFromHttpURL(urlType));
    }

    public String getUrl(String urlKey) {
        return mHttpUrls.get(urlKey);
    }

    /**
     * 异常拦截处理器
     *
     * @param handler 拦截处理中间件
     * @return apiCenter
     */
    public ApiCenter setExceptionInterceptHandler(@NonNull ExceptionHandler handler) {
        InterceptorManager.getInstance().setExceptionInterceptHandler(handler);
        return this;
    }

    public ApiCenter addInterceptor(IArgcInterceptor interceptor) {
        InterceptorManager.getInstance().addInterceptor(interceptor);
        return this;
    }

    ApiCenter addNetworkInterceptor(IArgcInterceptor interceptor) {
        NetworkInterceptorManager.getInstance().addInterceptor(interceptor);
        return this;
    }

    public <T> T getService(Class<T> clazz) {
        return getService(clazz, baseUrl);
    }

    public <T> T getService(Class<T> clazz, HttpURL urlType) {
        return getService(clazz, getUrl(urlType));
    }

    public <T> T getService(Class<T> clazz, HttpURL urlType, String key) {
        return getService(clazz, getUrl(urlType), key);
    }

    public <T> T getServiceByKey(Class<T> clazz, String urlKey) {
        return getService(clazz, getUrl(urlKey));
    }

    public <T> T getServiceByKey(Class<T> clazz, String urlKey, String key) {
        return getService(clazz, getUrl(urlKey), key);
    }

    public <T> T getService(Class<T> clazz, String baseUrl) {
        return getService(clazz, baseUrl, RetrofitKey.DEF);
    }

    /**
     * 注意getService 不要在多线程中初始化,因为没加锁!!!
     *
     * @param clazz
     * @param baseUrl
     * @param retrofitkey
     * @param <T>
     * @return
     */
    public <T> T getService(Class<T> clazz, String baseUrl, String retrofitkey) {
        if (mBuilders.isEmpty()) {
            throw new IllegalStateException("please call init first");
        }
        if (mBuilders.get(retrofitkey) == null) {
            Retrofit.Builder builder = okHttpClientFactory.obtainRetrofitBuilderByKey(retrofitkey);
            if (builder == null) {
                for (OkHttpClientFactory okhttpclientFactory : mAlternateOkHttpClientFactorys) {
                    builder = okhttpclientFactory.obtainRetrofitBuilderByKey(retrofitkey);
                    if (builder != null) {
                        break;
                    }
                }
                if (builder == null) {
                    throw new IllegalStateException("please register retrofit by key[" + retrofitkey + "]first!!!");
                }
            } else {
                Retrofit.Builder find = null;
                for (OkHttpClientFactory okhttpclientFactory : mAlternateOkHttpClientFactorys) {
                    find = okhttpclientFactory.obtainRetrofitBuilderByKey(retrofitkey);
                    if (find != null) {
                        break;
                    }
                }
                if (find != null) {
                    throw new IllegalStateException(" retrofit by key[" + retrofitkey + "] is duplicate definition!!!");
                }
            }
            mBuilders.put(retrofitkey, builder);
        }
        String key = clazz.getName() + baseUrl + retrofitkey;
        Object cache = cacheServices.get(key);
        if (cache == null) {
            T service = mBuilders.get(retrofitkey)
                    .baseUrl(baseUrl)
                    .build()
                    .create(clazz);
            cacheServices.put(key, service);
            return service;
        } else {
            return (T) cache;
        }
    }

    /**
     * 获取网络请求call封装对象
     *
     * @param function 接口
     * @param <T>      数据类型
     * @return 数据 LiveData<Resource<T>>
     * @deprecated please use
     * {@link RequestUtil#getResourceLiveData(Function)} instead
     */
    @Keep
    public <T> LiveData<Resource<T>> getResourceLiveData(Function<T> function) {
        return RequestUtil.getResourceLiveData(function);
    }

    private static ApiCenter instance = null;

    public static ApiCenter getInstance() {
        synchronized (ApiCenter.class) {
            if (instance == null) {
                synchronized (ApiCenter.class) {
                    instance = new ApiCenter();
                }
            }
        }
        return instance;
    }

    public ApiCenter setupOffset(short iOffset) {
        NetworkCacheManager.getInstance().setupOffset(iOffset);
        return this;
    }

    public boolean isEnableProxy() {
        return enableProxy;
    }

    private boolean enableProxy;

    public ApiCenter setupEnableProxy(boolean enableProxy) {
        this.enableProxy = enableProxy;
        return this;
    }
}
