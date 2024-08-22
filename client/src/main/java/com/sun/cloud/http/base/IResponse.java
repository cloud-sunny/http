package com.sun.cloud.http.base;

/**
 * Created on 2021/4/14
 * <p>
 * 网络请求返回数据接口类
 * 业务数据类必须实现此接口
 *
 * @author sunxiaoyun
 */
public interface IResponse<T> {

    /**
     * 返回状态码
     *
     * @return status
     */
    default int getCode(){
        return 0;
    }

    /**
     * 请求返回消息
     *
     * @return message
     */
    String getMessage();

    /**
     * 请求返回数据
     *
     * @return data
     */
    T getData();

    /**
     * 请求是否成功
     *
     * @return success
     */
    boolean isSuccessful();
}
