package com.ringdata.ringinterview.capi.components.data.remote;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.SurveyDao;

import java.util.List;

/**
 * Created by admin on 2018/6/18.
 */

public class SyncSurvey extends BaseSync {
    public SyncSurvey(Integer currentUserId, Integer currentSurveyId, Object bindTag, int syncType, Context context) {
        super(currentUserId, currentSurveyId, bindTag, syncType, context);
    }

    @Override
    protected void beginSync() {
        downloadSurvey();
    }

    private void downloadSurvey() {
        JSONObject json = new JSONObject();
        json.put("userId", currentUserId);
        RestClient.builder().url(App.orgHost + ApiUrl.DOWNLOAD_SURVEY)
                .raw(json.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (!responseJson.getSuccess()) {
                            sendMsgError(responseJson.getMsg());
                            return;
                        }
                        JSONObject data = responseJson.getDataAsObject();
                        List<Integer> deleteIdList = responseJson.getDeleteIdsAsInt("deleteIdList");
                        if (deleteIdList != null) {
                            SurveyDao.deleteList(deleteIdList, currentUserId);
                        }
                        List<MultiItemEntity> list = SurveyDao.getListFromNet(data.getJSONArray("projectList"), currentUserId);
                        SurveyDao.replaceList(list);
                        endSync();
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        sendErrorMsg();
                    }
                })
                .build()
                .post();
    }
}
