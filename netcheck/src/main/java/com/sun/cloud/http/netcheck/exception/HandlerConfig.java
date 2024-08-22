package com.sun.cloud.http.netcheck.exception;


import androidx.annotation.NonNull;

import java.util.HashMap;

/**
 * Created on 2019/10/22
 *
 * @author sunxiaoyun
 */
public class HandlerConfig {


    private ExceptionType[] types;
    private long mSillTime = CoreHandler.SILL_TIME_DEFAULT;
    private ExceptionSillListener mListener;
    private HashMap<ExceptionType, Integer> mSillCountMap;


    public HandlerConfig setHandleType(ExceptionType... types) {
        this.types = types;
        return this;
    }

    public HandlerConfig setSillTime(long sillTime) {
        if (sillTime < 0) {
            throw new IllegalArgumentException("sillTime should > 0");
        }
        this.mSillTime = sillTime;
        return this;
    }

    public HandlerConfig setTrigerListener(ExceptionSillListener listener) {
        this.mListener = listener;
        return this;
    }

    public HandlerConfig setTypeSill(ExceptionType type, int sillCount) {
        if (sillCount < 0) {
            throw new IllegalArgumentException("sillCount should > 0");
        }
        if (mSillCountMap == null) {
            mSillCountMap = new HashMap<>();
        }
        mSillCountMap.put(type, sillCount);
        return this;
    }

    public void config(@NonNull CoreHandler handler) {
        if (mSillCountMap != null) {
            handler.setSillCountMap(mSillCountMap);
        }
        if (mListener != null) {
            handler.setListener(mListener);
        }
        if (mSillTime > 0) {
            handler.setSillTime(mSillTime);
        }

        if (types != null) {
            handler.setHandType(types);
        }
    }
}
