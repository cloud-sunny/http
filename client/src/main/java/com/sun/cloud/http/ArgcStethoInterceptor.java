package com.sun.cloud.http;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import retrofit2.internal.EverythingIsNonNull;

/**
 * Created on 2020/9/27
 *
 * @author WingHawk
 */
public class ArgcStethoInterceptor implements Interceptor {
    private boolean enabled;
    private StethoInterceptor mInterceptor = new StethoInterceptor();

    private ArgcStethoInterceptor() {
    }

    public static ArgcStethoInterceptor getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    @EverythingIsNonNull
    public Response intercept(Chain chain) throws IOException {
        if (enabled) {
            return mInterceptor.intercept(chain);
        }
        return chain.proceed(chain.request());
    }

    private static class InstanceHolder {
        private static final ArgcStethoInterceptor INSTANCE = new ArgcStethoInterceptor();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
