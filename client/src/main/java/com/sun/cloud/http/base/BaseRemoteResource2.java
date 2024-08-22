package com.sun.cloud.http.base;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sun.cloud.http.AbsentLiveData;

/**
 * @author WingHawk
 */
public abstract class BaseRemoteResource2<Result, Request> extends NetworkResource<Result, Request> {

    private Request remoteData;

    @Override
    protected void saveCallResult(Request item) {
        remoteData = item;
    }

    @Override
    protected boolean shouldFetch(@Nullable Result data) {
        return true;
    }

    @NonNull
    @Override
    protected LiveData<Result> loadFromDb() {
        return AbsentLiveData.create();
    }

    @Override
    protected LiveData<Result> makeResultData(LiveData<Result> dbSource) {
        MutableLiveData<Result> result = new MutableLiveData<>();
        Result data = transform(remoteData);
        result.setValue(data);
        return result;
    }

    protected abstract Result transform(Request data);
}
