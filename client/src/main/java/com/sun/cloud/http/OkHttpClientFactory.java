package com.sun.cloud.http;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created on 2021/3/19
 *
 * @author WingHawk
 */
public interface OkHttpClientFactory {

    OkHttpClient createOkHttpClient();

    Retrofit.Builder createRetrofitBuilder(OkHttpClient client);

    default Retrofit.Builder obtainRetrofitBuilderByKey(String key) {
        return null;
    }
}
