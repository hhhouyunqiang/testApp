package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 17/11/29.
 */

public class SyncLogDao {

    /**
     * 插入同步记录
     */
    public static boolean insert(HashMap<String, String> hm) {
        ContentValues values = new ContentValues();
        Iterator iterator = hm.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            values.put(key, value);
        }
        return DBOperation.instanse.insertTableData(TableSQL.SYNC_LOG_NAME, values);
    }

    /**
     * 获取上次同步成功的时间
     */
    public static long queryLastSyncTime(int type, Integer userId, Integer projectId) {
        String sql = "";
        Cursor cursor;
        if (projectId == 1) {
            sql = "select max(sync_time) as sync_time from " + TableSQL.SYNC_LOG_NAME + " where type = ? and user_id = ? and success = 1";
            cursor = DBOperation.db.rawQuery(sql, new String[]{type + "", userId + ""});
        } else {
            sql = "select max(sync_time) as sync_time from " + TableSQL.SYNC_LOG_NAME + " where type = ? and user_id = ? and project_id = ? and success = 1";
            cursor = DBOperation.db.rawQuery(sql, new String[]{type + "", userId + "", projectId + ""});
        }
        long time = 0L;
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            time = cursor.getLong(cursor.getColumnIndex("sync_time"));
            Log.i("time", time + "");
        }
        cursor.close();
        return time;
    }

    /**
     * 获取新插入同步记录的id
     */
    public static int queryLastSyncId() {
        String sql = "select last_insert_rowid() from " + TableSQL.SYNC_LOG_NAME;
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{});
        int id = -1;
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    public static boolean update(int id, long syncTime, Integer success) {
        ContentValues values = new ContentValues();
        values.put("sync_time", syncTime);
        values.put("success", success);

        return DBOperation.instanse.updateTableData(TableSQL.SYNC_LOG_NAME, " id = ?", new String[]{id + ""}, values);
    }

    //获取全部的数据同步记录
    public static List<HashMap<String, Object>> queryList(Integer userId) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        String sql = "select * from " + TableSQL.SYNC_LOG_NAME + " where user_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        while (cursor.moveToNext()) {
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("id", cursor.getInt(cursor.getColumnIndex("id")));
            hm.put("type", cursor.getInt(cursor.getColumnIndex("type")));
            hm.put("time", cursor.getLong(cursor.getColumnIndex("sync_time")));

            list.add(hm);
        }
        cursor.close();
        return list;
    }
}
