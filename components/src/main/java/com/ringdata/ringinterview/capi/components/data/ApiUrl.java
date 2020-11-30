package com.ringdata.ringinterview.capi.components.data;

import com.ringdata.ringinterview.capi.components.App;

/**
 * Created by admin on 17/11/30.
 */

public class ApiUrl {

    public static final String version = "/appapi/";

    private static String uums = "https://i.ringdata.com/uums/";
    //private static final String uums = "http://175.102.15.229:18900/uums/";

    //登录
    public static final String LOGIN = uums + "oauth/token";

    //扫码登录网页端
    public static final String SCAN_LOGIN = version + "user/confirm/scan";

    //发送验证码
    public static final String SEND_CODE = uums + "code/v1/phone/sendMessage";

    //检查手机号是否被注册
    public static final String CHECK_PHONE_AVAILABLE = uums + "register/v1/phone/checkPhoneAvailable";

    //手机号注册
    public static final String REGISTER_PHONE = uums + "register/v1/phone/register";

    //邮箱注册
    public static final String REGISTER_EMAIL = uums + "register/v1/email/register";

    //手机号找回密码
    public static final String RESET_PWD_PHONE = uums + "reset/v1/phone/resetPWD";

    //判断用户名和邮箱是否匹配
    public static final String USER_MATCH_EMAIL = uums + "reset/v1/email/matchEmailAndName";

    //邮箱找回密码
    public static final String RESET_PWD_EMAIL = uums + "reset/v1/email/sendEmail";

    //获取用户基本信息
    public static final String USER_INFO = version + "user/getUserInfo";

    //修改密码
    public static final String UPDATE_PASSWORD = uums + "centerAuth/v1/updatePassword";

    //统计安装次数
    public static final String UPDATE_APP_DOWNLOAD = uums + "show/update/view/APK_DOWNLOAD";

    //app版本更新
    public static final String UPDATE_APP = version + "app/get";

    //问卷js版本更新
    public static final String UPDATE_JS = version + "version/update/js";

    //上传用户头像接口
    public static final String USER_UPLOAD_HEADIMAGE = version + "file/uploadAvatar";

    public static final String QRC = version + "cawiurl/saveCAWICodeAndParam";

    //访员定位
    public static final String UPLOAD_USER_LOCATION = version + "interviewer/travel/save";

    //知识
    public static final String DOWNLOAD_KNOWLEDGE = version + "knowledge/getKnowledgeList";

    //消息
    public static final String UPLOAD_MESSAGE = version + "response/updateResponseMessages";
    public static final String DOWNLOAD_MESSAGE = version + "response/getUserMessageList";

    //全部项目列表详细
    public static final String DOWNLOAD_SURVEY = version + "project/getProjectList";
    //单个项目详细
    public static final String GET_SURVEY_DETAIL = version + "project/getProjectDetails";
    public static final String APPLY_PROJECT = version + "project/apply";

    //下载样本列表
    public static final String DOWNLOAD_SAMPLE = version + "sample/getProjectSample"; //getSampleList
    //下载样本额外
    public static final String DOWNLOAD_SAMPLE_EXTRA_DATA = version + "sample/getSampleExtraInfoList";
    public static final String GET_SAMPLE_DETAIL = version + "sample/getSampleDetail";
    public static final String UPLOAD_SAMPLE = version + "sample/appUpdateSampleData";
    public static final String UPLOAD_SAMPLE_EXTRA_DATA = version + "sample/appUpdateSampleExtraInfo";
    public static final String UPLOAD_SAMPLE_RECORD = version + "sample/addSampleStatusRecord";
    public static final String GET_NEXT_SAMPLE = version + "sample/appGetNextSample";

    //问卷
    public static final String DOWNLOAD_QUESTION = version + "qnaire/getQuestionnaireList";
    public static final String SHARE_CAWI = version + "qnaire/get/share";

    //答卷列表
    public static final String DOWNLOAD_RESPONSE = version + "answer/getResponseList";
    //答卷
    public static final String DOWNLOAD_RESPONSE_DETAIL = version + "answer/getResponseDetail";
    public static final String UPLOAD_RESPONSE = version + "answer/capi/submit";

    //文件
    public static final String DOWNLOAD_RESPONSE_FILE = version + "file/downloadFile";
    public static final String UPLOAD_SURVEY_FILES = version + "file/syncFile";

    public static final String url2(String fileUrl) {
        //String  aa=App.orgHost + "/appapi" + fileUrl;
        //return "http://192.168.0.141:8200/appapi/js/395/A-1/survey.js";
        return App.orgHost + "/appapi" + fileUrl;

    }
}
