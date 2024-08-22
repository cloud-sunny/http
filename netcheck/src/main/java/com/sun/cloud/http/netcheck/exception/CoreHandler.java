package com.sun.cloud.http.netcheck.exception;

import androidx.annotation.NonNull;

import android.util.Log;

import com.google.gson.JsonParseException;
import com.sun.cloud.http.netcheck.NetExceptionHandler;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.sun.cloud.http.netcheck.exception.ExceptionType.HTTP_ERROR;
import static com.sun.cloud.http.netcheck.exception.ExceptionType.NETWORK_ERROR;
import static com.sun.cloud.http.netcheck.exception.ExceptionType.PARSE_ERROR;
import static com.sun.cloud.http.netcheck.exception.ExceptionType.SERVER_ERROR;
import static com.sun.cloud.http.netcheck.exception.ExceptionType.UNKNOWN_ERROR;


/**
 * Created on 2019/10/22
 *
 * @author sunxiaoyun
 */
public class CoreHandler {

    public static final long SILL_TIME_DEFAULT = 1000 * 60 * 3;

    /**
     * 处理异常类型，默认所有类型
     */
    private int mHandType = ExceptionType.ALL.type;

    /**
     * 异常累计存储
     */
    private HashMap<ExceptionType, ArrayList<NetException>> mExceptionMap;

    /**
     * 异常次数阈值
     */
    private HashMap<ExceptionType, Integer> mSillCountMap;

    /**
     * 异常发生时间跨度阈值
     */
    private long mSillTime = SILL_TIME_DEFAULT;

    /**
     * 异常堆积溢出监听
     */
    private ExceptionSillListener mListener;

    private boolean mEnable = true;

    public CoreHandler() {
        initDefault();
    }

    /**
     * 控制开关
     */
    public void setEnable(boolean enable) {
        this.mEnable = enable;

        // 如果关闭，则释放所有数据
        if (!mEnable) {
            release();
        }
    }

    public boolean isEnable() {
        return mEnable;
    }

    /**
     * 设置处理特定异常类型
     *
     * @param types
     */
    public void setHandType(ExceptionType... types) {
        mHandType = 0;
        for (ExceptionType et : types) {
            mHandType |= et.type;
        }
    }

    /**
     * 设置异常发生时间段阈值
     *
     * @param sillTime
     */
    public void setSillTime(long sillTime) {
        if (sillTime < 0) {
            throw new IllegalArgumentException("sillTime should > 0");
        }
        this.mSillTime = sillTime;
    }

    /**
     * 设置网络检测触发监听
     *
     * @param l
     */
    public void setListener(ExceptionSillListener l) {
        this.mListener = l;
    }

    /**
     * 设置异常次数阈值
     *
     * @param type
     * @param sillCount
     */
    public synchronized void setSill(ExceptionType type, int sillCount) {
        if (sillCount < 0) {
            throw new IllegalArgumentException("sillCount should > 0");
        }

        if (mSillCountMap == null) {
            mSillCountMap = new HashMap<>();
        }
        mSillCountMap.put(type, sillCount);
    }

    public void setSillCountMap(@NonNull HashMap<ExceptionType, Integer> map) {
        if (mSillCountMap == null) {
            mSillCountMap = new HashMap<>();
        }
        mSillCountMap.putAll(map);
    }

    private void initDefault() {
        mSillCountMap = new HashMap<>();
        mSillCountMap.put(HTTP_ERROR, 3); // 网络请求异常阈值3次
        mSillCountMap.put(SERVER_ERROR, 3); // 服务器异常 3次
        mSillCountMap.put(PARSE_ERROR, 3); // 解析异常 3次
        mSillCountMap.put(NETWORK_ERROR, 3); // 网络环境异常 3次
        mSillCountMap.put(UNKNOWN_ERROR, 3); // 其他异常 3次
    }

