package com.sun.httpsample;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.Utils;
import com.sun.cloud.http.ApiCenter;
import com.sun.cloud.http.ArgcStethoInterceptor;
import com.sun.cloud.http.DefaultOkHttpClientFactory;
import com.sun.cloud.http.HttpsUtils;
import com.sun.cloud.http.ResponseErrorProxy;
import com.sun.cloud.http.RetrofitKey;
import com.sun.cloud.http.annotation.HttpURL;
import com.sun.cloud.http.base.ExceptionHandler;
import com.sun.cloud.http.base.IResponse;
import com.sun.cloud.http.error.DefaultResponseErrorHandler;
import com.sun.cloud.http.netcheck.NetExceptionHandler;
import com.sun.httpsample.sdk.AsdkOkHttpClientFactory;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;


/**
 * Created on 2020-01-10
 * <p>
 *
 * @author sunxiaoyun
 */
public class App extends Application {
    public static final String TAG = "http";

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        // 配置网络框架
//        new Handler().postDelayed(this::initApiCenter, 5000);

        Utils.init(this);
        initApiCenter();
    }

    private void initApiCenter() {
        ApiCenter.getInstance().setupEnableProxy(true);
        ApiCenter.debug = BuildConfig.DEBUG;

        /**
         * 替补Factory会去创建key为test的实例,外部重写
         */
        ApiCenter.getInstance()
                .addAlternateOkHttpClientFactory(new DefaultOkHttpClientFactory(this, "test") {
                    @Override
                    public OkHttpClient createOkHttpClient(String key) {
                        switch (key) {
                            case "test":
                            case "test2":
                                return createTestOkHttpClientBuilder(key, context).build();
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
                                .addInterceptor(new Test2Interceptor().asOkHttpInterceptor())
                                .addInterceptor(new Test3Interceptor().asOkHttpInterceptor())
                                .addInterceptor(new Test1Interceptor().asOkHttpInterceptor())
                                .addInterceptor(new Test4Interceptor().asOkHttpInterceptor())
                                .addNetworkInterceptor(ArgcStethoInterceptor.getInstance())
                                .addInterceptor(new SignatureParamsInterceptor().asOkHttpInterceptor())
                                .addInterceptor(new SetCommonParamsInterceptor("1").asOkHttpInterceptor())
                                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                                .hostnameVerifier((hostname, session) -> true);
                    }
                });

        ApiCenter.getInstance().addAlternateOkHttpClientFactory(new AsdkOkHttpClientFactory(this, "test2"));
//        ApiCenter.getInstance().addAlternateOkHttpClientFactory(new BsdkOkHttpClientFactory());
//        ApiCenter.getInstance().addAlternateOkHttpClientFactory(new BsdkOkHttpClientFactory());
        ApiCenter.getInstance().setOkHttpClientFactory(new DefaultOkHttpClientFactory(this) {
            @Override
            public OkHttpClient createOkHttpClient(String key) {
                key += "";
                if (RetrofitKey.TITLE.equals(key)) {
                    return createDefaultOkHttpClientBuilder(context).proxy(enableProxy() ? null : Proxy.NO_PROXY)
                            .build();
                } else {
                    return super.createOkHttpClient(key);
                }
            }

            /**
             * 只创建RetrofitKey.DEF 和 RetrofitKey.TITLE的实例 其它类型不与创建
             * @param key
             * @return
             */
            @Override
            public Retrofit.Builder obtainRetrofitBuilderByKey(String key) {
                switch (key) {
                    case RetrofitKey.DEF:
                    case RetrofitKey.TITLE:
                        return super.obtainRetrofitBuilderByKey(key);
                    default:
                        return null;
                }
            }
        });
        String token
                = "app-455f9cc8b63744c48bea4b7a9ac84785";

        ApiCenter.getInstance()
                .addInterceptor(new SetCommonParamsInterceptor(token))
                .addInterceptor(new SetCommonParams2Interceptor(token))
                .configUrl(HttpURL.APP, "https://m.mapfarm.com/")
                .setExceptionInterceptHandler(new ExceptionHandler() {
                    @Override
                    public void onServerException(String url, String message) {
                        NetExceptionHandler.get().handException(url, message);
                    }

                    @Override
                    public void onHttpException(String url, Throwable throwable) {
                        NetExceptionHandler.get().handException(url, throwable);
                    }
                })
                .baseUrl("https://m.mapfarm.com/")
                .init(this);
//                .addInterceptor(new CommonInterceptor());
        //https://map.mapfarm.com/map_gis/tile_map/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=img&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX=12&TILEROW=1554&TILECOL=3371&subtoken=c1b8754451fd4455832b823edb7fd1a5
        ApiCenter.getInstance().getService(TestAptService.class, "https://map.mapfarm.com/", RetrofitKey.TITLE);
        ApiCenter.getInstance().getService(TestAptService.class, "https://map.mapfarm.com/", "test");
//        ApiCenter.getInstance().getService(TestAptService.class, "https://map.mapfarm.com/", "test100");

        ResponseErrorProxy.setProxy(new DefaultResponseErrorHandler() {
            @Override
            public IResponse errorToResponse(@Nullable Throwable throwable) {
                if (ApiCenter.debug) {
                    Log.i("网络返回", "异常信息[" + (throwable != null ? throwable.getMessage() : "") + "]");
                }
                return super.errorToResponse(throwable);
            }
        });
    }

}
