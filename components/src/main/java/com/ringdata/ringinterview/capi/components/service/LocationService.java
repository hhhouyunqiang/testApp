package com.ringdata.ringinterview.capi.components.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.app.Latte;
import com.ringdata.ringinterview.capi.components.constant.ProjectConstants;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.constant.SyncType;
import com.ringdata.ringinterview.capi.components.data.dao.LocationDao;
import com.ringdata.ringinterview.capi.components.data.dao.SurveyDao;
import com.ringdata.ringinterview.capi.components.data.model.Survey;
import com.ringdata.ringinterview.capi.components.data.remote.SyncOthers;
import com.ringdata.ringinterview.capi.components.helper.BDLocationHelper;
import com.ringdata.ringinterview.capi.components.utils.LocationTool;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/12/27.
 */

public class LocationService extends Service {
    private Handler handler;
    //定位的时间间隔
    private int TIME_SPACE;
    private int userId;
    SyncOthers syncOthers;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userId = SPUtils.getInstance().getInt(SPKey.USERID);
        syncOthers = new SyncOthers(userId, SPUtils.getInstance().getInt(SPKey.SURVEY_ID), getApplicationContext(), SyncType.SYNC_OTHERS, null);
        TIME_SPACE = 5 * 60 * 1000;
        if (handler == null) {
            handler = new Handler();
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                readInterval1();
                handler.postDelayed(this, TIME_SPACE);
            }
        };
        handler.post(runnable);
        return super.onStartCommand(intent, flags, startId);
    }
    private void readInterval1() {
        List<MultiItemEntity> allProject = SurveyDao.queryListAll(userId, ProjectConstants.PROJECT_ALL);
        for (int i = 0; i < allProject.size(); i++) {
            Survey project = (Survey) allProject.get(i);
            JSONObject configJson = JSON.parseObject(project.getConfig());
            if (configJson.getIntValue("trackInterviewer")!= 0 /*&& project.getRemainInterval() == 0*/) {
                startLocation();
                break;
            }
        }
        SurveyDao.replaceList(allProject);
    }
    private void readInterval() {
        List<MultiItemEntity> allProject = SurveyDao.queryListAll(userId, ProjectConstants.PROJECT_ALL);
        for (int i = 0; i < allProject.size(); i++) {
            Survey project = (Survey) allProject.get(i);
            JSONObject configJson = JSON.parseObject(project.getConfig());
            if (project.getRemainInterval() != null /*&& project.getRemainInterval() == 0*/) {
                //startLocation(project.getId());
                if (configJson.getIntValue("trackInterviewer") != 0 && configJson.getIntValue("trackInterviewer") != 4) {
                    project.setRemainInterval(configJson.getIntValue("trackInterviewer"));
                } else if (configJson.getIntValue("trackInterviewer") == 0) {
                    project.setRemainInterval(null);
                } else if (configJson.getIntValue("trackInterviewer") == 4) {
                    project.setRemainInterval(6);
                }
                project.setRemainInterval(project.getRemainInterval() - 1);
            }
        }
        SurveyDao.replaceList(allProject);
    }

    private void startLocation() {
        BDLocationHelper.initConfig(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                BDLocationHelper.stop();
                float radius = location.getRadius(); //获取定位精度，默认值为0.0f
                int code = location.getLocType();
                if (code == 61 || code == 161) { //定位成功
                    location = LocationTool.BAIDU_to_WGS84(location);
                    final float latitude = (float) location.getLatitude(); //获取纬度信息
                    final float longitude = (float) location.getLongitude(); //获取经度信息
                    final String address = location.getAddrStr(); //获取位置
                    Latte.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            List<MultiItemEntity> allProject = SurveyDao.queryListAll(userId, ProjectConstants.PROJECT_ALL);
                            for (int i = 0; i < allProject.size(); i++) {
                                Survey project = (Survey) allProject.get(i);
                                JSONObject configJson = JSON.parseObject(project.getConfig());
                                if (configJson.getIntValue("trackInterviewer")!= 0 ) {
                                    LocationDao.insert(latitude, longitude, address, new Date().getTime(), userId, project.getId());
                                    if (AppUtils.isAppForeground()) {
                                        syncOthers.uploadUserLocation1(project.getId());
                                    }
                                }
                            }
                            //以前写法
//                            LocationDao.insert(latitude, longitude, address, new Date().getTime(), userId, projectId);
//                            if (AppUtils.isAppForeground()) {
//                                syncOthers.uploadUserLocation1(projectId);
//                            }
                        }
                    });
                }
                BDLocationHelper.stop();
            }
        });
        BDLocationHelper.start();
    }
}
