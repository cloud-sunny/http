package com.sun.httpsample;

import android.location.Location;

import androidx.lifecycle.LiveData;

import com.sun.cloud.http.ApiResponse;
import com.sun.cloud.http.annotation.ApiCacheTime;
import com.sun.cloud.http.annotation.ApiDataBase;
import com.sun.cloud.http.annotation.ApiRepository;
import com.sun.cloud.http.annotation.HttpURL;
import com.sun.httpsample.bean.DictCropBean;
import com.sun.httpsample.bean.MachineCropType;
import com.sun.httpsample.bean.WorkType;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

@ApiRepository(baseUrl = HttpURL.APP)
public interface TestService {
    @GET("m/common/dict/dicts?type=hashrate&code=01")
    LiveData<ApiResponse<List<Dict>>> getRemoteExceptionPowerUpLimit();
    @GET("m/common/dict/dicts?type=hashrate&code=01")
    LiveData<ApiResponse<Dic>> getRemoteExceptionPowerUpLimit2();

    @GET("common/dict/dicts?type=serviceMobile&code=01")
    @ApiDataBase(ApiDataBase.Strategy.DB_ONLY)
    LiveData<ApiResponse<String>> queryDictServiceMobile();

    @GET("common/dict/crops")
    @ApiCacheTime(ApiCacheTime.CacheTime.ONE_MONTH)
    LiveData<ApiResponse<List<Location>>> queryAkatDictCrop();

    @GET("m/smart/machineryTask/queryCurrencyCrop")
    LiveData<ApiResponse<MachineCropType>> listCropType();

    @ApiDataBase(ApiDataBase.Strategy.DB_ONLY)
    @GET("m/smart/machineryTask/queryCurrencyTask")
    LiveData<DemoResponse2<List<WorkType>>> listWorkType();

    /**
     * 查询后台数据字典中作物及对应的作物code
     */
    @ApiCacheTime(ApiCacheTime.CacheTime.ONE_MIN)
    @GET("m/common/dict/dicts")
    LiveData<DemoResponse<List<DictCropBean>>> queryDictCrop(@Query("type") String type);

    /**
     * 查询后台数据字典中作物及对应的作物code
     */
    @ApiCacheTime(ApiCacheTime.CacheTime.ONE_MIN)
    @GET("m/common/dict/dicts?type=crop")
    LiveData<DemoResponse<List<DictCropBean>>> queryDictCrop2();

    /**
     * 查询后台数据字典中作物及对应的作物code
     */
    @GET("m/common/dict/dicts?type=crop")
    Call<DemoResponse<List<DictCropBean>>> queryDictCropCall();

    /**
     * 查询后台数据字典中作物及对应的作物code
     */
    @GET("m/common/dict/dicts?type=crop")
    Observable<DemoResponse<List<DictCropBean>>> queryDictCropOb();
}
