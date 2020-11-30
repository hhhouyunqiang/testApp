package com.ringdata.ringinterview.capi.components.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by admin on 17/11/11.
 */

public class DBOperation extends SQLiteOpenHelper {
    private static final int DB_VERSION = 3;  //数据库版本号
    private AtomicInteger mOpenCounter = new AtomicInteger();

    public static SQLiteDatabase db;
    public static DBOperation instanse;

    private DBOperation(Context context) {
        // super(context, TableSQL.DB_PATH + TableSQL.DB_NAME, null, DB_VERSION);
        super(context, TableSQL.DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.e("onUpgrade", "onUpgrade: " + oldVersion + " " + newVersion);
        this.db = sqLiteDatabase;
        switch (oldVersion) {
            case 1://1->2
                sqLiteDatabase.beginTransaction();
                try {
                    String sql = "Alter table " + TableSQL.RESPONSE_NAME + " add column welcome_text text,end_text text";
                    sqLiteDatabase.execSQL(sql);
                    sqLiteDatabase.setTransactionSuccessful();
                } finally {
                    sqLiteDatabase.endTransaction();
                }
                break;
        }
    }

    public static void init(Context context) {
        //自定义数据库路径需要手动创建数据库
        //FileUtils.createOrExistsFile(TableSQL.DB_PATH + TableSQL.DB_NAME);
        instanse = new DBOperation(context);
    }


    //自定义数据库路径
//    public SQLiteDatabase openDataBaseCustomPath() throws SQLException {
//        if (mOpenCounter.incrementAndGet() == 1) {
//            db = SQLiteDatabase.openOrCreateDatabase(TableSQL.DB_PATH + TableSQL.DB_NAME, null);
//        }
//        return db;
//    }


    public synchronized SQLiteDatabase openDataBase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            try {
                db = getWritableDatabase();//获取可写数据库
            } catch (SQLException e) {
                db = getReadableDatabase();//获取只读数据库
            }
        }
        return db;
    }

    //关闭数据库
    public synchronized void closeDataBase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            db.close();
        }
    }

    public void createLocalTables(HashMap<String, String> tableHashMap) {
        Iterator iter = tableHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();
            String table = entry.getValue().toString();
            db.execSQL(table);
        }
    }

    public void deleteLocalTables(HashMap<String, String> tableHashMap) {
        Iterator iter = tableHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();

            String dropSql = "drop table if exists " + key;
            db.execSQL(dropSql);
        }
    }


    public synchronized ArrayList<HashMap<String, String>> selectRow(String sql, String[] selectionArgs) {
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        Cursor c = null;
        try {
            c = db.rawQuery(sql, selectionArgs);
            if (c != null) {
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    for (int i = 0; i < c.getColumnCount(); i++) {
                        map.put(c.getColumnName(i), c.getString(i));
                    }
                    result.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }


    public synchronized ArrayList<HashMap<String, Object>> selectAllRow(String tableName, String fieldList, String orderBy) {
        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        Cursor cur = null;
        String[] arrFieldList = null;
        if (fieldList != "*" && fieldList.length() > 0)
            arrFieldList = fieldList.split(",");

        try {
            cur = db.query(tableName, arrFieldList, null, null, null, null, orderBy);
            HashMap<String, String> map = new HashMap<String, String>();
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                for (int i = 0; i < cur.getColumnCount(); i++)
                    map.put(cur.getColumnName(i), cur.getString(i));
                result.add((HashMap) map.clone());
                cur.moveToNext();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        return result;
    }


    public synchronized boolean isExists(String sql) {
        boolean result = false;
        Cursor c = null;
        try {
            c = db.rawQuery(sql, null);
            if (null != c) {
                if (c.moveToNext()) {
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }


    public synchronized boolean insertTableData(String tableName, ContentValues values) {
        boolean bool = false;
        long result = db.insert(tableName, null, values);
        if (result > 0) {
            bool = true;
        }
        return bool;
    }

    public synchronized boolean replaceTableData(String tableName, ContentValues values) {
        boolean bool = false;
        long result = db.replace(tableName, null, values);
        if (result > 0) {
            bool = true;
        }
        return bool;
    }


    public synchronized boolean updateTableData(String tableName, String whereClause, String[] whereArgs, ContentValues values) {
        boolean bool = false;
        int result = db.update(tableName, values, whereClause, whereArgs);
        if (result > 0) {
            bool = true;
        }
        return bool;
    }

    public synchronized boolean deleteTableData(String tableName, String whereClause, String[] whereArgs) {
        boolean bool = false;
        int result = db.delete(tableName, whereClause, whereArgs);
        if (result > 0) {
            bool = true;
        }
        return bool;
    }

    public static void clearTable(String[] tableName) {
        for (int i = 0; i < tableName.length; i++) {
            db.delete(tableName[i], null, null);
        }
    }


    public synchronized boolean isExist(String tableName, String primaryKey, String primaryValue) {
        boolean isExist = false;
        String sql = " select * from " + tableName + " where " + primaryKey + " = " + primaryValue;
        Cursor c = null;
        try {
            c = db.rawQuery(sql, null);
            if (c != null) {
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    isExist = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return isExist;
    }


    /**
     * 检测表中某个字段是否存在
     *
     * @param tableName
     * @param columnName
     * @return
     */
    public boolean checkColumnExist(String tableName, String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            //查询一行
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0"
                    , null);
            result = cursor != null && cursor.getColumnIndex(columnName) != -1;
        } catch (Exception e) {
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }
}
