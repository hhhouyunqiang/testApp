package com.ringdata.ringinterview.capi.components.helper;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.ringdata.base.app.Latte;

/**
 * Created by admin on 17/11/15.
 */

public class BDLocationHelper {

    private static LocationClient mLocationClient = null;

    public static void initConfig(BDAbstractLocationListener mDBListener) {
        //getApplicationConext()方法获取全进程有效的Context。
        mLocationClient = new LocationClient(Latte.getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(mDBListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setIsNeedAltitude(true);
        option.setIsNeedAddress(true);
        option.setScanSpan(0);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
    }

    public static void start() {
        mLocationClient.start();
    }

    public static void stop() {
        mLocationClient.stop();
    }

}

