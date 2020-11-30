package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;
import com.ringdata.ringinterview.capi.components.data.model.Questionnaire;
import com.ringdata.ringinterview.capi.components.ui.survey.questionnaire.QuestionnaireItemType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class QuestionnaireDao {

    private static Questionnaire cV2Obj(Cursor cursor) {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setItemType(QuestionnaireItemType.QUESTIONNAIRE_MAIN);
        questionnaire.setId(cursor.getInt(cursor.getColumnIndex("id")));
        questionnaire.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
        questionnaire.setSurveyId(cursor.getInt(cursor.getColumnIndex("survey_id")));
        questionnaire.setModuleId(cursor.getInt(cursor.getColumnIndex("module_id")));
        questionnaire.setType(cursor.getInt(cursor.getColumnIndex("type")));
        questionnaire.setName(cursor.getString(cursor.getColumnIndex("name")));
        questionnaire.setCode(cursor.getString(cursor.getColumnIndex("code")));
        questionnaire.setVersion(cursor.getInt(cursor.getColumnIndex("version")));
        questionnaire.setModuleDependency(cursor.getInt(cursor.getColumnIndex("module_dependency")));
        questionnaire.setSampleDependency(cursor.getString(cursor.getColumnIndex("sample_dependency")));
        questionnaire.setIsAllowedManualAdd(cursor.getInt(cursor.getColumnIndex("is_allowed_manual_add")));
        questionnaire.setQuotaMin(cursor.getInt(cursor.getColumnIndex("quota_min")));
        questionnaire.setQuotaMax(cursor.getInt(cursor.getColumnIndex("quota_max")));
        questionnaire.setGroupId(cursor.getInt(cursor.getColumnIndex("group_id")));
        questionnaire.setGroupName(cursor.getString(cursor.getColumnIndex("group_name")));
        questionnaire.setUrl(cursor.getString(cursor.getColumnIndex("url")));
        questionnaire.setQrcUrl(cursor.getString(cursor.getColumnIndex("qrc_url")));
        questionnaire.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
        questionnaire.setIsFileDownloadSuccess(cursor.getInt(cursor.getColumnIndex("is_download")));
        return questionnaire;
    }

    private static ContentValues obj2CV(Questionnaire questionnaire) {
        ContentValues values = new ContentValues();
        values.put("id", questionnaire.getId());
        values.put("user_id", questionnaire.getUserId());
        values.put("survey_id", questionnaire.getSurveyId());
        values.put("module_id", questionnaire.getModuleId());
        values.put("type", questionnaire.getType());
        values.put("name", questionnaire.getName());
        values.put("code", questionnaire.getCode());
        values.put("version", questionnaire.getVersion());
        values.put("module_dependency", questionnaire.getModuleDependency());
        if (questionnaire.getSampleDependency() != null && !"".equals(questionnaire.getSampleDependency())) {
            values.put("sample_dependency", questionnaire.getSampleDependency());
        } else {
            values.putNull("sample_dependency");
        }
        values.put("is_allowed_manual_add", questionnaire.getIsAllowedManualAdd());
        values.put("quota_min", questionnaire.getQuotaMin());
        values.put("quota_max", questionnaire.getQuotaMax());
        values.put("group_id", questionnaire.getGroupId());
        values.put("group_name", questionnaire.getGroupName());
        values.put("url", questionnaire.getUrl());
        values.put("qrc_url", questionnaire.getQrcUrl());
        values.put("create_time", questionnaire.getCreateTime());
        values.put("is_file_download_success", questionnaire.getIsFileDownloadSuccess());
        return values;
    }

    public static Questionnaire queryLatestQuestionnaire(Integer userId, Integer surveyId, Integer moduleId) {

        String sql = "select qu.* " + "from " + TableSQL.QUESTIONNAIRE_NAME + " qu " +
                "where qu.survey_id = ? and qu.user_id = ? and qu.module_id = ? ORDER BY qu.version DESC LIMIT 1";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{surveyId + "", userId + "", moduleId + ""});
        Questionnaire questionnaire = new Questionnaire();
        if (cursor.moveToFirst()) {
            questionnaire = cV2Obj(cursor);
            return questionnaire;
        }
        cursor.close();
        return questionnaire;
    }

    public static boolean deleteList(List<Integer> ids, Integer userId) {
        if (ids == null) {
            return false;
        }
        if (ids.size() == 0) {
            return true;
        }
        Boolean isSuccess = true;
        DBOperation.db.beginTransaction();
        for (Integer id : ids) {
            isSuccess = DBOperation.instanse.deleteTableData(TableSQL.QUESTIONNAIRE_NAME, "id = ? and user_id = ?", new String[]{id + "", userId + ""});
//            if (!isSuccess) {
//                //有任何一个删除失败就回滚不提交
//                DBOperation.db.endTransaction();
//                return false;
//            }
        }
        DBOperation.db.setTransactionSuccessful();
        DBOperation.db.endTransaction();
        return true;
    }

    public static boolean insertOrUpdate(Questionnaire questionnaire) {
        ContentValues values = obj2CV(questionnaire);
        boolean isSuccess = DBOperation.instanse.updateTableData(TableSQL.QUESTIONNAIRE_NAME, "id = ? and user_id = ? and survey_id = ? ", new String[]{questionnaire.getId() + "", questionnaire.getUserId() + "", questionnaire.getSurveyId() + ""}, values);
        if (!isSuccess) {
            isSuccess = DBOperation.instanse.insertTableData(TableSQL.QUESTIONNAIRE_NAME, values);
        }
        return isSuccess;
    }

    public static boolean insertOrUpdateList(List<MultiItemEntity> list) {
        if (list == null) {
            return false;
        }
        boolean b = true;
        for (int i = 0; i < list.size(); i++) {
            if (!insertOrUpdate((Questionnaire) list.get(i))) {
                b = false;
            }
        }
        return b;
    }

    public static boolean replaceList(List<MultiItemEntity> list) {
        if (list == null) {
            return false;
        }
        boolean b = true;
        for (int i = 0; i < list.size(); i++) {
            if (!replace((Questionnaire) list.get(i))) {
                b = false;
            }
        }
        return b;
    }

    private static boolean replace(Questionnaire questionnaire) {
        ContentValues values = obj2CV(questionnaire);
        return DBOperation.instanse.replaceTableData(TableSQL.QUESTIONNAIRE_NAME, values);
    }

    //根据问卷id查询完成的依赖问卷的名称，如果返回null，表示依赖问卷没有完成
    public static String getFinishDependencyName(Integer surveyId, Integer userId, String sampleId, Integer dependency) {
        String sql = "select qu.name " +
                "from " + TableSQL.QUESTIONNAIRE_NAME + " qu left join " + TableSQL.RESPONSE_NAME + " re " +
                "on re.questionnaire_id = qu.id and re.sample_guid = ? and re.user_id = qu.user_id " +
                "where qu.survey_id = ? and qu.user_id = ? and qu.type = 1 and qu.module_id = ? and (re.response_status = 6 or re.response_status = 8 or re.response_status = 5 or re.response_status = 9 or re.response_status = 10)";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{sampleId + "", surveyId + "", userId + "", dependency + ""});
        String name = null;
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex("name"));
        }
        cursor.close();
        return name;
    }

    //获取子问卷的列表
    public static List<MultiItemEntity> getSubQuestionListWithResponseList(Integer surveyId, Integer userId, String sampleId, Integer groupId) {
        String sql = "";
        Cursor cursor;
        if (groupId > 0) {
            //查询子问卷
            sql = "select id,name,code,type,module_id,module_dependency,is_allowed_manual_add,quota_min,quota_max,url from " + TableSQL.QUESTIONNAIRE_NAME +
                    " where survey_id = ? and user_id = ? and group_id = ? and type = 0 group by module_id order by create_time";
            cursor = DBOperation.db.rawQuery(sql, new String[]{surveyId + "", userId + "", groupId + ""});
        } else {
            sql = "select id,name,code,type,module_id,module_dependency,is_allowed_manual_add,quota_min,quota_max,url from " + TableSQL.QUESTIONNAIRE_NAME +
                    " where survey_id = ? and user_id = ? and type = 0 group by module_id order by create_time";
            cursor = DBOperation.db.rawQuery(sql, new String[]{surveyId + "", userId + ""});
        }
        LinkedList<MultiItemEntity> questionnaireList = new LinkedList<>();
        while (cursor.moveToNext()) {
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setItemType(QuestionnaireItemType.QUESTIONNAIRE_SUB);
            questionnaire.setType(cursor.getInt(cursor.getColumnIndex("type")));
            questionnaire.setId(cursor.getInt(cursor.getColumnIndex("id")));
            questionnaire.setName(cursor.getString(cursor.getColumnIndex("name")));
            questionnaire.setCode(cursor.getString(cursor.getColumnIndex("code")));
            questionnaire.setModuleId(cursor.getInt(cursor.getColumnIndex("module_id")));
            questionnaire.setModuleDependency(cursor.getInt(cursor.getColumnIndex("module_dependency")));
            questionnaire.setIsAllowedManualAdd(cursor.getInt(cursor.getColumnIndex("is_allowed_manual_add")));
            questionnaire.setQuotaMin(cursor.getInt(cursor.getColumnIndex("quota_min")));
            questionnaire.setQuotaMax(cursor.getInt(cursor.getColumnIndex("quota_max")));
            questionnaire.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            questionnaireList.add(questionnaire);
            //获取对应的子答卷
            List<MultiItemEntity> responseList = ResponseDao
                    .querySubByModuleIdAndSampleId(userId, surveyId, questionnaire.getModuleId(), sampleId);
            questionnaireList.addAll(responseList);
        }
        cursor.close();
        return questionnaireList;
    }

    //查询某项目下子问卷的答卷是否达到数量要求
    public static boolean ifSubQuestionListResponseQuota(Integer surveyId, Integer userId, String sampleId) {
        String sql = "select id,name,code,module_id,quota_min,quota_max from " + TableSQL.QUESTIONNAIRE_NAME +
                " where survey_id = ? and user_id = ? and type = 0 group by module_id order by create_time";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{surveyId + "", userId + ""});
        boolean result = true;
        while (cursor.moveToNext()) {
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setItemType(QuestionnaireItemType.QUESTIONNAIRE_SUB);
            int questionnaireId = cursor.getInt(cursor.getColumnIndex("id"));
            questionnaire.setId(questionnaireId);
            questionnaire.setName(cursor.getString(cursor.getColumnIndex("name")));
            questionnaire.setCode(cursor.getString(cursor.getColumnIndex("code")));
            questionnaire.setModuleId(cursor.getInt(cursor.getColumnIndex("module_id")));
            int quotaMin = cursor.getInt(cursor.getColumnIndex("quota_min"));
            questionnaire.setQuotaMin(quotaMin);
            questionnaire.setQuotaMax(cursor.getInt(cursor.getColumnIndex("quota_max")));
            if (ResponseDao.countSubmitSubResponse(userId, sampleId, surveyId, questionnaireId) < quotaMin) {
                result = false;
            }
        }
        cursor.close();
        return result;
    }

    //统计某个项目下子问卷的数量
    public static Integer countSubQuesBySurveyId(Integer userId, Integer surveyId) {
        String sql = "select count(distinct module_id) as count from " + TableSQL.QUESTIONNAIRE_NAME + " where survey_id = ? and user_id = ? and type = 0";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{surveyId + "", userId + ""});
        Integer count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("count"));
        }
        cursor.close();
        return count;
    }

    //统计某个项目下主问卷的数量
    public static Integer countMainBySurveyId(Integer userId, Integer surveyId) {
        String sql = "select count(distinct module_id) as count from " + TableSQL.QUESTIONNAIRE_NAME + " where survey_id = ? and user_id = ? and type = 1";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{surveyId + "", userId + ""});
        Integer count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("count"));
        }
        cursor.close();
        return count;
    }

    public static List<Questionnaire> getGroupList(Integer userId, Integer surveyId) {
        String sql = "select distinct group_id,group_name from " + TableSQL.QUESTIONNAIRE_NAME + " where user_id = ? and survey_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", surveyId + ""});
        List<Questionnaire> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Questionnaire group = new Questionnaire();
            group.setGroupId(cursor.getInt(cursor.getColumnIndex("group_id")));
            group.setGroupName(cursor.getString(cursor.getColumnIndex("group_name")));
            list.add(group);
        }
        cursor.close();
        return list;
    }

    //获取未下载或者下载失败的url地址
    public static String[] getQuestionnaireUrl(Integer userId) {
        String sql = "select ques.url,sur.name from " + TableSQL.QUESTIONNAIRE_NAME + " ques left join "+ TableSQL.SURVEY_NAME + " sur on ques.survey_id = sur.id where ques.user_id = ? and ques.is_file_download_success = 1 and ques.url is not null";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "",});
        String[] result = new String[2];
        if (cursor.moveToFirst()) {
            result[0] = cursor.getString(cursor.getColumnIndex("url"));
            result[1] = cursor.getString(cursor.getColumnIndex("name"));
        }
        cursor.close();
        return result;
    }

    //下载成功更新数据状态
    public static boolean updateDownloadStatus(String url) {
        ContentValues values = new ContentValues();
        values.put("is_file_download_success", 2);
        return DBOperation.instanse.updateTableData(TableSQL.QUESTIONNAIRE_NAME, " url = ?", new String[]{url + ""}, values);
    }

    public static LinkedList<MultiItemEntity> getListFromNet(JSONArray dataArray, Integer userId) {
        final LinkedList<MultiItemEntity> list = new LinkedList<>();
        if (dataArray == null) {
            return list;
        }
        try {
            final int size = dataArray.size();
            for (int i = 0; i < size; i++) {
                JSONObject data = dataArray.getJSONObject(i);
                JSONArray moduleList = data.getJSONArray("moduleList");
                for (int j = 0; j < moduleList.size(); j++) {
                    Questionnaire questionnaire = new Questionnaire();
                    questionnaire.setUserId(userId);
                    questionnaire.setGroupId(data.getIntValue("groupId"));
                    questionnaire.setGroupName(data.getString("groupName"));
                    questionnaire.setIsFileDownloadSuccess(1);
                    questionnaire.setId(moduleList.getJSONObject(j).getIntValue("questionnaireId"));
                    questionnaire.setSurveyId(moduleList.getJSONObject(j).getIntValue("projectId"));
                    questionnaire.setModuleId(moduleList.getJSONObject(j).getIntValue("moduleId"));
                    questionnaire.setType(moduleList.getJSONObject(j).getIntValue("type"));
                    questionnaire.setItemType(moduleList.getJSONObject(j).getIntValue("type"));
                    questionnaire.setName(moduleList.getJSONObject(j).getString("name"));
                    questionnaire.setCode(moduleList.getJSONObject(j).getString("code"));
                    questionnaire.setVersion(moduleList.getJSONObject(j).getIntValue("questionnaireVersion"));
                    questionnaire.setModuleDependency(moduleList.getJSONObject(j).getIntValue("moduleDependency"));
                    if (moduleList.getJSONObject(j).getString("sampleDependency") != null && !"".equals(moduleList.getJSONObject(j).getString("sampleDependency"))) {
                        questionnaire.setSampleDependency(moduleList.getJSONObject(j).getString("sampleDependency"));
                    }
                    questionnaire.setIsAllowedManualAdd(moduleList.getJSONObject(j).getIntValue("isAllowedManualAdd"));
                    questionnaire.setQuotaMin(moduleList.getJSONObject(j).getIntValue("quotaMin"));
                    questionnaire.setQuotaMax(moduleList.getJSONObject(j).getIntValue("quotaMax"));
                    questionnaire.setUrl(moduleList.getJSONObject(j).getString("questionnaireUrl"));
//                    questionnaire.setQrcUrl(data.getString("questionnaireQrCodeUrl"));
                    questionnaire.setCreateTime(moduleList.getJSONObject(j).getLong("createTime"));
                    list.add(questionnaire);
                }
            }
        } catch (Exception e) {
            return list;
        }
        return list;
    }

}
