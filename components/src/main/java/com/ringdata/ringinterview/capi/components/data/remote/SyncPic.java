package com.ringdata.ringinterview.capi.components.data.remote;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.FileUtils;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.FileData;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseQuestionFileDao;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2018/6/18.
 */

public class SyncPic extends BaseSync {

    public SyncPic(Integer currentUserId, Integer currentSurveyId, Object bindTag, int syncType, Context context) {
        super(currentUserId, currentSurveyId, bindTag, syncType, context);
    }

    @Override
    protected void beginSync() {
        uploadResponsePicFileData();
    }

    //提交答卷文件
    private void uploadResponsePicFileData() {
        HashMap<String, Object> hm = ResponseQuestionFileDao.queryNoUpload(currentUserId, currentSurveyId);
        if (hm == null) {
            downloadResponseFileUrl();
            endSync();
            return;
        }

        final String filename = (String) hm.get("filename");
        final File file = new File(FileData.getSurveyDir(currentUserId) + filename);
        if (file == null || file.length() == 0) {
            ResponseQuestionFileDao.updateUploadFileStatus(currentUserId, filename);
            uploadResponsePicFileData();
            return;
        }
        RestClient.builder().url(App.orgHost + ApiUrl.UPLOAD_SURVEY_FILES)
                .params("projectId", currentSurveyId)
                .params("type", "attach")
                .file(file)
                .tag(this)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (responseJson.getSuccess()) {
                            ResponseQuestionFileDao.updateUploadFileStatus(currentUserId, filename);
                            uploadResponsePicFileData();
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

    //下载图片url
    private void downloadResponseFileUrl() {
        JSONObject json = new JSONObject();
        json.put("lastSyncTime", mSyncTime);
        RestClient.builder().url(App.orgHost + ApiUrl.DOWNLOAD_RESPONSE_FILE)
                .raw(json.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        final ResponseJson responseJson = new ResponseJson(response);
                        if (!responseJson.getSuccess()) {
                            sendMsgError(responseJson.getMsg());
                        }
                        List<HashMap<String, Object>> list = ResponseQuestionFileDao.getListFromNet(responseJson.getDataAsArray());
                        if (list != null && list.size() >= 1) {
                            ResponseQuestionFileDao.insertOrUpdateList(list, currentUserId);
                        }
                        downloadResponseFile();
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

    //下载图片
    private void downloadResponseFile() {
        final String fileurl = ResponseQuestionFileDao.getFileUrl(currentUserId);
        if (fileurl == null) {
            endSync();
            return;
        }
        String dir = FileData.getSurveyDir(currentUserId);
        final String localFilePath = dir + FileUtils.getFileName(fileurl);
        RestClient.builder().url(ApiUrl.url2(fileurl))
                .filePath(localFilePath)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        File file = new File(localFilePath + FileUtils.getFileName(response));
                        if (!file.exists()) {
                            ResponseQuestionFileDao.updateDownloadStatus(FileUtils.getFileName(response));
                            downloadResponseFile();
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        if (code == 404) {
                            ResponseQuestionFileDao.updateDownloadStatus(FileUtils.getFileName(fileurl));
                            downloadResponseFile();
                        } else {
                            endSync();
                        }
                    }
                })
                .build()
                .download();

    }
}
