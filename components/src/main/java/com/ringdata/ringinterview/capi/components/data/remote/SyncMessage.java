package com.ringdata.ringinterview.capi.components.data.remote;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.MessageDao;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2018/6/18.
 */

public class SyncMessage extends BaseSync {

    public SyncMessage(Integer currentUserId, Integer currentSurveyId, Object bindTag, int syncType, Context context) {
        super(currentUserId, currentSurveyId, bindTag, syncType, context);
    }

    @Override
    protected void beginSync() {
        uploadMessage();
    }

    public void uploadMessage() {
        List<HashMap<String, Object>> list = MessageDao.queryUploadList(currentUserId);
        if (list == null || list.size() == 0) {
            downloadMessage();
            return;
        }
        RestClient.builder().url(App.orgHost + ApiUrl.UPLOAD_MESSAGE)
                .params("messages", JSON.toJSONString(list))
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        downloadMessage();
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

    public void downloadMessage() {
        RestClient.builder().url(App.orgHost + ApiUrl.DOWNLOAD_MESSAGE)
                .params("userId", currentUserId)
                .params("lastSyncTimeStr", mSyncTime)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (!responseJson.getSuccess()) {
                            sendErrorMsg();
                            return;
                        }
                        if (responseJson.getDataAsArray() != null) {
                            List list = MessageDao.getListFromNet(responseJson.getDataAsArray(), currentUserId);
                            MessageDao.replaceList(list);
                        }
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
