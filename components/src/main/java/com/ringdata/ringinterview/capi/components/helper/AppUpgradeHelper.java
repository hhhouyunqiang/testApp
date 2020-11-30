package com.ringdata.ringinterview.capi.components.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.ResponseBody;

/**
 * Created by Xie Chenghao on 16/12/5.
 */
public class AppUpgradeHelper {
    private Activity _activity;
    private KProgressHUD _hud;
    private File _downloadDir;
    private File _fileDownloaded;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    _hud.dismiss();
                    if (msg.arg1 == 0 && _fileDownloaded != null) {
                        installApk(_activity, _fileDownloaded);
                    } else {
                        toast("新版本下载失败, 您可以稍后重试安装新版本!");
                    }
                    break;
                default:
                    break;
            }

        }

    };

    public AppUpgradeHelper(Activity act, File downloadDir) {
        _downloadDir = downloadDir;
        _activity = act;
    }

    public void startUpgrade(final String url) {
        final File local = new File(_downloadDir, getFilename(url));
        if (local.exists()) {
            local.delete();
        }

        _hud = KProgressHUD.create(_activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("下载更新版本")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.3f);
        _hud.show();
        startDownload(url, local);
    }


    private void startDownload(final String url, final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.what = 1;
                FileOutputStream fileOutputStream = null;
                InputStream inputStream = null;
                try {
                    URL furl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) furl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    int rcode = connection.getResponseCode();
                    if (rcode == 200) {
                        inputStream = connection.getInputStream();

                        if (inputStream != null) {
                            long filesize = connection.getContentLength();
                            long downloaded = 0;
                            Log.d("LAMI", "File size " + filesize);
                            fileOutputStream = new FileOutputStream(file);
                            byte[] buffer = new byte[4096];
                            int length = 0;

                            while ((length = inputStream.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, length);
                                onProgressUpdated(downloaded, filesize);
                                downloaded += length;
                            }
                            fileOutputStream.flush();
                        }
                        _fileDownloaded = file;
                        message.arg1 = 0;
                        Log.d("RS", "File downloaded ");
                    } else {
                        message.arg1 = 1;
                        Log.d("RS", "No file response code " + rcode);
                    }

                } catch (Exception e) {
                    Log.e("RS", "Failed to download file", e);
                    message.arg1 = 1;
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                        }
                    }
                }
                handler.sendMessage(message);
            }
        }).start();
    }


    private void installApk(Context context, File file) {
        Log.d("RS", "Install APK");
        Intent intent = null;
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= 24) {
            Uri uri = FileProvider.getUriForFile(context,
                    "com.ringdata.ringsurvey.capi.components.fileprovider", file);
            Log.d("RS", "Uri " + uri);
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            Uri uri = Uri.fromFile(file);
            Log.d("RS", "Uri " + uri);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        _activity.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid()); //如果不加上这句的话在apk安装完成之后会崩溃
    }

    private void openApk(File f) {
        PackageManager manager = _activity.getPackageManager();
        // 这里的是你下载好的文件路径
        PackageInfo info = manager.getPackageArchiveInfo(f.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
        if (info != null) {
            Intent intent = manager.getLaunchIntentForPackage(info.applicationInfo.packageName);
            _activity.startActivity(intent);
        }
    }

    private void onProgressUpdated(final long downloaded, long total) {
        final long p = (downloaded * 100) / total;
        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _hud.setDetailsLabel(" " + p + "%");
            }
        });
    }


    private boolean writeResponseBodyToDisk(ResponseBody body, File f) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(f);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;
                    onProgressUpdated(fileSizeDownloaded, fileSize);
                    Log.d("RS", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    private String getFilename(String url) {
        return url.substring(url.lastIndexOf("/"), url.length());
    }


    private void toast(String message) {
        ToastUtils.setBgColor(Color.GRAY);
        ToastUtils.showShort(message);
    }

}
