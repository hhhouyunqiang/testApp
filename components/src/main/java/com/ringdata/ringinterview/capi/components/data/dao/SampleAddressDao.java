package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;
import com.ringdata.ringinterview.capi.components.data.model.SampleAddress;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by admin on 2018/6/4.
 */

public class SampleAddressDao {

    private static ContentValues obj2CV(SampleAddress sampleAddress) {
        ContentValues values = new ContentValues();
        values.put("id", sampleAddress.getId());
        values.put("user_id", sampleAddress.getUserId());
        values.put("project_id", sampleAddress.getProjectId());
        values.put("sample_guid", sampleAddress.getSampleGuid());
        values.put("name", sampleAddress.getName());
        values.put("province", sampleAddress.getProvince());
        values.put("city", sampleAddress.getCity());
        values.put("district", sampleAddress.getDistrict());
        values.put("town", sampleAddress.getTown());
        values.put("village", sampleAddress.getVillage());
        values.put("address", sampleAddress.getAddress());
        values.put("description", sampleAddress.getDescription());
        values.put("create_time", sampleAddress.getCreateTime());
        values.put("create_user", sampleAddress.getCreateUser());
        values.put("is_delete", sampleAddress.getIsDelete());
        values.put("delete_user", sampleAddress.getDeleteUser());
        values.put("is_upload", sampleAddress.getIsUpload());
        return values;
    }

    private static SampleAddress cursor2Obj(Cursor cursor) {
        SampleAddress sampleAddress = new SampleAddress();
        sampleAddress.setId(cursor.getInt(cursor.getColumnIndex("id")));
        sampleAddress.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
        sampleAddress.setProjectId(cursor.getInt(cursor.getColumnIndex("project_id")));
        sampleAddress.setSampleGuid(cursor.getString(cursor.getColumnIndex("sample_guid")));
        sampleAddress.setName(cursor.getString(cursor.getColumnIndex("name")));
        sampleAddress.setProvince(cursor.getString(cursor.getColumnIndex("province")));
        sampleAddress.setCity(cursor.getString(cursor.getColumnIndex("city")));
        sampleAddress.setDistrict(cursor.getString(cursor.getColumnIndex("district")));
        sampleAddress.setTown(cursor.getString(cursor.getColumnIndex("town")));
        sampleAddress.setVillage(cursor.getString(cursor.getColumnIndex("village")));
        sampleAddress.setAddress(cursor.getString(cursor.getColumnIndex("address")));
        sampleAddress.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        sampleAddress.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
        sampleAddress.setCreateUser(cursor.getInt(cursor.getColumnIndex("create_user")));
        sampleAddress.setIsDelete(cursor.getInt(cursor.getColumnIndex("is_delete")));
        sampleAddress.setDeleteUser(cursor.getInt(cursor.getColumnIndex("delete_user")));
        sampleAddress.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
        return sampleAddress;
    }

    public static boolean deleteByUserID(Integer userId) {
        return DBOperation.instanse.deleteTableData(TableSQL.SAMPLE_ADDRESS_NAME, "user_id = ?", new String[]{userId + ""});
    }

    public static boolean deleteByAddressId(Integer addressId, Integer userId) {
        ContentValues values = new ContentValues();
        values.put("is_delete", 1);
        return DBOperation.instanse.updateTableData(TableSQL.SAMPLE_ADDRESS_NAME, "id = ? and user_id = ?", new String[]{addressId + "", userId + ""}, values);
    }

    public static boolean insertOrUpdate(SampleAddress sampleAddress) {
        ContentValues values = obj2CV(sampleAddress);
        boolean isSuccess = DBOperation.instanse.updateTableData(TableSQL.SAMPLE_ADDRESS_NAME, "id = ? and user_id = ? and project_id = ? ", new String[]{sampleAddress.getId() + "", sampleAddress.getUserId() + "", sampleAddress.getProjectId() + ""}, values);
        if (!isSuccess) {
            isSuccess = DBOperation.instanse.insertTableData(TableSQL.SAMPLE_ADDRESS_NAME, values);
        }
        return isSuccess;
    }

    public static boolean insertOrUpdateList(List<SampleAddress> list) {
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

    public static boolean replace(SampleAddress sampleAddress) {
        ContentValues values = obj2CV(sampleAddress);
        return DBOperation.instanse.replaceTableData(TableSQL.SAMPLE_ADDRESS_NAME, values);
    }

    public static LinkedList<MultiItemEntity> queryBySampleGuid(String sampleGuid, Integer userId) {
        String sql = "select * from " + TableSQL.SAMPLE_ADDRESS_NAME + " where sample_guid = ? and user_id = ? and is_delete = 0";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{sampleGuid + "", userId + ""});
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            list.add(cursor2Obj(cursor));
        }
        cursor.close();
        return list;
    }

    public static int countAddressBySample(Integer userId, Integer surveyId, String sampleGuid) {
        String sql = "select count(*) as count from " + TableSQL.SAMPLE_ADDRESS_NAME + " where user_id = ? and project_id = ? and sample_guid = ? and is_delete = 0";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", surveyId + "", sampleGuid});
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //更新提交状态
    public static boolean updateUploadStatus(Integer userId, Integer surveyId) {
        ContentValues values = new ContentValues();
        values.put("is_upload", 2);
        return DBOperation.instanse.updateTableData(TableSQL.SAMPLE_ADDRESS_NAME, " user_id = ? and project_id = ?", new String[]{userId + "", surveyId + ""}, values);
    }

    //获取未提交数据
    public static List<SampleAddress> queryNoUploadList(Integer userId, Integer surveyId) {
        String sql = "select * from " + TableSQL.SAMPLE_ADDRESS_NAME + " where is_upload = 1 and user_id = ? and project_id = ? and is_delete = 0";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", surveyId + ""});
        List<SampleAddress> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(cursor2Obj(cursor));
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
            if (!replace((SampleAddress) list.get(i))) {
                b = false;
            }
        }
        return b;
    }

    public static LinkedList<SampleAddress> getListFromNet(JSONArray dataArray, Integer userId, Integer projectId) {
        final LinkedList<SampleAddress> list = new LinkedList<>();
        if (dataArray == null) {
            return list;
        }
        try {
            final int size = dataArray.size();
            for (int i = 0; i < size; i++) {
                SampleAddress sampleAddress = new SampleAddress();
                final JSONObject data = dataArray.getJSONObject(i);
                sampleAddress.setId(data.getInteger("id"));
                sampleAddress.setUserId(userId);
                sampleAddress.setProjectId(projectId);
                sampleAddress.setSampleGuid(data.getString("sampleGuid"));
                sampleAddress.setName(data.getString("name"));
                sampleAddress.setProvince(data.getString("province"));
                sampleAddress.setCity(data.getString("city"));
                sampleAddress.setDistrict(data.getString("district"));
                sampleAddress.setTown(data.getString("town"));
                sampleAddress.setVillage(data.getString("village"));
                sampleAddress.setAddress(data.getString("address"));
                sampleAddress.setDescription(data.getString("description"));
                sampleAddress.setCreateTime(data.getLong("createTime"));
                sampleAddress.setCreateUser(data.getInteger("createUser"));
                sampleAddress.setIsDelete(data.getIntValue("isDelete"));
                sampleAddress.setDeleteUser(data.getIntValue("deleteUser"));
                sampleAddress.setIsUpload(2);
                list.add(sampleAddress);
            }
        } catch (Exception e) {
        }
        return list;
    }

}
