package com.sun.cloud.http.netcheck.check.resource.ping;


import androidx.annotation.NonNull;
import android.util.Log;

import com.sun.cloud.http.netcheck.NetCheck;
import com.sun.cloud.http.netcheck.check.common.CheckResult;
import com.sun.cloud.http.netcheck.check.common.CheckType;
import com.sun.cloud.http.netcheck.check.common.Ping;
import com.sun.cloud.http.netcheck.check.inter.CheckDataCallback;

public class PingHelper {

    public static void getPingParam(CheckType type, String url, @NonNull CheckDataCallback callback) {
        Ping ping = new Ping(url);
        PingBean pingBean = ping.getPingInfo();
        Log.i(NetCheck.TAG, "Ping is end");

        callback.onResult(type, analysis(type, pingBean), pingBean.toJSONObject());
    }

    private static CheckResult analysis(CheckType type, PingBean pingBean) {
        boolean success = pingBean.getReceive() > 0 && pingBean.getReceive() == pingBean.getTransmitted();
        if (success) {
            return CheckResult.SUCCESS;
        }
        if (type == CheckType.PING_INTERNET) {
            return CheckResult.NET_INVALIDE;
        }
        if (type == CheckType.PING_SERVER) {
            return CheckResult.SERVER_NOT_CONNECT;
        }

        if (type == CheckType.ACCESS_SERVICE) {
            return CheckResult.SERVICE_INVALIDE;
        }

        return CheckResult.NET_INVALIDE;
    }
}
