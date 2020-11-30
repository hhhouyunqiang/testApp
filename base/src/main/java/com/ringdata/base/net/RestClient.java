package com.ringdata.base.net;

import android.content.Context;
import android.util.Log;

import com.ringdata.base.net.callback.IEnd;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.IStart;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.base.net.callback.RequestCallbacks;
import com.ringdata.base.net.download.DownloadHandler;
import com.ringdata.base.ui.loader.LatteLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public final class RestClient {
    //存放所有的请求
    private static List<CallBean> callList = Collections.synchronizedList(new ArrayList<CallBean>());
    private final WeakHashMap<String, Object> PARAMS = RestCreator.getParams();
    private final WeakHashMap<String, String> HEADERS = RestCreator.getHeaders();
    private final Object TAG;
    private final String URL;
    private final IStart START;
    private final IEnd END;
    private final String FILE_PATH;
    private final ISuccess SUCCESS;
    private final IError ERROR;
    private final RequestBody BODY;
    private final List<File> FILES;
    private final File FILE;
    private final Context CONTEXT;
    private final String LOADING_TEXT;

    RestClient(Object tag, String url,
               Map<String, Object> params,
               Map<String, String> headers,
               String filePath,
               IStart start,
               IEnd end,
               ISuccess success,
               IError error,
               RequestBody body,
               List<File> files, File file, Context context,
               String loadingText) {
        this.TAG = tag;
        this.URL = url;
        PARAMS.putAll(params);
        HEADERS.putAll(headers);
        this.FILE_PATH = filePath;
        this.START = start;
        this.END = end;
        this.SUCCESS = success;
        this.ERROR = error;
        this.BODY = body;
        this.CONTEXT = context;
        this.FILES = files;
        this.FILE = file;
        this.LOADING_TEXT = loadingText;
    }

    public static RestClientBuilder builder() {
        return new RestClientBuilder();
    }

    private void request(HttpMethod method) {
        final RestService service = RestCreator.getRestService();
        Call<String> call = null;
        if (CONTEXT != null) {
            LatteLoader.showLoading(CONTEXT, LOADING_TEXT);
        }
        if (START != null) {
            START.onStart();
        }

        switch (method) {
            case GET:
                call = service.get(URL, PARAMS);
                break;
            case POST:
                call = service.post(URL, PARAMS);
                break;
            case POST_TOKEN:
                call = service.postToken(URL, HEADERS);
                break;
            case POST_RAW:
                call = service.postRaw(URL, BODY);
                break;
            case POST_RAW_TOKEN:
                call = service.postRawToken(URL, BODY, HEADERS);
                break;
            case PUT:
                call = service.put(URL, PARAMS);
                break;
            case PUT_RAW:
                call = service.putRaw(URL, BODY);
                break;
            case DELETE:
                call = service.delete(URL, PARAMS);
                break;
            case UPLOAD_FILES:
                List<MultipartBody.Part> parts = new ArrayList<>(FILES.size());
                for (File file : FILES) {
                    RequestBody requestBody =
                            RequestBody.create(MultipartBody.FORM, file);
                    MultipartBody.Part part =
                            MultipartBody.Part.createFormData("files", file.getName(), requestBody);
                    parts.add(part);
                }
                call = service.uploadFiles(URL, parts, PARAMS);
                break;
            case UPLOAD_FILE:
                final RequestBody requestBody =
                        RequestBody.create(MultipartBody.FORM, FILE);
                final MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file", FILE.getName(), requestBody);
                call = service.uploadFile(URL, body, PARAMS);
                break;
            default:
                break;
        }

        if (call != null) {
            call.enqueue(getRequestCallback());
            addCall(call, TAG);
        }
    }

    private Callback<String> getRequestCallback() {
        return new RequestCallbacks(
                START,
                END,
                SUCCESS,
                ERROR,
                CONTEXT,
                LOADING_TEXT
        );
    }

    public final void get() {
        request(HttpMethod.GET);
    }

    public final void post() {
        if (BODY == null && HEADERS.size() == 0) {
            request(HttpMethod.POST);
        } else if (BODY != null) {
            if (HEADERS.size() > 0) {
                request(HttpMethod.POST_RAW_TOKEN);
            } else {
                if (!PARAMS.isEmpty()) {
                    throw new RuntimeException("params must be null!");
                }
                request(HttpMethod.POST_RAW);
            }
        } else {
            request(HttpMethod.POST_TOKEN);
        }
    }

    public final void put() {
        if (BODY == null) {
            request(HttpMethod.PUT);
        } else {
            if (!PARAMS.isEmpty()) {
                throw new RuntimeException("params must be null!");
            }
            request(HttpMethod.PUT_RAW);
        }
    }

    public final void delete() {
        request(HttpMethod.DELETE);
    }

    public final void uploadFile() {
        request(HttpMethod.UPLOAD_FILE);
    }

    public final void uploadFiles() {
        request(HttpMethod.UPLOAD_FILES);
    }

    public final void download() {
        new DownloadHandler(URL, START, END, FILE_PATH,
                SUCCESS, ERROR)
                .handleDownload();
    }

    //把请求加入管理器
    private static void addCall(Call call, Object tag) {
        Log.i("calllistsieze", callList.size() + "");
        synchronized (callList) {
            CallBean callBean = new CallBean();
            callBean.setCall(call);
            callBean.setTag(tag);
            callList.add(callBean);
        }
    }

    public static final void cancelWithTag(Object tag) {
        synchronized (callList) {
            Iterator<CallBean> iterator = callList.iterator();
            while (iterator.hasNext()) {
                Log.i("calllistsieze", callList.size() + "");
                CallBean callBean = iterator.next();
                Call mCall = callBean.getCall();
                Object mTag = callBean.getTag();
                if (mTag == null) {
                    continue;
                }
                if (mTag.equals(tag)) {
                    if (mCall == null || mCall.isCanceled()) {
                        continue;
                    }
                    mCall.cancel();
                    iterator.remove();
                }
            }
        }
    }

    public static final void cancelAll() {
        synchronized (callList) {
            Iterator<CallBean> iterator = callList.iterator();
            while (iterator.hasNext()) {
                CallBean callBean = iterator.next();
                Call mCall = callBean.getCall();
                if (mCall == null || mCall.isCanceled()) {
                    continue;
                }
                mCall.cancel();
                iterator.remove();
            }
        }
    }

    public final void tokenPost() {
        RestService service = RestCreator.getTokenRestService();
        Call<String> call = service.post(URL, PARAMS);
        if (CONTEXT != null) {
            LatteLoader.showLoading(CONTEXT, LOADING_TEXT);
        }
        if (START != null) {
            START.onStart();
        }
        if (call != null) {
            call.enqueue(getRequestCallback());
            addCall(call, TAG);
        }
    }
}
