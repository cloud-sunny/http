package com.sun.cloud.http.netcheck.exception;

/**
 * Created on 2019/10/22
 * <p>
 * 服务器业务逻辑异常
 *
 * @author sunxiaoyun
 */
public class ServerException extends RuntimeException {

    public String url;
    public String message;

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
