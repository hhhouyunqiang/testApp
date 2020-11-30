package com.ringdata.ringinterview.capi.components.ui.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.SPUtils;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.ProjectConstants;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.ui.survey.web.WebViewActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 17/11/7.
 */

public class SurveyChartDelegate extends LatteDelegate {
    private int currentSurveyId;
    private String token;

    @BindView(R2.id.rl_survey_explorer)
    LinearLayout ll_explorer;
    @BindView(R2.id.rl_survey_source)
    LinearLayout ll_source;

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @OnClick(R2.id.rl_survey_process)
    void onClickProcess() {
//        String url = App.orgHost.substring(0, App.orgHost.length() - 4) + "8200/ringsurvey/report_app/answer?id=" + currentSurveyId + "&tokenWeb=" + token;
        String url = App.orgHost + "/report_app/answer?id=" + currentSurveyId + "&tokenWeb=" + token;
//        String url = "http://192.168.1.116:8200/ringsurvey/report_app/answer?id=" + currentSurveyId + "&tokenWeb=" + token;
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", "答卷进度");
        getActivity().startActivity(intent);
    }

    @OnClick(R2.id.rl_survey_map)
    void onClickMap() {
//        String url = App.orgHost.substring(0, App.orgHost.length() - 4) + "8200/ringsurvey/report_app/map?id=" + currentSurveyId + "&tokenWeb=" + token;
        String url = App.orgHost + "/report_app/map?id=" + currentSurveyId + "&tokenWeb=" + token;
//        String url = "http://192.168.1.116:8200/ringsurvey/report_app/map?id=" + currentSurveyId + "&tokenWeb=" + token;
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", "调查地图");
        getActivity().startActivity(intent);
    }

    @OnClick(R2.id.rl_survey_sample_status)
    void onClickSampleStatus() {
//        String url = App.orgHost.substring(0, App.orgHost.length() - 4) + "8200/ringsurvey/report_app/sample_status?id=" + currentSurveyId + "&tokenWeb=" + token;
        String url = App.orgHost + "/report_app/sample_status?id=" + currentSurveyId + "&tokenWeb=" + token;
//        String url = "http://192.168.1.116:8200/ringsurvey/report_app/sample_status?id=" + currentSurveyId + "&tokenWeb=" + token;
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", "样本状态分布");
        getActivity().startActivity(intent);
    }

    @OnClick(R2.id.rl_survey_index)
    void onClickIndex() {
//        String url = App.orgHost.substring(0, App.orgHost.length() - 4) + "8200/ringsurvey/report_app/index_dynamic?id=" + currentSurveyId + "&tokenWeb=" + token;
        String url = App.orgHost + "/report_app/index_dynamic?id=" + currentSurveyId + "&tokenWeb=" + token;
//        String url = "http://192.168.1.116:8200/ringsurvey/report_app/index_dynamic?id=" + currentSurveyId + "&tokenWeb=" + token;
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", "指标动态");
        getActivity().startActivity(intent);
    }

    @OnClick(R2.id.rl_survey_sample_use)
    void onClickSampleUse() {
//        String url = App.orgHost.substring(0, App.orgHost.length() - 4) + "8200/ringsurvey/report_app/sample_dynamic?id=" + currentSurveyId + "&tokenWeb=" + token;
        String url = App.orgHost + "/report_app/sample_dynamic?id=" + currentSurveyId + "&tokenWeb=" + token;
//        String url = "http://192.168.1.116:8200/ringsurvey/report_app/sample_dynamic?id=" + currentSurveyId + "&tokenWeb=" + token;
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", "样本使用动态");
        getActivity().startActivity(intent);
    }

    @OnClick(R2.id.rl_survey_interviewer)
    void onClickInterviewer() {
//        String url = App.orgHost.substring(0, App.orgHost.length() - 4) + "8200/ringsurvey/report_app/sample_complete?id=" + currentSurveyId + "&tokenWeb=" + token;
        String url = App.orgHost + "/report_app/sample_complete?id=" + currentSurveyId + "&tokenWeb=" + token;
//        String url = "http://192.168.1.116:8200/ringsurvey/report_app/sample_complete?id=" + currentSurveyId + "&tokenWeb=" + token;
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", "访员绩效");
        getActivity().startActivity(intent);
    }

    @OnClick(R2.id.rl_survey_explorer)
    void onClickExplorer() {
//        String url = App.orgHost.substring(0, App.orgHost.length() - 4) + "8200/ringsurvey/report_app/browser?id=" + currentSurveyId + "&tokenWeb=" + token;
        String url = App.orgHost + "/report_app/browser?id=" + currentSurveyId + "&tokenWeb=" + token;
//        String url = "http://192.168.1.116:8200/ringsurvey/report_app/browser?id=" + currentSurveyId + "&tokenWeb=" + token;
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", "浏览器参数");
        getActivity().startActivity(intent);
    }

    @OnClick(R2.id.rl_survey_source)
    void onClickSource() {
//        String url = App.orgHost.substring(0, App.orgHost.length() - 4) + "8200/ringsurvey/report_app/source_report?id=" + currentSurveyId + "&tokenWeb=" + token;
        String url = App.orgHost + "/report_app/source_report?id=" + currentSurveyId + "&tokenWeb=" + token;
//        String url = "http://192.168.1.116:8200/ringsurvey/report_app/source_report?id=" + currentSurveyId + "&tokenWeb=" + token;
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", "来源报告");
        getActivity().startActivity(intent);
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_survey_chart;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        currentSurveyId = SPUtils.getInstance().getInt(SPKey.SURVEY_ID);
        token = SPUtils.getInstance().getString(SPKey.ACCESS_TOKEN);
        if (SPUtils.getInstance().getString(SPKey.SURVEY_TYPE).equals(ProjectConstants.CAPI_PROJECT)) {
            ll_explorer.setVisibility(View.GONE);
            ll_source.setVisibility(View.GONE);
        }
    }
}
