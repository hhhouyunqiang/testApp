package com.ringdata.ringinterview.capi;

import com.blankj.utilcode.util.SPUtils;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.survey.SurveyAccess;

import java.io.File;


/**
 * Created by xch on 2017/5/13.
 */

public class MTApp extends App {
    @Override
    public void onCreate() {
        super.onCreate();
        SPUtils.getInstance().put(SPKey.APP_QUESTIONNAIRE_LIFT_TITLE, "主问卷");
        SPUtils.getInstance().put(SPKey.APP_QUESTIONNAIRE_RIGHT_TITLE, "子问卷");
        SPUtils.getInstance().put(SPKey.APP_SIGNIN_DESCRIPT, "Powerby 锐研·云调查");

        File _workspace = getExternalFilesDir(null);
        SurveyAccess.ASSETS_SURVEY_JS_VERSION = BuildConfig.ASSETS_SURVEY_JS_VERSION;
        SurveyAccess.init(getApplicationContext(), _workspace, getSharedPreferences("testsurvey", 0));
    }

    @Override
    public String host() {
         return "https://dc.ringdata.com";
         //return "http://192.168.0.141:8075";
        //return "http://175.102.15.229:18900";

    }
}
