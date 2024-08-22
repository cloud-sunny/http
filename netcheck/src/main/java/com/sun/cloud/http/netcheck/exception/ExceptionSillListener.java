package com.sun.cloud.http.netcheck.exception;

/**
 * Created on 2019/10/22
 *
 * 异常阈值触发
 * @author sunxiaoyun
 */
public interface ExceptionSillListener {
    public void onTriger(NetException e);
}
