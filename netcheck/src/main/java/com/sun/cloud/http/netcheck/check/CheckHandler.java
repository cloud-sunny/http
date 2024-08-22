package com.sun.cloud.http.netcheck.check;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.sun.cloud.http.netcheck.check.common.CheckResult;
import com.sun.cloud.http.netcheck.check.common.CheckType;
import com.sun.cloud.http.netcheck.check.inter.CheckDataCallback;
import com.sun.cloud.http.netcheck.check.inter.CheckStepListener;
import com.sun.cloud.http.netcheck.check.resource.base.BaseData;
import com.sun.cloud.http.netcheck.check.resource.net.NetHelper;
import com.sun.cloud.http.netcheck.check.resource.ping.PingHelper;

import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

/**
 * Created on 2019/10/23
 *
 * @author sunxiaoyun
 */
public class CheckHandler implements CheckDataCallback {

    private static final long CHECK_DELAY = 200;

    private HandlerThread mHandlerThread;
    private Handler mHandler;

    /**
     * 类型检测与次数
     */
    private ArrayMap<CheckType, Integer> mCheckTypeMap;

    private ArrayMap<CheckType, Integer> mCheckTypeMapTmp;

    private CheckStepListener mListener;
    private int mTotal;
    private int mCurrentStep;

    private String mAddress;
    private boolean mEnable;

    private CheckResult mCheckErrorResult;

    public CheckHandler() {
        initDefault();
    }

    private void initDefault() {
        if (mCheckTypeMap == null) {
            mCheckTypeMap = new ArrayMap<>();
        }
        mCheckTypeMap.put(CheckType.NET_PERMISSION, 1);
        mCheckTypeMap.put(CheckType.NET_CONNECT, 1);
        mCheckTypeMap.put(CheckType.PING_INTERNET, 3);
        mCheckTypeMap.put(CheckType.PING_SERVER, 3);
        mCheckTypeMap.put(CheckType.ACCESS_SERVICE, 2);
    }

    public void setCheckType(CheckType type, int count) {
        if (mCheckTypeMap == null) {
            mCheckTypeMap = new ArrayMap<>();
        }
        mCheckTypeMap.put(type, count);
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public void setCheckStepListener(CheckStepListener listener) {
        mListener = listener;
    }

    public synchronized void start() {
        if (mEnable) {
            return;
        }

        if (TextUtils.isEmpty(mAddress)) {
            throw new IllegalArgumentException("address url should not empty");
        }
        prepareHandler();
        mEnable = true;
        mCheckErrorResult = null;
        mCheckTypeMapTmp = new ArrayMap<>(mCheckTypeMap);
        calcProgress();
        if (mListener != null) {
            mListener.onStepStart();
        }
        performCheck();
    }

    private void calcProgress() {
        mCurrentStep = 0;
        mTotal = 0;
        for (Map.Entry<CheckType, Integer> checkTypeIntegerEntry : mCheckTypeMapTmp.entrySet()) {
            Integer count = checkTypeIntegerEntry.getValue();
            mTotal += count;
        }
    }

    /**
     * 从第一个开始执行
     */
    private void performCheck() {
        if (mCheckTypeMapTmp.isEmpty()) {
            if (mListener != null) {
                mListener.onStepFinish(mCheckErrorResult);
            }
            stop();
        } else {
            mHandler.postDelayed(() -> {
                CheckType type = findNext(mCheckTypeMapTmp);
                if (type != null) {
                    mHandler.obtainMessage(type.getValue()).sendToTarget();
                }
            }, CHECK_DELAY);
        }
    }

    /**
     * 找下一个指令类型
     *
     * @param map
     * @return
     */
    private CheckType findNext(ArrayMap<CheckType, Integer> map) {
        CheckType type = null;
        for (CheckType ct : map.keySet()) {
            if (type == null) {
                type = ct;
            } else if (type.getValue() > ct.getValue()) {
                type = ct;
            }
        }
        return type;
    }

    /**
     * 执行线程
     */
    private void prepareHandler() {
        if (isMainThread()) {
            if (mHandlerThread == null) {
                mHandlerThread = new HandlerThread("check" + UUID.randomUUID().toString().substring(0, 8));
                mHandlerThread.start();
            } else if (!mHandlerThread.isAlive()) {
                mHandlerThread.start();
            }
            mHandler = new Handler(mHandlerThread.getLooper(), new MessageCallback());
        } else {
            mHandler = new Handler(new MessageCallback());
        }
    }

    public void stop() {
        mEnable = false;
        mCheckErrorResult = null;
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }

    public void release() {
        mCheckTypeMap.clear();
        mCheckTypeMapTmp.clear();
        if (mEnable) {
            stop();
        }
        mHandler = null;
    }

    public boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    @Override
    public void onResult(CheckType type, CheckResult result, JSONObject jsonObject) {
        if (!mEnable) {
            // 如果已经取消，无需再处理
            return;
        }
        mCurrentStep++;
        Integer oriCount = mCheckTypeMap.get(type);
        Integer count = mCheckTypeMapTmp.get(type);
        if (count != null && count > 1) {
            mCheckTypeMapTmp.put(type, count - 1);
        } else {
            mCheckTypeMapTmp.remove(type);
        }

        if (mCheckErrorResult == null && result != CheckResult.SUCCESS) {
            // 第一次产生异常
            mCheckErrorResult = result;
        }

        if (mListener != null) {
            int subCount = oriCount != null && count != null ? oriCount - count : 1;
            mListener.onStepProgress(type, subCount, result, mCurrentStep, mTotal);
        }
        // 执行下一步
        performCheck();
    }

    private class MessageCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == CheckType.NET_PERMISSION.getValue()) {
                //检测网络权限
                NetHelper.getNetParam(CheckType.NET_PERMISSION, CheckHandler.this);
            } else if (msg.what == CheckType.NET_CONNECT.getValue()) {
                NetHelper.getNetParam(CheckType.NET_CONNECT, CheckHandler.this);
            } else if (msg.what == CheckType.PING_INTERNET.getValue()) {
                PingHelper.getPingParam(CheckType.PING_INTERNET, BaseData.PING_INNER_NET, CheckHandler.this);
            } else if (msg.what == CheckType.PING_SERVER.getValue()) {
                PingHelper.getPingParam(CheckType.PING_SERVER, mAddress, CheckHandler.this);
            } else if (msg.what == CheckType.ACCESS_SERVICE.getValue()) {
                PingHelper.getPingParam(CheckType.ACCESS_SERVICE, mAddress, CheckHandler.this);
            }
            return true;
        }
    }
}
