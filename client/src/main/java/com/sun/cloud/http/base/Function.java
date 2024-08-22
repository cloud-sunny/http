package com.sun.cloud.http.base;


import androidx.lifecycle.LiveData;

/**
 * Created on 2019/11/01
 *
 * @author sunxiaoyun
 */
public interface Function<T> {

    /**
     * @return
     */
    LiveData<? extends IResponse<T>> apply();
}
