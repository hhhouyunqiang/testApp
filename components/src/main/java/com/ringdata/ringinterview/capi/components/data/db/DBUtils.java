package com.ringdata.ringinterview.capi.components.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Xie Chenghao on 16/8/29.
 */
public class DBUtils {

    static final public CursorReader<Void> LOG_READER = new CursorReader<Void>() {
        @Override
        public Void read(int row, Cursor c) {
            int n = c.getColumnCount();
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < n; ++i) {
                builder.append(c.getColumnName(i)).append( " = ");
                String value = null;
                switch (c.getType(i)) {
                    case Cursor.FIELD_TYPE_STRING:
                        value = "\"" + c.getString(i) + "\"";
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        value = "" + c.getInt(i);
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        value = "<BLOB>";
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        value = "" + c.getFloat(i);
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        value = "<NULL>";
                        break;
                }
                builder.append(value).append(" , ");
            }
            Log.d("CAPI", "Row " + row + " - ["+ builder.toString() + "]");
            return null;
        }
    };

    static final public CursorReader<ContentValues> CONTENTVALUES_READER = new CursorReader<ContentValues>() {
        @Override
        public ContentValues read(int row, Cursor c) {
            ContentValues cv = new ContentValues();
            int n = c.getColumnCount();
            for(int i = 0; i < n; ++i) {
                String name = c.getColumnName(i);
                switch (c.getType(i)) {
                    case Cursor.FIELD_TYPE_STRING:
                        cv.put(name, c.getString(i));
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        cv.put(name, c.getInt(i));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        cv.put(name, c.getBlob(i));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        cv.put(name, c.getFloat(i));
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        cv.putNull(name);
                        break;
                }
            }
            return cv;
        }
    };


    static final public CursorReader<ArrayValues> ARRAYVALUES_READER = new CursorReader<ArrayValues>() {
        @Override
        public ArrayValues read(int row, Cursor c) {
            int n = c.getColumnCount();
            ArrayValues cv = new ArrayValues(n);
            for(int i = 0; i < n; ++i) {
                switch (c.getType(i)) {
                    case Cursor.FIELD_TYPE_STRING:
                        cv.set(i, c.getString(i));
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        cv.set(i, c.getInt(i));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        cv.set(i, c.getBlob(i));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        cv.set(i, c.getFloat(i));
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        cv.set(i, null);
                        break;
                }
            }
            return cv;
        }
    };

    static final public CursorReader<String> STRING_READER = new CursorReader<String>() {
        @Override
        public String read(int row, Cursor c) {
            return c.getString(0);
        }
    };


    static public interface CursorReader<T> {
        public T read(int row, Cursor c);
    }

    static public void logDBError(String msg, Exception e) {
        Log.e("CAPI", "DB Error : " + msg, e);
    }

    static public void createTable(SQLiteDatabase db, String tablename, String[] columns) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + tablename + " (" + TextUtils.join(",", columns) + ")");
        } catch (SQLException e) {
            logDBError("Failed to create table " + tablename, e);
        }

    }

    static public long insert(SQLiteDatabase db, String tablename, ContentValues values) {
        long rid = -1;
        try {
            rid = db.insert(tablename, null, values);
        } catch (Exception e) {
            logDBError("Failed to insert " + tablename, e);
        }
        return rid;
    }

    static public void insert(SQLiteDatabase db, String table, String[] cols, Object[] values) {
        try {
            String[] params = new String[cols.length];
            Arrays.fill(params, "?");
            String sql = "INSERT INTO "+table + "(" + TextUtils.join(",", cols) + ") VALUES("+TextUtils.join(",", params)+")";
            Log.d("LAMI", "INSERT WITH SQL : " + sql);
            db.execSQL(sql, values);
        } catch (Exception e) {
            logDBError("Failed to insert " + table, e);
        }
    }

    static public void update(SQLiteDatabase db, String table, ContentValues v, String field, String value) {
        db.update(table, v, field + " = ?", new String[]{value});
    }

    static public void delete(SQLiteDatabase db, String table, String field, String value) {
        db.delete(table,  field + " = ?", new String[]{value});
    }
    static public void delete(SQLiteDatabase db, String table, String where) {
        db.delete(table, where, null);
    }

    static public void delete(SQLiteDatabase db, String table, String where, String[] args) {
        db.delete(table, where, args);
    }

    static public Cursor query(SQLiteDatabase db, String sql) {
        Cursor c = null;
        try {
            c = db.rawQuery(sql, null);
        } catch (Exception e) {
            logDBError("Failed to exec query " + sql, e);
        }
        return c;
    }

    static public <T> List<T> queryList(SQLiteDatabase db, CursorReader<T> reader, String sql, String arg) {
        return queryList(db, reader, sql, new String[]{arg});
    }


    static public <T> List<T> queryList(SQLiteDatabase db, CursorReader<T> reader, String sql, String[] args) {
        List<T> results = new ArrayList<>();
        Cursor c = db.rawQuery(sql, args);
        int r = 0;
        while (c.moveToNext()) {
            T obj = null;
            try {
                obj = reader.read(r++, c);
            } catch (Exception e) {
                logDBError("Failed to read data from cursor", e);
            }
            if(obj != null) {
                results.add(obj);
            }
        }
        return results;
    }

    static public <T> T queryObject(SQLiteDatabase db, CursorReader<T> reader, String sql, String arg) {
        return queryObject(db, reader, sql, new String[]{arg});
    }

    static public <T> T queryObject(SQLiteDatabase db, CursorReader<T> reader, String sql, String[] args) {
        T obj = null;
        Cursor c = db.rawQuery(sql, args);
        if(c.moveToNext()) {
            try {
                obj = reader.read(0, c);
            } catch (Exception e) {
                logDBError("Failed to read data from cursor", e);
            }
        }
        return obj;
    }

    static public ContentValues queryContentValue(SQLiteDatabase db, String sql, String arg) {
        return queryObject(db, CONTENTVALUES_READER, sql, new String[]{arg});
    }

    static public ContentValues queryContentValue(SQLiteDatabase db, String sql, String[] args) {
        return queryObject(db, CONTENTVALUES_READER, sql, args);
    }

    static public List<ContentValues> queryContentValues(SQLiteDatabase db, String sql, String arg) {
        return queryList(db, CONTENTVALUES_READER, sql, new String[]{arg});
    }

    static public List<ContentValues> queryContentValues(SQLiteDatabase db, String sql, String[] args) {
        return queryList(db, CONTENTVALUES_READER, sql, args);
    }

    static public void printQueryResult(SQLiteDatabase db, String sql, String[] args) {
        Log.d("CAPI", "Query : " +sql);
        Log.d("CAPI", "********************");
        queryList(db, LOG_READER, sql, args);
        Log.d("CAPI", "********************");
    }

    static public List<String> listTables(SQLiteDatabase db) {
        List<String> tables = new ArrayList<>();
        Cursor c = DBUtils.query(db, "SELECT name FROM my_db.sqlite_master WHERE type='table'");
        if(c != null) {
            while (c.moveToNext()) {
                tables.add(c.getString(0));
            }
        }
        return tables;
    }

}
