package com.ringdata.ringinterview.capi.components.ui;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.app.Latte;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;
import com.ringdata.ringinterview.capi.components.ui.sign.setting.SettingDelegate;
import com.ringdata.ringinterview.capi.components.ui.sign.signIn.SignInDelegate;
import com.ringdata.ringinterview.capi.components.ui.survey.SurveyDelegate;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;

import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by admin on 17/10/15.
 */

public class LauncherDelegate extends LatteDelegate {
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        Latte.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createDB();
            }
        }, 1500);
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_launcher;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    private void createDB() {
        RxPermissions rxPermission = new RxPermissions(getActivity());
        rxPermission
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean permission) throws Exception {
                        if (permission) {
                            //FileData.backUpDb();
                            DBOperation.init(getActivity().getApplicationContext());
                            DBOperation.instanse.openDataBase();
                            DBOperation.instanse.createLocalTables(TableSQL.tableSqlHashMap());
                            checkIsShowScroll();
                        } else {
                            ToastUtils.setBgColor(Color.GRAY);
                            ToastUtils.showShort("没有权限，启动失败");
                        }
                    }
                });
    }

    //判断是否显示滑动启动页
    private void checkIsShowScroll() {
        if (SPUtils.getInstance().getBoolean(SPKey.FIRST_RUN, true)) {
            //第一次安装初始化配置默认
            SPUtils.getInstance().put(SPKey.FIRST_RUN, false);
            SPUtils.getInstance().put(SPKey.SETTING_SAVE_ACCOUNT, true);
            if (SPUtils.getInstance().getBoolean(SPKey.SETTING_SYNC_MEDIA_WIFI, false)) {
                SPUtils.getInstance().put(SPKey.SETTING_SYNC_MEDIA_WIFI, true);
            } else {
                SPUtils.getInstance().put(SPKey.SETTING_SYNC_MEDIA_WIFI, false);
            }
            SPUtils.getInstance().put(SPKey.SETTING_AUTO_SYNC_DATA, true);
            SPUtils.getInstance().put(SPKey.APP_LOCATION_INTERVAL, 5);
            OkHttpClient okHttpClient = new OkHttpClient();
            FormBody.Builder builder = new FormBody.Builder();
            Request request = new Request.Builder()
                    .url(ApiUrl.UPDATE_APP_DOWNLOAD)
                    .post(builder.build())
                    .build();
            okHttpClient.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.code() == 200) {
                                Log.i("DEV", "success");
                            } else {
                                Log.e("DEV", "error");
                            }
                        }
                    });
        }
        String host = SPUtils.getInstance().getString(SPKey.HOST);
        if (!host.isEmpty()) {
            App.orgHost = host;
        }
        //检查是否设置记住密码
        if (SPUtils.getInstance().getBoolean(SPKey.SETTING_SAVE_ACCOUNT)) {
            Integer userId = SPUtils.getInstance().getInt(SPKey.USERID);
            String password = SPUtils.getInstance().getString(SPKey.PASSWORD);
            if (userId != 0 && !StringUtils.isEmpty(password)) {
                startWithPop(new SurveyDelegate());
                return;
            }
        }
        //检查用户是否已经配置了HOST
        if (!App.orgHost.isEmpty()) {
            Latte.init(getContext()).withApiHost(App.orgHost).configure();
            startWithPop(new SignInDelegate());
        } else {
            startWithPop(new SettingDelegate());
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
