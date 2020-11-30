package com.ringdata.base.net;

import android.content.Context;

import com.ringdata.base.net.callback.IEnd;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.IStart;
import com.ringdata.base.net.callback.ISuccess;

import java.io.File;
import java.util.List;
import java.util.WeakHashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public final class RestClientBuilder {

    private final WeakHashMap<String, Object> PARAMS = RestCreator.getParams();
    private WeakHashMap<String, String> HEADERS = new WeakHashMap<>();
    private Object mTag = null;
    private String mUrl = null;
    private IStart mIStart = null;
    private IEnd mIEnd = null;
    private ISuccess mISuccess = null;
    private IError mIError = null;
    private RequestBody mBody = null;
    private File mFile = null;
    private List<File> mFiles;
    private String mFilePath = null;
    private Context mContext;
    private String mLoadingText;

    RestClientBuilder() {

    }

    public final RestClientBuilder tag(Object tag) {
        this.mTag = tag;
        return this;
    }

    public final RestClientBuilder url(String url) {
        this.mUrl = url;
        return this;
    }

    public final RestClientBuilder params(WeakHashMap<String, Object> params) {
        PARAMS.putAll(params);
        return this;
    }

    public final RestClientBuilder params(String key, Object value) {
        PARAMS.put(key, value);
        return this;
    }

    public final RestClientBuilder header(String key, String value) {
        HEADERS.put(key, value);
        return this;
    }

    public final RestClientBuilder files(List<File> files) {
        this.mFiles = files;
        return this;
    }

    public final RestClientBuilder file(File file) {
        this.mFile = file;
        return this;
    }

    public final RestClientBuilder filePath(String filePath) {
        this.mFilePath = filePath;
        return this;
    }

    public final RestClientBuilder raw(String raw) {
        this.mBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), raw);
        return this;
    }

    public final RestClientBuilder onStart(IStart iStart) {
        this.mIStart = iStart;
        return this;
    }

    public final RestClientBuilder onEnd(IEnd iEnd) {
        this.mIEnd = iEnd;
        return this;
    }

    public final RestClientBuilder success(ISuccess iSuccess) {
        this.mISuccess = iSuccess;
        return this;
    }

    public final RestClientBuilder error(IError iError) {
        this.mIError = iError;
        return this;
    }

    public final RestClientBuilder loader(Context context, String loadingText) {
        this.mContext = context;
        this.mLoadingText = loadingText;
        return this;
    }

    public final RestClient build() {
        return new RestClient(mTag, mUrl, PARAMS, HEADERS,
                mFilePath,
                mIStart, mIEnd, mISuccess,
                mIError, mBody, mFiles, mFile, mContext, mLoadingText);
    }
}
