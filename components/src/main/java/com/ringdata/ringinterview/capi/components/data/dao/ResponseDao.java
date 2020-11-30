package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.util.GUIDUtil;
import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;
import com.ringdata.ringinterview.capi.components.data.model.Response;
import com.ringdata.ringinterview.capi.components.ui.survey.questionnaire.QuestionnaireItemType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by admin on 17/11/14.
 */

public class ResponseDao {

    private static Response cursor2Obj(Cursor cursor) {
        Response response = new Response();
        response.setId(cursor.getString(cursor.getColumnIndex("id")));
        response.setItemType(QuestionnaireItemType.QUESTIONNAIRE_MAIN);
        response.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
        response.setInterviewerId(cursor.getInt(cursor.getColumnIndex("interviewer_id")));
        response.setProjectId(cursor.getInt(cursor.getColumnIndex("project_id")));
        response.setSampleGuid(cursor.getString(cursor.getColumnIndex("sample_guid")));
        response.setModuleId(cursor.getInt(cursor.getColumnIndex("module_id")));
        response.setModuleCode(cursor.getString(cursor.getColumnIndex("module_code")));
        response.setModuleName(cursor.getString(cursor.getColumnIndex("module_name")));
        response.setVersion(cursor.getInt(cursor.getColumnIndex("version")));
        response.setUrl(cursor.getString(cursor.getColumnIndex("url")));
        response.setQuestionnaireId(cursor.getInt(cursor.getColumnIndex("questionnaire_id")));
        response.setQuestionnaireName(cursor.getString(cursor.getColumnIndex("questionnaire_name")));
        response.setQuestionnaireType(cursor.getInt(cursor.getColumnIndex("questionnaire_type")));
        response.setQuestionnaireUrl(cursor.getString(cursor.getColumnIndex("questionnaire_url")));
        response.setSubJson(cursor.getString(cursor.getColumnIndex("sub_json")));
        response.setSubQuestionnaireJson(cursor.getString(cursor.getColumnIndex("sub_questionnaire_json")));
        response.setSamplingStatus(cursor.getInt(cursor.getColumnIndex("sampling_status")));
        response.setSubmitData(cursor.getString(cursor.getColumnIndex("submit_data")));
        response.setSubmitState(cursor.getString(cursor.getColumnIndex("submit_state")));
        response.setQuestionData(cursor.getString(cursor.getColumnIndex("question_data")));
        response.setResponseData(cursor.getString(cursor.getColumnIndex("response_data")));
        response.setResponseDuration(cursor.getLong(cursor.getColumnIndex("response_duration")));
        response.setResponseType(cursor.getInt(cursor.getColumnIndex("response_type")));
        response.setResponseStatus(cursor.getInt(cursor.getColumnIndex("response_status")));
        response.setResponseIdentifier(cursor.getString(cursor.getColumnIndex("response_identifier")));
        response.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
        response.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
        response.setCaxiWebStatus(cursor.getInt(cursor.getColumnIndex("caxi_web_status")));
        response.setIsDownloadDetail(cursor.getInt(cursor.getColumnIndex("is_download_detail")));
        response.setLastModifyTime(cursor.getLong(cursor.getColumnIndex("last_modify_time")));
        response.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
        response.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
        return response;
    }

    private static ContentValues obj2contentValues(Response response) {
        ContentValues values = new ContentValues();
        values.put("id", response.getId());
        values.put("user_id", response.getUserId());
        values.put("interviewer_id", response.getInterviewerId());
        values.put("project_id", response.getProjectId());
        values.put("sample_guid", response.getSampleGuid());
        values.put("module_id", response.getModuleId());
        values.put("module_code", response.getModuleCode());
        values.put("module_name", response.getModuleName());
        values.put("version", response.getVersion());
        values.put("url", response.getUrl());
        values.put("questionnaire_id", response.getQuestionnaireId());
        values.put("questionnaire_name", response.getQuestionnaireName());
        values.put("questionnaire_type", response.getQuestionnaireType());
        values.put("questionnaire_url", response.getQuestionnaireUrl());
        values.put("sub_json", response.getSubJson());
        values.put("sub_questionnaire_json", response.getSubQuestionnaireJson());
        values.put("sampling_status", response.getSamplingStatus());

        values.put("submit_data", response.getSubmitData());
        values.put("submit_state", response.getSubmitState());
        values.put("question_data", response.getQuestionData());
        values.put("response_data", response.getResponseData());
        values.put("response_duration", response.getResponseDuration());
        values.put("response_type", response.getResponseType());
        values.put("response_status", response.getResponseStatus());
        values.put("response_identifier", response.getResponseIdentifier());
        values.put("lon", response.getLon());
        values.put("lat", response.getLat());
        values.put("address", response.getAddress());
        values.put("start_time", response.getStartTime());
        values.put("end_time", response.getEndTime());
        values.put("caxi_web_status", response.getCaxiWebStatus());
        values.put("last_modify_time", response.getLastModifyTime());
        values.put("create_time", response.getCreateTime());
        values.put("is_upload", response.getIsUpload());
        values.put("is_download_detail", response.getIsDownloadDetail());

        return values;
    }

