package com.ringdata.ringinterview.capi.components.data;

import android.os.Environment;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseQuestionFileDao;

/**
 * Created by admin on 17/11/30.
 */

public class FileData {

    public final static String ROOT = Environment.getExternalStorageDirectory() + "/ringsurvey-capi/data/";
    //public final static String ROOT = "/ringsurvey-capi/data/";
    public final static String DBPath = "/data/data/" + AppUtils.getAppPackageName() + "/databases/ringsurvey.db";

    public final static String KNOWLEDGE_CACHE = ROOT + "knowledge-cache/";

    public final static String USER = ROOT + "user/";

    public final static String UPDATE_APK = ROOT + "update/";

    public final static String UPDATE_JS = ROOT + "update/";

    public final static String getQuestionnaireDir(int userId) {
        return ROOT + userId + "/questionnaire/";
    }

    public final static String getSurveyDir(int userId) {
        //项目答卷文件（项目下面所有的答卷文件包括，录音文件，题目拍照文件等）
        return ROOT + userId + "/survey/";
    }

    public final static void removeFile(int userId, String filename) {
        FileUtils.deleteFile(getSurveyDir(userId) + filename);
        ResponseQuestionFileDao.delete(userId, filename);
    }

//    public static void backUpDb() {
//        File oldDbFile = new File("/data/data/" + AppUtils.getAppPackageName() + "/databases/ringsurvey.db");
//        File newDbFile = new File(ROOT + "ringsurvey.db");
//        if (FileUtils.isFileExists(oldDbFile)) {
//            try {
//                org.apache.commons.io.FileUtils.copyFile(oldDbFile, newDbFile);
//                org.apache.commons.io.FileUtils.forceDeleteOnExit(oldDbFile);
//            } catch (Exception e) {
//                ToastUtils.showShort("迁移数据库失败");
//            }
//        }
//    }
}
