package com.sun.httpsample.sdk;

import android.content.Context;
import android.util.Log;

import com.sun.cloud.http.ApiCenter;
import com.sun.cloud.http.ArgcStethoInterceptor;
import com.sun.cloud.http.DefaultOkHttpClientFactory;
import com.sun.cloud.http.HttpsUtils;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @className: AsdkOkHttpClientFactory
 * @description: AsdkOkHttpClientFactory 类描述
 * @author: AsdkOkHttpClientFactory
 * @date: 2023/10/31 11:18 星期二
 **/
public class AsdkOkHttpClientFactory extends DefaultOkHttpClientFactory {
    public AsdkOkHttpClientFactory(Context context) {
        super(context);
    }

    public AsdkOkHttpClientFactory(Context context, String... keys) {
        super(context, keys);
    }
    @Override
    public OkHttpClient createOkHttpClient(String key) {
        switch (key) {
            case "test2":
                return createTestOkHttpClientBuilder(key, context).build();
            default:
                break;
        }
        return super.createOkHttpClient(key);
    }

    public OkHttpClient.Builder createTestOkHttpClientBuilder(String key, Context context) {
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
                .proxy(enableProxy() ? null : Proxy.NO_PROXY)
//                        .addInterceptor(new CommonParamsInterceptor().asOkHttpInterceptor())
//                        .addInterceptor(new AuthInterceptor().asOkHttpInterceptor())
                .addNetworkInterceptor(ArgcStethoInterceptor.getInstance())
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier((hostname, session) -> true);
    }
}
