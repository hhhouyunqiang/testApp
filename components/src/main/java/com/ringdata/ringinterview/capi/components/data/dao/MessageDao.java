package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;
import com.ringdata.ringinterview.capi.components.data.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by admin on 17/11/11.
 */

public class MessageDao {
    private static ContentValues obj2CV(Message message) {
        ContentValues values = new ContentValues();
        values.put("id", message.getId());
        values.put("title", message.getTitle());
        values.put("context", message.getContext());
        values.put("status", message.getStatus());
        values.put("is_delete", message.getIsDelete());
        values.put("is_read", message.getIsRead());
        values.put("create_time", message.getCreateTime());
        values.put("interviewer_cellphone", message.getInterviewerCellphone());

        values.put("sample_identifier", message.getSampleIdentifier());
        values.put("user_id", message.getUserId());
        values.put("samples_map", message.getSamplesMap());
        values.put("survey_name", message.getSurveyName());
        values.put("module_name", message.getModuleName());

        values.put("audit_questions", message.getAuditQuestions());
        values.put("audit_return_reason", message.getAuditReturnReason());
        values.put("interviewer_name", message.getInterviewerName());
        values.put("interviewer_username", message.getInterviewerUsername());
        values.put("response_status_text", message.getResponseStatusText());
        return values;
    }

    private static Message cursor2Obj(Cursor cursor) {
        Message message = new Message();
        message.setId(cursor.getInt(cursor.getColumnIndex("id")));
        message.setSurveyName(cursor.getString(cursor.getColumnIndex("survey_name")));
        message.setModuleName(cursor.getString(cursor.getColumnIndex("module_name")));
        message.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        message.setContext(cursor.getString(cursor.getColumnIndex("context")));
        message.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
        message.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        message.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
        message.setSampleIdentifier(cursor.getString(cursor.getColumnIndex("sample_identifier")));
        message.setIsRead(cursor.getInt(cursor.getColumnIndex("is_read")));
        message.setSamplesMap(cursor.getString(cursor.getColumnIndex("samples_map")));
        message.setAuditQuestions(cursor.getString(cursor.getColumnIndex("audit_questions")));
        message.setAuditReturnReason(cursor.getString(cursor.getColumnIndex("audit_return_reason")));
        message.setInterviewerName(cursor.getString(cursor.getColumnIndex("interviewer_name")));
        message.setInterviewerUsername(cursor.getString(cursor.getColumnIndex("interviewer_username")));
        message.setResponseStatusText(cursor.getString(cursor.getColumnIndex("response_status_text")));
        message.setIsDelete(cursor.getInt(cursor.getColumnIndex("is_delete")));
        message.setInterviewerCellphone(cursor.getString(cursor.getColumnIndex("interviewer_cellphone")));
        return message;
    }

    public static boolean replace(Message message) {
        ContentValues values = obj2CV(message);
        return DBOperation.instanse.replaceTableData(TableSQL.MESSAGE_NAME, values);
    }

    public static boolean read(Integer id, Integer userId) {
        ContentValues values = new ContentValues();
        values.put("is_read", 1);
        return DBOperation.instanse.updateTableData(TableSQL.MESSAGE_NAME, " user_id = ? and id = ?", new String[]{userId + "", id + ""}, values);
    }


    public static boolean delete(Integer id, Integer userId) {
        ContentValues values = new ContentValues();
        values.put("is_delete", 1);
        return DBOperation.instanse.updateTableData(TableSQL.MESSAGE_NAME, " user_id = ? and id = ?", new String[]{userId + "", id + ""}, values);
    }

    public static Integer countNoRead(Integer userId) {
        String sql = "select count(*) as count from " + TableSQL.MESSAGE_NAME + " where user_id = ? and is_read = 0";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        Integer count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("count"));
        }
        cursor.close();
        return count;
    }

    public static boolean replaceList(List<MultiItemEntity> list) {
        if (list == null) {
            return false;
        }
        if (list.size() == 0) {
            return true;
        }
        boolean b = true;
        for (int i = 0; i < list.size(); i++) {
            if (!replace((Message) list.get(i))) {
                b = false;
            }
        }
        return b;
    }


    public static List<HashMap<String, Object>> queryUploadList(Integer userId) {
        String sql = "select * from " + TableSQL.MESSAGE_NAME + " where user_id = ? and (is_delete = 1 or is_read = 1)";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        List<HashMap<String, Object>> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("user_id", cursor.getInt(cursor.getColumnIndex("user_id")));
            hm.put("id", cursor.getInt(cursor.getColumnIndex("id")));
            hm.put("is_delete", cursor.getInt(cursor.getColumnIndex("is_delete")));
            hm.put("is_read", cursor.getString(cursor.getColumnIndex("is_read")));
            list.add(hm);
        }
        cursor.close();
        return list;
    }

    public static LinkedList<MultiItemEntity> queryList(Integer userId) {
        String sql = "select * from " + TableSQL.MESSAGE_NAME + " where user_id = ? and is_delete != 1";
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        while (cursor.moveToNext()) {
            list.add(cursor2Obj(cursor));
        }
        cursor.close();
        return list;
    }

    public static LinkedList<MultiItemEntity> queryList(Integer userId, String keyword) {
        String sql = "select * from " + TableSQL.MESSAGE_NAME + " where user_id = ? and is_delete != 1 and (survey_name like '%" + keyword + "%' or module_name like '%" + keyword + "%' or context like '%" + keyword + "%')";
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        while (cursor.moveToNext()) {
            list.add(cursor2Obj(cursor));
        }
        cursor.close();
        return list;
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
            isSuccess = DBOperation.instanse.deleteTableData(TableSQL.MESSAGE_NAME, "id = ? and user_id = ?", new String[]{id + "", userId + ""});
            if (!isSuccess) {
//                //有任何一个删除失败就回滚不提交
//                DBOperation.db.endTransaction();
//                return false;
            }
        }
        DBOperation.db.setTransactionSuccessful();
        DBOperation.db.endTransaction();
        return true;
    }


    public static LinkedList<MultiItemEntity> getListFromNet(JSONArray dataArray, Integer userId) {

        final LinkedList<MultiItemEntity> list = new LinkedList<>();

        final int size = dataArray.size();
        for (int i = 0; i < size; i++) {
            Message message = new Message();
            final JSONObject data = dataArray.getJSONObject(i);
            message.setId(data.getInteger("id"));
            message.setSurveyName(data.getString("survey_name"));
            message.setModuleName(data.getString("module_name"));
            message.setCreateTime(data.getLong("create_time"));
            message.setContext(data.getString("audit_content"));
            message.setIsDelete(data.getInteger("is_delete"));
            message.setIsRead(data.getInteger("is_read"));
            message.setUserId(userId);
            message.setSampleIdentifier(data.getString("sample_identifier"));
            message.setAuditQuestions(data.getString("audit_questions"));
            message.setAuditReturnReason(data.getString("audit_return_reason"));
            message.setInterviewerName(data.getString("interviewer_name"));
            message.setSamplesMap(data.getString("samplesMap"));
            message.setInterviewerCellphone(data.getString("interviewer_cellphone"));
            message.setInterviewerUsername(data.getString("interviewer_username"));
            message.setResponseStatusText(data.getString("response_status_text"));
            list.add(message);
        }
        return list;
    }
}

