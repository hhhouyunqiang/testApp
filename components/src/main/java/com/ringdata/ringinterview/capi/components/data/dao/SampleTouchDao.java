package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;
import com.ringdata.ringinterview.capi.components.data.model.SampleTouch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by admin on 2018/6/4.
 */

public class SampleTouchDao {

    private static ContentValues obj2CV(SampleTouch sampleTouch) {
        ContentValues values = new ContentValues();
        values.put("id", sampleTouch.getId());
        values.put("user_id", sampleTouch.getUserId());
        values.put("project_id", sampleTouch.getProjectId());
        values.put("sample_guid", sampleTouch.getSampleGuid());
        values.put("type", sampleTouch.getType());
        values.put("status", sampleTouch.getStatus());
        values.put("description", sampleTouch.getDescription());
        values.put("create_time", sampleTouch.getCreateTime());
        values.put("create_user", sampleTouch.getCreateUser());
        values.put("is_delete", sampleTouch.getIsDelete());
        values.put("delete_user", sampleTouch.getDeleteUser());
        values.put("is_upload", sampleTouch.getIsUpload());
        return values;
    }

    private static SampleTouch cursor2Obj(Cursor cursor) {
        SampleTouch sampleTouch = new SampleTouch();
        sampleTouch.setId(cursor.getInt(cursor.getColumnIndex("id")));
        sampleTouch.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
        sampleTouch.setProjectId(cursor.getInt(cursor.getColumnIndex("project_id")));
        sampleTouch.setSampleGuid(cursor.getString(cursor.getColumnIndex("sample_guid")));
        sampleTouch.setType(cursor.getString(cursor.getColumnIndex("type")));
        sampleTouch.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        sampleTouch.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        sampleTouch.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
        sampleTouch.setCreateUser(cursor.getInt(cursor.getColumnIndex("create_user")));
        sampleTouch.setIsDelete(cursor.getInt(cursor.getColumnIndex("is_delete")));
        sampleTouch.setDeleteUser(cursor.getInt(cursor.getColumnIndex("delete_user")));
        sampleTouch.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
        return sampleTouch;
    }

    public static boolean insertOrUpdate(SampleTouch sampleTouch) {
        ContentValues values = obj2CV(sampleTouch);
        boolean isSuccess = DBOperation.instanse.updateTableData(TableSQL.SAMPLE_TOUCH_NAME, "id = ? and user_id = ? and project_id = ? ", new String[]{sampleTouch.getId() + "", sampleTouch.getUserId() + "", sampleTouch.getProjectId() + ""}, values);
        if (!isSuccess) {
            isSuccess = DBOperation.instanse.insertTableData(TableSQL.SAMPLE_TOUCH_NAME, values);
        }
        return isSuccess;
    }

    public static boolean insertOrUpdateList(List<SampleTouch> list) {
        if (list == null) {
            return false;
        }
        boolean b = true;
        for (int i = 0; i < list.size(); i++) {
            if (!insertOrUpdate(list.get(i))) {
                b = false;
            }
        }
        return b;
    }

    public static boolean replace(SampleTouch sampleTouch) {
        ContentValues values = obj2CV(sampleTouch);
        return DBOperation.instanse.replaceTableData(TableSQL.SAMPLE_TOUCH_NAME, values);
    }

    public static LinkedList<MultiItemEntity> queryBySampleGuid(String sampleGuid, Integer userId) {
        String sql = "select * from " + TableSQL.SAMPLE_TOUCH_NAME + " where sample_guid = ? and user_id = ? and is_delete = 0";
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{sampleGuid + "", userId + ""});
        while (cursor.moveToNext()) {
            SampleTouch sampleTouch = new SampleTouch();
            sampleTouch = cursor2Obj(cursor);
            list.add(sampleTouch);
        }
        cursor.close();
        return list;
    }

    public static boolean replaceList(List<MultiItemEntity> list) {
        if (list == null) {
            return false;
        }
        boolean b = true;
        for (int i = 0; i < list.size(); i++) {
            if (!replace((SampleTouch) list.get(i))) {
                b = false;
            }
        }
        return b;
    }

    public static boolean deleteByUserID(Integer userId) {
        return DBOperation.instanse.deleteTableData(TableSQL.SAMPLE_TOUCH_NAME, "user_id = ?", new String[]{userId + ""});
    }

    public static boolean deleteByTouchId(Integer touchId, Integer userId) {
        ContentValues values = new ContentValues();
        values.put("is_delete", 1);
        return DBOperation.instanse.updateTableData(TableSQL.SAMPLE_TOUCH_NAME, "id = ? and user_id = ?", new String[]{touchId + "", userId + ""}, values);
    }

    //更新提交状态
    public static boolean updateUploadStatus(Integer userId, Integer surveyId) {
        ContentValues values = new ContentValues();
        values.put("is_upload", 2);
        return DBOperation.instanse.updateTableData(TableSQL.SAMPLE_TOUCH_NAME, " user_id = ? and project_id = ?", new String[]{userId + "", surveyId + ""}, values);
    }

    public static int countTouchBySample(Integer userId, Integer surveyId, String sampleGuid) {
        String sql = "select count(*) as count from " + TableSQL.SAMPLE_TOUCH_NAME + " where user_id = ? and project_id = ? and sample_guid = ? and is_delete = 0";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", surveyId + "", sampleGuid});
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //获取未提交数据
    public static List<SampleTouch> queryNoUploadList(Integer userId, Integer surveyId) {
        String sql = "select * from " + TableSQL.SAMPLE_TOUCH_NAME + " where is_upload = 1 and user_id = ? and project_id = ? and is_delete = 0";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", surveyId + ""});
        List<SampleTouch> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(cursor2Obj(cursor));
        }
        cursor.close();
        return list;
    }

    public static LinkedList<SampleTouch> getListFromNet(JSONArray dataArray, Integer userId, Integer projectId) {
        final LinkedList<SampleTouch> list = new LinkedList<>();
        if (dataArray == null) {
            return list;
        }
        try {
            final int size = dataArray.size();
            for (int i = 0; i < size; i++) {
                SampleTouch sampleTouch = new SampleTouch();
                final JSONObject data = dataArray.getJSONObject(i);
                sampleTouch.setId(data.getInteger("id"));
                sampleTouch.setUserId(userId);
                sampleTouch.setProjectId(projectId);
                sampleTouch.setSampleGuid(data.getString("sampleGuid"));
                sampleTouch.setType(data.getString("type"));
                sampleTouch.setStatus(data.getInteger("status"));
                sampleTouch.setDescription(data.getString("description"));
                sampleTouch.setCreateTime(data.getLong("createTime"));
                sampleTouch.setCreateUser(data.getInteger("createUser"));
                sampleTouch.setIsDelete(data.getIntValue("isDelete"));
                sampleTouch.setDeleteUser(data.getIntValue("deleteUser"));
                sampleTouch.setIsUpload(2);
                list.add(sampleTouch);
            }
        } catch (Exception e) {
        }
        return list;
    }
}