    /**
     * 这个可以处理服务器请求成功，但是业务逻辑失败，比如token失效需要重新登陆
     */
    public NetException serviceException(String url, String content) {
        ServerException serverException = new ServerException();
        serverException.setMessage(content);
        serverException.setUrl(url);
        return handleException(serverException, url);
    }

    private synchronized void putException(NetException e) {
        if (mExceptionMap == null) {
            mExceptionMap = new HashMap<>();
        }
        ArrayList<NetException> list = mExceptionMap.get(e.getType());
        if (list == null) {
            list = new ArrayList<>();
            list.add(e);
            mExceptionMap.put(e.getType(), list);
        } else {
            list.add(e);
        }
        // 计算
        calcTriger(e);
    }

    private void calcTriger(@NonNull NetException e) {
        ArrayList<NetException> list = mExceptionMap.get(e.getType());
        Integer count = mSillCountMap.get(e.getType());
        // 数量达到阈值
        if (list != null && count != null && list.size() >= count) {
            long start = list.get(0).getTime();
            long end = list.get(list.size() - 1).getTime();
            if (end - start < mSillTime) {
                // 时间跨度在范围之内，出发网络检测监听
                Log.d(NetExceptionHandler.TAG, "onTriger : " + e.toString());
                if (mListener != null) {
                    mListener.onTriger(e);
                }
            }
            // 时间拖长或者触发监听，则清空数据
            release();
        }
    }

    /**
     * 释放缓存
     */
    public synchronized void release() {
        if (mExceptionMap != null) {
            mExceptionMap.clear();
            mExceptionMap = null;
        }
    }

    /**
     * 这个是处理网络异常，也可以处理业务中的异常
     *
     * @param e   e异常
     * @param url
     */
    public NetException handleException(Throwable e, String url) {
        if (!mEnable) {
            return null;
        }

        NetException ex = null;
        //HTTP错误   网络请求异常 比如常见404 500之类的等
        if (e instanceof retrofit2.HttpException && HTTP_ERROR.in(mHandType)) {
            retrofit2.HttpException httpException = (retrofit2.HttpException) e;
            ex = new NetException(e, HTTP_ERROR);
            ex.setDisplayMessage("网络错误" + httpException.code());
        } else if (e instanceof ServerException && SERVER_ERROR.in(mHandType)) {
            //服务器返回的错误
            ServerException resultException = (ServerException) e;
            String message = resultException.getMessage();
            ex = new NetException(resultException, SERVER_ERROR);
            ex.setDisplayMessage(message);
        } else if ((e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) && PARSE_ERROR.in(mHandType)) {
            ex = new NetException(e, PARSE_ERROR);
            //均视为解析错误
            ex.setDisplayMessage("解析错误");
        } else if (e instanceof ConnectException && NETWORK_ERROR.in(mHandType)) {
            ex = new NetException(e, NETWORK_ERROR);
            //均视为网络错误
            ex.setDisplayMessage("连接失败");
        } else if (e instanceof java.net.UnknownHostException && NETWORK_ERROR.in(mHandType)) {
            ex = new NetException(e, NETWORK_ERROR);
            //网络未连接
            ex.setDisplayMessage("网络未连接");
        } else if ((e instanceof SocketTimeoutException
                || e instanceof SocketException) && NETWORK_ERROR.in(mHandType)) {
            ex = new NetException(e, NETWORK_ERROR);
            //网络未连接
            ex.setDisplayMessage("服务器响应超时");
        } else if (e instanceof IOException && NETWORK_ERROR.in(mHandType)) {
            ex = new NetException(e, NETWORK_ERROR);
            //IO异常
            ex.setDisplayMessage("网络IO异常");
        } else if (UNKNOWN_ERROR.in(mHandType)) {
            ex = new NetException(e, UNKNOWN_ERROR);
            //未知错误
            ex.setDisplayMessage("未知错误");
        }

        if (ex != null) {
            ex.setTime(System.currentTimeMillis());
            ex.setUrl(url);
            putException(ex);

            Log.d(NetExceptionHandler.TAG, "add exception : " + ex.toString());
        }
        return ex;
    }
}
