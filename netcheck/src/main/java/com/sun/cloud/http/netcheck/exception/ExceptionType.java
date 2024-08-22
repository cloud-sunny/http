package com.sun.cloud.http.netcheck.exception;

/**
 * Created on 2019/10/22
 *
 * @author sunxiaoyun
 */
public enum ExceptionType {
    HTTP_ERROR(0x00001),
    SERVER_ERROR(0x00010),
    PARSE_ERROR(0x00100),
    NETWORK_ERROR(0x01000),
    UNKNOWN_ERROR(0x10000),
    ALL(0x11111);

    public int type;

    ExceptionType(int type) {
        this.type = type;
    }

    public boolean in(int types) {
        return (this.type & types) > 0;
    }
}
