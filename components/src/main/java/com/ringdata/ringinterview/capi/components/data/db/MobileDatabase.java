package com.ringdata.ringinterview.capi.components.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xch on 2017/10/17.
 */

public class MobileDatabase extends SQLiteOpenHelper {
    final static int DB_LATEST_VERSION = 1;
    final static String DB_NAME = "capi_mobile";
    public int _version;

    public MobileDatabase(Context context) {
        this(context, DB_NAME, DB_LATEST_VERSION);
    }

    public MobileDatabase(Context context, String name, int version) {
        super(context, name, null, version);
        _version = version;
    }

//    public void setGson(Gson gson) {
//        _gson = gson;
//    }

    static void createTablesVersion1(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        switch (_version) {
            case 1:
                createTablesVersion1(sqLiteDatabase);
                break;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }


    public long insert(String table, ContentValues v) {
        return DBUtils.insert(getWritableDatabase(), table, v);
    }

    public void update(String table, ContentValues v, String where, String[] args) {
        getWritableDatabase().update(table, v, where, args);
    }

    public void update(String table, ContentValues v, String field, String value) {
        DBUtils.update(getWritableDatabase(), table, v, field, value);
    }

    public void delete(String table, String field, String arg) {
        DBUtils.delete(getWritableDatabase(), table, field, arg);
    }

    public void delete(String table, String where, String[] args) {
        DBUtils.delete(getWritableDatabase(), table, where, args);
    }

    public ArrayValues queryArrayValues(String sql, String arg) {
        return queryArrayValues(sql, new String[]{arg});
    }

    public ArrayValues queryArrayValues(String sql, String[] args) {
        return DBUtils.queryObject(getReadableDatabase(),
                DBUtils.ARRAYVALUES_READER, sql, args);
    }

    public List<String> queryStringValues(String sql, String[] args) {
        return DBUtils.queryList(getReadableDatabase(),
                DBUtils.STRING_READER, sql, args);
    }

    public <T> List<T> queryJSONObject(Class<T> clazz, String sql, Object arg) {
        return queryJSONObject(clazz, sql, arg == null ? null : new String[]{arg.toString()});
    }

    public <T> List<T> queryJSONObject(Class<T> clazz, String sql,String[] args) {
        List<T> results = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(sql, args);
        int r = 0;
        while (c.moveToNext()) {
            T obj = null;
            try {
                //obj = _gson.fromJson(c.getString(0), clazz);
            } catch (Exception e) {
                DBUtils.logDBError("Failed to read data from cursor", e);
            }
            if(obj != null) {
                results.add(obj);
            }
        }
        return results;
    }

    public void beginTransaction() {
        getWritableDatabase().beginTransaction();
    }

    public void endTransaction(boolean succ) {
        SQLiteDatabase db = getWritableDatabase();
        if(succ) {
            db.setTransactionSuccessful();
        }
        db.endTransaction();
    }

    public List<ContentValues> queryContentValues(String sql, String[] args) {
        return DBUtils.queryContentValues(getReadableDatabase(), sql, args);
    }

    public void printQuery(String sql, String[] args) {
        DBUtils.printQueryResult(getReadableDatabase(), sql, args);
    }

    public void printTable(String table) {
        printQuery("SELECT * FROM " + table, null);
    }
}
