package com.sun.cloud.http;

/**
 * 缓存时间提供对象
 * @author WingHawk
 */
@FunctionalInterface
public interface ICacheTimeProvider {
    /**
     * 获取接口缓存时间
     *
     * @param host 域名，比如 uatgw.xxx.com
     * @param path 接口路径。比如 /m/farm/queryFarmList，以 "/" 开头
     * @return 接口缓存时间，单位：秒
     */
    int getCacheTimeInSec(String host, String path);
}