package com.sun.cloud.http.netcheck.check.inter;


import androidx.annotation.Nullable;

import com.sun.cloud.http.netcheck.check.common.CheckResult;
import com.sun.cloud.http.netcheck.check.common.CheckType;



/**
 * Created on 2019/10/23
 *
 * @author sunxiaoyun
 */
public interface CheckStepListener {
    public void onStepProgress(CheckType type, int subCount, CheckResult result, int step, int totalStep);

    public void onStepStart();

    public void onStepFinish(@Nullable CheckResult result);
}
