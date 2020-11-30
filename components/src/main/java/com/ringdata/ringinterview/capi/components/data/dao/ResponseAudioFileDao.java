package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 17/12/5.
 */

public class ResponseAudioFileDao {

    public static boolean insert(HashMap<String, Object> hm) {
        String filename = (String) hm.get("local_filename");
        if (TextUtils.isEmpty(filename)) {
            return true;
        }
        ContentValues values = new ContentValues();
        values.put("user_id", (Integer) hm.get("user_id"));
        values.put("interview_id", (Integer) hm.get("user_id"));
        values.put("project_id", (Integer) hm.get("project_id"));
        values.put("response_guid", (String) hm.get("response_guid"));
        values.put("question_id", (Integer) hm.get("question_id"));
        values.put("local_filename", filename);
        values.put("audio_name", filename);
        values.put("file_path", (String) hm.get("file_path"));
        values.put("type", (Integer) hm.get("type"));
        values.put("start_time", (Long) hm.get("start_time"));
        values.put("end_time", (Long) hm.get("end_time"));
        values.put("audio_duration", (Long) hm.get("audio_duration"));
        values.put("begin_pos", (Long) hm.get("begin_pos"));
        values.put("end_pos", (Long) hm.get("end_pos"));
        values.put("is_upload", 1);
        values.put("create_time", (Long) hm.get("create_time"));
        return DBOperation.instanse.insertTableData(TableSQL.RESPONSE_AUDIO_FILE_NAME, values);
    }

    /**
     * 查询未上传的所有数据
     *
     * @param userId
     * @return
     */
    public static List<HashMap<String, Object>> queryNoUploadList(Integer userId, String responseId) {
        String sql = "select * from " + TableSQL.RESPONSE_AUDIO_FILE_NAME + " where is_upload = ? and user_id = ? and response_guid = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{1 + "", userId + "", responseId});
        List<HashMap<String, Object>> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("userId", cursor.getInt(cursor.getColumnIndex("user_id")));
            hm.put("interviewId", cursor.getInt(cursor.getColumnIndex("interview_id")));
            hm.put("projectId", cursor.getInt(cursor.getColumnIndex("project_id")));
            hm.put("responseGuid", cursor.getString(cursor.getColumnIndex("response_guid")));
            hm.put("questionId", cursor.getInt(cursor.getColumnIndex("question_id")));
            hm.put("localFilename", cursor.getString(cursor.getColumnIndex("local_filename")));
            hm.put("filePath", cursor.getString(cursor.getColumnIndex("file_path")));
            hm.put("audioName", cursor.getString(cursor.getColumnIndex("audio_name")));
            hm.put("type", cursor.getString(cursor.getColumnIndex("type")));
            hm.put("startTime", cursor.getString(cursor.getColumnIndex("start_time")));
            hm.put("endTime", cursor.getString(cursor.getColumnIndex("end_time")));
            hm.put("audioDuration", cursor.getString(cursor.getColumnIndex("audio_duration")));
            hm.put("beginPos", cursor.getInt(cursor.getColumnIndex("begin_pos")));
            hm.put("endPos", cursor.getInt(cursor.getColumnIndex("end_pos")));
            hm.put("createTime", cursor.getString(cursor.getColumnIndex("create_time")));
            list.add(hm);
        }
        cursor.close();
        return list;
    }

    //统计用户未上传的答卷文件的数量
    public static Integer countNoUploadFile(Integer userId) {
        String sql = "select count(*) as count from " + TableSQL.RESPONSE_AUDIO_FILE_NAME + " where user_id = ? and is_upload_file = 1";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public static boolean updateUploadStatus(Integer userId, String responseId) {
        ContentValues values = new ContentValues();
        values.put("is_upload", 2);
        return DBOperation.instanse.updateTableData(TableSQL.RESPONSE_AUDIO_FILE_NAME, " user_id = ? and response_guid = ?", new String[]{userId + "", responseId}, values);
    }

    public static boolean updateUploadFileStatus(Integer userId, String filename) {
        ContentValues values = new ContentValues();
        values.put("is_upload_file", 2);
        return DBOperation.instanse.updateTableData(TableSQL.RESPONSE_AUDIO_FILE_NAME, " local_filename = ? and user_id = ?", new String[]{filename + "", userId + ""}, values);
    }

    public static HashMap<String, Object> queryNoUpload(Integer userId, Integer projectId) {
        String sql = "select * from " + TableSQL.RESPONSE_AUDIO_FILE_NAME + " where is_upload_file = 1 and user_id = ? and project_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", projectId + ""});
        HashMap<String, Object> hm = null;
        if (cursor.moveToFirst()) {
            hm = new HashMap<>();
            Integer surveyId = cursor.getInt(cursor.getColumnIndex("project_id"));
            String filename = cursor.getString(cursor.getColumnIndex("local_filename"));
            hm.put("filename", filename);
            hm.put("surveyId", surveyId);
        }
        cursor.close();
        return hm;
    }

}
