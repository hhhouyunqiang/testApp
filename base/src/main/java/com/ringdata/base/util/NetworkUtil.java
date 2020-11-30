package com.ringdata.base.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import com.ringdata.base.app.Latte;


/**
 * Created by admin on 2017/12/27.
 */

public class NetworkUtil {
    static public boolean isNetworkAvailable() {
        //return NetworkUtils.isAvailableByPing();
        return isNetworkAvailable2();
    }

    static public boolean isNetworkAvailable2() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Latte.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //新版本调用方法获取网络状态
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            if (networks == null) {
                return false;
            }
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo == null) {
                    return false;
                }
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info == null) {
                    return false;
                }
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
