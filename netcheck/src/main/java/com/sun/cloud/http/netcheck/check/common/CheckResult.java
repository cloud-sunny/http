package com.sun.cloud.http.netcheck.check.common;

/**
 * Created on 2019/10/23
 *
 * @author sunxiaoyun
 */
public enum CheckResult {
    SUCCESS(0, "成功"),
    NET_NO_PERMISSION(1, "无网络权限，请手动授权开启"),
    NET_NOT_OPEN(2, "WIFI和移动数据已关闭，请手动开启"),
    NET_NOT_CONNECT(3, "网络未连接，请检查网络环境"),
    NET_INVALIDE(4, "未连接到互联网， 请检查网络环境"),
    SERVER_NOT_CONNECT(5, "无法连接到服务器，请稍后操作或联系客服"),
    SERVICE_INVALIDE(6, "服务无响应，请稍后操作"),
    UNKNOW_ERROR(7, "未知错误，请稍后操作");

    public int code;
    public String desc;

    CheckResult(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
