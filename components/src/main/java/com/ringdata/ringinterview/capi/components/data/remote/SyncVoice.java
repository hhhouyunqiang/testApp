package com.ringdata.ringinterview.capi.components.data.remote;

import android.content.Context;

import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.FileData;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseAudioFileDao;

import java.io.File;
import java.util.HashMap;

/**
 * Created by admin on 2018/6/18.
 */

public class SyncVoice extends BaseSync {

    public SyncVoice(Integer currentUserId, Integer currentSurveyId, Object bindTag, int syncType, Context context) {
        super(currentUserId, currentSurveyId, bindTag, syncType, context);
    }

    @Override
    protected void beginSync() {
        uploadResponseAudioFileData();
    }

    //提交答卷音频数据（答卷录音+单题录音）
    public void uploadResponseAudioFileData() {
        HashMap<String, Object> hm = ResponseAudioFileDao.queryNoUpload(currentUserId, currentSurveyId);
        if (hm == null) {
//            ToastUtils.setBgColor(Color.GRAY);
//            ToastUtils.showShort("所有音频已上传完毕");
            endSync();
            return;
        }
        final String filename = (String) hm.get("filename");
        File file = new File(FileData.getSurveyDir(currentUserId) + filename);
        if (file == null || file.length() == 0) {
            ResponseAudioFileDao.updateUploadFileStatus(currentUserId, filename);
            uploadResponseAudioFileData();
            return;
        }
        RestClient.builder().url(App.orgHost + ApiUrl.UPLOAD_SURVEY_FILES)
                .params("projectId", currentSurveyId)
                .params("type", "voice")
                .file(file)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (responseJson.getSuccess()) {
                            ResponseAudioFileDao.updateUploadFileStatus(currentUserId, filename);
                            uploadResponseAudioFileData();
                        } else {
                            sendMsgError(responseJson.getMsg());
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        sendErrorMsg();
                    }
                })
                .build()
                .uploadFile();
    }
}
