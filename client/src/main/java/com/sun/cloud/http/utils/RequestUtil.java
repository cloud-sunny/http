package com.sun.cloud.http.utils;

import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.blankj.utilcode.util.ObjectUtils;
import com.sun.cloud.http.ApiCenter;
import com.sun.cloud.http.Resource;
import com.sun.cloud.http.annotation.ApiDataBase;
import com.sun.cloud.http.annotation.HttpURL;
import com.sun.cloud.http.base.BaseRemoteResource;
import com.sun.cloud.http.base.BooleanFunction;
import com.sun.cloud.http.base.Function;
import com.sun.cloud.http.base.IResponse;

import okhttp3.HttpUrl;

/**
 * Created on 2021/4/29
 * <p>
 *
 * @author sunxiaoyun
 */
public class RequestUtil {
    /**
     * 获取网络请求call封装对象
     *
     * @param function 接口
     * @param <T>      数据类型
     * @return 数据 LiveData<Resource<T>>
     */
    @Keep
    public static <T> LiveData<Resource<T>> getResourceLiveData(@NonNull Function<T> function) {
        return new BaseRemoteResource<T>() {
            @NonNull
            @Override
            protected LiveData<? extends IResponse<T>> createCall() {
                return function.apply();
            }
        }.asLiveData();
    }

    /**
     * 获取网络请求call封装对象
     *
     * @param function   接口
     * @param dbLiveData 数据库数据
     * @param strategy   策略
     * @param <T>        数据类型
     * @return 数据 LiveData<Resource<T>>
     */
    @Keep
    public static <T> LiveData<Resource<T>> getResourceLiveData(@NonNull final Function<T> function,
                                                                @Nullable final LiveData<T> dbLiveData,
                                                                @Nullable final ApiDataBase.Strategy strategy) {

        return getResourceLiveData(function, dbLiveData, strategy == null ? null : (t -> {
            if (strategy == ApiDataBase.Strategy.DB_ONLY) {
                return false;
            } else if (strategy == ApiDataBase.Strategy.DB_EMPTY_FETCH_SERVER) {
                return ObjectUtils.isEmpty(t);
            } else if (strategy == ApiDataBase.Strategy.DB_AND_SERVER) {
                return true;
            }
            return true;
        }));

    }

    /**
     * 获取网络请求call封装对象
     *
     * @param function        接口
     * @param dbLiveData      数据库数据
     * @param shouldFetchFunc 控制是否从网络fetch
     * @param <T>             数据类型
     * @return 数据 LiveData<Resource<T>>
     */
    @Keep
    public static <T> LiveData<Resource<T>> getResourceLiveData(@NonNull final Function<T> function,
                                                                @Nullable final LiveData<T> dbLiveData,
                                                                @Nullable final BooleanFunction<T> shouldFetchFunc) {
        return new BaseRemoteResource<T>() {
            @NonNull
            @Override
            protected LiveData<? extends IResponse<T>> createCall() {
                return function.apply();
            }

            @NonNull
            @Override
            protected LiveData<T> loadFromDb() {
                if (dbLiveData != null) {
                    return dbLiveData;
                }
                return super.loadFromDb();
            }

            @Override
            protected boolean shouldFetch(@Nullable T data) {
                if (shouldFetchFunc != null) {
                    return shouldFetchFunc.apply(data);
                }
                return super.shouldFetch(data);
            }
        }.asLiveData();
    }

    /**
     * 添加缓存path
     *
     * @param baseUrl        host
     * @param path           路径
     * @param cacheTimeInSec 缓存时间
     */
    public static void addCachePath(String baseUrl, String path, int cacheTimeInSec) {
        HttpUrl httpUrl = HttpUrl.parse(baseUrl);
        if (httpUrl != null) {
            httpUrl = httpUrl.resolve(path);
        }
        if (httpUrl == null) {
            Log.e(ApiCenter.TAG,
                    String.format("addCachePath error: baseUrl = %1$s, path = %2$s", baseUrl, path));
            return;
        }
        ApiCenter.getInstance().addCachePath(httpUrl.host(), httpUrl.encodedPath(), cacheTimeInSec);
    }

    /**
     * 添加缓存path
     *
     * @param httpURL        服务器枚举
     * @param path           路径
     * @param cacheTimeInSec 缓存时间
     */
    public static void addCachePath(HttpURL httpURL, String path, int cacheTimeInSec) {
        String host = ApiCenter.getInstance().getUrl(httpURL);
        addCachePath(host, path, cacheTimeInSec);
    }

    /**
     * 添加缓存path
     *
     * @param urlKey         服务器key值
     * @param path           路径
     * @param cacheTimeInSec 缓存时间
     */
    public static void addCachePathByUrlKey(String urlKey, String path, int cacheTimeInSec) {
        String host = ApiCenter.getInstance().getUrl(urlKey);
        addCachePath(host, path, cacheTimeInSec);
    }
}
