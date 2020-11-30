package com.ringdata.ringinterview.capi.components.ui.survey;

import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.constant.ProjectConstants;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.model.Survey;

/**
 * Created by admin on 2018/6/25.
 */

public class ItemClickListener extends SimpleClickListener {
    private final LatteDelegate DELEGATE;

    ItemClickListener(LatteDelegate delegate) {
        this.DELEGATE = delegate;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Survey survey = (Survey) adapter.getData().get(position);
        SPUtils.getInstance().put(SPKey.SURVEY_ID, survey.getId());
        SPUtils.getInstance().put(SPKey.SURVEY_NAME, survey.getName());
        SPUtils.getInstance().put(SPKey.SURVEY_TYPE, survey.getType());
        SPUtils.getInstance().put(SPKey.SURVEY_STATUS, survey.getStatus());
        String config = survey.getConfig();
        JSONObject configJsonObject = JSON.parseObject(config);
        SPUtils.getInstance().put(SPKey.SURVEY_IS_NEED_RESPONSE_AUDIO, configJsonObject.getInteger("responseAudio") == ProjectConstants.PROJECT_CONFIG_ON);
        SPUtils.getInstance().put(SPKey.SURVEY_IS_NEED_RESPONSE_POS, configJsonObject.getInteger("responsePosition") == ProjectConstants.PROJECT_CONFIG_ON);
        SPUtils.getInstance().put(SPKey.SURVEY_IS_SUPPORT_SAMPLE_CONTACT_RECORD, configJsonObject.getInteger("moreSampleInfo") == ProjectConstants.PROJECT_CONFIG_ON);
        SPUtils.getInstance().put(SPKey.SURVEY_IS_CAN_ADD_SAMPLE, configJsonObject.getInteger("interviewerSaveSample") == ProjectConstants.PROJECT_CONFIG_ON);
        SPUtils.getInstance().put(SPKey.SURVEY_SAMPLE_ASSIGN_TYPE, configJsonObject.getInteger("sampleAssignType"));
        SPUtils.getInstance().put(SPKey.SURVEY_IS_SUPPORT_MULTI_QUESTIONNAIRE, configJsonObject.getInteger("multipleQuestionnaire") == ProjectConstants.PROJECT_CONFIG_ON);
        SPUtils.getInstance().put(SPKey.SURVEY_IS_SUPPORT_TWICE_SUBMIT, configJsonObject.getInteger("secondSubmit") == ProjectConstants.PROJECT_CONFIG_ON);
        String recordStatus = configJsonObject.getString("concat_record");

        if (recordStatus != null) {
            SPUtils.getInstance().put(SPKey.SURVEY_STATUS_JSON, recordStatus);
        } else {
            SPUtils.getInstance().put(SPKey.SURVEY_STATUS_JSON, "");
        }

        //样本使用属性
        String surveySampleProperty = survey.getUseProperty();
        if (surveySampleProperty != null) {
            SPUtils.getInstance().put(SPKey.SURVEY_SAMPLE_PROPERTY, surveySampleProperty);
        }
        //样本标识
        String surveySampleIdentifier = survey.getMarkProperty();
        if (surveySampleIdentifier != null) {
            SPUtils.getInstance().put(SPKey.SURVEY_SAMPLE_IDENTIFIER, surveySampleIdentifier);
        }
        DELEGATE.start(new SurveyDetailDelegate());
//        if (survey.getType().equals(ProjectConstants.CAWI_PROJECT)) {
//            DELEGATE.start(new SurveyDetailDelegate());
//        }
    }

    @Override
    public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

    }
}
