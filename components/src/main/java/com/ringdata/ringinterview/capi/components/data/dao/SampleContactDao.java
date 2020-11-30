package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;
import com.ringdata.ringinterview.capi.components.data.model.SampleContact;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by admin on 2018/6/4.
 */

public class SampleContactDao {
    private static ContentValues obj2CV(SampleContact sampleContact) {
        ContentValues values = new ContentValues();
        values.put("id", sampleContact.getId());
        values.put("user_id", sampleContact.getUserId());
        values.put("project_id", sampleContact.getProjectId());
        values.put("sample_guid", sampleContact.getSampleGuid());
        values.put("name", sampleContact.getName());
        values.put("relation", sampleContact.getRelation());
        values.put("mobile", sampleContact.getMobile());
        values.put("phone", sampleContact.getPhone());
        values.put("email", sampleContact.getEmail());
        values.put("qq", sampleContact.getQq());
        values.put("weixin", sampleContact.getWeixin());
        values.put("weibo", sampleContact.getWeibo());
        values.put("description", sampleContact.getDescription());
        values.put("create_time", sampleContact.getCreateTime());
        values.put("create_user", sampleContact.getCreateUser());
        values.put("is_delete", sampleContact.getIsDelete());
        values.put("delete_user", sampleContact.getDeleteUser());
        values.put("is_upload", sampleContact.getIsUpload());
        return values;
    }

    public static boolean deleteByUserID(Integer userId) {
        return DBOperation.instanse.deleteTableData(TableSQL.SAMPLE_CONTACT_NAME, "user_id = ?", new String[]{userId + ""});
    }

    public static boolean deleteByContactId(Integer contactId, Integer userId) {
        ContentValues values = new ContentValues();
        values.put("is_delete", 1);
        return DBOperation.instanse.updateTableData(TableSQL.SAMPLE_CONTACT_NAME, "id = ? and user_id = ?", new String[]{contactId + "", userId + ""}, values);
    }

    private static SampleContact cursor2Obj(Cursor cursor) {
        SampleContact sampleContact = new SampleContact();
        sampleContact.setId(cursor.getInt(cursor.getColumnIndex("id")));
        sampleContact.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
        sampleContact.setProjectId(cursor.getInt(cursor.getColumnIndex("project_id")));
        sampleContact.setSampleGuid(cursor.getString(cursor.getColumnIndex("sample_guid")));
        sampleContact.setName(cursor.getString(cursor.getColumnIndex("name")));
        sampleContact.setRelation(cursor.getString(cursor.getColumnIndex("relation")));
        sampleContact.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
        sampleContact.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
        sampleContact.setEmail(cursor.getString(cursor.getColumnIndex("email")));
        sampleContact.setQq(cursor.getString(cursor.getColumnIndex("qq")));
        sampleContact.setWeixin(cursor.getString(cursor.getColumnIndex("weixin")));
        sampleContact.setWeibo(cursor.getString(cursor.getColumnIndex("weibo")));
        sampleContact.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        sampleContact.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
        sampleContact.setCreateUser(cursor.getInt(cursor.getColumnIndex("create_user")));
        sampleContact.setIsDelete(cursor.getInt(cursor.getColumnIndex("is_delete")));
        sampleContact.setDeleteUser(cursor.getInt(cursor.getColumnIndex("delete_user")));
        sampleContact.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
        return sampleContact;
    }

    public static boolean insertOrUpdate(SampleContact sampleContact) {
        ContentValues values = obj2CV(sampleContact);
        boolean isSuccess = DBOperation.instanse.updateTableData(TableSQL.SAMPLE_CONTACT_NAME, "id = ? and user_id = ? and project_id = ? ", new String[]{sampleContact.getId() + "", sampleContact.getUserId() + "", sampleContact.getProjectId() + ""}, values);
        if (!isSuccess) {
            isSuccess = DBOperation.instanse.insertTableData(TableSQL.SAMPLE_CONTACT_NAME, values);
        }
        return isSuccess;
    }

    public static boolean insertOrUpdateList(List<SampleContact> list) {
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

    public static boolean replace(SampleContact sampleContact) {
        ContentValues values = obj2CV(sampleContact);
        return DBOperation.instanse.replaceTableData(TableSQL.SAMPLE_CONTACT_NAME, values);
    }

    //更新提交状态
    public static boolean updateUploadStatus(Integer userId, Integer surveyId) {
        ContentValues values = new ContentValues();
        values.put("is_upload", 2);
        return DBOperation.instanse.updateTableData(TableSQL.SAMPLE_CONTACT_NAME, " user_id = ? and project_id = ?", new String[]{userId + "", surveyId + ""}, values);
    }

    public static LinkedList<SampleContact> queryBySampleGuid(String sampleGuid, Integer userId) {
        String sql = "select * from " + TableSQL.SAMPLE_CONTACT_NAME + " where sample_guid = ? and user_id = ? and is_delete = 0";
        LinkedList<SampleContact> list = new LinkedList<>();
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{sampleGuid + "", userId + ""});
        while (cursor.moveToNext()) {
            list.add(cursor2Obj(cursor));
        }
        cursor.close();
        return list;
    }

