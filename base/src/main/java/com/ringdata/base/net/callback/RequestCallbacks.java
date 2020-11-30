package com.ringdata.base.net.callback;

import android.content.Context;
import android.graphics.Color;

import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.ui.loader.LatteLoader;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin on 17/10/11.
 */

public final class RequestCallbacks implements Callback<String> {

    private final IStart START;
    private final IEnd END;
    private final ISuccess SUCCESS;
    private final IError ERROR;
    private final Context CONTEXT;
    private final String LOADING_TEXT;

    public RequestCallbacks(IStart start, IEnd end, ISuccess success, IError error, Context context, String loading_text) {
        this.START = start;
        this.END = end;
        this.SUCCESS = success;
        this.ERROR = error;
        this.CONTEXT = context;
        this.LOADING_TEXT = loading_text;
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
            if (call.isExecuted()) {
                if (SUCCESS != null) {
                    final String responseBody = response.body();
                    SUCCESS.onSuccess(responseBody);
                }
            }
        } else {
            if (ERROR != null) {
                try {
                    ERROR.onError(response.code(), response.errorBody().string());
                } catch (IOException e) {
                    ERROR.onError(response.code(), response.message());
                    e.printStackTrace();
                }
            }
        }
        onRequestFinish();
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        if (t instanceof SocketTimeoutException) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("连接超时");
            if (ERROR != null) {
                ERROR.onError(-1, "连接超时");
            }
        } else if (t instanceof ConnectException) {
            if (ERROR != null) {
                ERROR.onError(-2, "连接超时");
            }
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("连接超时");
        } else if (t instanceof NoRouteToHostException) {
            if (ERROR != null) {
                ERROR.onError(-3, "连接超时");
            }
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("连接超时");
        }
        onRequestFinish();
    }

    private void onRequestFinish() {
        if (CONTEXT != null) {
            LatteLoader.stopLoading();
        }
        //一次请求结束
        if (END != null) {
            END.onEnd();
        }
    }
}