package com.ringdata.base.net.download;

import android.os.AsyncTask;

import com.ringdata.base.net.RestCreator;
import com.ringdata.base.net.callback.IEnd;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.IStart;
import com.ringdata.base.net.callback.ISuccess;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.WeakHashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public final class DownloadHandler {

    private final String URL;
    private static final WeakHashMap<String, Object> PARAMS = RestCreator.getParams();
    private final IStart START;
    private final IEnd END;
    private final String FILE_PATH;
    private final ISuccess SUCCESS;
    private final IError ERROR;

    public DownloadHandler(String url,
                           IStart start,
                           IEnd end,
                           String filePath,
                           ISuccess success,
                           IError error) {
        this.URL = url;
        this.START = start;
        this.END = end;
        this.FILE_PATH = filePath;
        this.SUCCESS = success;
        this.ERROR = error;
    }
    public final void handleDownload() {
        if (START != null) {
            START.onStart();
        }
        RestCreator
                .getRestService()
                .download(URL, PARAMS)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            final ResponseBody responseBody = response.body();
                            final SaveFileTask task = new SaveFileTask(END, SUCCESS);
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                    FILE_PATH, responseBody);
                            //这里一定要注意判断，否则文件下载不全
                            if (task.isCancelled()) {
                                if (END != null) {
                                    END.onEnd();
                                }
                            }
                        } else {
                            if (ERROR != null) {
                                ERROR.onError(response.code(), response.message());
                            }
                        }
                        RestCreator.getParams().clear();
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (ERROR != null) {
                            if (t instanceof SocketTimeoutException) {
                                ERROR.onError(-1, t.toString());
                            } else if (t instanceof ConnectException) {
                                ERROR.onError(-2, t.toString());
                            }
                            RestCreator.getParams().clear();
                        }
                    }
                });
    }
}