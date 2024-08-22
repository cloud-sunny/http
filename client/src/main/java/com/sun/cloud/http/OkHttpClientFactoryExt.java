package com.sun.cloud.http;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @className: OkHttpClientFactoryExt
 * @description: OkHttpClientFactoryExt 类描述
 * @author: OkHttpClientFactoryExt
 * @date: 2023/10/23 10:30 星期一
 **/
public interface OkHttpClientFactoryExt extends OkHttpClientFactory {
    OkHttpClient createOkHttpClient(String key);

    default boolean enableProxy() {
        return false;
    }

    default List<String> keys() {
        return new ArrayList<>();
    }

    Retrofit.Builder createRetrofitBuilder(String key, OkHttpClient client);
}
