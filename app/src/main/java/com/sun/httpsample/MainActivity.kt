package com.sun.httpsample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sun.cloud.http.ApiCenter
import com.sun.cloud.http.Resource
import com.sun.cloud.http.Status
import com.sun.cloud.http.utils.RequestUtil
import com.sun.httpsample.bean.DictCropBean
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private var testRepository: TestRepository? = null
    private val mTestService: TestAptService by lazy {
        ApiCenter.getInstance().getService(TestAptService::class.java)
    }
    private val dbLiveData = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testRepository = TestRepository()
        findViewById<View>(R.id.ins1).setOnClickListener { view: View -> ins1(view) }
        findViewById<View>(R.id.ins2).setOnClickListener { view: View -> ins2(view) }
        findViewById<View>(R.id.test).setOnClickListener { view: View -> testFragment(view) }
        findViewById<View>(R.id.fetch).setOnClickListener { view: View -> testNetFetch(view) }
        findViewById<View>(R.id.fetch2).setOnClickListener { view: View -> testNetFetch2(view) }
        findViewById<View>(R.id.fetch3).setOnClickListener { view: View -> testNetFetch3(view) }
        dbLiveData.setValue("卧槽啊哈哈哈哈哈")
    }

    @SuppressLint("RestrictedApi")
    private fun ins1(view: View) {
        val mainThread = ArchTaskExecutor.getInstance().isMainThread
        Log.d("ins1", "ins1 --mainThread-->" + mainThread)
        // 指定
        var api = ApiCenter.getInstance()
            .getService(TestAptService::class.java, "https://uatgw.mapfarm.com/", "test");
//        api.queryDictCrop("crop").observe(this,{
//            Log.d("ins1", "ins1 ========不能这样调用==============---->" + JSON.toJSONString(it))
//        })
        bs(api).observe(this, {
            Log.d("ins1", "ins1 ========方式一==============---->" + JSON.toJSONString(it))
        });
    }
    var enableProxy: Boolean = true
    private fun ins2(view: View) {
//        ApiCenter.getInstance().setupEnableProxy(enableProxy)
//        enableProxy=!enableProxy
        var api = ApiCenter.getInstance()
            .getService(TestAptService::class.java, "https://uatgw.mapfarm.com/", "test2");
        var test = ApiCenter.getInstance()
            .getService(TestService::class.java, "https://uatgw.mapfarm.com/", "test2")
        getRemoteExceptionPowerUpLimit(test).observe(this, {
            Log.d("ins2", "ins2 --TestService-->" + JSON.toJSONString(it))
        })
        api.queryDictCropCall().enqueue(object : Callback<DemoResponse<List<DictCropBean>>> {


            override fun onResponse(
                call: Call<DemoResponse<List<DictCropBean>>>,
                response: Response<DemoResponse<List<DictCropBean>>>
            ) {
                Log.d("ins2", "ins2 ---->" + JSON.toJSONString(response.body()))
            }

            override fun onFailure(call: Call<DemoResponse<List<DictCropBean>>>, t: Throwable) {
                Log.d("ins2", "ins2 ---->" + JSON.toJSONString(t))
            }
        })
    }

    private fun testNetFetch3(view: View) {
//        testRepository!!.queryDictCropOb().observeOn(Schedulers.newThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe(object : Consumer<DemoResponse<List<DictCropBean?>?>?> {
//                @Throws(Exception::class)
//                override fun accept(listDemoResponse2: DemoResponse<List<DictCropBean?>?>) {
//                    LogUtils.d("Observer is ---->" + listDemoResponse2.data)
//                }
//            })
        LogUtils.d("执行")
//        val scope = MainScope() + CoroutineName("MyActivity")
//        val job = scope.launch(start = CoroutineStart.LAZY) {
//            LogUtils.d("开始")
//            delay(1000)
//            LogUtils.d("调用")
//            val result = mTestService.queryDictCropSuspend("crop")
//            LogUtils.d("结果回来")
//            when (result.code) {
//                200 -> {
//                    Toast.makeText(
//                        this@MainActivity,
//                        "第一个数据: " + result.data?.get(0)?.toString(),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                else -> LogUtils.d(result.data)
//            }
//            delay(2000)
//            LogUtils.d("调用main")
//            withContext(Dispatchers.Main) {
//                LogUtils.d("执行main")
//                Toast.makeText(this@MainActivity, "数据来了", Toast.LENGTH_LONG).show()
//            }
//
//        }
//        job.start()
    }

    private fun testNetFetch2(view: View) {
//        testRepository!!.queryDictCropCall()
//            .enqueue(object : Callback<DemoResponse<List<DictCropBean?>?>?> {
//                override fun onResponse(
//                    call: Call<DemoResponse<List<DictCropBean?>?>>,
//                    response: Response<DemoResponse<List<DictCropBean?>?>>
//                ) {
//                    LogUtils.d(response.body()!!.data)
//                }
//
//                override fun onFailure(
//                    call: Call<DemoResponse<List<DictCropBean?>?>>,
//                    t: Throwable
//                ) {
//                    LogUtils.d("onFailure")
//                }
//            })
    }

    private fun testNetFetch(view: View) {
//        val source: LiveData<Resource<List<Dict>>> =
//            testRepository!!.getRemoteExceptionPowerUpLimit()
//        source.observe(this, Observer {
//            onFectResultx(it);
//        })
//        val mService2868 = ApiCenter.getInstance().getService(TestService::class.java, HttpURL.ZN)
//        testRepository?.getRemoteExceptionPowerUpLimit()?.observe(this, {
//            Log.d("ins3", "testNetFetch ---->" + JSON.toJSONString(it))
//            ToastUtils.showShort("成功!!!!")
//        })
        testRepository?.getRemoteExceptionPowerUpLimit2()?.observe(this, {
            Log.d("ins3", "getRemoteExceptionPowerUpLimit2 ---->" + JSON.toJSONString(it))
            ToastUtils.showShort(JSON.toJSONString(it)+"||")
        })
//        testRepository!!.queryDictCrop2().observe(
//            this,
//            Observer { machineCropTypeResource: Resource<List<DictCropBean>> ->
//                onFectResult(
//                    machineCropTypeResource
//                )
//            })
//        testRepository!!.queryDictServiceMobile(dbLiveData)
//            .observe(this, Observer { stringResource: Resource<String?> ->
//                if (stringResource.status == Status.SUCCESS) {
//                    LogUtils.d(stringResource.data)
//                }
//            })
    }

    private fun onFectResultx(machineCropTypeResource: Resource<List<Dict>>) {
        when (machineCropTypeResource.status) {
            Status.ERROR -> ToastUtils.showShort(machineCropTypeResource.message)
            Status.LOADING -> ToastUtils.showShort("loading...")
            Status.SUCCESS -> {
                LogUtils.d(machineCropTypeResource.data)
                ToastUtils.showShort("成功!!!!")
                JSON.toJSONString("")
            }
        }
    }

    private fun onFectResult(machineCropTypeResource: Resource<List<DictCropBean>>) {
        when (machineCropTypeResource.status) {
            Status.ERROR -> ToastUtils.showShort(machineCropTypeResource.message)
            Status.LOADING -> ToastUtils.showShort("loading...")
            Status.SUCCESS -> LogUtils.d(machineCropTypeResource.data)
        }
    }

    fun getRemoteExceptionPowerUpLimit(api: TestService): LiveData<Resource<List<Dict>>> {
        return RequestUtil.getResourceLiveData { api.getRemoteExceptionPowerUpLimit() }
    }

    fun bs(api: TestAptService): LiveData<Resource<List<DictCropBean>>> {
        return RequestUtil.getResourceLiveData { api.queryDictCrop("crop") }
    }

    private fun testFragment(view: View) {
        val bundle = Bundle()
        bundle.putString("content", "来自主界面的内容")
        ContainerActivity.start(this, TestFragment::class.java, bundle)
    }
}