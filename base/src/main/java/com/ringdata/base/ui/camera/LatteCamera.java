package com.ringdata.base.ui.camera;

import android.app.Activity;


/**
 * Created by 傅令杰
 * 照相机调用类
 */

public class LatteCamera {

    public static void start(Activity context,String filePath) {
        new CameraHandler(context,filePath).beginCameraDialog();
    }
}
