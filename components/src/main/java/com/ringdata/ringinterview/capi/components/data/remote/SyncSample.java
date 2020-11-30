package com.ringdata.ringinterview.capi.components.data.remote;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
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
import com.ringdata.ringinterview.capi.components.data.dao.SampleAddressDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleContactDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleRecordDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleTouchDao;
import com.ringdata.ringinterview.capi.components.data.dao.SurveyDao;
import com.ringdata.ringinterview.capi.components.data.model.Sample;
import com.ringdata.ringinterview.capi.components.data.model.SampleAddress;
import com.ringdata.ringinterview.capi.components.data.model.SampleContact;
import com.ringdata.ringinterview.capi.components.data.model.SampleRecord;
import com.ringdata.ringinterview.capi.components.data.model.SampleTouch;
import com.ringdata.ringinterview.capi.components.data.model.Survey;

import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by admin on 2018/6/18.
 */

public class SyncSample extends BaseSync {

    public SyncSample(Integer currentUserId, Integer currentSurveyId, Object bindTag, int syncType, Context context) {
        super(currentUserId, currentSurveyId, bindTag, syncType, context);
    }

    @Override
    protected void beginSync() {
        uploadUserSampleRecord();
    }

    //提交样本更改记录
    private void uploadUserSampleRecord() {
        List<SampleRecord> sampleRecordData = SampleRecordDao.getNeedUploadListByUserId(currentUserId, currentSurveyId);
        if (sampleRecordData.size() == 0) {
            uploadUserSample();
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sampleStatusRecord", sampleRecordData);
        RestClient.builder().url(App.orgHost + ApiUrl.UPLOAD_SAMPLE_RECORD)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (responseJson.getSuccess()) {
                            SampleRecordDao.updateUploadStatus(currentUserId, currentSurveyId);
                            uploadUserSample();
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
                .post();
    }

    //提交样本
    private void uploadUserSample() {
        List<Sample> sampleData = SampleDao.getNeedUploadListByUserId(currentUserId, currentSurveyId);
        if (sampleData.size() == 0) {
            if (SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_SUPPORT_SAMPLE_CONTACT_RECORD)) {
                uploadUserSampleExtraData();
            } else {
                downloadSample();
            }
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", currentSurveyId);
        jsonObject.put("sampleData", sampleData);
        RestClient.builder().url(App.orgHost + ApiUrl.UPLOAD_SAMPLE)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (responseJson.getSuccess()) {
                            SampleDao.updateUploadStatus(currentUserId, currentSurveyId);
                            if (SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_SUPPORT_SAMPLE_CONTACT_RECORD)) {
                                uploadUserSampleExtraData();
                            } else {
                                downloadSample();
                            }
                        } else {
                            if (responseJson.getMsg() != null && !"".equals(responseJson.getMsg())) {
                                sendMsgError(responseJson.getMsg());
                                //要加载样本列表数据
                                if (SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_SUPPORT_SAMPLE_CONTACT_RECORD)) {
                                    uploadUserSampleExtraData();
                                } else {
                                    downloadSample();
                                }
                            } else {
                                sendMsgError(responseJson.getMsg());
                            }
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
                .post();
    }

    //提交样本额外
    private void uploadUserSampleExtraData() {
        List<SampleAddress> sampleAddresses = SampleAddressDao.queryNoUploadList(currentUserId, currentSurveyId);
        List<SampleTouch> sampleTouches = SampleTouchDao.queryNoUploadList(currentUserId, currentSurveyId);
        List<SampleContact> sampleContacts = SampleContactDao.queryNoUploadList(currentUserId, currentSurveyId);
        if (sampleAddresses.size() == 0 && sampleTouches.size() == 0 && sampleContacts.size() == 0) {
            downloadSample();
            return;
        }

        WeakHashMap<String, Object> data = new WeakHashMap<>();
        data.put("sampleAddressList", sampleAddresses);
        data.put("sampleContactsRecordList", sampleTouches);
        data.put("sampleContactsList", sampleContacts);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sampleExtraInfo", data);
        jsonObject.put("lastSyncTime", mSyncTime);
        RestClient.builder().url(App.orgHost + ApiUrl.UPLOAD_SAMPLE_EXTRA_DATA)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (responseJson.getSuccess()) {
                            SampleAddressDao.updateUploadStatus(currentUserId, currentSurveyId);
                            SampleTouchDao.updateUploadStatus(currentUserId, currentSurveyId);
                            SampleContactDao.updateUploadStatus(currentUserId, currentSurveyId);
                            downloadSample();
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
                .post();
    }

    //下载样本
    private void downloadSample() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", currentSurveyId);
        jsonObject.put("lastSyncTime", mSyncTime);
        RestClient.builder().url(App.orgHost + ApiUrl.DOWNLOAD_SAMPLE)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        final ResponseJson responseJson = new ResponseJson(response);
                        JSONObject data = responseJson.getDataAsObject();
                        if (!responseJson.getSuccess()) {
                            sendMsgError(responseJson.getMsg());
                            return;
                        }
                        List<String> deleteIds = responseJson.getDeleteIdsAsString("deleteSampleList");
                        if (deleteIds != null) {
                            SampleDao.deleteList(deleteIds, currentUserId);
                        }
                        List<MultiItemEntity> list = SampleDao.getListFromNet(data.getJSONArray("sampleList"), currentUserId);
                        SampleDao.insertOrUpdateList(list);
                        Survey newSurvey = SurveyDao.getNewSurveyFromNet(data, currentUserId, currentSurveyId);
                        SurveyDao.update(newSurvey);
                        if (SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_SUPPORT_SAMPLE_CONTACT_RECORD)) {
                            downloadSampleExtraData();
                        } else {
                            endSync();
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
                .post();
    }

    //下载样本额外
    private void downloadSampleExtraData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", currentSurveyId);
        jsonObject.put("lastSyncTime", mSyncTime);
        RestClient.builder().url(App.orgHost + ApiUrl.DOWNLOAD_SAMPLE_EXTRA_DATA)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        final ResponseJson responseJson = new ResponseJson(response);
                        if (!responseJson.getSuccess()) {
                            sendMsgError(responseJson.getMsg());
                            return;
                        }
                        JSONObject data = responseJson.getDataAsObject();
                        if (data == null) {
                            endSync();
                            return;
                        }
                        JSONArray sampleAddressListJsonArray = data.getJSONArray("sampleAddressList");
                        JSONArray sampleContactListJsonArray = data.getJSONArray("sampleContactList");
                        JSONArray sampleTouchListJsonArray = data.getJSONArray("sampleTouchList");
                        if (sampleAddressListJsonArray != null) {
                            final List<SampleAddress> list = SampleAddressDao.getListFromNet(sampleAddressListJsonArray, currentUserId, currentSurveyId);
                            SampleAddressDao.insertOrUpdateList(list);
                        }
                        if (sampleContactListJsonArray != null) {
                            final List<SampleContact> list = SampleContactDao.getListFromNet(sampleContactListJsonArray, currentUserId, currentSurveyId);
                            SampleContactDao.insertOrUpdateList(list);
                        }

                        if (sampleTouchListJsonArray != null) {
                            final List<SampleTouch> list = SampleTouchDao.getListFromNet(sampleTouchListJsonArray, currentUserId, currentSurveyId);
                            SampleTouchDao.insertOrUpdateList(list);
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
