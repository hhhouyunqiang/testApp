package com.ringdata.ringinterview.capi.components.data.remote;

import android.content.Context;

import com.blankj.utilcode.util.SPUtils;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.constant.SyncType;

/**
 * Created by admin on 2018/5/25.
 */

public class SyncManager {

    private SyncSurvey syncSurvey;
    private SyncSample syncSample;
    private SyncQuestionnaire syncQuestionnaire;
    private SyncResponse syncResponse;
    private SyncPic syncPic;
    private SyncVoice syncVoice;
    private SyncMessage syncMessage;
    private SyncOthers syncOthers;

    public SyncManager(Object bindTag, Context context) {
        int currentUserId = SPUtils.getInstance().getInt(SPKey.USERID);
        int currentSurveyId = SPUtils.getInstance().getInt(SPKey.SURVEY_ID);
        syncSurvey = new SyncSurvey(currentUserId, currentSurveyId, bindTag, SyncType.SYNC_SURVEY, context);
        syncSample = new SyncSample(currentUserId, currentSurveyId, bindTag, SyncType.SYNC_SAMPLE, context);
        syncResponse = new SyncResponse(currentUserId, currentSurveyId, bindTag, SyncType.SYNC_RESPONSE, context);
        syncQuestionnaire = new SyncQuestionnaire(currentUserId, currentSurveyId, bindTag, SyncType.SYNC_QUESTIONNAIRE, context);
        syncPic = new SyncPic(currentUserId, currentSurveyId, bindTag, SyncType.SYNC_PICTURE, context);
        syncVoice = new SyncVoice(currentUserId, currentSurveyId, bindTag, SyncType.SYNC_VOICE, context);
        syncMessage = new SyncMessage(currentUserId, currentSurveyId, bindTag, SyncType.SYNC_MESSAGE, context);
        syncOthers = new SyncOthers(currentUserId, currentSurveyId, bindTag, SyncType.SYNC_OTHERS, context);
    }

    //1.同步项目
    public void syncSurvey() {
        syncSurvey.start();
    }

    //2.同步样本
    public void syncSample() {
        syncSample.start();
    }

    //3.同步问卷数据
    public void syncQuestionnaire() {
        syncQuestionnaire.start();
    }

    //4.同步答卷数据
    public void syncResponse() {
        syncResponse.start();
    }

    //5.同步图片数据
    public void syncPicture() {
        syncPic.start();
    }

    //6.同步录音数据
    public void syncVoice() {
        syncVoice.start();
    }

    //7.同步消息
    public void syncMessage() {
        syncMessage.start();
    }

    //8.同步位置信息
    public void syncOthers() {
        syncOthers.start();
    }
}
