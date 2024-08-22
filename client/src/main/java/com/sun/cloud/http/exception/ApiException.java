package com.sun.cloud.http.exception;

/**
 * Created on 2020/1/8
 *
 * @author WingHawk
 */
public class ApiException extends Exception {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException() {
    }
}
