package com.sun.cloud.http.netcheck;

import android.content.Context;

import com.sun.cloud.http.netcheck.check.CheckHandler;
import com.sun.cloud.http.netcheck.check.common.CheckType;
import com.sun.cloud.http.netcheck.check.inter.CheckStepListener;

/**
 * Created on 2019/10/18
 * <p>
 * 网络诊断工具
 * 流程如下：
 * 1，判断网络权限是否拥有
 * 2，检查网络是否连接
 * 3，能否ping通主流网站，判断是否连接上互联网
 * 4，能否ping通目标服务器
 * 5，能否访问接口
 *
 * @author sunxiaoyun
 */
public class NetCheck {

    public static final String TAG = "NetCheck";

    private CheckHandler mHandler;
    private boolean isChina = true;
    private Context mContext;

    public boolean isChina() {
        return isChina;
    }

    public void setChina(boolean china) {
        isChina = china;
    }

    public Context getContext() {
        return mContext;
    }

    public NetCheck setAddress(String address) {
        assertHandler();
        mHandler.setAddress(address);
        return this;
    }

    public NetCheck setCheckStepListener(CheckStepListener listener) {
        assertHandler();
        mHandler.setCheckStepListener(listener);
        return this;
    }

    public NetCheck setCheckType(CheckType type, int count) {
        assertHandler();
        mHandler.setCheckType(type, count);
        return this;
    }

    public NetCheck start() {
        assertHandler();
        mHandler.start();
        return this;
    }

    public NetCheck stop() {
        if (mHandler != null) {
            mHandler.stop();
        }
        return this;
    }

    public void detach() {
        if (mHandler != null) {
            mHandler.release();
            mHandler = null;
        }
        mContext = null;
    }


    /**
     * when use, should call attach(context)
     * end if release, must call detach()
     *
     * @param context
     * @return
     */
    public NetCheck attach(Context context) {
        mContext = context;
        mHandler = new CheckHandler();
        return this;
    }

    private void assertHandler() {
        if (mHandler == null) {
            throw new IllegalStateException("please call attach first");
        }
    }

    private static NetCheck instance;

    private NetCheck() {
    }

    public static NetCheck get() {
        if (instance == null) {
            instance = new NetCheck();
        }
        return instance;
    }

}
