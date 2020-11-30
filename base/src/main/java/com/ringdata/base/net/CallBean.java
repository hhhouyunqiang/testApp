package com.ringdata.base.net;


import retrofit2.Call;

/**
 * Created by admin on 2018/4/16.
 */

public class CallBean {
    private Object tag;
    private Call call;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }
}