    public static boolean deleteById(String id, Integer userId) {
        if (TextUtils.isEmpty(id)) {
            return false;
        }
        return DBOperation.instanse.deleteTableData(TableSQL.RESPONSE_NAME, "id = ? and user_id = ?", new String[]{id + "", userId + ""});
    }

    public static boolean deleteList(List<String> ids, Integer userId) {
        if (ids == null) {
            return false;
        }
        if (ids.size() == 0) {
            return true;
        }
        Boolean isSuccess = true;
        DBOperation.db.beginTransaction();
        for (String id : ids) {
            isSuccess = DBOperation.instanse.deleteTableData(TableSQL.RESPONSE_NAME, "response_guid = ? and user_id = ?", new String[]{id + "", userId + ""});
            // if (!isSuccess) {
            //有任何一个删除失败就回滚不提交
//                DBOperation.db.endTransaction();
            // return false;
            // }
        }
        DBOperation.db.setTransactionSuccessful();
        DBOperation.db.endTransaction();
        return true;
    }

    //获取未提交的答卷数据
    public static List<Response> queryNoUploadList(Integer userId) {
        String sql = "select * from " + TableSQL.RESPONSE_NAME + " where is_upload = 1 and user_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        List<Response> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(cursor2Obj(cursor));
        }
        cursor.close();
        return list;
    }

    public static HashMap<String, String> queryNoUploadResponse(Integer userId, Integer projectId) {
        String sql = "select * from " + TableSQL.RESPONSE_NAME + " where is_upload = 1 and user_id = ? and project_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", projectId + ""});
        ArrayList<HashMap<String, String>> hmList = new ArrayList<>();
        while (cursor.moveToNext()) {
            HashMap<String, String> hm = new HashMap<>();
            String submitData = cursor.getString(cursor.getColumnIndex("submit_data"));
            JSONObject data = JSON.parseObject(submitData);
            if (data != null) {
                JSONArray answeredQuestions = data.getJSONArray("history");
                String answeredCurrent = answeredQuestions.size() > 0 ? answeredQuestions.getJSONObject(answeredQuestions.size() - 1).getString("qid") : "";
                hm.put("answeredCurrent", answeredCurrent);
                hm.put("answeredQuestions", answeredQuestions.toJSONString());
            } else {
                hm.put("answeredCurrent", "");
                hm.put("answeredQuestions", "");
            }
            hm.put("interviewerId", userId + "");
            hm.put("respLon", cursor.getString(cursor.getColumnIndex("lon")));
            hm.put("respLat", cursor.getString(cursor.getColumnIndex("lat")));
            hm.put("address", cursor.getString(cursor.getColumnIndex("address")));
            hm.put("moduleId", cursor.getInt(cursor.getColumnIndex("module_id")) + "");
            hm.put("projectId", cursor.getInt(cursor.getColumnIndex("project_id")) + "");
            hm.put("questionData", cursor.getString(cursor.getColumnIndex("question_data")));
            hm.put("questionnaireId", cursor.getInt(cursor.getColumnIndex("questionnaire_id")) + "");
            hm.put("responseData", cursor.getString(cursor.getColumnIndex("response_data")));
            hm.put("responseGuid", cursor.getString(cursor.getColumnIndex("id")));
            hm.put("responseType", cursor.getInt(cursor.getColumnIndex("response_type")) + "");
            hm.put("sampleGuid", cursor.getString(cursor.getColumnIndex("sample_guid")));
            hm.put("startTime", cursor.getString(cursor.getColumnIndex("start_time")));
            hm.put("endTime", cursor.getString(cursor.getColumnIndex("end_time")));
            hm.put("submitData", submitData);
            hm.put("submitState", cursor.getString(cursor.getColumnIndex("submit_state")));
            hm.put("version", cursor.getInt(cursor.getColumnIndex("version")) + "");
            hmList.add(hm);
        }
        if (hmList.size() > 0) {
            return hmList.get(0);
        } else {
            return null;
        }
    }

    public static Response queryById(Integer userId, String id) {
        String sql = "select * from " + TableSQL.RESPONSE_NAME + " where id = ? and user_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{id, userId + "",});
        Response response = null;
        if (cursor.moveToFirst()) {
            response = cursor2Obj(cursor);
        }
        cursor.close();
        return response;
    }

    public static boolean deleteById(Integer userId, String id) {
        return DBOperation.instanse.deleteTableData(TableSQL.RESPONSE_NAME, "user_id = ? and id = ?", new String[]{userId + "", id + ""});
    }

    //获取某个模块的子答卷
    public static List<MultiItemEntity> querySubByModuleIdAndSampleId(Integer userId, Integer surveyId, Integer moduleId, String sampleId) {
        String sql = "select re.sample_guid,re.id,re.interviewer_id,re.sub_json,re.caxi_web_status,re.is_download_detail,re.response_status,re.response_duration" +
                ",re.start_time,re.end_time,re.is_upload,re.version,re.response_identifier,re.questionnaire_id,re.module_code,re.questionnaire_name" +
                ",qu.url,qu.qrc_url,qu.module_id,qu.code,qu.module_dependency from " + TableSQL.RESPONSE_NAME + " re left join " + TableSQL.QUESTIONNAIRE_NAME +
                " qu on re.questionnaire_id = qu.id and qu.module_id = " + moduleId + " and qu.user_id = re.user_id where re.module_id = ? and re.user_id = ? and re.sample_guid = ? and re.project_id = ? ";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{moduleId + "", userId + "", sampleId + "", surveyId + ""});
        List<MultiItemEntity> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Response response = new Response();
            response.setItemType(QuestionnaireItemType.QUESTIONNAIRE_MAIN);
            response.setQuestionnaireUrl(cursor.getString(cursor.getColumnIndex("url")));
