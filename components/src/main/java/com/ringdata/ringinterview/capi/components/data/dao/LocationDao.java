package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 17/11/25.
 */
public class LocationDao {

    public static List<HashMap<String,Object>> queryNoUploadList(Integer userId, Integer projectId){
        String sql = "select * from " + TableSQL.USER_LOCATION_NAME + " where is_upload = 1 and user_id = ? and project_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql,new String[]{userId + "", projectId + ""});
        List<HashMap<String,Object>> list = new ArrayList<>();
        while (cursor.moveToNext()){
            HashMap<String,Object> hm = new HashMap<>();
            hm.put("userId",cursor.getInt(cursor.getColumnIndex("user_id")));
            hm.put("projectId",cursor.getInt(cursor.getColumnIndex("project_id")));
            hm.put("lat",cursor.getString(cursor.getColumnIndex("lat")));
            hm.put("lon",cursor.getString(cursor.getColumnIndex("lon")));
            hm.put("address",cursor.getString(cursor.getColumnIndex("address")));
            hm.put("posTime",cursor.getString(cursor.getColumnIndex("create_time")));
            list.add(hm);
        }
        cursor.close();
        return  list;
    }

    public static boolean updateUploadStatus(Integer userId, Integer projectId){
        ContentValues values = new ContentValues();
        values.put("is_upload", 2);
        return DBOperation.instanse.updateTableData(TableSQL.USER_LOCATION_NAME, " user_id = ? and project_id = ?", new String[] {userId + "", projectId + ""}, values);
    }

    public static boolean insert(Float lat,Float lon,String address,Long createTime,Integer userId,Integer projectId){
        ContentValues values = new ContentValues();
        values.put("user_id",userId);
        values.put("project_id",projectId);
        values.put("lat",lat);
        values.put("lon",lon);
        values.put("address",address);
        values.put("create_time",createTime);
        return DBOperation.instanse.insertTableData(TableSQL.USER_LOCATION_NAME, values);
    }

}
