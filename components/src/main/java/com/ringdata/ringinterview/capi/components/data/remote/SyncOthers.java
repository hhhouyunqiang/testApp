package com.ringdata.ringinterview.capi.components.data.remote;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.LocationDao;
import com.ringdata.ringinterview.capi.components.data.dao.SurveyDao;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2018/6/18.
 */

public class SyncOthers extends BaseSync {

    public SyncOthers(Integer currentUserId, Integer currentSurveyId, Object bindTag, int syncType, Context context) {
        super(currentUserId, currentSurveyId, bindTag, syncType, context);
    }

    @Override
    protected  void beginSync() {

    }

    public void uploadUserLocation() {
        final List<Integer> projectIdList = SurveyDao.queryAllSurveyId(currentUserId);
        for (final Integer projectId : projectIdList) {
            List<HashMap<String, Object>> list = LocationDao.queryNoUploadList(currentUserId, projectId);
            if (list == null || list.size() == 0) {
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("positionData", list);
            jsonObject.put("projectId", projectId);
            String ul=App.orgHost + ApiUrl.UPLOAD_USER_LOCATION;
            String uls="http://192.168.0.102:8075/appapi/interviewer/travel/save";
            RestClient.builder().url(ul)
                    .raw(jsonObject.toJSONString())
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            ResponseJson responseJson = new ResponseJson(response);
                            if (responseJson.getSuccess()) {
                                LocationDao.updateUploadStatus(currentUserId, projectId);
                            }
                        }
                    })
                    .build()
                    .post();
        }
    }
    //这是我加的，把读项目列表去掉，不循环请求
    public void uploadUserLocation1(final int projectId) {
            List<HashMap<String, Object>> list = LocationDao.queryNoUploadList(currentUserId, projectId);
            if (list == null || list.size() == 0) {
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("positionData", list);
            jsonObject.put("projectId", projectId);
            String ul=App.orgHost + ApiUrl.UPLOAD_USER_LOCATION;
            //String uls="http://192.168.0.102:8075/appapi/interviewer/travel/save";
            RestClient.builder().url(ul)
                    .raw(jsonObject.toJSONString())
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            ResponseJson responseJson = new ResponseJson(response);
                            if (responseJson.getSuccess()) {
                                LocationDao.updateUploadStatus(currentUserId, projectId);
                            }
                        }
                    })
                    .build()
                    .post();

    }
}
