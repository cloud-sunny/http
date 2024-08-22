package com.sun.cloud.http.netcheck.check.resource.base;


import com.sun.cloud.http.netcheck.NetCheck;

import org.json.JSONObject;

import java.io.Serializable;


public class BaseBean implements Serializable {

    protected JSONObject jsonObject = new JSONObject();


    protected JSONObject toJSONObject() {
        return jsonObject;
    }

    protected BaseBean() {

    }

    public boolean isChina() {
        return NetCheck.get().isChina();
    }

}
