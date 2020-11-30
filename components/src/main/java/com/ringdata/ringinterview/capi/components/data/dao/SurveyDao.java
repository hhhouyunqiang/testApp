package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.ringinterview.capi.components.constant.ProjectConstants;
import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;
import com.ringdata.ringinterview.capi.components.data.model.Survey;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by admin on 17/11/11.
 */

public class SurveyDao {


    private static Survey cursor2Obj(Cursor cursor) {
        Survey survey = new Survey();
        survey.setId(cursor.getInt(cursor.getColumnIndex("id")));
        survey.setName(cursor.getString(cursor.getColumnIndex("name")));
        survey.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        survey.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        survey.setType(cursor.getString(cursor.getColumnIndex("type")));
        survey.setLabelText(cursor.getString(cursor.getColumnIndex("label_text")));
        survey.setConfig(cursor.getString(cursor.getColumnIndex("config")));
        survey.setRoleName(cursor.getString(cursor.getColumnIndex("role_name")));
        survey.setCreateUserId(cursor.getInt(cursor.getColumnIndex("create_user_id")));
        survey.setCreateUserName(cursor.getString(cursor.getColumnIndex("create_user_name")));

        survey.setCreateTime(cursor.getString(cursor.getColumnIndex("create_time")));
        survey.setCreateTimeStr(cursor.getString(cursor.getColumnIndex("create_time_str")));
        survey.setUpdateTime(cursor.getString(cursor.getColumnIndex("update_time")));
        survey.setBeginTime(cursor.getString(cursor.getColumnIndex("begin_time")));
        survey.setEndTime(cursor.getString(cursor.getColumnIndex("end_time")));
        survey.setPauseTime(cursor.getString(cursor.getColumnIndex("pause_time")));

        survey.setUseProperty(cursor.getString(cursor.getColumnIndex("use_property")));
        survey.setMarkProperty(cursor.getString(cursor.getColumnIndex("mark_property")));

        survey.setNumOfNotStarted(cursor.getInt(cursor.getColumnIndex("num_of_not_started")));
        survey.setNumOfInProgress(cursor.getInt(cursor.getColumnIndex("num_of_in_progress")));
        survey.setNumOfFinish(cursor.getInt(cursor.getColumnIndex("num_of_finish")));
        survey.setNumOfRefuse(cursor.getInt(cursor.getColumnIndex("num_of_refuse")));
        survey.setNumOfIdentify(cursor.getInt(cursor.getColumnIndex("num_of_identify")));
        survey.setNumOfAppointment(cursor.getInt(cursor.getColumnIndex("num_of_appointment")));
        survey.setNumOfUnableToContact(cursor.getInt(cursor.getColumnIndex("num_of_unable_to_contact")));
        survey.setNumOfInTheCall(cursor.getInt(cursor.getColumnIndex("num_of_in_the_call")));
        survey.setNumOfNoAnswer(cursor.getInt(cursor.getColumnIndex("num_of_no_answer")));
        survey.setNumOfAuditInvalid(cursor.getInt(cursor.getColumnIndex("num_of_audit_invalid")));
        survey.setNumOfReturn(cursor.getInt(cursor.getColumnIndex("num_of_return")));
        survey.setNumOfAuditSuccess(cursor.getInt(cursor.getColumnIndex("num_of_audit_success")));
        survey.setRemainInterval(cursor.getInt(cursor.getColumnIndex("remain_interval")));
        return survey;
    }

