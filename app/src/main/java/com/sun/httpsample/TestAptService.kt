package com.sun.httpsample

import androidx.lifecycle.LiveData
import com.sun.httpsample.bean.DictCropBean
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url


interface TestAptService {
    /**
     * 查询后台数据字典中作物及对应的作物code
     */
    @GET("m/common/dict/dicts")
    fun queryDictCrop(@Query("type") type: String?): LiveData<DemoResponse<List<DictCropBean>>>

    //    /**
    //     * 查询后台数据字典中作物及对应的作物code
    //     */
    //    @GET("m/common/dict/dicts?type=crop")
    //    Call<DemoResponse<List<DictCropBean>>> queryDictCropCall();
    //
    //    /**
    //     * 查询后台数据字典中作物及对应的作物code
    //     */
    //    @GET("m/common/dict/dicts?type=crop")
    //    Observable<DemoResponse<List<DictCropBean>>> queryDictCropOb();
    @GET("m/common/dict/dicts?type=crop")
    fun queryDictCropCall(): Call<DemoResponse<List<DictCropBean>>>;

    @GET("m/common/dict/dicts")
    suspend fun queryDictCropSuspend(@Query("type") type: String?): DemoResponse<List<DictCropBean?>?>

    @Streaming
    @GET
    fun downloadFileWithDynamicUrlAsync(@Url fileUrl: String?): Call<ResponseBody?>?
}