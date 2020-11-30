package com.ringdata.base.util.callback;

/**
 * Created by admin on 2018/3/30.
 */

public interface AsyncCallBack {
    public void before();
    public abstract void execute();
    public abstract void after();
}

