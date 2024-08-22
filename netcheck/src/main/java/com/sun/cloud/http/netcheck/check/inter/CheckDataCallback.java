package com.sun.cloud.http.netcheck.check.inter;

import com.sun.cloud.http.netcheck.check.common.CheckResult;
import com.sun.cloud.http.netcheck.check.common.CheckType;

import org.json.JSONObject;

/**
 * Created on 2019/10/23
 *
 * @author sunxiaoyun
 */
public interface CheckDataCallback {
    public void onResult(CheckType type, CheckResult result, JSONObject jsonObject);
}