//            response.setQuestionnaireQRCodeUrl(cursor.getString(cursor.getColumnIndex("qrc_url")));
            response.setId(cursor.getString(cursor.getColumnIndex("id")));
            response.setVersion(cursor.getInt(cursor.getColumnIndex("version")));
            response.setResponseIdentifier(cursor.getString(cursor.getColumnIndex("response_identifier")));
            response.setInterviewerId(cursor.getInt(cursor.getColumnIndex("interviewer_id")));
//            response.setDependencyCode(cursor.getString(cursor.getColumnIndex("dependency")));
//            response.setType(cursor.getInt(cursor.getColumnIndex("type")));
            response.setModuleCode(cursor.getString(cursor.getColumnIndex("module_code")));
            response.setModuleId(cursor.getInt(cursor.getColumnIndex("module_id")));
            response.setQuestionnaireId(cursor.getInt(cursor.getColumnIndex("questionnaire_id")));
            response.setQuestionnaireName(cursor.getString(cursor.getColumnIndex("questionnaire_name")));
            response.setDependencyId(cursor.getInt(cursor.getColumnIndex("module_dependency")));
            response.setResponseDuration(cursor.getLong(cursor.getColumnIndex("response_duration")));
            response.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
            response.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
            response.setResponseStatus(cursor.getInt(cursor.getColumnIndex("response_status")));
            response.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
            response.setCaxiWebStatus(cursor.getInt(cursor.getColumnIndex("caxi_web_status")));
            response.setSubJson(cursor.getString(cursor.getColumnIndex("sub_json")));
