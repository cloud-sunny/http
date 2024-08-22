package com.sun.cloud.http.netcheck.check.resource.net;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.sun.cloud.http.netcheck.NetCheck;
import com.sun.cloud.http.netcheck.check.common.CheckResult;
import com.sun.cloud.http.netcheck.check.common.CheckType;
import com.sun.cloud.http.netcheck.check.common.LogTime;
import com.sun.cloud.http.netcheck.check.common.Net;
import com.sun.cloud.http.netcheck.check.inter.CheckDataCallback;

public class NetHelper {

    public static void getNetParam(CheckType type, @NonNull CheckDataCallback callback) {
        long startTime = LogTime.getLogTime();
        final NetBean netBean = new NetBean();
        Context context = NetCheck.get().getContext();
        netBean.setNetworkAvailable(Net.isNetworkAvailable(context));
        netBean.setNetWorkType(Net.networkType(context));
        netBean.setMobileType(Net.networkTypeMobile(context));
        netBean.setWifiRssi(Net.getWifiRssi(context));
        netBean.setWifiLevel(Net.calculateSignalLevel(netBean.getWifiRssi()));
        netBean.setWifiLevelValue(Net.checkSignalRssi(netBean.getWifiLevel()));
        netBean.setIp(Net.getClientIp());
        netBean.setRoaming(Net.checkIsRoaming(context));
        Net.getMobileDbm(context, netBean);
        netBean.setMobLevelValue(Net.checkSignalRssi(netBean.getMobLevel()));
        netBean.setTotalName(LogTime.getElapsedMillis(startTime));
        Log.i(NetCheck.TAG, "Net is end");

        callback.onResult(type, analysis(context, netBean), netBean.toJSONObject());
    }

    private static CheckResult analysis(Context context, NetBean netBean) {
        if (netBean.isNetworkAvailable()) {
            return CheckResult.SUCCESS;
        }

        if (!Net.isWifiOpened(context) && !Net.isMobileEnabled(context)) {
            return CheckResult.NET_NOT_OPEN;
        }

        if (Net.isMobileEnabled(context) && Net.hasSimCard(context) && "Other".equals(netBean.getNetWorkType())) {
            return CheckResult.NET_NO_PERMISSION;
        }
        return CheckResult.NET_NOT_CONNECT;
    }

}
