package com.sun.httpsample;

import android.location.Location;

import androidx.lifecycle.LiveData;

import com.sun.cloud.http.ApiResponse;
import com.sun.cloud.http.annotation.ApiCacheTime;
import com.sun.cloud.http.annotation.ApiDataBase;
import com.sun.cloud.http.annotation.ApiRepository;
import com.sun.cloud.http.base.Function;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.PATCH;

@ApiRepository(urlKey = HttpUrlKey.MAP_ARGC)
public interface ArgcDictService {
    String SWITCH_CODE_OF_PESTS_DISASTER = "01";

    /**
     * 查询后台数据字典中客服电话号码
     */
    @ApiCacheTime(ApiCacheTime.CacheTime.ONE_MONTH)
    @PATCH("common/dict/dicts?type=serviceMobile&code=01")
    LiveData<ApiResponse<String>> queryDictServiceMobile();

    /**
     * 查询后台数据字典中作物及对应的作物code
     */
    @ApiDataBase(ApiDataBase.Strategy.DB_AND_SERVER)
    @ApiCacheTime(ApiCacheTime.CacheTime.ONE_HOUR)
    @GET("common/dict/dicts?type=crop")
    LiveData<ApiResponse<List<Object>>> queryDictCrop(String id, String name, Map<String, Object> args);

    @ApiDataBase(ApiDataBase.Strategy.DB_ONLY)
    @GET("common/dict/dicts?type=crop")
    @ApiCacheTime
    Single<ApiResponse<List<Object>>> queryDictCrop2(String id, String name, Map<String, Object> args);

    @GET("common/dict/dicts?type=crop")
    Flowable<ApiResponse<List<Object>>> queryDictCrop3(String id, String name, Map<String, Object> args);

    @GET("common/dict/dicts?type=crop")
    Maybe<ApiResponse<List<Object>>> queryDictCrop4(String id, String name, Map<String, Object> args);

    @GET("common/dict/dicts?type=crop")
    Function<ApiResponse<List<Object>>> queryDictCrop5(String id, String name, Map<String, Object> args);


    @ApiCacheTime(ApiCacheTime.CacheTime.ONE_HOUR)
    @GET("common/dict/crops")
    LiveData<ApiResponse<List<Location>>> queryAkatDictCrop();

    @ApiCacheTime(ApiCacheTime.CacheTime.ONE_HOUR)
    @GET("common/dict/crops")
    LiveData<ApiResponse<List<Location>>> queryAkatDictCrop(String test);

    @ApiRepository(urlKey = HttpUrlKey.DA_SHI)
    @GET("common/dict/crops")
    LiveData<ApiResponse<List<Location>>> queryAkatDictCrop222();
}
