package com.ringdata.ringinterview.capi.components.data.remote;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.FileData;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.QuestionnaireDao;

import java.util.List;

/**
 * Created by admin on 2018/6/18.
 */

public class SyncQuestionnaire extends BaseSync {

    public SyncQuestionnaire(Integer currentUserId, Integer currentSurveyId, Object bindTag, int syncType, Context context) {
        super(currentUserId, currentSurveyId, bindTag, syncType, context);
    }

    @Override
    protected void beginSync() {
        downloadQuestionnaireText();
    }

    //下载问卷文本数据
    public void downloadQuestionnaireText() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", currentSurveyId);
        jsonObject.put("lastSyncTime", mSyncTime);
        RestClient.builder().url(App.orgHost + ApiUrl.DOWNLOAD_QUESTION)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        JSONObject data = responseJson.getDataAsObject();
                        if (!responseJson.getSuccess()) {
                            sendMsgError(responseJson.getMsg());
                            return;
                        }
                        List<Integer> deleteIds = responseJson.getDeleteIdsAsInt("draftQuestionnaireIdList");
                        List<MultiItemEntity> list = QuestionnaireDao.getListFromNet(data.getJSONArray("questionnaireInfoDTOList"), currentUserId);
                        if (deleteIds != null) {
                            QuestionnaireDao.deleteList(deleteIds, currentUserId);
                        }
                        QuestionnaireDao.insertOrUpdateList(list);
                        downloadQuestionnaireFile();
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

    //下载问卷文件
    private void downloadQuestionnaireFile() {
        String[] result = QuestionnaireDao.getQuestionnaireUrl(currentUserId);
        final String url = result[0];
        surveyName = result[1];
        if (url == null) {
            endSync();
            return;
        }
        RestClient.builder().url(ApiUrl.url2(url))
                .filePath(FileData.getQuestionnaireDir(currentUserId) + url)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        QuestionnaireDao.updateDownloadStatus(url);
                        downloadQuestionnaireFile();
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        sendErrorMsg();
                    }
                })
                .build()
                .download();
    }
}