//            response.setQrc(cursor.getString(cursor.getColumnIndex("qrc")));
            response.setIsDownloadDetail(cursor.getInt(cursor.getColumnIndex("is_download_detail")));

            list.add(response);
        }
        cursor.close();
        return list;
    }

    private static List<MultiItemEntity> querySubByQuestionnaireIdAndSampleId(Integer userId, Integer surveyId, Integer questionnaireId, String sampleId) {
        String sql = "select re.sample_guid,re.id,re.caxi_web_status,re.interviewer_id,re.sub_json,re.submit_data,re.submit_state,re.response_status,re.response_duration" +
                " ,re.start_time,re.end_time,re.is_upload,re.version,re.response_identifier,re.questionnaire_id,re.is_download_detail,re.module_code,re.questionnaire_name" +
                " ,qu.url,qu.qrc_url,qu.module_id,qu.module_dependency from " + TableSQL.RESPONSE_NAME + " re left join " + TableSQL.QUESTIONNAIRE_NAME +
                " qu on re.questionnaire_id = qu.id and re.user_id = qu.user_id where re.questionnaire_id = ? and re.user_id = ? and re.sample_guid = ? and re.project_id = ? order by re.create_time desc";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{questionnaireId + "", userId + "", sampleId + "", surveyId + ""});
        List<MultiItemEntity> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Response response = new Response();
            response.setItemType(QuestionnaireItemType.QUESTIONNAIRE_MAIN);
            response.setQuestionnaireUrl(cursor.getString(cursor.getColumnIndex("url")));
//            response.setQuestionnaireQRCodeUrl(cursor.getString(cursor.getColumnIndex("qrc_url")));
            response.setId(cursor.getString(cursor.getColumnIndex("id")));
            response.setResponseIdentifier(cursor.getString(cursor.getColumnIndex("response_identifier")));
            response.setInterviewerId(cursor.getInt(cursor.getColumnIndex("interviewer_id")));
            response.setDependencyId(cursor.getInt(cursor.getColumnIndex("module_dependency")));
//            response.setType(cursor.getInt(cursor.getColumnIndex("type")));
            response.setModuleId(cursor.getInt(cursor.getColumnIndex("module_id")));
            response.setModuleCode(cursor.getString(cursor.getColumnIndex("module_code")));
//            response.setName(cursor.getString(cursor.getColumnIndex("name")));
            response.setQuestionnaireId(cursor.getInt(cursor.getColumnIndex("questionnaire_id")));
            response.setQuestionnaireName(cursor.getString(cursor.getColumnIndex("questionnaire_name")));
            response.setResponseDuration(cursor.getLong(cursor.getColumnIndex("response_duration")));
            response.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
            response.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
            response.setResponseStatus(cursor.getInt(cursor.getColumnIndex("response_status")));
            response.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
            response.setVersion(cursor.getInt(cursor.getColumnIndex("version")));

            response.setCaxiWebStatus(cursor.getInt(cursor.getColumnIndex("caxi_web_status")));
            response.setSubJson(cursor.getString(cursor.getColumnIndex("sub_json")));
            response.setSubmitData(cursor.getString(cursor.getColumnIndex("submit_data")));
            response.setSubmitState(cursor.getString(cursor.getColumnIndex("submit_state")));
