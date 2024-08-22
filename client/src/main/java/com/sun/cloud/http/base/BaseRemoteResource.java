package com.sun.cloud.http.base;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sun.cloud.http.AbsentLiveData;

/**
 * @author WingHawk
 */
public abstract class BaseRemoteResource<Result> extends NetworkResource<Result, Result> {

	private Result remoteData;

	@Override
	protected void saveCallResult(Result item) {
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
		result.setValue(remoteData);
		return result;
	}
}