    public static int countContactBySample(Integer userId, Integer surveyId, String sampleGuid) {
        String sql = "select count(*) as count from " + TableSQL.SAMPLE_CONTACT_NAME + " where user_id = ? and project_id = ? and sample_guid = ? and is_delete = 0";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", surveyId + "", sampleGuid});
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public static boolean replaceList(List<MultiItemEntity> list) {
        if (list == null) {
            return false;
        }
        boolean b = true;
        for (int i = 0; i < list.size(); i++) {
            if (!replace((SampleContact) list.get(i))) {
                b = false;
            }
        }
        return b;
    }

    //获取未提交数据
    public static List<SampleContact> queryNoUploadList(Integer userId, Integer surveyId) {
        String sql = "select * from " + TableSQL.SAMPLE_CONTACT_NAME + " where is_upload = 1 and user_id = ? and project_id = ? and is_delete = 0";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", surveyId + ""});
        List<SampleContact> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(cursor2Obj(cursor));
        }
        cursor.close();
        return list;
    }

    public static LinkedList<SampleContact> getListFromNet(JSONArray dataArray, Integer userId, Integer projectId) {
        final LinkedList<SampleContact> list = new LinkedList<>();
        if (dataArray == null) {
            return list;
        }
        try {
            final int size = dataArray.size();
            for (int i = 0; i < size; i++) {
                SampleContact sampleContact = new SampleContact();
                final JSONObject data = dataArray.getJSONObject(i);
                sampleContact.setId(data.getInteger("id"));
                sampleContact.setUserId(userId);
                sampleContact.setProjectId(projectId);
                sampleContact.setSampleGuid(data.getString("sampleGuid"));
                sampleContact.setName(data.getString("name"));
                sampleContact.setRelation(data.getString("relation"));
                sampleContact.setMobile(data.getString("mobile"));
                sampleContact.setPhone(data.getString("phone"));
                sampleContact.setEmail(data.getString("email"));
                sampleContact.setQq(data.getString("qq"));
                sampleContact.setWeixin(data.getString("weixin"));
                sampleContact.setWeibo(data.getString("weibo"));
                sampleContact.setDescription(data.getString("description"));
                sampleContact.setCreateTime(data.getLong("createTime"));
                sampleContact.setCreateUser(data.getInteger("createUser"));
                sampleContact.setIsDelete(data.getIntValue("isDelete"));
                sampleContact.setDeleteUser(data.getIntValue("deleteUser"));
                sampleContact.setIsUpload(2);
                list.add(sampleContact);
            }
        } catch (Exception e) {
        }
        return list;
    }

    public static LinkedList<SampleContact> getListFromNet(JSONArray dataArray, Integer userId, Integer projectId, String sampleGuid) {
        final LinkedList<SampleContact> list = new LinkedList<>();
        if (dataArray == null) {
            return list;
        }
        try {
            final int size = dataArray.size();
            for (int i = 0; i < size; i++) {
                SampleContact sampleContact = new SampleContact();
                final JSONObject data = dataArray.getJSONObject(i);
                sampleContact.setId(data.getInteger("id"));
                sampleContact.setUserId(userId);
                sampleContact.setProjectId(projectId);
                sampleContact.setSampleGuid(sampleGuid);
                sampleContact.setName(data.getString("name"));
                sampleContact.setRelation(data.getString("relation"));
                sampleContact.setMobile(data.getString("mobile"));
                sampleContact.setPhone(data.getString("phone"));
                sampleContact.setEmail(data.getString("email"));
                sampleContact.setQq(data.getString("qq"));
                sampleContact.setWeixin(data.getString("weixin"));
                sampleContact.setWeibo(data.getString("weibo"));
                sampleContact.setDescription(data.getString("description"));
                sampleContact.setCreateTime(data.getLong("createTime"));
                sampleContact.setCreateUser(data.getInteger("createUser"));
                sampleContact.setIsDelete(data.getIntValue("isDelete"));
                sampleContact.setDeleteUser(data.getIntValue("deleteUser"));
                sampleContact.setIsUpload(2);
                list.add(sampleContact);
            }
        } catch (Exception e) {
        }
        return list;
    }
}
