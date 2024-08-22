package com.sun.cloud.http;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.GsonUtils;
import com.sun.cloud.http.calladapter.LiveDataCallAdapterFactory;
import com.sun.cloud.http.converter.FastJsonConverterFactory;
import com.sun.cloud.http.interceptor.InterceptorManager;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 2021/3/19
 *
 * @author WingHawk
 * 默认只会创建2个 指定key 一个是主配置[default] 一个是瓦片图sdk使用[title]
 */
public class DefaultOkHttpClientFactory implements OkHttpClientFactoryExt {
    protected final Context context;
    protected final List<String> keys = new ArrayList<>();

    public DefaultOkHttpClientFactory(Context context) {
        this.context = context;
        this.keys.add(RetrofitKey.DEF);
        this.keys.add(RetrofitKey.TITLE);
    }

    public DefaultOkHttpClientFactory(Context context, String... keys) {
        this.context = context;
        if (keys != null && keys.length > 0) {
            this.keys.addAll(Arrays.asList(keys));
        }
    }

    @Override
    public List<String> keys() {
        return keys;
    }

    @Override
    public OkHttpClient createOkHttpClient() {
        final OkHttpClient.Builder proxy = createDefaultOkHttpClientBuilder(context)
                .proxy(enableProxy() ? null : Proxy.NO_PROXY);
        return proxy.build();
    }

    @Override
    public boolean enableProxy() {
        return ApiCenter.getInstance().isEnableProxy();
    }

    @Override
    public Retrofit.Builder createRetrofitBuilder(OkHttpClient client) {
        return createDefaultRetrofitBuilder(client);
    }

    public Retrofit.Builder createDefaultRetrofitBuilder(OkHttpClient client) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory( GsonConverterFactory.create(GsonUtils.getGson()))
                .addConverterFactory(new FastJsonConverterFactory())
                .client(client);
    }

    public OkHttpClient.Builder createDefaultOkHttpClientBuilder(Context context) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null, null);
        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor(message -> Log.d(ApiCenter.TAG, message))
                        // 仅 debug 模式输出详细日志
                        .setLevel(ApiCenter.debug ? HttpLoggingInterceptor.Level.BODY
                                : HttpLoggingInterceptor.Level.NONE))
                .addInterceptor(InterceptorManager.getInstance())
                .addNetworkInterceptor(NetworkInterceptorManager.getInstance())
                .addNetworkInterceptor(ArgcStethoInterceptor.getInstance())
                .cache(new Cache(context.getCacheDir(), 10240 * 1024))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .proxy(enableProxy() ? null : Proxy.NO_PROXY)
                .hostnameVerifier((hostname, session) -> true);
    }

    @Override
    public OkHttpClient createOkHttpClient(String key) {
        return createOkHttpClient();
    }

    @Override
    public Retrofit.Builder createRetrofitBuilder(String key, OkHttpClient client) {
        return createRetrofitBuilder(client);
    }

    @Override
    public Retrofit.Builder obtainRetrofitBuilderByKey(String key) {
        if (!keys.isEmpty()) {
            if (!keys.contains(key)) {
                return null;
            }
        }
        return createRetrofitBuilder(key, createOkHttpClient(key));
    }
}
