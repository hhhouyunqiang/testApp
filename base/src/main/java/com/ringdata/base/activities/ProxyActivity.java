package com.ringdata.base.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.ContentFrameLayout;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.R;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.ui.camera.CameraImageBean;
import com.ringdata.base.ui.camera.RequestCodes;
import com.ringdata.base.util.GUIDUtil;
import com.ringdata.base.util.callback.CallbackManager;
import com.ringdata.base.util.callback.CallbackType;
import com.ringdata.base.util.callback.IGlobalCallback;
import com.ringdata.base.util.file.FileUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import me.yokeyword.fragmentation.SupportActivity;

public abstract class ProxyActivity extends SupportActivity {
    public abstract LatteDelegate setRootDelegate();
    private File cropImageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }
        //强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initContainer(savedInstanceState);
    }

    private void initContainer(@Nullable Bundle savedInstanceState) {
        final ContentFrameLayout container = new ContentFrameLayout(this);
        container.setId(R.id.delegate_container);
        setContentView(container);
        if (savedInstanceState == null) {
            loadRootFragment(R.id.delegate_container, setRootDelegate());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //System.gc();
        //System.runFinalization();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int currentApiVersion = android.os.Build.VERSION.SDK_INT;
            switch (requestCode) {
                case RequestCodes.TAKE_PHOTO:
                    if (SPUtils.getInstance().getBoolean("CAMERA_IF_HEADIMAGE")) {
                        File file = CameraImageBean.getInstance().getFile();
                        Uri resultUri;
                        if (currentApiVersion >= 24) {
                            resultUri = FileProvider.getUriForFile(this,
                                    "com.ringdata.ringsurvey.capi.components.fileprovider", file);
                        } else {
                            resultUri = Uri.fromFile(file);
                        }
                        startCrop(resultUri);
                    } else {
                        Uri resultUri = CameraImageBean.getInstance().getPath();
                        UCrop.of(resultUri, resultUri)
                                .withMaxResultSize(400, 400)
                                .start(this);
                    }
                    break;
                case RequestCodes.PICK_PHOTO:
                    if (data != null) {
                        final Uri pickPath = data.getData();
                        if (SPUtils.getInstance().getBoolean("CAMERA_IF_HEADIMAGE")) {
                            startCrop(pickPath);
                        } else {
                            //从相册选择后需要有个路径存放剪裁过的图片
                            final String pickCropPath = Environment.getExternalStorageDirectory() + "/ringsurvey-capi/data/user/" + GUIDUtil.getGuidStr() + ".png";
                            UCrop.of(pickPath, Uri.parse(pickCropPath))
                                    .withMaxResultSize(400, 400)
                                    .start(this);
                        }
                    }
                    break;
                case RequestCodes.CROP_PHOTO:
                    final Uri cropUri = UCrop.getOutput(data);
                    //拿到剪裁后的数据进行处理
                    @SuppressWarnings("unchecked")
                    final IGlobalCallback<Uri> callback = CallbackManager
                            .getInstance()
                            .getCallback(CallbackType.ON_CROP);
                    if (callback != null) {
                        callback.executeCallback(cropUri);
                    }
                    break;
                case RequestCodes.CROP_PHOTO_OVAL:
                    //拿到剪裁后的数据进行处理
                    @SuppressWarnings("unchecked")
                    final IGlobalCallback<File> callbacks = CallbackManager
                            .getInstance()
                            .getCallback(CallbackType.ON_OVAL_CROP);
                    if (callbacks != null) {
                        callbacks.executeCallback(cropImageFile);
                    }
                    break;
                case RequestCodes.CROP_ERROR:
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("剪裁出错");
                    break;
                default:
                    break;
            }
        }
    }

    private void startCrop(Uri originUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        File file = FileUtil.createFile(Environment.getExternalStorageDirectory() + "/ringsurvey-capi/data/user/" + GUIDUtil.getGuidStr() + ".png");
        //兼容7.0及以上的写法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, file.getPath());
            final Uri uri = this.getContentResolver().
                    insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            cropImageFile = FileUtils.getFileByPath(FileUtil.getRealFilePath(this, uri));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            final Uri fileUri = Uri.fromFile(file);
            cropImageFile = file;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        }
        intent.setDataAndType(originUri,"image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        //设置图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("return-data", false); //data不需要返回，避免图片太大异常
        intent.putExtra("noFaceDetection", true); //no face detection
        startActivityForResult(intent, RequestCodes.CROP_PHOTO_OVAL);
    }
}
