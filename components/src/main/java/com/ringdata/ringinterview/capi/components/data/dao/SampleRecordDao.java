package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;
import com.ringdata.ringinterview.capi.components.data.model.SampleRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 17/11/11.
 */

public class SampleRecordDao {

    private static SampleRecord cursor2Obj(Cursor cursor) {
        SampleRecord sampleRecord = new SampleRecord();
        sampleRecord.setId(cursor.getInt(cursor.getColumnIndex("id")));
        sampleRecord.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
        sampleRecord.setProjectId(cursor.getInt(cursor.getColumnIndex("project_id")));
        sampleRecord.setSampleGuid(cursor.getString(cursor.getColumnIndex("sample_guid")));
        sampleRecord.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        sampleRecord.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
        sampleRecord.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
        return sampleRecord;
    }

    private static ContentValues obj2contentValues(SampleRecord sampleRecord) {
        ContentValues values = new ContentValues();
        values.put("id", sampleRecord.getId());
        values.put("user_id", sampleRecord.getUserId());
        values.put("project_id", sampleRecord.getProjectId());
        values.put("sample_guid", sampleRecord.getSampleGuid());
        values.put("status", sampleRecord.getStatus());
        values.put("is_upload", sampleRecord.getIsUpload());
        values.put("create_time", sampleRecord.getCreateTime());
        return values;
    }

    public static SampleRecord queryById(Integer id, Integer userId) {
        String sql = "select * from " + TableSQL.SAMPLE_RECORD_NAME + " where id = ? and user_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{id + "", userId + ""});
        SampleRecord sampleRecord = new SampleRecord();
        if (cursor.moveToFirst()) {
            sampleRecord = cursor2Obj(cursor);
        }
        cursor.close();
        return sampleRecord;
    }

    //查询账号下需要上传的样本更改记录（新增样本，样本状态改变的样本）
    public static List<SampleRecord> getNeedUploadListByUserId(Integer userId, Integer surveyId) {
        List<SampleRecord> sampleRecordList = new ArrayList<>();
        String sql = "select * from "
                + TableSQL.SAMPLE_RECORD_NAME
                + " where user_id = ? and project_id = ? and is_upload = 1";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", surveyId + ""});
        while (cursor.moveToNext()) {
            sampleRecordList.add(cursor2Obj(cursor));
        }
        cursor.close();
        return sampleRecordList;
    }

    public static boolean updateUploadStatus(Integer userId, Integer surveyId) {
        ContentValues values = new ContentValues();
        values.put("is_upload", 2);
        return DBOperation.instanse.updateTableData(TableSQL.SAMPLE_RECORD_NAME, " user_id = ? and project_id = ?", new String[]{userId + "", surveyId + ""}, values);
    }

    public static boolean insert(HashMap<String, String> hm) {
        ContentValues values = new ContentValues();
        Iterator iterator = hm.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            values.put(key, value);
        }
        return DBOperation.instanse.insertTableData(TableSQL.SAMPLE_RECORD_NAME, values);
    }

    public static boolean replace(SampleRecord sampleRecord) {
        ContentValues values = obj2contentValues(sampleRecord);
        return DBOperation.instanse.replaceTableData(TableSQL.SAMPLE_RECORD_NAME, values);
    }

    public static boolean replaceList(List<MultiItemEntity> list) {
        if (list == null) {
            return false;
        }
        boolean b = true;
        for (int i = 0; i < list.size(); i++) {
            if (!replace((SampleRecord) list.get(i))) {
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
            Boolean isSuccess = DBOperation.instanse.deleteTableData(TableSQL.SAMPLE_RECORD_NAME, "id = ? and user_id = ?", new String[]{id + "", userId + ""});
            if (!isSuccess) {
                //有任何一个删除失败就回滚不提交
                DBOperation.db.endTransaction();
                return false;
            }
        }
        DBOperation.db.setTransactionSuccessful();
        DBOperation.db.endTransaction();
        return true;
    }
}