    private static ContentValues obj2contentValues(Survey survey) {
        ContentValues values = new ContentValues();
        values.put("id", survey.getId());
        values.put("user_id", survey.getUserId());
        values.put("name", survey.getName());
        values.put("description", survey.getDescription());
        values.put("status", survey.getStatus());
        values.put("type", survey.getType());
        values.put("label_text", survey.getLabelText());
        values.put("config", survey.getConfig());
        values.put("role_name", survey.getRoleName());
        values.put("create_user_id", survey.getCreateUserId());
        values.put("create_user_name", survey.getCreateUserName());

        values.put("create_time", survey.getCreateTime());
        values.put("create_time_str", survey.getCreateTimeStr());
        values.put("update_time", survey.getUpdateTime());
        values.put("begin_time", survey.getBeginTime());
        values.put("end_time", survey.getEndTime());
        values.put("pause_time", survey.getPauseTime());

        values.put("use_property", survey.getUseProperty());
        values.put("mark_property", survey.getMarkProperty());

        values.put("num_of_not_started", survey.getNumOfNotStarted());
        values.put("num_of_in_progress", survey.getNumOfInProgress());
        values.put("num_of_finish", survey.getNumOfFinish());
        values.put("num_of_refuse", survey.getNumOfRefuse());
        values.put("num_of_identify", survey.getNumOfIdentify());
        values.put("num_of_appointment", survey.getNumOfAppointment());
        values.put("num_of_unable_to_contact", survey.getNumOfUnableToContact());
        values.put("num_of_in_the_call", survey.getNumOfInTheCall());
        values.put("num_of_no_answer", survey.getNumOfNoAnswer());
        values.put("num_of_audit_invalid", survey.getNumOfAuditInvalid());
        values.put("num_of_return", survey.getNumOfReturn());
        values.put("num_of_audit_success", survey.getNumOfAuditSuccess());
        values.put("remain_interval", survey.getRemainInterval());
        return values;
    }

    public static Survey queryById(Integer id, Integer userId) {
        String sql = "select * from " + TableSQL.SURVEY_NAME + " where id = ? and user_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{id + "", userId + ""});
        Survey survey = new Survey();
        if (cursor.moveToFirst()) {
            survey = cursor2Obj(cursor);
        }
        cursor.close();
        return survey;
    }

