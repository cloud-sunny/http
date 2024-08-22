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

package com.sun.cloud.http.calladapter;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import com.sun.cloud.http.ApiCenter;
import com.sun.cloud.http.ResponseErrorProxy;
import com.sun.cloud.http.base.IResponse;
import com.sun.cloud.http.converter.ResponseBodyConverter;
import com.sun.cloud.http.exception.RequestInterruptedException;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 *
 * @param <R>
 * @author WingHawk
 */
public class LiveDataCallAdapter<T, R extends IResponse<T>> implements CallAdapter<R, LiveData<IResponse<T>>> {
    private final Type responseType;

    LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @NonNull
    @Override
    public Type responseType() {
        return responseType;
    }

    @NonNull
    @Override
    public LiveData<IResponse<T>> adapt(@NonNull Call<R> call) {
        return new ApiResponseLiveData<>(call, responseType);
    }

    static class ApiResponseLiveData<T, R extends IResponse<T>> extends LiveData<IResponse<T>> {
        private final AtomicBoolean mStarted = new AtomicBoolean(false);
        private final DestroyObserver mDestroyObserver = new DestroyObserver();
        private final Call<R> call;
        private LifecycleOwner lifecycleOwner;
        private final Type responseType;

        ApiResponseLiveData(Call<R> call, Type responseType) {
            this.call = call;
            this.responseType = responseType;
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super IResponse<T>> observer) {
            super.observe(owner, observer);
            this.lifecycleOwner = owner;
            owner.getLifecycle().addObserver(mDestroyObserver);
        }

        @Override
        protected void onActive() {
            if (mStarted.compareAndSet(false, true)) {
                call.enqueue(new Callback<R>() {
                    @Override
                    public void onResponse(@NonNull Call<R> call, @NonNull Response<R> response) {
                        removeDestroyObserver();
                        if (!response.isSuccessful()
                                && response.body() == null
                                && response.errorBody() != null
                        ) {
                            try {
                                postValue(new ResponseBodyConverter<R>(responseType)
                                        .convert(response.errorBody()));
                            } catch (Throwable e) {
                                postValue(ResponseErrorProxy.errorToResponse(e));
                            }
                        } else {
                            postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<R> call, @NonNull Throwable throwable) {
                        removeDestroyObserver();
                        if (ApiCenter.debug) {
                            String http = call.request().url().toString();
                            Log.i("[http网络请求]", "请求链接[" + http + "]异常信息[" + (throwable != null ? throwable.getMessage() : "") + "]");
                        }
                        if (!(throwable instanceof RequestInterruptedException)) {
                            postValue(ResponseErrorProxy.errorToResponse(throwable));
                        }
                    }
                });
            }
        }

        private void removeDestroyObserver() {
            if (lifecycleOwner != null) {
                lifecycleOwner.getLifecycle().removeObserver(mDestroyObserver);
            }
        }

        class DestroyObserver implements LifecycleObserver {

            @SuppressWarnings("unused")
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            void onDestroy() {
                if (call != null && !call.isCanceled()) {
                    call.cancel();
                }
            }
        }
    }
}
