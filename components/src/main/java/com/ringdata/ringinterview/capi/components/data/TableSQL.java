package com.ringdata.ringinterview.capi.components.data;

import java.util.HashMap;

/***
 *
 * @author 此类 为MepDb 提供交互数据
 */
public class TableSQL {
    private static HashMap<String, String> tableMap = new HashMap<String, String>(); //数据表集合
    //public static final String DB_PATH = FileData.ROOT; //数据库名称
    public static final String DB_NAME = "ringsurvey.db"; //数据库名称
    public static final String USER_NAME = "rs_user"; //用户信息表
    public static final String MESSAGE_NAME = "rs_message"; //消息表
    public static final String SURVEY_NAME = "rs_survey"; //项目表
    public static final String SAMPLE_NAME = "rs_sample"; // 样本表
    public static final String SAMPLE_RECORD_NAME = "rs_sample_record"; //样本变更记录表
    public static final String QUESTIONNAIRE_NAME = "rs_questionnaire"; //问卷表
    public static final String RESPONSE_NAME = "rs_response"; //答卷表
    public static final String RESPONSE_AUDIO_FILE_NAME = "rs_response_audio_file"; //答卷录音文件表
    public static final String RESPONSE_QUESTION_FILE_NAME = "rs_response_question_file"; //答卷题目文件表（拍照题+录音题）
    public static final String USER_LOCATION_NAME = "rs_user_location"; //用户（访员）位置信息表
    public static final String SYNC_LOG_NAME = "rs_sync_log"; //数据接口同步记录表

    public static final String SAMPLE_ADDRESS_NAME = "rs_sample_address"; //样本地址
    public static final String SAMPLE_CONTACT_NAME = "rs_sample_contact"; //样本联系方式
    public static final String SAMPLE_TOUCH_NAME = "rs_sample_touch"; //样本接触记录

    //样本地址
    private static final String SAMPLE_ADDRESS_SQL =
            "create table if not exists " + SAMPLE_ADDRESS_NAME +
                    "(" +
                    "id Integer," +
                    "user_id Integer," +
                    "project_id Integer," +
                    "sample_guid varchar(50)," +
                    "name varchar(50)," +
                    "province text," +
                    "city text," +
                    "district text," +
                    "town text," +
                    "village text," +
                    "address text," +
                    "description text," +
                    "create_time text," +
                    "create_user Integer," +
                    "is_delete Integer default 0," + //0 未删除，1 已删除
                    "delete_user Integer," +
                    "is_upload Integer default 1," + //1 未上传，2 已上传
                    "PRIMARY KEY(id)" +
                    ")";

    //样本接触记录
    private static final String SAMPLE_TOUCH_SQL =
            "create table if not exists " + SAMPLE_TOUCH_NAME +
                    "(" +
                    "id Integer," +
                    "user_id Integer," +
                    "project_id Integer," +
                    "sample_guid varchar(50)," +
                    "type varchar(50)," +
                    "status Integer," + //0 失败，1 成功
                    "description text," +
                    "create_time varchar(50)," +
                    "create_user Integer," +
                    "is_delete Integer default 0," + //0 未删除，1 已删除
                    "delete_user Integer," +
                    "is_upload Integer default 1," + //1 未上传，2 已上传
                    "PRIMARY KEY(id)" +
                    ")";

    //样本联系方式
    private static final String SAMPLE_CONTACT_SQL =
            "create table if not exists " + SAMPLE_CONTACT_NAME +
                    "(" +
                    "id Integer," +
                    "user_id Integer," +
                    "project_id Integer," +
                    "sample_guid varchar(50)," +
                    "name varchar(50)," +
                    "relation varchar(50)," +
                    "mobile varchar(50)," +
                    "phone varchar(50)," +
                    "email varchar(50)," +
                    "qq varchar(50)," +
                    "weixin varchar(50)," +
                    "weibo varchar(50)," +
                    "description text," +
                    "create_time varchar(50)," +
                    "create_user Integer," +
                    "is_delete Integer default 0," + //0 未删除，1 已删除
                    "delete_user Integer," +
                    "is_upload Integer default 1," + //1 未上传，2 已上传
                    "PRIMARY KEY(id)" +
                    ")";

