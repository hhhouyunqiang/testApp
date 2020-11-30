package com.ringdata.base.ui.camera;

import android.net.Uri;

import java.io.File;

public final class CameraImageBean {

    private Uri mPath = null;
    private File file = null;

    private static final CameraImageBean INSTANCE = new CameraImageBean();

    public static CameraImageBean getInstance(){
        return INSTANCE;
    }

    public Uri getPath() {
        return mPath;
    }

    public void setPath(Uri mPath) {
        this.mPath = mPath;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
