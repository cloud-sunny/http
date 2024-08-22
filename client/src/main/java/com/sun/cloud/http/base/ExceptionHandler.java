package com.sun.cloud.http.base;

/**
 * Created on 2021/4/13
 * <p>
 *
 * @author sunxiaoyun
 */
public interface ExceptionHandler {
    /**
     * 服务接口异常
     *
     * @param url
     * @param message
     */
    void onServerException(String url, String message);

    /**
     * 网络请求异常
     *
     * @param url
     * @param throwable
     */
    void onHttpException(String url, Throwable throwable);

    /**
     * @param url
     * @param throwable 发生这种情况 会导致loading不消失,当该接口使用loading时,该异常无法形成闭合
     */
    default void onRequestInterruptedException(String url, Throwable throwable) {

    }
}
