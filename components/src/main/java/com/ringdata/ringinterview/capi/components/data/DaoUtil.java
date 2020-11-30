package com.ringdata.ringinterview.capi.components.data;

import com.ringdata.ringinterview.capi.components.data.dao.QuestionnaireDao;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseAudioFileDao;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseDao;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseQuestionFileDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleDao;

/**
 * Created by admin on 2018/2/4.
 */

public class DaoUtil {

    public static Integer countNoUploadSampleByUserId(Integer userId) {
        return SampleDao.countNoUpload(userId);
    }

    public static Integer countNoUploadResponseByUserId(Integer userId) {
        return ResponseDao.countNoUpload(userId);
    }

    public static Integer countNoUploadPicByUserId(Integer userId) {
        return ResponseQuestionFileDao.countNoUploadFile(userId);
    }

    public static Integer countNoUploadAudioByUserId(Integer userId) {
        return ResponseAudioFileDao.countNoUploadFile(userId);
    }

    public static Integer countAllResponse(Integer userId, Integer surveyId, String sampleId) {
        int a = ResponseDao.countSubResponse(userId, sampleId, surveyId);
        int b = QuestionnaireDao.countMainBySurveyId(userId, surveyId);
        return a + b;
    }

    public static void clearLocalData() {
        String[] tableName = {
                TableSQL.SURVEY_NAME,
                TableSQL.SAMPLE_NAME,
                TableSQL.QUESTIONNAIRE_NAME,
                TableSQL.RESPONSE_NAME,
                TableSQL.RESPONSE_QUESTION_FILE_NAME,
                TableSQL.RESPONSE_AUDIO_FILE_NAME,
                TableSQL.USER_LOCATION_NAME,
                TableSQL.SYNC_LOG_NAME,
                TableSQL.MESSAGE_NAME,
                TableSQL.SAMPLE_ADDRESS_NAME,
                TableSQL.SAMPLE_CONTACT_NAME,
                TableSQL.SAMPLE_TOUCH_NAME
        };
        DBOperation.instanse.clearTable(tableName);
    }

}
