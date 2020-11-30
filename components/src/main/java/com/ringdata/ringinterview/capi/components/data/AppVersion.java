package com.ringdata.ringinterview.capi.components.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.BuildConfig;
import com.ringdata.ringinterview.capi.components.constant.Msg;
import com.ringdata.ringinterview.capi.components.helper.AppUpgradeHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by admin on 17/11/30.
 * 版本更新管理
 */

public class AppVersion {

//    private static ProgressDialog progressDialog;
    private static final int localVersionCode = BuildConfig.VERSION_CODE;

    public static void checkAppVersion(final Activity activity) {
        RestClient.builder().url(App.orgHost + ApiUrl.UPDATE_APP)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            ResponseJson responseJson = new ResponseJson(response);
                            if (responseJson.getSuccess()) {
                                JSONObject dataJsonObject = responseJson.getDataAsObject();
                                int versionCode = dataJsonObject.getInteger("versionCode");
                                final String versionName = dataJsonObject.getString("versionName");
                                final String versionDesc = dataJsonObject.getString("versionDesc").replace("\\n", "\n");
                                final String versionUrl = dataJsonObject.getString("versionUrl");
                                final int isMust = dataJsonObject.getIntValue("isMust");
                                Log.d("DEV", "check local " + localVersionCode + " " + versionCode);

                                if (versionCode > localVersionCode) {
                                    AlertDialog dialog = new AlertDialog.Builder(activity)
                                            .setTitle("新版本(" + versionName + ")")//设置对话框的标题
                                            .setMessage(versionDesc)//设置对话框的内容
                                            .setCancelable(false)
                                            //设置对话框的按钮
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    EventBus.getDefault().post(new MessageEvent(Msg.R_E_APP, this));
                                                }
                                            })
                                            .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    File file = new File(FileData.UPDATE_APK);
                                                    if (!file.exists()) {
                                                        file.mkdirs();
                                                    }
                                                    AppUpgradeHelper upgradeHelper = new AppUpgradeHelper(activity, file);
                                                    upgradeHelper.startUpgrade(App.orgHost + "/appapi" + versionUrl);
                                                    dialog.dismiss();
                                                }
                                            }).create();
                                    dialog.show();
                                } else {
                                    EventBus.getDefault().post(new MessageEvent(Msg.R_E_APP, this));
                                }
                            } else {
                                EventBus.getDefault().post(new MessageEvent(Msg.R_E_APP, this));
                            }
                        } catch (Exception e) {
                            EventBus.getDefault().post(new MessageEvent(Msg.R_E_APP, this));
                        }
                    }
                })
                .build()
                .post();
    }
}
