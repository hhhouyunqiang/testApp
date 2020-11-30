package com.ringdata.base.net.download;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.ringdata.base.app.Latte;
import com.ringdata.base.net.callback.IEnd;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.base.util.file.FileUtil;

import java.io.File;
import java.io.InputStream;

import okhttp3.ResponseBody;


final class SaveFileTask extends AsyncTask<Object, Void, File> {

    private final IEnd END;
    private final ISuccess SUCCESS;

    SaveFileTask(IEnd end, ISuccess success) {
        this.END = end;
        this.SUCCESS = success;
    }

    @Override
    protected File doInBackground(Object... params) {
        String filePath = (String) params[0];
        final ResponseBody body = (ResponseBody) params[1];
        final InputStream is = body.byteStream();
        return FileUtil.writeToDisk(is, filePath);
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if (SUCCESS != null) {
            SUCCESS.onSuccess(file.getPath());
        }
        if (END != null) {
            END.onEnd();
        }
        autoInstallApk(file);
    }

    private void autoInstallApk(File file) {
        if (FileUtil.getExtension(file.getPath()).equals("apk")) {
//            final Intent install = new Intent();
//            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            install.setAction(Intent.ACTION_VIEW);
//            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//            Latte.getApplicationContext().startActivity(install);
            //AppUtils.installApp(file.getAbsoluteFile(),"com.ringdata.ringsurvey.capi.components.fileprovider");

            if(Build.VERSION.SDK_INT >=24 ) {
                //判读版本是否在7.0以上
                Uri apkUri = FileProvider.getUriForFile(Latte.getApplicationContext(), "com.ringdata.ringsurvey.capi.components.fileprovider", file);//在AndroidManifest中的android:authorities值
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                Latte.getApplicationContext().startActivity(install);
            } else{
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Latte.getApplicationContext().startActivity(install);
            }
        }
    }

}
