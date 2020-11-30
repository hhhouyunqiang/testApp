package com.ringdata.ringinterview.capi.components.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.util.GUIDUtil;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.DBOperation;
import com.ringdata.ringinterview.capi.components.data.TableSQL;
import com.ringdata.ringinterview.capi.components.data.model.Sample;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 17/11/16.
 */

public class SampleDao {

    private static Sample cursor2Obj(Cursor cursor) {
        Sample sample = new Sample();
        sample.setId(cursor.getInt(cursor.getColumnIndex("id")));
        sample.setSampleGuid(cursor.getString(cursor.getColumnIndex("sample_guid")));
        sample.setName(cursor.getString(cursor.getColumnIndex("name")));
        sample.setCode(cursor.getString(cursor.getColumnIndex("code")));
        sample.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
        sample.setSurveyId(cursor.getInt(cursor.getColumnIndex("survey_id")));

        sample.setGender(cursor.getString(cursor.getColumnIndex("gender")));
        sample.setEmail(cursor.getString(cursor.getColumnIndex("email")));
        sample.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
        sample.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));

        sample.setProvince(cursor.getString(cursor.getColumnIndex("province")));
        sample.setCity(cursor.getString(cursor.getColumnIndex("city")));
        sample.setDistrict(cursor.getString(cursor.getColumnIndex("district")));
        sample.setTown(cursor.getString(cursor.getColumnIndex("town")));
        sample.setAddress(cursor.getString(cursor.getColumnIndex("address")));
        sample.setOrganization(cursor.getString(cursor.getColumnIndex("organization")));
        sample.setLon(cursor.getFloat(cursor.getColumnIndex("lon")));
        sample.setLat(cursor.getFloat(cursor.getColumnIndex("lat")));

        sample.setExtraData(cursor.getString(cursor.getColumnIndex("extra_data")));
        sample.setResponseVariable(cursor.getString(cursor.getColumnIndex("response_variable")));
        sample.setDisplayName(cursor.getString(cursor.getColumnIndex("display_name")));
        if (!cursor.isNull(cursor.getColumnIndex("start_time"))) {
            sample.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
        }
        if (!cursor.isNull(cursor.getColumnIndex("end_time"))) {
            sample.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
        }
        sample.setInterviewerId(cursor.getInt(cursor.getColumnIndex("interviewer_id")));
        sample.setAssignmentType(cursor.getInt(cursor.getColumnIndex("assignment_type")));
        sample.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        if (!cursor.isNull(cursor.getColumnIndex("appoint_visit_time"))) {
            sample.setAppointVisitTime(cursor.getLong(cursor.getColumnIndex("appoint_visit_time")));
        }
        sample.setDescription(cursor.getString(cursor.getColumnIndex("description")));

        sample.setCustom1(cursor.getString(cursor.getColumnIndex("custom1")));
        sample.setCustom2(cursor.getString(cursor.getColumnIndex("custom2")));
        sample.setCustom3(cursor.getString(cursor.getColumnIndex("custom3")));
        sample.setCustom4(cursor.getString(cursor.getColumnIndex("custom4")));
        sample.setCustom5(cursor.getString(cursor.getColumnIndex("custom5")));

        if (!cursor.isNull(cursor.getColumnIndex("create_time"))) {
            sample.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
        }
        if (!cursor.isNull(cursor.getColumnIndex("last_modify_time"))) {
            sample.setLastModifyTime(cursor.getLong(cursor.getColumnIndex("last_modify_time")));
        }
        sample.setIsAdd(cursor.getInt(cursor.getColumnIndex("is_add")));
        sample.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
        return sample;
    }

    private static ContentValues obj2contentValues(Sample sample) {
        ContentValues values = new ContentValues();
        values.put("id", sample.getId());
        values.put("sample_guid", sample.getSampleGuid());
        values.put("name", sample.getName());
        values.put("code", sample.getCode());
        values.put("user_id", sample.getUserId());
        values.put("survey_id", sample.getSurveyId());

        values.put("gender", sample.getGender());
        values.put("email", sample.getEmail());
        values.put("phone", sample.getPhone());
        values.put("mobile", sample.getMobile());

        values.put("province", sample.getProvince());
        values.put("city", sample.getCity());
        values.put("district", sample.getDistrict());
        values.put("town", sample.getTown());
        values.put("address", sample.getAddress());
        values.put("organization", sample.getOrganization());
        values.put("lon", sample.getLon());
        values.put("lat", sample.getLat());

        values.put("extra_data", sample.getExtraData());
        values.put("response_variable", sample.getResponseVariable());
        values.put("display_name", sample.getDisplayName());
        values.put("start_time", sample.getStartTime());
        values.put("end_time", sample.getEndTime());
        values.put("interviewer_id", sample.getInterviewerId());
        values.put("assignment_type", sample.getAssignmentType());
        values.put("status", sample.getStatus());
        values.put("appoint_visit_time", sample.getAppointVisitTime());
        values.put("description", sample.getDescription());

        values.put("custom1", sample.getCustom1());
        values.put("custom2", sample.getCustom2());
        values.put("custom3", sample.getCustom3());
        values.put("custom4", sample.getCustom4());
        values.put("custom5", sample.getCustom5());

        values.put("create_time", sample.getCreateTime());
        values.put("last_modify_time", sample.getLastModifyTime());
        values.put("is_add", sample.getIsAdd());
        values.put("is_upload", sample.getIsUpload());

        return values;
    }

    public static HashMap<String, String> queryByIdAndSurveyId(Integer userId, String id, Integer surveyId) {
        String sql = "select * from " + TableSQL.SAMPLE_NAME + " where sample_guid = ? and survey_id = ? and user_id = ?";
        ArrayList<HashMap<String, String>> HMlist = DBOperation.instanse.selectRow(sql, new String[]{id + "", surveyId + "", userId + ""});
        HashMap<String, String> hm = new HashMap<>();
        if (HMlist.size() == 0) {
            return hm;
        }
        return HMlist.get(0);
    }

    public static Sample getLatestSample(Integer userId, Integer surveyId) {
        String sql = "select * from " + TableSQL.SAMPLE_NAME + " where user_id = ? and survey_id = ? and create_time is not null order by create_time desc";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", surveyId + ""});
        Sample sample = new Sample();
        if (cursor.moveToFirst()) {
            sample = cursor2Obj(cursor);
            return sample;
        }
        cursor.close();
        return sample;
    }

    public static Integer countExistSampleCode(Integer userId, Integer projectId, String code) {
        String sql = "select count(*) as count from " + TableSQL.SAMPLE_NAME + " where user_id = ? and survey_id = ? and code = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", projectId + "", code});
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //样本
    public static boolean deleteByUserId(Integer userId) {
        return DBOperation.instanse.deleteTableData(TableSQL.SAMPLE_NAME, "user_id = ? and is_upload = 2", new String[]{userId + ""});
    }

    public static boolean delete(Integer userId, Integer surveyId) {
        return DBOperation.instanse.deleteTableData(TableSQL.SAMPLE_NAME, "user_id = ? and survey_id = ? and is_upload = 2", new String[]{userId + "", surveyId + ""});
    }

    public static boolean deleteById(Integer userId, String sampleId) {
        return DBOperation.instanse.deleteTableData(TableSQL.SAMPLE_NAME, "user_id = ? and sample_guid = ?", new String[]{userId + "", sampleId + ""});
    }

    public static boolean deleteList(List<String> ids, Integer userId) {
        if (ids.size() == 0) {
            return true;
        }
        Boolean isSuccess = true;
        DBOperation.db.beginTransaction();
        for (String id : ids) {
            isSuccess = DBOperation.instanse.deleteTableData(TableSQL.SAMPLE_NAME, "sample_guid = ? and user_id = ?", new String[]{id + "", userId + ""});
//            if (!isSuccess) {
//                //有任何一个删除失败就回滚不提交
//                DBOperation.db.endTransaction();
//                return false;
//            }
        }
        DBOperation.db.setTransactionSuccessful();
        DBOperation.db.endTransaction();
        return true;
    }

    public static boolean insertOrUpdate(Sample sample) {
        ContentValues values = obj2contentValues(sample);
        boolean isSuccess = DBOperation.instanse.updateTableData(TableSQL.SAMPLE_NAME, "sample_guid = ? and user_id = ? and survey_id = ? ", new String[]{sample.getSampleGuid() + "", sample.getUserId() + "", sample.getSurveyId() + ""}, values);
        if (!isSuccess) {
            isSuccess = DBOperation.instanse.insertTableData(TableSQL.SAMPLE_NAME, values);
        }
        return isSuccess;
    }

    public static boolean insertOrUpdateList(List<MultiItemEntity> list) {
        if (list == null) {
            return false;
        }
        boolean b = true;
        for (int i = 0; i < list.size(); i++) {
            if (!insertOrUpdate((Sample) list.get(i))) {
                b = false;
            }
        }
        return b;
    }

    public static boolean insert(HashMap<String, String> hm) {
        ContentValues values = new ContentValues();
        Iterator iterator = hm.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            values.put(key, value);
        }
        return DBOperation.instanse.insertTableData(TableSQL.SAMPLE_NAME, values);
    }

    public static boolean update(HashMap<String, String> hm, String sampleId) {
        ContentValues values = new ContentValues();
        Iterator iterator = hm.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            values.put(key,value);
        }
        return DBOperation.instanse.updateTableData(TableSQL.SAMPLE_NAME, " sample_guid = ?", new String[]{sampleId}, values);
    }

    public static List<Sample> selectListForMapBySurveyId(Integer surveyId, Integer userId) {
        String sql = "select id,sa.sample_guid,user_id,name,lat,lon,phone,address from " + TableSQL.SAMPLE_NAME +
                " where survey_id = ? and user_id = ? ";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{surveyId + "", userId + ""});
        LinkedList<Sample> list = new LinkedList<>();

        while (cursor.moveToNext()) {
            Sample sample = new Sample();
            sample.setId(cursor.getInt(cursor.getColumnIndex("id")));
            sample.setSampleGuid(cursor.getString(cursor.getColumnIndex("sample_guid")));
            sample.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            sample.setName(cursor.getString(cursor.getColumnIndex("name")));
            sample.setLat(cursor.getFloat(cursor.getColumnIndex("lat")));
            sample.setLon(cursor.getFloat(cursor.getColumnIndex("lon")));
            sample.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            sample.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            list.add(sample);
        }
        cursor.close();
        return list;
    }
        //查询本地数据表样本表，答卷表基本基本信息
    public static List<MultiItemEntity> selectListBySurveyId(Integer surveyId, Integer userId) {
        String sql = "select sa.id,sa.sample_guid,sa.user_id,sa.name,sa.code,sa.display_name,sa.status,sa.create_time,sa.is_upload,sa.assignment_type,sa.appoint_visit_time," +
                "min(re.start_time) as start_time,max(re.end_time) as end_time,sum(re.response_duration) as duration " +
                "from " + TableSQL.SAMPLE_NAME + " sa " +
                "left join " + TableSQL.RESPONSE_NAME + " re on re.sample_guid = sa.sample_guid and re.user_id = sa.user_id " +
                "where sa.survey_id = ? and sa.user_id = ? " + "group by sa.sample_guid order by sa.create_time desc,sa.name desc";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{surveyId + "", userId + ""});
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            Sample sample = new Sample();
            sample.setId(cursor.getInt(cursor.getColumnIndex("id")));
            sample.setSampleGuid(cursor.getString(cursor.getColumnIndex("sample_guid")));
            sample.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            sample.setName(cursor.getString(cursor.getColumnIndex("name")));
            sample.setCode(cursor.getString(cursor.getColumnIndex("code")));
            sample.setDisplayName(cursor.getString(cursor.getColumnIndex("display_name")));
            sample.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
            sample.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
            sample.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
            sample.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
            sample.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));
            sample.setAppointVisitTime(cursor.getLong(cursor.getColumnIndex("appoint_visit_time")));
            sample.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
            sample.setAssignmentType(cursor.getInt(cursor.getColumnIndex("assignment_type")));
            list.add(sample);
        }
        cursor.close();
        return list;
    }

    public static List<MultiItemEntity> queryByKeyword(Integer surveyId, Integer userId, String keyword) {
        String sql = "select sa.id,sa.sample_guid,sa.user_id,sa.name,sa.code,sa.display_name,sa.status,sa.create_time,sa.is_upload,sa.appoint_visit_time," +
                "min(re.start_time) as start_time,max(re.end_time) as end_time,sum(re.response_duration) as duration " +
                "from " + TableSQL.SAMPLE_NAME + " sa " +
                "left join " + TableSQL.RESPONSE_NAME + " re on re.sample_guid = sa.sample_guid and re.user_id = sa.user_id " +
                "where sa.survey_id = ? and sa.user_id = ? and sa.display_name like ? group by sa.id order by sa.create_time desc,sa.name desc";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{surveyId + "", userId + "", "%" + keyword + "%"});
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            Sample sample = new Sample();
            sample.setId(cursor.getInt(cursor.getColumnIndex("id")));
            sample.setSampleGuid(cursor.getString(cursor.getColumnIndex("sample_guid")));
            sample.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            sample.setName(cursor.getString(cursor.getColumnIndex("name")));
            sample.setCode(cursor.getString(cursor.getColumnIndex("code")));
            sample.setDisplayName(cursor.getString(cursor.getColumnIndex("display_name")));
            sample.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
            sample.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
            sample.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
            sample.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
            sample.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));
            sample.setAppointVisitTime(cursor.getLong(cursor.getColumnIndex("appoint_visit_time")));
            sample.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
            sample.setAssignmentType(cursor.getInt(cursor.getColumnIndex("assignment_type")));
            list.add(sample);
        }
        cursor.close();
        return list;
    }

    public static List<MultiItemEntity> queryByStatus(Integer surveyId, Integer userId, int status) {
        String sql = "";
        Cursor cursor;
        if (status != -1) {
            sql = "select sa.id,sa.sample_guid,sa.user_id,sa.name,sa.code,sa.display_name,sa.status,sa.create_time,sa.is_upload,sa.appoint_visit_time," +
                    "min(re.start_time) as start_time,max(re.end_time) as end_time,sum(re.response_duration) as duration " +
                    "from " + TableSQL.SAMPLE_NAME + " sa " +
                "left join " + TableSQL.RESPONSE_NAME + " re on re.sample_guid = sa.sample_guid and re.user_id = sa.user_id " +
                    "where sa.survey_id = ? and sa.user_id = ? and sa.status = ? group by sa.id order by sa.create_time desc,sa.name desc";
            cursor = DBOperation.db.rawQuery(sql, new String[]{surveyId + "", userId + "", status + ""});
        } else {
            sql = "select sa.id,sa.sample_guid,sa.user_id,sa.name,sa.code,sa.display_name,sa.status,sa.create_time,sa.is_upload,sa.appoint_visit_time," +
                    "min(re.start_time) as start_time,max(re.end_time) as end_time,sum(re.response_duration) as duration " +
                    "from " + TableSQL.SAMPLE_NAME + " sa " +
                    "left join " + TableSQL.RESPONSE_NAME + " re on re.sample_guid = sa.sample_guid and re.user_id = sa.user_id " +
                    "where sa.survey_id = ? and sa.user_id = ? group by sa.id order by sa.create_time desc,sa.name desc";
            cursor = DBOperation.db.rawQuery(sql, new String[]{surveyId + "", userId + ""});
        }
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            Sample sample = new Sample();
            sample.setId(cursor.getInt(cursor.getColumnIndex("id")));
            sample.setSampleGuid(cursor.getString(cursor.getColumnIndex("sample_guid")));
            sample.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            sample.setName(cursor.getString(cursor.getColumnIndex("name")));
            sample.setCode(cursor.getString(cursor.getColumnIndex("code")));
            sample.setDisplayName(cursor.getString(cursor.getColumnIndex("display_name")));
            sample.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
            sample.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
            sample.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
            sample.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
            sample.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));
            sample.setAppointVisitTime(cursor.getLong(cursor.getColumnIndex("appoint_visit_time")));
            sample.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
            sample.setAssignmentType(cursor.getInt(cursor.getColumnIndex("assignment_type")));
            list.add(sample);
        }
        cursor.close();
        return list;
    }
    //本地数据排序 sort
    public static List<MultiItemEntity> queryByOrder(Integer surveyId, Integer userId, int sort) {
        String basicSql = "select sa.id,sa.sample_guid,sa.user_id,sa.name,sa.code,sa.display_name,sa.status,sa.create_time,sa.is_upload,sa.appoint_visit_time," +
                "min(re.start_time) as start_time,max(re.end_time) as end_time,sum(re.response_duration) as duration " +
                "from " + TableSQL.SAMPLE_NAME + " sa " +
                "left join " + TableSQL.RESPONSE_NAME + " re on re.sample_guid = sa.sample_guid and re.user_id = sa.user_id " +
                "where sa.survey_id = ? and sa.user_id = ? group by sa.id order by sa.create_time desc";
        if (sort == 1) {
            basicSql = basicSql + ",sa.name desc";
        } else if (sort == 2) {
            basicSql = basicSql + ",sa.name asc";
        } else if (sort == 3) {
            basicSql = basicSql + ",sa.code desc";
        } else if (sort == 4) {
            basicSql = basicSql + ",sa.code asc";
        } else if (sort == 5) {
            basicSql = basicSql + ",sa.start_time desc";
        } else if (sort == 6) {
            basicSql = basicSql + ",sa.start_time asc";
        }
        Cursor cursor = DBOperation.db.rawQuery(basicSql, new String[]{surveyId + "", userId + ""});
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            Sample sample = new Sample();
            sample.setId(cursor.getInt(cursor.getColumnIndex("id")));
            sample.setSampleGuid(cursor.getString(cursor.getColumnIndex("sample_guid")));
            sample.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            sample.setName(cursor.getString(cursor.getColumnIndex("name")));
            sample.setCode(cursor.getString(cursor.getColumnIndex("code")));
            sample.setDisplayName(cursor.getString(cursor.getColumnIndex("display_name")));
            sample.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
            sample.setCreateTime(cursor.getLong(cursor.getColumnIndex("create_time")));
            sample.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
            sample.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
            sample.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));
            sample.setAppointVisitTime(cursor.getLong(cursor.getColumnIndex("appoint_visit_time")));
            sample.setIsUpload(cursor.getInt(cursor.getColumnIndex("is_upload")));
            sample.setAssignmentType(cursor.getInt(cursor.getColumnIndex("assignment_type")));
            list.add(sample);
        }
        cursor.close();
        return list;
    }

    //查询本地所有的样本状态改变的样本
    public static ArrayList<HashMap<String, String>> getSampleStatusByUserId(Integer userId) {
        String sql = "select sa.id,sa.guid,sa.survey_id,sa.user_id from "
                + TableSQL.SAMPLE_NAME
                + " sa where survey_id in (select su.id from "
                + TableSQL.SURVEY_NAME + " su where su.user_id = ?)";
        return DBOperation.instanse.selectRow(sql, new String[]{userId + ""});
    }

    //统计用户未上传的样本的数量
    public static Integer countNoUpload(Integer userId) {
        String sql = "select count(*) as count from "
                + TableSQL.SAMPLE_NAME
                + " where user_id = ? and is_upload = 1";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + ""});
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //查询该项目下需要上传的样本数据
    public static List<Sample> getNeedUploadList(Integer userId, Integer surveyId) {
        List<Sample> sampleList = new ArrayList<>();
        String sql = "select sa.* from "
                + TableSQL.SAMPLE_NAME
                + " sa left join " + TableSQL.RESPONSE_NAME + " re on re.sample_id = sa.id and re.user_id = sa.user_id where sa.user_id = ? and sa.survey_id = ? and sa.is_upload = 1";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", surveyId + ""});
        while (cursor.moveToNext()) {
            sampleList.add(cursor2Obj(cursor));
        }
        cursor.close();
        return sampleList;
    }

    //查询账号下需要上传的样本（新增样本，样本状态改变的样本）
    public static List<Sample> getNeedUploadListByUserId(Integer userId, Integer surveyId) {
        List<Sample> sampleList = new ArrayList<>();
//        String sql = "select sa.* from "
//                + TableSQL.SAMPLE_NAME
//                + " sa left join " + TableSQL.RESPONSE_NAME + " re on re.sample_guid = sa.sample_guid and re.user_id = sa.user_id where sa.user_id = ? and sa.is_upload = 1 and re.id != ''";
        String sql2 = "select * from "
                + TableSQL.SAMPLE_NAME
                + " where user_id = ? and survey_id = ? and is_upload = 1";
        Cursor cursor = DBOperation.db.rawQuery(sql2, new String[]{userId + "", surveyId + ""});
        while (cursor.moveToNext()) {
            sampleList.add(cursor2Obj(cursor));
        }
        cursor.close();
        return sampleList;
    }

    //更新本地样本提交状态
    public static boolean updateUploadStatus(Integer userId) {
        ContentValues values = new ContentValues();
        values.put("is_upload", 2);
        return DBOperation.instanse.updateTableData(TableSQL.SAMPLE_NAME, " user_id = ?", new String[]{userId + ""}, values);
    }

    public static Sample getById(Integer userId, String sampleId) {
        String sql = "select * from " + TableSQL.SAMPLE_NAME + " where user_id = ? and sample_guid = ?";
        Cursor cursor = DBOperation.db.rawQuery(sql, new String[]{userId + "", sampleId + ""});
        Sample sample = new Sample();
        while (cursor.moveToNext()) {
            sample = cursor2Obj(cursor);
        }
        cursor.close();
        return sample;
    }

    public static boolean updateUploadStatus(Integer userId, Integer surveyId) {
        ContentValues values = new ContentValues();
        values.put("is_add", 2);
        values.put("is_upload", 2);
        return DBOperation.instanse.updateTableData(TableSQL.SAMPLE_NAME, " user_id = ? and survey_id = ?", new String[]{userId + "", surveyId + ""}, values);
    }
    //问卷提交时候样本状态 第一次未开始到进行中
    public static boolean updateSampleStatus(Integer userId, String sampleId, Integer sampleStatus) {
        ContentValues values = new ContentValues();
        values.put("status", sampleStatus);
        values.put("is_upload", 1);
        return DBOperation.instanse.updateTableData(TableSQL.SAMPLE_NAME, " user_id = ? and sample_guid = ?", new String[]{userId + "", sampleId}, values);
    }

    //跨模块传值
    public static boolean addResponseVariable(JSONObject submitData, String sampleId, String modelCode) {
        Sample sample = SampleDao.getById(SPUtils.getInstance().getInt(SPKey.USERID), sampleId);
        //submitData.remove("prop_dict");
        //submitData.remove("history");
        //submitData.remove("estatus");
        try {
            JSONObject mJsonObject = new JSONObject();
            JSONObject mVarDict = submitData.getJSONObject("var_dict");
            Log.i("mVarDict", mVarDict.toJSONString());
            if (mVarDict != null) {
                mJsonObject.put("var_dict", mVarDict);
                Log.i("mJsonObject", mJsonObject.toJSONString());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(modelCode, mJsonObject);
                Log.i("jsonObject", jsonObject.toJSONString());
                String var = sample.getResponseVariable();
                if (TextUtils.isEmpty(var)) {
                    var = jsonObject.toJSONString();
                } else {
                    JSONObject mResVarJsonObj = JSONObject.parseObject(var);
                    mResVarJsonObj.remove(modelCode);
                    Log.i("mResVarJsonObj", mResVarJsonObj.toJSONString());
                    mResVarJsonObj.put(modelCode, mJsonObject);
                    Log.i("mResVarJsonObj", mResVarJsonObj.toJSONString());
                    var = mResVarJsonObj.toJSONString();
                }
                sample.setResponseVariable(var);
                return SampleDao.insertOrUpdate(sample);
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean addSamples(JSONArray jsonArray, Integer userId, Integer surveyId) {
        List<MultiItemEntity> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
            Sample sample = new Sample();
            String name = jsonObject1.getString("name");

            if (TextUtils.isEmpty(name)) {
                name = "随机样本" + i + 1;
            }
            sample.setGender(jsonObject1.getString("gender"));
            sample.setLat(jsonObject1.getFloat("latitude"));
            sample.setLon(jsonObject1.getFloat("longitude"));
            sample.setPhone(jsonObject1.getString("phone"));
            sample.setAddress(jsonObject1.getString("address"));
            sample.setEmail(jsonObject1.getString("email"));
            sample.setProvince(jsonObject1.getString("province"));
            sample.setCity(jsonObject1.getString("city"));
            sample.setDistrict(jsonObject1.getString("district"));
            sample.setTown(jsonObject1.getString("town"));
            sample.setInterviewerId(jsonObject1.getInteger("interviewer_id"));
            sample.setDisplayName(jsonObject1.getString("display_name"));

            JSONObject extraDataJson = jsonObject1.getJSONObject("extra_data");
            if (extraDataJson != null) {
                sample.setExtraData(extraDataJson.toJSONString());
            }
            sample.setName(name);
            sample.setUserId(userId);
            sample.setSurveyId(surveyId);
            sample.setIsUpload(1);
            sample.setSampleGuid(GUIDUtil.getGuidStr());
            sample.setCreateTime(new Date().getTime());
            list.add(sample);
        }
        insertOrUpdateList(list);
        return true;
    }

    public static boolean updateUploadStatus(Integer userId, String sampleId) {
        ContentValues values = new ContentValues();
        values.put("is_upload", 2);
        return DBOperation.instanse.updateTableData(TableSQL.RESPONSE_NAME, " user_id = ? and sample_guid = ?", new String[]{userId + "", sampleId}, values);
    }

    public static LinkedList<MultiItemEntity> getListFromNet(JSONArray dataArray, Integer userId) {
        final LinkedList<MultiItemEntity> list = new LinkedList<>();
        if (dataArray == null) {
            return list;
        }
        try {
            final int size = dataArray.size();
            for (int i = 0; i < size; i++) {
                Sample sample = new Sample();
                final JSONObject data = dataArray.getJSONObject(i);
                sample.setId(data.getInteger("id"));
                sample.setSampleGuid(data.getString("sampleGuid"));
                sample.setName(data.getString("name"));
                sample.setCode(data.getString("code"));
                sample.setUserId(userId);
                sample.setSurveyId(data.getInteger("projectId"));

                sample.setGender(data.getString("gender"));
                sample.setEmail(data.getString("email"));
                sample.setPhone(data.getString("phone"));
                sample.setMobile(data.getString("mobile"));

                sample.setProvince(data.getString("province"));
                sample.setCity(data.getString("city"));
                sample.setDistrict(data.getString("district"));
                sample.setTown(data.getString("town"));
                sample.setAddress(data.getString("address"));

                sample.setLon(data.getFloat("longitude"));
                sample.setLat(data.getFloat("latitude"));

                sample.setExtraData(data.getString("extraData"));
                sample.setResponseVariable(data.getString("responseVariable"));
                sample.setDisplayName(data.getString("displayName"));
                sample.setStatus(data.getInteger("status"));
                sample.setAppointVisitTime(data.getLong("appointVisitTime"));
                sample.setDescription(data.getString("description"));
                sample.setAssignmentType(data.getIntValue("assignmentType"));

                sample.setCustom1(data.getString("custom1"));
                sample.setCustom2(data.getString("custom2"));
                sample.setCustom3(data.getString("custom3"));
                sample.setCustom4(data.getString("custom4"));
                sample.setCustom5(data.getString("custom5"));

                sample.setCreateTime(data.getLong("createTime"));
                sample.setLastModifyTime(data.getLong("createTime"));
                sample.setIsAdd(2);
                sample.setIsUpload(2);

                list.add(sample);
            }
        } catch (Exception e) {
            Log.i("exception", e.getMessage());
        }
        return list;
    }

    public static Sample getSampleFromNet(JSONObject data, Integer userId, Integer projectId) {
        Sample sample = new Sample();
        sample.setId(data.getInteger("id"));
        sample.setSampleGuid(data.getString("sampleGuid"));
        sample.setName(data.getString("name"));
        sample.setCode(data.getString("code"));
        sample.setUserId(userId);
        sample.setSurveyId(projectId);

        sample.setGender(data.getString("gender"));
        sample.setEmail(data.getString("email"));
        sample.setPhone(data.getString("phone"));
        sample.setMobile(data.getString("mobile"));

        sample.setProvince(data.getString("province"));
        sample.setCity(data.getString("city"));
        sample.setDistrict(data.getString("district"));
        sample.setTown(data.getString("town"));
        sample.setAddress(data.getString("address"));

        sample.setLon(data.getFloat("longitude"));
        sample.setLat(data.getFloat("latitude"));

        sample.setExtraData(data.getString("extraData"));
        sample.setResponseVariable(data.getString("responseVariable"));
        sample.setDisplayName(data.getString("displayName"));
        sample.setStatus(data.getInteger("status"));
        sample.setAppointVisitTime(data.getLong("appointVisitTime"));
        sample.setDescription(data.getString("description"));
        sample.setAssignmentType(data.getIntValue("assignmentType"));

        sample.setCustom1(data.getString("custom1"));
        sample.setCustom2(data.getString("custom2"));
        sample.setCustom3(data.getString("custom3"));
        sample.setCustom4(data.getString("custom4"));
        sample.setCustom5(data.getString("custom5"));

        sample.setCreateTime(data.getLong("createTime"));
        sample.setLastModifyTime(data.getLong("createTime"));
        sample.setIsAdd(2);
        sample.setIsUpload(2);

        insertOrUpdate(sample);

        return sample;
    }

}