    //创建数据接口同步记录表
    private static final String SYNC_LOG_SQL =
            "create table if not exists " + SYNC_LOG_NAME +
                    "(" +
                    "id Integer," +
                    "user_id Integer," +
                    "project_id Integer," +
                    "type Integer," +
                    "success Integer," +
                    "sync_time Long," +
                    "PRIMARY KEY(id)" +
                    ")";

    //创建用户表
    private static final String USER_SQL =
            "create table if not exists " + USER_NAME +
                    " (" +
                    "id Integer," +
                    "name varchar(50)," +
                    "role Integer," +
                    "password varchar(50)," +
                    "avatar_path varchar(50)," +
                    "org_code varchar(50)," +
                    "org_host varchar(50)," +
                    "PRIMARY KEY(id)" +
                    ")";

    //创建消息表
    private static final String MESSAGE_SQL =
            "create table if not exists " + MESSAGE_NAME +
                    "(" +
                    "id Integer," +
                    "user_id Integer," +
                    "title varchar(50)," +

                    "interviewer_username varchar(50)," +
                    "response_status_text text," +
                    "interviewer_name varchar(50)," +
                    "audit_questions text," +
                    "audit_return_reason text," +
                    "samples_map text," +
                    "sample_identifier text," +
                    "survey_name varchar(50)," +
                    "module_name varchar(50)," +
                    "interviewer_cellphone varchar(50)," +

                    "context text," +
                    "status Integer," +
                    "create_time Integer," +
                    "is_delete Integer," +
                    "is_read Integer," +
                    "is_upload Integer default 1," +//1 未上传，2 已上传
                    "PRIMARY KEY(id,user_id)" +
                    ")";

    /* 创建项目表 */
    private static final String SURVEY_SQL =
            "create table if not exists " + SURVEY_NAME +
                    "(" +
                    "id Integer," +
                    "user_id Integer," +
                    "name varchar(50)," +
                    "description text," +
                    "status Integer," +
                    "type varchar(50)," +
                    "label_text text," +
                    "config text," +
                    "role_name text," +
                    "create_user_id Integer," +
                    "create_user_name text," +
                    "create_time text," +
                    "create_time_str text," +
                    "update_time text," +
                    "begin_time text," +
                    "end_time text," +
                    "pause_time text," +
                    "use_property text," +
                    "mark_property text," +
                    "num_of_not_started Integer," +
                    "num_of_in_progress Integer," +
                    "num_of_finish Integer," +
                    "num_of_refuse Integer," +
                    "num_of_identify Integer," +
                    "num_of_appointment Integer," +
                    "num_of_unable_to_contact Integer," +
                    "num_of_in_the_call Integer," +
                    "num_of_no_answer Integer," +
                    "num_of_audit_invalid Integer," +
                    "num_of_return Integer," +
                    "num_of_audit_success Integer," +
                    "remain_interval Integer default 0," +
                    "PRIMARY KEY(id,user_id)" +
                    ")";

