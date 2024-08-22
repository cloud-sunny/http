package com.sun.cloud.http.netcheck;

import com.sun.cloud.http.netcheck.exception.CoreHandler;
import com.sun.cloud.http.netcheck.exception.HandlerConfig;

/**
 * Created on 2019/10/22
 * <p>
 * 接口异常接收处理
 *
 * @author sunxiaoyun
 */
public class NetExceptionHandler {

    public static final String TAG = "NetExceptionHandler";


    private CoreHandler mHandler;

    /**
     * 接收异常，onError/ onFailed
     *
     * @param url
     * @param e
     */
    public void handException(String url, Throwable e) {
        mHandler.handleException(e, url);
    }

    /**
     * 接收业务异常，处理服务异常
     *
     * @param url
     * @param content
     */
    public void handException(String url, String content) {
        mHandler.serviceException(url, content);
    }

    private NetExceptionHandler() {
        mHandler = new CoreHandler();
    }

    public void open() {
        mHandler.setEnable(true);
    }

    public boolean isOpened() {
        return mHandler.isEnable();
    }

    public void close() {
        mHandler.setEnable(false);
    }


    /**
     * 手动释放缓存
     */
    public void release() {
        mHandler.release();
    }

    /**
     * 配置参数
     *
     * @return
     */
    public void config(HandlerConfig handlerConfig) {
        handlerConfig.config(mHandler);
    }


    /**
     * 获取异常解析全局单例
     *
     * @return
     */
    public static NetExceptionHandler get() {
        return NetExceptionHandlerLoader.instance;
    }

    private static class NetExceptionHandlerLoader {
        private static final NetExceptionHandler instance = new NetExceptionHandler();
    }
}