//            response.setQrc(cursor.getString(cursor.getColumnIndex("qrc")));
            response.setIsDownloadDetail(cursor.getInt(cursor.getColumnIndex("is_download_detail")));
            list.add(response);
        }
        cursor.close();
        return list;
    }

    public static boolean replace(Response response) {
        ContentValues values = obj2contentValues(response);
        return DBOperation.instanse.replaceTableData(TableSQL.RESPONSE_NAME, values);
    }

    public static Response queryLatestResponse(Integer userId, Integer surveyId, Integer moduleId) {

        String sql = "select qu.id as questionnaire_id,qu.type,qu.qrc_url,qu.url,qu.name as questionnaire_name,qu.module_id,qu.code,qu.module_dependency " +
                "from " + TableSQL.QUESTIONNAIRE_NAME + " qu " +
                "where qu.survey_id = ? and qu.user_id = ? and qu.module_id = ? ORDER BY qu.version DESC LIMIT 1";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{surveyId + "", userId + "", moduleId + ""});
        Response response = new Response();
        if (cursor.moveToFirst()) {
            response.setItemType(QuestionnaireItemType.QUESTIONNAIRE_MAIN);
            response.setUserId(userId);
            response.setProjectId(surveyId);
            response.setModuleId(cursor.getInt(cursor.getColumnIndex("module_id")));
            response.setModuleCode(cursor.getString(cursor.getColumnIndex("code")));
            response.setQuestionnaireUrl(cursor.getString(cursor.getColumnIndex("url")));
            response.setQuestionnaireName(cursor.getString(cursor.getColumnIndex("questionnaire_name")));
//            response.setQuestionnaireQRCodeUrl(cursor.getString(cursor.getColumnIndex("qrc_url")));
            response.setQuestionnaireId(cursor.getInt(cursor.getColumnIndex("questionnaire_id")));
            response.setDependencyId(cursor.getInt(cursor.getColumnIndex("module_dependency")));
            return response;
        }
        cursor.close();
        return response;
    }

    public static boolean replaceList(List<MultiItemEntity> list) {
        if (list == null) {
            return false;
        }
        boolean b = true;
        for (int i = 0; i < list.size(); i++) {
            if (!replace((Response) list.get(i))) {
                b = false;
            }
        }
        return b;
    }

    //获取主问卷的列表
    public static List<MultiItemEntity> getMainQuestionList(Integer userId, Integer surveyId, String sampleId, Integer groupId) {
        String sql = "";
        Cursor cursor;
        if (groupId > 0) {
            sql = "select qu.id as questionnaire_id,qu.name as questionnaire_name,qu.code,qu.module_id,qu.module_dependency,qu.url,qu.qrc_url," +
                    "re.id,re.interviewer_id,re.sub_json,re.caxi_web_status,re.is_download_detail,re.response_status,re.response_duration," +
                    "re.start_time,re.end_time,re.version,re.is_upload,re.response_identifier,re.response_type" +
                    " from " + TableSQL.QUESTIONNAIRE_NAME + " qu left join " + TableSQL.RESPONSE_NAME + " re " +
                    "on re.questionnaire_id = qu.id and re.sample_guid = ? and re.user_id = qu.user_id " +
                    " where qu.survey_id = ? and qu.user_id = ? and qu.group_id = ? and qu.type = 1 group by qu.module_id HAVING CASE when re.id is null then max(qu.version) ELSE max(re.response_duration) > 0 END order by qu.create_time desc";
            cursor = DBOperation.db.rawQuery(sql, new String[]{sampleId + "", surveyId + "", userId + "", groupId + ""});
        } else {
            sql = "select qu.id as questionnaire_id,qu.name as questionnaire_name,qu.code,qu.module_id,qu.module_dependency,qu.sample_dependency,qu.url,qu.qrc_url," +
                    "re.id,re.interviewer_id,re.sub_json,re.caxi_web_status,re.is_download_detail,re.response_status,re.response_duration," +
                    "re.start_time,re.end_time,re.version,re.is_upload,re.response_identifier,re.response_type" +
                    " from " + TableSQL.QUESTIONNAIRE_NAME + " qu left join " + TableSQL.RESPONSE_NAME + " re " +
                    "on re.questionnaire_id = qu.id and re.sample_guid = ? and re.user_id = qu.user_id " +
                    " where qu.survey_id = ? and qu.user_id = ? and qu.type = 1 group by qu.module_id HAVING CASE when re.id is null then max(qu.version) ELSE max(re.response_duration) > 0 END order by qu.create_time desc";
            cursor = DBOperation.db.rawQuery(sql, new String[]{sampleId + "", surveyId + "", userId + ""});
        }
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            Response response = new Response();
            response.setItemType(QuestionnaireItemType.QUESTIONNAIRE_MAIN);
            response.setId(cursor.getString(cursor.getColumnIndex("id")));
            response.setResponseIdentifier(cursor.getString(cursor.getColumnIndex("response_identifier")));
            response.setInterviewerId(cursor.getInt(cursor.getColumnIndex("interviewer_id")));
            response.setDependencyId(cursor.getInt(cursor.getColumnIndex("module_dependency")));
//            response.setType(cursor.getInt(cursor.getColumnIndex("type")));
            response.setModuleId(cursor.getInt(cursor.getColumnIndex("module_id")));
            response.setModuleCode(cursor.getString(cursor.getColumnIndex("code")));
            response.setQuestionnaireUrl(cursor.getString(cursor.getColumnIndex("url")));
            response.setVersion(cursor.getInt(cursor.getColumnIndex("version")));
            response.setQuestionnaireName(cursor.getString(cursor.getColumnIndex("questionnaire_name")));
//            response.setQuestionnaireQRCodeUrl(cursor.getString(cursor.getColumnIndex("qrc_url")));
            response.setQuestionnaireId(cursor.getInt(cursor.getColumnIndex("questionnaire_id")));
            response.setResponseDuration(cursor.getLong(cursor.getColumnIndex("response_duration")));
            response.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
            response.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
            response.setResponseStatus(cursor.getInt(cursor.getColumnIndex("response_status")));
            response.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
            response.setCaxiWebStatus(cursor.getInt(cursor.getColumnIndex("caxi_web_status")));
            response.setSubJson(cursor.getString(cursor.getColumnIndex("sub_json")));
//            response.setQrc(cursor.getString(cursor.getColumnIndex("qrc")));
            response.setIsDownloadDetail(cursor.getInt(cursor.getColumnIndex("is_download_detail")));

            //依赖样本属性为空；依赖样本属性值和当前样本属性值相同 ——才显示
            String sampleDependency = cursor.getString(cursor.getColumnIndex("sample_dependency"));
            if (sampleDependency != null && sampleDependency != "null" && !sampleDependency.trim().isEmpty()) {
                boolean ifNeed  = querySampleWithPropertyValue(sampleId, sampleDependency);
                if (ifNeed) {
                    list.add(response);
                }
            } else {
                list.add(response);
            }
        }
        return list;
    }

    /**
     * 判断样本是否满足问卷的样本依赖
     * @param sampleId
     * @param sampleDependency
     * @return
     */
    private static Boolean querySampleWithPropertyValue(String sampleId, String sampleDependency) {
        String sql = "select * from " + TableSQL.SAMPLE_NAME + " where sample_guid = ? and (" +sampleDependency+ ")";
        Cursor cursor = DBOperation.db.rawQuery(sql , new String[]{sampleId});
        boolean ifExist = false;
        if(cursor.getCount() > 0){
            ifExist =  true;
        }
        cursor.close();
        return ifExist;

    }

    /**
     * 生成每个子问卷子答卷,答卷是样本级别的
     *
     * @return
     */
    public static boolean insertSubResponse(JSONArray subQuestionnaireJsonArray, String sampleId, Integer userId, Integer surveyId) {
        //问卷列表
        for (int i = 0; i < subQuestionnaireJsonArray.size(); i++) {
            JSONObject subQuestionnaireJsonObject = (JSONObject) subQuestionnaireJsonArray.get(i);
            String moduleCode = subQuestionnaireJsonObject.getString("moduleCode");

            //根据问卷code获取子问卷id
            String sql = "select id,url,module_id from " + TableSQL.QUESTIONNAIRE_NAME +
                    " where user_id = ? and survey_id = ? order by version DESC";
            ArrayList<HashMap<String, String>> list = DBOperation.instanse.selectRow(sql, new String[]{userId + "", surveyId + ""});
            if (list.size() == 0) {
                continue;
            }
            //子问卷ID
            Integer questionnaireId = Integer.parseInt(list.get(0).get("id"));
            //子问卷的问卷文件本地路径
            String questionFilename = list.get(0).get("url");
            //查询是否已经生成子答卷
            List<MultiItemEntity> responseList =
                    querySubByQuestionnaireIdAndSampleId(userId, surveyId, questionnaireId, sampleId);
            //如果已经生成的就不再生成了
            if (responseList.size() > 0) {
                continue;
            }

            //问卷下面的答卷列表
            JSONArray responseJsonArray = subQuestionnaireJsonObject.getJSONArray("list");
            int k = 1;
            Integer moduleId = Integer.parseInt(list.get(0).get("module_id"));
            for (int j = 0; j < responseJsonArray.size(); j++) {
                JSONObject responseSubJsonObject = (JSONObject) responseJsonArray.get(j);
                //生成每个子问卷对应的答卷
                String name = responseSubJsonObject.getString("name");
                if (TextUtils.isEmpty(name)) {
                    name = "随机问卷" + k;
                    k++;
                }
                Response response = new Response();
                response.setQuestionnaireName(name);
                response.setModuleCode(moduleCode);
                response.setModuleId(moduleId);
                response.setId(GUIDUtil.getGuidStr());
                response.setQuestionnaireId(questionnaireId);
                response.setUserId(userId);
                response.setInterviewerId(userId);
                response.setResponseStatus(0);
                response.setIsUpload(1);
                response.setQuestionnaireUrl(questionFilename);
                response.setProjectId(surveyId);
                response.setSampleGuid(sampleId);
                response.setSubJson(responseSubJsonObject.toString());
                ContentValues values = obj2contentValues(response);
                DBOperation.instanse.insertTableData(TableSQL.RESPONSE_NAME, values);
            }
        }
        return true;
    }

    //新增随机子问卷
    public static boolean insertRandomSubResponse2(Integer questionnaireId, String questionnaireName, Integer userId, String sampleId, Integer surveyId, Integer moduleId, String moduleCode, Integer num) {
        Response newResponse = new Response();
        int index = num + 1;
        newResponse.setQuestionnaireName(questionnaireName + index);
        newResponse.setIsUpload(1);
        newResponse.setResponseType(2);
        newResponse.setResponseStatus(0);
        newResponse.setId(GUIDUtil.getGuidStr());
        newResponse.setInterviewerId(userId);
        newResponse.setModuleId(moduleId);
        newResponse.setSampleGuid(sampleId);
        newResponse.setUserId(userId);
        newResponse.setProjectId(surveyId);
        newResponse.setModuleCode(moduleCode);
        newResponse.setQuestionnaireId(questionnaireId);
        newResponse.setCreateTime(new Date().getTime());
        Boolean b = DBOperation.instanse.insertTableData(TableSQL.RESPONSE_NAME, obj2contentValues(newResponse));
        return b;
    }

    //统计用户未上传的答卷的数量
    public static Integer countNoUpload(Integer userId) {
        String sql = "select count(*) as count from " + TableSQL.RESPONSE_NAME + " where is_upload = 1 and user_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public static int countAllSubmitResponse(Integer userId, Integer surveyId, String sampleId) {
        String sql = "select count(*) as count from " + TableSQL.RESPONSE_NAME +
                " where sample_guid = ? and user_id = ? and project_id = ? and (response_status = 6 or response_status = 8 or response_status = 9 or response_status = 10 or response_status = 11)";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{sampleId + "", userId + "", surveyId + ""});
        Integer count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("count"));
        }
        cursor.close();
        return count;
    }

    public static int countResponse(Integer userId, Integer surveyId, String sampleId) {
        String sql = "select count(*) as count from " + TableSQL.RESPONSE_NAME + " re " +
                "where re.sample_guid = ? and re.user_id = ? and re.project_id = ? group by re.module_id ";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{sampleId + "", userId + "", surveyId + ""});
        Integer count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("count"));
        }
        cursor.close();
        return count;
    }

    public static int countSubResponse(Integer userId, String sampleId, Integer projectId) {
        String sql = "select count(*) as count " +
                "from " + TableSQL.RESPONSE_NAME + " re left join " + TableSQL.QUESTIONNAIRE_NAME + " qu " +
                "on re.questionnaire_id = qu.id and re.user_id = qu.user_id " +
                "where qu.type = 0 and re.sample_guid = ? and re.user_id = ? and re.project_id = ?";

//        let sql =  "select count(*) as count " +
//                "from " + TableSQL.RESPONSE_NAME + " re left join "  + TableSQL.QUESTIONNAIRE_NAME + " qu " +
//                "on re.questionnaire_id = qu.id and re.user_id = qu.user_id " +
//                "where qu.type = 0 and re.sample_guid = '\(sampleId)' and re.survey_id = \(surveyId) and re.user_id = \(userId) group by re.module_id";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{sampleId + "", userId + "", projectId + ""});
        Integer count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("count"));
        }
        cursor.close();
        return count;
    }

    public static int countSubmitSubResponse(Integer userId, String sampleId, Integer projectId, Integer questionnaireId) {
        String sql = "select count(*) as count " +
                "from " + TableSQL.RESPONSE_NAME + " re left join " + TableSQL.QUESTIONNAIRE_NAME + " qu " +
                "on re.questionnaire_id = qu.id and re.user_id = qu.user_id " +
                "where qu.type = 0 and qu.id = ? and re.sample_guid = ? and re.user_id = ? and re.project_id = ? " +
                "and (re.response_status = 6 or re.response_status = 8 or re.response_status = 9 or re.response_status = 10 or re.response_status = 11)";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{questionnaireId + "", sampleId + "", userId + "", projectId + ""});
        Integer count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("count"));
        }
        cursor.close();
        return count;
    }

    //获取需要下载详情的答卷ID
    public static HashMap<String, Object> getNeedDownloadDetailId(Integer userId) {
        String sql = "select id,project_id from " + TableSQL.RESPONSE_NAME + " where (is_download_detail = 1 or is_download_detail is null) and user_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        HashMap<String, Object> hm = null;
        if (cursor.moveToFirst()) {
            hm = new HashMap<>();
            String id = cursor.getString(cursor.getColumnIndex("id"));
            Integer surveyId = cursor.getInt(cursor.getColumnIndex("project_id"));
            hm.put("id", id);
            hm.put("surveyId", surveyId);
        }
        cursor.close();
        return hm;
    }

    //更新答卷详情
    public static boolean updateResponseDetail(Integer userId, String responseId, String submitData, String submitState) {
        ContentValues values = new ContentValues();
        values.put("submit_data", submitData);
        values.put("submit_state", submitState);
        values.put("is_download_detail", 2);
        return DBOperation.instanse.updateTableData(TableSQL.RESPONSE_NAME, " user_id = ? and id = ?", new String[]{userId + "", responseId + ""}, values);
    }

    public static boolean updateResponseStatus(Integer userId, String responseId) {
        ContentValues values = new ContentValues();
        values.put("is_download_detail", 2);
        return DBOperation.instanse.updateTableData(TableSQL.RESPONSE_NAME, " user_id = ? and id = ?", new String[]{userId + "", responseId + ""}, values);
    }

    //更新提交状态
    public static boolean updateUploadStatus(int userId, String responseId) {
        ContentValues values = new ContentValues();
        values.put("is_upload", 2);
        return DBOperation.instanse.updateTableData(TableSQL.RESPONSE_NAME, " user_id = ? and id = ? ", new String[]{userId + "", responseId}, values);
    }

    //更新提交状态
    public static boolean updateQRC(String qrc, Integer userId, String id) {
        ContentValues values = new ContentValues();
        values.put("qrc", qrc);
        return DBOperation.instanse.updateTableData(TableSQL.RESPONSE_NAME, " user_id = " + userId + " and id = ?", new String[]{id}, values);
    }

    public static LinkedList<MultiItemEntity> getListFromNet(JSONArray dataArray, Integer userId) {
        final LinkedList<MultiItemEntity> list = new LinkedList<>();
        if (dataArray == null) {
            return list;
        }
        try {
            final int size = dataArray.size();
            for (int i = 0; i < size; i++) {
                Response response = new Response();
                final JSONObject data = dataArray.getJSONObject(i);
                response.setUserId(userId);
                response.setIsUpload(2);
//                response.setName(data.getString("name"));
                response.setInterviewerId(data.getInteger("interviewerId"));
                response.setInterviewerId(userId);
                response.setVersion(data.getInteger("version"));
                response.setResponseIdentifier(data.getString("responseIdentifier"));
                response.setSubmitState(data.getString("submitState"));
                response.setProjectId(data.getInteger("projectId"));
//                response.setAddress(data.getString("address"));
                response.setQuestionnaireId(data.getInteger("questionnaireId"));
                response.setSampleGuid(data.getString("sampleGuid"));

                response.setResponseDuration(data.getLong("responseDuration"));
                response.setLastModifyTime(data.getLong("lastModifyTime"));

                response.setResponseStatus(data.getInteger("responseStatus"));

                response.setEndTime(data.getLong("endTime"));
                response.setId(data.getString("responseGuid"));
                response.setModuleCode(data.getString("moduleCode"));
                response.setSubJson(data.getString("subQuestionnaireJson"));
                response.setCreateTime(data.getLong("createTime"));
                response.setStartTime(data.getLong("startTime"));
                response.setModuleId(data.getInteger("moduleId"));
//                response.setCaxiWebStatus(data.getInteger("caxi_web_status"));
                response.setQuestionData(data.getString("questionData"));
                response.setResponseData(data.getString("responseData"));
                list.add(response);
            }
        } catch (Exception e) {
            Log.i("Exception", e.toString());
            return null;
        }
        return list;
    }
}