    /*创建样本表*/
    private static final String SAMPLE_SQL =
            "create table if not exists " + SAMPLE_NAME +
                    "(" +
                    "id Integer," +
                    "sample_guid varchar(50)," +
                    "name varchar(50)," +
                    "code varchar(50)," +
                    "user_id Integer," +
                    "survey_id Integer," +
                    "gender varchar(50)," +
                    "birth varchar(50)," +
                    "age varchar(50)," +
                    "weixin varchar(50)," +
                    "qq varchar(50)," +
                    "email varchar(50)," +
                    "phone varchar(50)," +
                    "mobile varchar(50)," +
                    "weibo varchar(50)," +
                    "province varchar(50)," +
                    "city varchar(50)," +
                    "district varchar(50)," +
                    "town varchar(50)," +
                    "address varchar(50)," +
                    "organization varchar(50)," +
                    "marriageStatus varchar(50)," +
                    "education varchar(50)," +
                    "politicalStatus varchar(50)," +
                    "nationality varchar(50)," +
                    "profession varchar(50)," +
                    "position varchar(50)," +
                    "placeOfBirth varchar(50)," +
                    "religion varchar(50)," +
                    "language varchar(50)," +
                    "dialects varchar(50)," +
                    "detail varchar(50)," +
                    "lon Long," +
                    "lat Long," +
                    "extra_data text," +
                    "response_variable text," +
                    "display_name text," +
                    "start_time Long," +
                    "end_time Long," +
                    "interviewer_id Integer," +
                    "assignment_type Integer," +
                    "status Integer," +
                    "appoint_visit_time Long," +
                    "description text," +
                    "custom1 varchar(50)," +
                    "custom2 varchar(50)," +
                    "custom3 varchar(50)," +
                    "custom4 varchar(50)," +
                    "custom5 varchar(50)," +
                    "create_time Long," +
                    "last_modify_time Long," +
                    "is_add Integer default 2," +
                    "is_upload Integer default 1," + //1 未上传，2 已上传
                    "PRIMARY KEY(sample_guid,user_id)" +
                    ")";

    /*样本变更记录表*/
    private static final String SAMPLE_RECORD_SQL =
            "create table if not exists " + SAMPLE_RECORD_NAME +
                    "(" +
                    "id Integer," +
                    "user_id Integer," +
                    "project_id Integer," +
                    "sample_guid varchar(50)," +
                    "status Integer," +
                    "is_upload Integer default 1," + //1 未上传，2 已上传
                    "create_time Long," +
                    "PRIMARY KEY(id)" +
                    ")";

    /*创建问卷表*/
    private static final String QUESTIONNAIRE_SQL =
            "create table if not exists " + QUESTIONNAIRE_NAME +
                    "(" +
                    "id Integer," +
                    "user_id Integer," +
                    "survey_id Integer," +
                    "module_id Integer," +
                    "type Integer," +
                    "name varchar(50)," +
                    "code varchar(100)," +
                    "version Integer default 0," +
                    "module_dependency Integer," +
                    "sample_dependency text," +
                    "is_allowed_manual_add Integer," +
                    "quota_min Integer," +
                    "quota_max Integer," +
                    "group_id Integer," +
                    "group_name varchar(50)," +
                    "url varchar(100)," +
                    "qrc_url varchar(100)," +
                    "create_time Long," +
                    "is_file_download_success Integer default 1," + //默认失败，2成功，文件是否下载成功
                    "PRIMARY KEY(id,user_id)" +
                    ")";

    /*创建答卷表*/
    private static final String RESPONSE_SQL =
            "create table if not exists " + RESPONSE_NAME +
                    "(" +
                    "id varchar(100)," +
                    "user_id Integer," +
                    "interviewer_id Integer," +
                    "project_id Integer," +
                    "sample_guid varchar(50)," +
                    "module_id Integer," +
                    "module_code varchar(100)," +
                    "module_name varchar(50)," +
                    "questionnaire_id Integer," +
                    "questionnaire_name varchar(50)," +
                    "questionnaire_type Integer," +
                    "url text," +
                    "questionnaire_url text," +
                    "version Integer," +
                    "sub_questionnaire_json text," +
                    "sampling_status Integer," +
                    "start_time varchar(100)," +
                    "end_time varchar(100)," +
                    "submit_data text," +
                    "submit_state text," +
                    "question_data text," +
                    "response_data text," +
                    "response_duration varchar(100)," +
                    "response_type Integer," +
                    "response_status Integer default 1," +
                    "response_identifier," +
                    "lon varchar(50)," +
                    "lat varchar(50)," +
                    "address varchar(50)," +
                    "sub_json text," +
                    "is_download_detail Integer default 1," + //是否下载详情 1否，2是
                    "caxi_web_status Integer default 0," +
                    "last_modify_time Long," +
                    "create_time Long," +
                    "is_upload Integer default 1," +
                    "PRIMARY KEY(id,user_id)" +
                    ")";

