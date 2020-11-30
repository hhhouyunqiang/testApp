package com.ringdata.ringinterview.capi.components;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.ringdata.base.BaseApp;
import com.ringdata.base.app.Latte;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.service.LocationService;
import com.ringdata.ringinterview.capi.components.service.NetworkChangeService;
import com.ringdata.ringinterview.capi.components.ui.icon.FontModule;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by admin on 17/12/1.
 */
public class App extends BaseApp {
    public static String orgHost;
    public static String surveyVariable = "{\"interviewer_type\":1}";
    public static Intent netWorkService;
    public static Intent locationService;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        if (!"com.ringdata.ringsurvey.capi:remote".equals(getProcessName(this))) {
            Logger.addLogAdapter(new AndroidLogAdapter());
            CrashReport.initCrashReport(getApplicationContext(), "5df8c362d6", false);

            if (TextUtils.isEmpty(SPUtils.getInstance().getString(SPKey.HOST))) {
                SPUtils.getInstance().put(SPKey.HOST, host());
            }
            Latte.init(this)
                    .withIcon(new FontAwesomeModule())
                    .withIcon(new FontModule())
                    .withLoaderDelayed(1000)
                    .withApiHost(SPUtils.getInstance().getString(SPKey.HOST))
                    .configure();
            netWorkService = new Intent(this, NetworkChangeService.class);
            locationService = new Intent(this, LocationService.class);
            startService(netWorkService);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        stopService(netWorkService);
        stopService(locationService);
        DBOperation.instanse.closeDataBase();
    }

    public String host() {
        return "";
    }

    public String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
