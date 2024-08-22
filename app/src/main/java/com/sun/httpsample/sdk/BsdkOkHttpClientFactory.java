package com.sun.httpsample.sdk;

import android.util.Log;

import androidx.annotation.Nullable;

import com.sun.cloud.http.ApiCenter;
import com.sun.cloud.http.ArgcStethoInterceptor;
import com.sun.cloud.http.HttpsUtils;
import com.sun.cloud.http.OkHttpClientFactory;
import com.sun.cloud.http.calladapter.LiveDataCallAdapterFactory;
import com.sun.cloud.http.converter.FastJsonConverterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @date: 2023/10/31 11:18 星期二
 * OkHttpClientFactory 构造okhttp时 请匹配自己的key,不是自己定义的key请return null!!!
 **/
public class BsdkOkHttpClientFactory implements OkHttpClientFactory {
    @Override
    public int hashCode() {
        return BsdkOkHttpClientFactory.class.getSimpleName().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        return super.getClass().getSimpleName().equals(obj.getClass().getSimpleName());
    }

    @Override
    public OkHttpClient createOkHttpClient() {
        return createTestOkHttpClientBuilder(BsdkOkHttpClientFactory.class.getSimpleName()).build();
    }

    public OkHttpClient.Builder createTestOkHttpClientBuilder(String key) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null, null);
        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .addInterceptor(new HttpLoggingInterceptor(message -> Log.i("[" + key + "]", message))
                        // 仅 debug 模式输出详细日志
                        .setLevel(ApiCenter.debug ? HttpLoggingInterceptor.Level.BODY
                                : HttpLoggingInterceptor.Level.NONE))
//                        .addInterceptor(new CommonParamsInterceptor().asOkHttpInterceptor())
//                        .addInterceptor(new AuthInterceptor().asOkHttpInterceptor())
                .addNetworkInterceptor(ArgcStethoInterceptor.getInstance())
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier((hostname, session) -> true);
    }

    @Override
    public Retrofit.Builder createRetrofitBuilder(OkHttpClient client) {
        return createDefaultRetrofitBuilder(client);
    }

    public Retrofit.Builder createDefaultRetrofitBuilder(OkHttpClient client) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(new FastJsonConverterFactory())
                .client(client);
    }

    public OkHttpClient createOkHttpClient(String key) {
        return createTestOkHttpClientBuilder(key).build();
    }

    @Override
    public Retrofit.Builder obtainRetrofitBuilderByKey(String key) {
        if (BsdkOkHttpClientFactory.class.getSimpleName().equals(key)) {
            return createRetrofitBuilder(createOkHttpClient(key));
        }
        return null;
    }
}