    //位置信息表
    private static final String USER_LOCATION_SQL =
            "create table if not exists " + USER_LOCATION_NAME +
                    "(" +
                    "user_id Integer," +
                    "project_id Integer," +
                    "lat REAL," +
                    "lon REAL," +
                    "address varchar(200)," +
                    "create_time Integer," +
                    "is_upload Integer default 1" +
                    ")";

    //答卷录音文件
    private static final String RESPONSE_AUDIO_FILE_SQL =
            "create table if not exists " + RESPONSE_AUDIO_FILE_NAME +
                    "(" +
                    "id Integer," +
                    "user_id Integer," +
                    "interview_id Integer," +
                    "project_id Integer," +
                    "response_guid varchar(50)," +
                    "question_id Integer," +
                    "file_path varchar(50)," +
                    "local_filename varchar(50)," +
                    "audio_name varchar(50)," +
                    "start_time Integer," +
                    "end_time Integer," +
                    "type Integer," + //录音类型 1:答卷录音 2:单题录音
                    "audio_duration Integer," +
                    "begin_pos Integer," + //单题录音开始时间点
                    "end_pos Integer," + //单题录音结束时间点
                    "create_time Integer," +
                    "is_upload Integer default 1," +
                    "is_upload_file Integer default 1," +
                    "PRIMARY KEY(id)" +
                    ")";

    //答卷题目文件
    private static final String RESPONSE_QUESTION_FILE_SQL =
            "create table if not exists " + RESPONSE_QUESTION_FILE_NAME +
                    "(" +
                    "user_id Integer," +
                    "project_id Integer," +
                    "response_guid varchar(50)," +
                    "question_id varchar(50)," +
                    "file_type varchar(50)," +
                    "file_name varchar(50)," +
                    "file_path varchar(50)," +
                    "file_url varchar(50)," +
                    "local_filename varchar(50)," +
                    "create_time varchar(50)," +
                    "is_upload Integer default 1," +
                    "is_upload_file Integer default 1," +
                    "is_file_download_success Integer default 1," + //文件是否下载成功 1失败（默认），2成功
                    "PRIMARY KEY(user_id,local_filename)" +
                    ")";

    public static HashMap<String, String> tableSqlHashMap() {

        tableMap.put(USER_NAME, USER_SQL);
        tableMap.put(SYNC_LOG_NAME, SYNC_LOG_SQL);
        tableMap.put(USER_LOCATION_NAME, USER_LOCATION_SQL);
        tableMap.put(SURVEY_NAME, SURVEY_SQL);
        tableMap.put(QUESTIONNAIRE_NAME, QUESTIONNAIRE_SQL);
        tableMap.put(MESSAGE_NAME, MESSAGE_SQL);
        tableMap.put(SAMPLE_NAME, SAMPLE_SQL);
        tableMap.put(SAMPLE_RECORD_NAME, SAMPLE_RECORD_SQL);
        tableMap.put(RESPONSE_NAME, RESPONSE_SQL);
        tableMap.put(RESPONSE_AUDIO_FILE_NAME, RESPONSE_AUDIO_FILE_SQL);
        tableMap.put(RESPONSE_QUESTION_FILE_NAME, RESPONSE_QUESTION_FILE_SQL);

        tableMap.put(SAMPLE_ADDRESS_NAME, SAMPLE_ADDRESS_SQL);
        tableMap.put(SAMPLE_TOUCH_NAME, SAMPLE_TOUCH_SQL);
        tableMap.put(SAMPLE_CONTACT_NAME, SAMPLE_CONTACT_SQL);

        return tableMap;
    }
}