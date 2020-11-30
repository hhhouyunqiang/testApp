package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.blankj.utilcode.util.FileUtils;
import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by admin on 17/12/1.
 */

public class ResponseQuestionFileDao {

    //插入本地文件
    public static boolean insertLocalFile(Integer userId, Integer surveyId, String responseId, String questionId, String filename, String filepath, String fileType, Long createTime) {
        if (TextUtils.isEmpty(filename)) {
            return true;
        }
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("project_id", surveyId);
        values.put("response_guid", responseId);
        values.put("question_id", questionId);
        values.put("local_filename", filename);
        values.put("file_name", filename);
        values.put("file_path", filepath);
        values.put("file_type", fileType);
        values.put("is_upload", 1);//本地数据默认需要上传
        values.put("is_upload_file", 1);//本地数据默认需要上传
        values.put("is_file_download_success", 2);//本地的数据不需要下载
        values.put("create_time", createTime);
        return DBOperation.instanse.insertTableData(TableSQL.RESPONSE_QUESTION_FILE_NAME, values);
    }

    /**
     * 查询未上传的所有数据（同步答卷时）
     *
     * @param userId
     * @return
     */
    public static List<HashMap<String, Object>> queryNoUploadList(Integer userId, String responseId) {
        String sql = "select * from " + TableSQL.RESPONSE_QUESTION_FILE_NAME + " where is_upload = 1 and user_id = ? and response_guid = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", responseId});
        List<HashMap<String, Object>> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("userId", cursor.getInt(cursor.getColumnIndex("user_id")));
            hm.put("projectId", cursor.getInt(cursor.getColumnIndex("project_id")));
            hm.put("responseGuid", cursor.getString(cursor.getColumnIndex("response_guid")));
            hm.put("questionId", cursor.getString(cursor.getColumnIndex("question_id")));
            hm.put("filename", cursor.getString(cursor.getColumnIndex("local_filename")));
            hm.put("fileType", cursor.getString(cursor.getColumnIndex("file_type")));
            hm.put("filePath", cursor.getString(cursor.getColumnIndex("file_path")));
            hm.put("createTime", cursor.getString(cursor.getColumnIndex("create_time")));

            list.add(hm);
        }
        cursor.close();
        return list;
    }

    /**
     * 查询某项目未上传的数据
     */
    public static List<String> queryNoUploadFilenameList(int userId, Integer surveyId) {
        String sql = "select * from " + TableSQL.RESPONSE_QUESTION_FILE_NAME + " where is_upload_file = ? and user_id = ? and project_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{1 + "", userId + "", surveyId + ""});
        List<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String filename = cursor.getString(cursor.getColumnIndex("local_filename"));
            list.add(filename);
        }
        cursor.close();
        return list;
    }

    public static void delete(int userId, String filename) {
        DBOperation.instanse.deleteTableData(TableSQL.RESPONSE_QUESTION_FILE_NAME, "local_filename = ? and user_id = ?", new String[]{filename, userId + ""});
    }

    /**
     * 查询未上传的所有数据（同步图片时）
     *
     * @param userId
     * @return
     */
    public static HashMap<String, Object> queryNoUpload(Integer userId, Integer projectId) {
        String sql = "select * from " + TableSQL.RESPONSE_QUESTION_FILE_NAME + " where is_upload_file = 1 and user_id = ? and project_id = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", projectId + ""});
        HashMap<String, Object> hm = null;
        if (cursor.moveToFirst()) {
            hm = new HashMap<>();
            String surveyId = cursor.getString(cursor.getColumnIndex("project_id"));
            String filename = cursor.getString(cursor.getColumnIndex("local_filename"));
            hm.put("filename", filename);
            hm.put("surveyId", surveyId);
        }
        cursor.close();
        return hm;
    }

    //统计用户未上传的答卷文件的数量
    public static Integer countNoUploadFile(Integer userId) {
        String sql = "select count(*) as count from " + TableSQL.RESPONSE_QUESTION_FILE_NAME + " where user_id = ? and is_upload_file = 1";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public static boolean updateUploadStatus(Integer userId, String responseId) {
        ContentValues values = new ContentValues();
        values.put("is_upload", 2);
        return DBOperation.instanse.updateTableData(TableSQL.RESPONSE_QUESTION_FILE_NAME, " user_id = ? and response_guid = ? ", new String[]{userId + "", responseId}, values);
    }

    public static boolean updateUploadFileStatus(Integer userId, String filename) {
        ContentValues values = new ContentValues();
        values.put("is_upload_file", 2);
        return DBOperation.instanse.updateTableData(TableSQL.RESPONSE_QUESTION_FILE_NAME, " local_filename = ? and user_id = ?", new String[]{filename + "", userId + ""}, values);
    }

    //插入待下载的文件数据
    public static boolean insertOrUpdateList(List<HashMap<String, Object>> list, Integer userId) {
        Boolean isSuccess = true;
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, Object> hm = list.get(i);
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("file_url", hm.get("fileUrl") + "");
            values.put("is_upload", 2);
            values.put("is_upload_file", 2);
            String filename = hm.get("filename") + "";
            values.put("local_filename", filename);
            isSuccess = DBOperation.instanse.updateTableData(TableSQL.RESPONSE_QUESTION_FILE_NAME, "local_filename = ? and user_id = ?", new String[]{filename, userId + ""}, values);
            if (!isSuccess) {
                isSuccess = DBOperation.instanse.insertTableData(TableSQL.RESPONSE_QUESTION_FILE_NAME, values);
            }
        }
        return isSuccess;
    }

    //获取未下载或者下载失败的url地址
    public static String getFileUrl(Integer userId) {
        String sql = "select file_url from " + TableSQL.RESPONSE_QUESTION_FILE_NAME + " where user_id = ? and is_file_download_success = 1";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "",});
        String fileurl = null;
        if (cursor.moveToFirst()) {
            fileurl = cursor.getString(cursor.getColumnIndex("file_url"));
        }
        cursor.close();
        return fileurl;
    }

    //下载成功更新数据状态
    public static boolean updateDownloadStatus(String localFilename) {
        ContentValues values = new ContentValues();
        values.put("is_file_download_success", 2);
        return DBOperation.instanse.updateTableData(TableSQL.RESPONSE_QUESTION_FILE_NAME, " local_filename = ?", new String[]{localFilename + ""}, values);
    }

    //解析从服务器下载的图片路径数据
    public static List<HashMap<String, Object>> getListFromNet(JSONArray dataArray) {
        final List<HashMap<String, Object>> list = new LinkedList<>();
        try {
            if (dataArray == null) {
                return list;
            }
            final int size = dataArray.size();
            for (int i = 0; i < size; i++) {
                String fileUrl = dataArray.getString(i);
                HashMap<String, Object> hm = new HashMap<>();
                String filename = FileUtils.getFileName(fileUrl);
                hm.put("fileUrl", fileUrl);
                hm.put("filename", filename);
                list.add(hm);
            }
        } catch (Exception e) {
        }
        return list;
    }
}
