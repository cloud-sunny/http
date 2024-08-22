package com.sun.cloud.http.netcheck.check.common;

/**
 * Created on 2019/10/23
 *
 * @author sunxiaoyun
 */
public enum CheckType {
    NET_PERMISSION(0),
    NET_CONNECT(1),
    PING_INTERNET(2),
    PING_SERVER(3),
    ACCESS_SERVICE(4);

    private int value;

    CheckType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
