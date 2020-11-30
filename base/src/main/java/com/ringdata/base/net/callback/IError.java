package com.ringdata.base.net.callback;

/**
 * Created by admin on 17/10/11.
 */

public interface IError {
    void onError(int code, String msg);
}