    public static List<Integer> queryAllSurveyId(Integer userId) {
        String sql = "select id from " + TableSQL.SURVEY_NAME + " where user_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        List<Integer> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(cursor.getInt(cursor.getColumnIndex("id")));
        }
        cursor.close();
        return result;
    }

    public static List<MultiItemEntity> queryListAll(Integer userId, Integer userType) {
        String sql = "";
        Cursor cursor;
        if (userType == ProjectConstants.PROJECT_CREATED_BY_ME) {
            sql = "select * from " + TableSQL.SURVEY_NAME + " where user_id = ? and create_user_id = ? order by create_time desc";
            cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", userId + ""});
        } else if (userType == ProjectConstants.PROJECT_DONE_BY_ME) {
            sql = "select * from " + TableSQL.SURVEY_NAME + " where user_id = ? and create_user_id != ? order by create_time desc";
            cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", userId + ""});
        } else {
            sql = "select * from " + TableSQL.SURVEY_NAME + " where user_id = ? order by create_time desc";
            cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        }
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            list.add(cursor2Obj(cursor));
        }
        cursor.close();
        return list;
    }

    public static List<MultiItemEntity> queryListByKeyword(Integer userId, String keyword) {
        String sql = "select * from " + TableSQL.SURVEY_NAME + " where user_id = ? and (name like '%" + keyword + "%' or label_text like '%" + keyword + "%' or create_user_name like '%" + keyword + "%') order by create_time desc";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            list.add(cursor2Obj(cursor));
        }
        cursor.close();
        return list;
    }

    public static List<MultiItemEntity> queryListByType(Integer userId, String type) {
        String sql = "";
        Cursor cursor;
        if (type != null && !"".equals(type)) {
            sql = "select * from " + TableSQL.SURVEY_NAME + " where user_id = ? and type = ? order by create_time desc";
            cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", type});
        } else {
            sql = "select * from " + TableSQL.SURVEY_NAME + " where user_id = ? order by create_time desc";
            cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        }
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            list.add(cursor2Obj(cursor));
        }
        cursor.close();
        return list;
    }

    public static List<MultiItemEntity> queryListByStatus(Integer userId, Integer status) {
        String sql = "";
        Cursor cursor;
        if (status == 0 || status == 1 || status == 2 || status == 3) {
            sql = "select * from " + TableSQL.SURVEY_NAME + " where user_id = ? and status = ? order by create_time desc";
            cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", status + ""});
        } else {
            sql = "select * from " + TableSQL.SURVEY_NAME + " where user_id = ? order by create_time desc";
            cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        }
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            list.add(cursor2Obj(cursor));
        }
        cursor.close();
        return list;
    }

    public static List<MultiItemEntity> queryListByOrder(Integer userId, Integer sort) {
        String sql = "";
        if (sort == ProjectConstants.PROJECT_SORT_BY_CREATE_TIME) {
            sql = "select * from " + TableSQL.SURVEY_NAME + " where user_id = ? order by create_time desc";
        } else if (sort == ProjectConstants.PROJECT_SORT_BY_UPDATE_TIME) {
            sql = "select * from " + TableSQL.SURVEY_NAME + " where user_id = ? order by update_time desc";
        }
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            list.add(cursor2Obj(cursor));
        }
        cursor.close();
        return list;
    }

    public static boolean replace(Survey survey) {
        ContentValues values = obj2contentValues(survey);
        return DBOperation.instanse.replaceTableData(TableSQL.SURVEY_NAME, values);
    }

    public static boolean replaceList(List<MultiItemEntity> list) {
        if (list == null) {
            return false;
        }
        boolean b = true;
        for (int i = 0; i < list.size(); i++) {
            if (!replace((Survey) list.get(i))) {
                b = false;
            }
        }
        return b;
    }

    public static boolean deleteList(List<Integer> ids, Integer userId) {
        if (ids == null) {
            return false;
        }
        if (ids.size() == 0) {
            return true;
        }
        DBOperation.db.beginTransaction();
        for (Integer id : ids) {
            Boolean isSuccess = DBOperation.instanse.deleteTableData(TableSQL.SURVEY_NAME, "id = ? and user_id = ?", new String[]{id + "", userId + ""});
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

    public static boolean update(Survey survey) {
        ContentValues values = new ContentValues();
        values.put("id", survey.getId());
        values.put("user_id", survey.getUserId());
        values.put("num_of_not_started", survey.getNumOfNotStarted());
        values.put("num_of_in_progress", survey.getNumOfInProgress());
        values.put("num_of_finish", survey.getNumOfFinish());
        values.put("num_of_refuse", survey.getNumOfRefuse());
        values.put("num_of_identify", survey.getNumOfIdentify());
        values.put("num_of_appointment", survey.getNumOfAppointment());
        values.put("num_of_unable_to_contact", survey.getNumOfUnableToContact());
        values.put("num_of_in_the_call", survey.getNumOfInTheCall());
        values.put("num_of_no_answer", survey.getNumOfNoAnswer());
        values.put("num_of_audit_invalid", survey.getNumOfAuditInvalid());
        values.put("num_of_return", survey.getNumOfReturn());
        values.put("num_of_audit_success", survey.getNumOfAuditSuccess());

        return DBOperation.instanse.updateTableData(TableSQL.SURVEY_NAME, "id = ? and user_id = ?", new String[]{survey.getId() + "", survey.getUserId() + ""}, values);
    }

    public static Survey getNewSurveyFromNet(JSONObject data, Integer userId, Integer surveyId) {
        Survey survey = new Survey();
        survey.setId(surveyId);
        survey.setUserId(userId);
        survey.setNumOfNotStarted(data.getIntValue("numOfNotStarted"));
        survey.setNumOfInProgress(data.getIntValue("numOfInProgress"));
        survey.setNumOfFinish(data.getIntValue("numOfFinish"));
        survey.setNumOfRefuse(data.getIntValue("numOfRefuse"));
        survey.setNumOfIdentify(data.getIntValue("numOfIdentify"));
        survey.setNumOfAppointment(data.getIntValue("numOfAppointment"));
        survey.setNumOfUnableToContact(data.getIntValue("numOfUnableToContact"));
        survey.setNumOfInTheCall(data.getIntValue("numOfInTheCall"));
        survey.setNumOfNoAnswer(data.getIntValue("numOfNoAnswer"));
        survey.setNumOfAuditInvalid(data.getIntValue("numOfAuditInvalid"));
        survey.setNumOfReturn(data.getIntValue("numOfReturn"));
        survey.setNumOfAuditSuccess(data.getIntValue("numOfAuditSuccess"));
        return survey;
    }

    public static LinkedList<MultiItemEntity> getListFromNet(JSONArray dataArray, Integer userId) {
        final LinkedList<MultiItemEntity> list = new LinkedList<>();
        if (dataArray == null) {
            return list;
        }
        try {
            final int size = dataArray.size();
            for (int i = 0; i < size; i++) {
                Survey survey = new Survey();
                final JSONObject data = dataArray.getJSONObject(i);
                JSONArray useProperty = data.getJSONArray("useProperty");
                JSONArray markProperty = data.getJSONArray("markProperty");
                JSONObject config = data.getJSONObject("projectConfigDTO");

                survey.setId(data.getInteger("id"));
                survey.setUserId(userId);
                survey.setName(data.getString("name"));
                survey.setDescription(data.getString("description"));
                survey.setStatus(data.getInteger("status"));
                survey.setType(data.getString("type"));
                survey.setConfig(config.toJSONString());
                survey.setRoleName(data.getString("roleName"));
                survey.setCreateUserId(data.getInteger("createUser"));
                survey.setCreateUserName(data.getString("userName"));
//                if (config.getIntValue("trackInterviewer") != 0 && config.getIntValue("trackInterviewer") != 4) {
//                    survey.setRemainInterval(config.getIntValue("trackInterviewer"));
//                } else if (config.getIntValue("trackInterviewer") == 0) {
//                    survey.setRemainInterval(null);
//                } else if (config.getIntValue("trackInterviewer") == 4) {
//                    survey.setRemainInterval(6);
//                }

                if (data.getString("createTimeStr") != null) {
                    survey.setCreateTimeStr(data.getString("createTimeStr"));
                }
                survey.setCreateTime(data.getString("createTime"));
                if (data.getString("updateTimeStr") != null) {
                    survey.setUpdateTime(data.getString("updateTimeStr"));
                } else {
                    survey.setUpdateTime(data.getString("updateTime"));
                }
                if (data.getString("beginDateStr") != null) {
                    survey.setBeginTime(data.getString("beginDateStr"));
                } else {
                    survey.setBeginTime(data.getString("beginDate"));
                }
                if (data.getString("endDateStr") != null) {
                    survey.setEndTime(data.getString("endDateStr"));
                } else {
                    survey.setEndTime(data.getString("endDate"));
                }
                if (data.getString("pauseTimeStr") != null) {
                    survey.setPauseTime(data.getString("pauseTimeStr"));
                } else {
                    survey.setPauseTime(data.getString("pauseTime"));
                }

                if (useProperty != null) {
                    survey.setUseProperty(useProperty.toJSONString());
                }
                if (markProperty != null) {
                    survey.setMarkProperty(markProperty.toJSONString());
                }

                survey.setNumOfNotStarted(data.getIntValue("numOfNotStarted"));
                survey.setNumOfInProgress(data.getIntValue("numOfInProgress"));
                survey.setNumOfFinish(data.getIntValue("numOfFinish"));
                survey.setNumOfRefuse(data.getIntValue("numOfRefuse"));
                survey.setNumOfIdentify(data.getIntValue("numOfIdentify"));
                survey.setNumOfAppointment(data.getIntValue("numOfAppointment"));
                survey.setNumOfUnableToContact(data.getIntValue("numOfUnableToContact"));
                survey.setNumOfInTheCall(data.getIntValue("numOfInTheCall"));
                survey.setNumOfNoAnswer(data.getIntValue("numOfNoAnswer"));
                survey.setNumOfAuditInvalid(data.getIntValue("numOfAuditInvalid"));
                survey.setNumOfReturn(data.getIntValue("numOfReturn"));
                survey.setNumOfAuditSuccess(data.getIntValue("numOfAuditSuccess"));

                list.add(survey);
            }
        } catch (Exception e) {
        }
        return list;
    }
}
