/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.cloud.http.base;

import android.text.TextUtils;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.core.util.ObjectsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.sun.cloud.http.Resource;


/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 * @param <Result>
 * @param <Request>
 */
public abstract class NetworkResource<Result, Request> {

    protected MediatorLiveData<Resource<Result>> result = new MediatorLiveData<>();

    @MainThread
    protected NetworkResource() {
        result.setValue(Resource.loading(null));
        LiveData<Result> dbSource = loadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            } else {
                result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
            }
        });
    }

    protected void setValue(Resource<Result> newValue) {
        if (!ObjectsCompat.equals(result.getValue(), newValue)) {
            result.postValue(newValue);
        }
    }

    protected void fetchFromNetwork(final LiveData<Result> dbSource) {
        LiveData<? extends IResponse<Request>> apiResponse = createCall();
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            if (response != null && response.isSuccessful()) {
                if (response.getData() == null ||
                        ((response.getData() instanceof Iterable) && !((Iterable) response.getData()).iterator().hasNext())) {
                    // reload from disk whatever we had
                    result.addSource(loadFromDb(), newData -> setValue(Resource.success(newData, response.getMessage())));
                } else {
                    Request requestData = processResponse(response);
                    saveCallResult(requestData);
                    // we specially request a new live data,
                    // otherwise we will get immediately last cached value,
                    // which may not be updated with latest results received from network.
                    LiveData<Result> source = makeResultData(dbSource);
                    result.addSource(source, newData -> setValue(Resource.success(newData, response.getMessage())));
                }
            } else {
                onFetchFailed();
                result.addSource(dbSource, newData -> {
                    String message = response == null ||
                            TextUtils.isEmpty(response.getMessage()) ||
                            "null".equalsIgnoreCase(response.getMessage())
                            ? "服务异常" : response.getMessage();
                    setValue(Resource.error(message, newData, response != null ? response.getCode() : 0));
                });
            }
        });
    }

    public LiveData<Resource<Result>> asLiveData() {
        return result;
    }

    protected LiveData<Result> makeResultData(LiveData<Result> dbSource) {
        return loadFromDb();
    }

    protected void onFetchFailed() {

    }

    @WorkerThread
    protected <R extends IResponse<Request>> Request processResponse(R response) {
        return response.getData();
    }

    @WorkerThread
    protected abstract void saveCallResult(Request item);

    protected abstract boolean shouldFetch(@Nullable Result data);

    @NonNull
    protected abstract LiveData<Result> loadFromDb();

    @NonNull
    protected abstract LiveData<? extends IResponse<Request>> createCall();
}
