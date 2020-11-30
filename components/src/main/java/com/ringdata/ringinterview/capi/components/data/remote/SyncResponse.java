package com.ringdata.ringinterview.capi.components.data.remote;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseAudioFileDao;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseDao;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseQuestionFileDao;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2018/6/18.
 */

public class SyncResponse extends BaseSync {
//    private int remainDownloadNumber = 0;
//    private int remainUploadNumber = 0;

    public SyncResponse(Integer currentUserId, Integer currentSurveyId, Object bindTag, int syncType, Context context) {
        super(currentUserId, currentSurveyId, bindTag, syncType, context);
    }

    @Override
    protected void beginSync() {
        uploadResponseText();
    }

    public void uploadResponseText() {
        //答卷基本数据
        final HashMap<String, String> response = ResponseDao.queryNoUploadResponse(currentUserId, currentSurveyId);
        if (response == null) {
            downloadResponseText();
            return;
        }
        final String responseId = (String) response.get("responseGuid");
        //答卷录音文件数据
        List<HashMap<String, Object>> responseAudio = ResponseAudioFileDao.queryNoUploadList(currentUserId, responseId);
        //题目文件数据
        List<HashMap<String, Object>> responseQuestionFile = ResponseQuestionFileDao.queryNoUploadList(currentUserId, responseId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(response);
        jsonObject.put("lastModifyResource", SPUtils.getInstance().getString(SPKey.SURVEY_TYPE));
        if (responseAudio != null && responseAudio.size() > 0) {
            jsonObject.put("responseAudioData", responseAudio);
        } else {
            jsonObject.put("responseAudioData", null);
        }
        if (responseQuestionFile != null && responseQuestionFile.size() > 0) {
            jsonObject.put("responseQuestionFileData", responseQuestionFile);
        } else {
            jsonObject.put("responseQuestionFileData", null);
        }
        String url1="http://192.168.0.141:8075/appapi/answer/capi/submit";
        String url2=App.orgHost + ApiUrl.UPLOAD_RESPONSE;
        RestClient.builder().url(url2)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String httpResponse) {
                        ResponseJson responseJson = new ResponseJson(httpResponse);
                        if (!responseJson.getSuccess()) {
                            sendMsgError(responseJson.getMsg());
                            return;
                        }
                        ResponseDao.updateUploadStatus(currentUserId, responseId);
                        ResponseAudioFileDao.updateUploadStatus(currentUserId, responseId);
                        ResponseQuestionFileDao.updateUploadStatus(currentUserId, responseId);
                        uploadResponseText();
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

    public void downloadResponseText() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", currentSurveyId);
        jsonObject.put("lastSyncTime", mSyncTime);
        RestClient.builder().url(App.orgHost + ApiUrl.DOWNLOAD_RESPONSE)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        final ResponseJson responseJson = new ResponseJson(response);
                        if (!responseJson.getSuccess()) {
                            sendMsgError(responseJson.getMsg());
                            return;
                        }
                        List<String> deleteIds = responseJson.getDeleteIdsAsString("deletedResGuidList");
                        if (deleteIds != null) {
                            ResponseDao.deleteList(deleteIds, currentUserId);
                        }
                        JSONObject data = responseJson.getDataAsObject();
                        final List<MultiItemEntity> list = ResponseDao.getListFromNet(data.getJSONArray("responseByLastTimeList"), currentUserId);
                        ResponseDao.replaceList(list);
                        downloadResponseDetail();
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

    public void downloadResponseDetail() {
        HashMap<String, Object> hm = ResponseDao.getNeedDownloadDetailId(currentUserId);
        if (hm == null) {
            endSync();
            return;
        }
        final String responseGuid = (String) hm.get("id");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", currentSurveyId);
        jsonObject.put("responseGuid", responseGuid);
        RestClient.builder().url(App.orgHost + ApiUrl.DOWNLOAD_RESPONSE_DETAIL)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        final ResponseJson responseJson = new ResponseJson(response);
                        if (!responseJson.getSuccess()) {
                            sendMsgError(responseJson.getMsg());
                            return;
                        }
                        JSONObject jsonObject = responseJson.getDataAsObject();
                        if (jsonObject != null) {
                            String submitData = jsonObject.getString("submitData");
                            String submitState = jsonObject.getString("submitState");
                            ResponseDao.updateResponseDetail(currentUserId, responseGuid, submitData, submitState);
                        } else {
                            ResponseDao.updateResponseStatus(currentUserId, responseGuid);
                        }
                        downloadResponseDetail();
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
