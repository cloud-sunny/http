package com.sun.cloud.http.interceptor;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sun.cloud.http.ApiCenter;
import com.sun.cloud.http.base.ExceptionHandler;
import com.sun.cloud.http.exception.RequestInterruptedException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 加载注册的 interceptors 集合，并在请求和响应的时候调用。
 * 注意：所有的 interceptor 都常驻内存，请勿在其成员变量中持有 activity 相关的引用。
 * <p>
 * Created on 2019/7/3
 *
 * @author WingHawk
 */

public class InterceptorManager implements Interceptor {

    private final CopyOnWriteArrayList<IArgcInterceptor> mInterceptors = new CopyOnWriteArrayList<>();
    private ExceptionHandler mExceptionHandler;
    private final Comparator<IArgcInterceptor> comparator = (Comparator<IArgcInterceptor>) IArgcInterceptor::compareTo;

    protected InterceptorManager() {
    }

    public InterceptorManager addInterceptor(IArgcInterceptor interceptor) {
        mInterceptors.add(interceptor);
        sort();
        return this;
    }

    /**
     * 这里不能直接用Collections.sort(),
     * 因为List排序默认用的ListIterator,
     * 在CopyOnWriteArrayList中ListIterator的set会报UnsupportedOperationException
     */
    private void sort() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // comparator 不能为空，android 7.x 上会判空抛异常
            mInterceptors.sort(comparator);
        } else {
            IArgcInterceptor[] elements = mInterceptors.toArray(new IArgcInterceptor[]{});
            Arrays.sort(elements, comparator);
            mInterceptors.clear();
            mInterceptors.addAll(Arrays.asList(elements));
        }
    }

    public InterceptorManager setExceptionInterceptHandler(@NonNull ExceptionHandler handler) {
        mExceptionHandler = handler;
        return this;
    }

    public static InterceptorManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final InterceptorManager INSTANCE = new InterceptorManager();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (mExceptionHandler != null) {
            Response response;
            String url = chain.request().url().toString();
            try {
                response = interceptInner(chain);
                if (!response.isSuccessful()) {
                    // 服务器响应异常处理
                    String message = "Response{ protocol="
                            + response.protocol()
                            + ", code="
                            + response.code()
                            + ", message="
                            + response.message()
                            + " }";
                    mExceptionHandler.onServerException(url, message);
                }
            } catch (RequestInterruptedException e) {
                mExceptionHandler.onRequestInterruptedException(url, e);
                throw e;
            } catch (IOException e) {
                // 代码/网络 异常处理
                mExceptionHandler.onHttpException(url, e);
                throw e;
            }
            return response;
        }
        return interceptInner(chain);
    }

    private Response interceptInner(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        ListIterator<IArgcInterceptor> listIterator = mInterceptors.listIterator();
        while (listIterator.hasNext()) {
            IArgcInterceptor interceptor = listIterator.next();
            if (ApiCenter.debug) {
                Log.d(ApiCenter.TAG, "intercept beforeProceed : "
                        + interceptor.getClass().getName());
            }
            request = interceptor.beforeProceed(chain, request);

            if (request == null) {
                throw new RequestInterruptedException();
            }
            if (interceptor.isTerminal()) {
                break;
            }
        }
        Response response = chain.proceed(request);
        listIterator = mInterceptors.listIterator(mInterceptors.size());
        while (listIterator.hasPrevious()) {
            IArgcInterceptor interceptor = listIterator.previous();
            if (ApiCenter.debug) {
                Log.d(ApiCenter.TAG, "intercept afterProceed : "
                        + interceptor.getClass().getName());
            }
            response = interceptor.afterProceed(chain, response);
            if (response == null) {
                throw new RequestInterruptedException();
            }
            if (interceptor.isTerminal()) {
                break;
            }
        }
        return response;
    }
}
