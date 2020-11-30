package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;
import com.ringdata.ringinterview.capi.components.data.model.User;

/**
 * Created by admin on 17/11/11.
 */

public class UserDao {
    public static User query(String username, String password) {
        String sql = "select * from " + TableSQL.USER_NAME + " where name = ? and password = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{username, password});
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        User user = new User();
        cursor.moveToFirst();
        user.setId(cursor.getInt(cursor.getColumnIndex("id")));
        user.setName(cursor.getString(cursor.getColumnIndex("name")));
        user.setRole(cursor.getInt(cursor.getColumnIndex("role")));
        user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
        user.setAvatarPath(cursor.getString(cursor.getColumnIndex("avatar_path")));
        user.setOrgCode(cursor.getString(cursor.getColumnIndex("org_code")));
        user.setOrgHost(cursor.getString(cursor.getColumnIndex("org_host")));
        cursor.close();
        return user;
    }

    public static User query(Integer userId) {
        String sql = "select * from " + TableSQL.USER_NAME + " where id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        User user = new User();
        cursor.moveToFirst();
        user.setId(cursor.getInt(cursor.getColumnIndex("id")));
        user.setName(cursor.getString(cursor.getColumnIndex("name")));
        user.setRole(cursor.getInt(cursor.getColumnIndex("role")));
        user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
        user.setAvatarPath(cursor.getString(cursor.getColumnIndex("avatar_path")));
        user.setOrgCode(cursor.getString(cursor.getColumnIndex("org_code")));
        user.setOrgHost(cursor.getString(cursor.getColumnIndex("org_host")));
        cursor.close();
        return user;
    }

    public static boolean replace(User user) {
        ContentValues values = obj2CV(user);
        return DBOperation.instanse.replaceTableData(TableSQL.USER_NAME, values);
    }

    public static boolean deleteUser(Integer userId) {
        DBOperation.db.beginTransaction();
        Boolean isSuccess = DBOperation.instanse.deleteTableData(TableSQL.USER_NAME, "id = ?", new String[]{userId + ""});
        DBOperation.db.setTransactionSuccessful();
        DBOperation.db.endTransaction();
        return isSuccess;
    }

    private static ContentValues obj2CV(User user) {
        ContentValues values = new ContentValues();
        values.put("id", user.getId());
        if (user.getName() != null) {
            values.put("name", user.getName());
        }
        if (user.getId() != null) {
            values.put("role", user.getRole());
        }
        if (user.getPassword() != null) {
            values.put("password", user.getPassword());
        }
        if (user.getAvatarPath() != null) {
            values.put("avatar_path", user.getAvatarPath());
        }
        if (user.getOrgCode() != null) {
            values.put("org_code", user.getOrgCode());
        }
        if (user.getOrgHost() != null) {
            values.put("org_host", user.getOrgHost());
        }
        return values;
    }
}
