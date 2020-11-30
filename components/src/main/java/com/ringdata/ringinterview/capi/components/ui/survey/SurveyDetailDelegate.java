package com.ringdata.ringinterview.capi.components.ui.survey;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.joanzapata.iconify.widget.IconTextView;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.base.util.NetworkUtil;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.ProjectConstants;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.SurveyDao;
import com.ringdata.ringinterview.capi.components.data.model.Survey;
import com.ringdata.ringinterview.capi.components.ui.survey.questionnaire.qrc.QuestionnaireQRCodeDelegate;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.SampleDelegate;
import com.ringdata.ringinterview.capi.components.utils.AppUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by admin on 17/11/8.
 */

public class SurveyDetailDelegate extends LatteDelegate implements OnRefreshListener {
    @BindView(R2.id.tv_item_survey_title)
    TextView tv_item_survey_title;
    @BindView(R2.id.tv_item_survey_cawi_icon)
    TextView tv_item_survey_cawi_icon;
    @BindView(R2.id.tv_item_survey_status)
    TextView tv_item_survey_status;
    @BindView(R2.id.tv_item_survey_description)
    TextView tv_item_survey_description;
    @BindView(R2.id.tv_item_survey_role)
    TextView tv_item_survey_role;
    @BindView(R2.id.tv_item_survey_creator)
    TextView tv_item_survey_creator;
    @BindView(R2.id.tv_item_survey_start_time)
    TextView tv_item_survey_start_time;
    @BindView(R2.id.tv_item_survey_type_icon)
    IconTextView tv_item_survey_type_icon;
    @BindView(R2.id.tv_item_survey_type_text)
    TextView tv_item_survey_type_text;

    @BindView(R2.id.ll_my_sample)
    LinearLayout ll_my_sample;

    @BindView(R2.id.tv_survey_sample_number)
    TextView tv_survey_sample_number;
    @BindView(R2.id.tv_survey_sample_preparing_number)
    TextView tv_survey_sample_preparing_number;
    @BindView(R2.id.tv_survey_sample_executing_number)
    TextView tv_survey_sample_executing_number;
    @BindView(R2.id.tv_survey_sample_submit_number)
    TextView tv_survey_sample_submit_number;
    @BindView(R2.id.tv_survey_sample_refuse_number)
    TextView tv_survey_sample_refuse_number;
    @BindView(R2.id.tv_survey_sample_off_number)
    TextView tv_survey_sample_off_number;
    @BindView(R2.id.ll_survey_detail_sample_data)
    LinearLayout ll_survey_detail_sample_data;

    @BindView(R2.id.tv_survey_detail_access)
    TextView tv_survey_detail_access;
    @BindView(R2.id.tv_survey_detail_group)
    TextView tv_survey_detail_group;
    @BindView(R2.id.tv_survey_detail_finish_percent)
    TextView tv_survey_detail_finish_percent;
    @BindView(R2.id.tv_survey_detail_sample)
    TextView tv_survey_detail_sample;
    @BindView(R2.id.tv_survey_detail_success_percent)
    TextView tv_survey_detail_success_percent;
    @BindView(R2.id.tv_survey_detail_questionnaire)
    TextView tv_survey_detail_questionnaire;
    @BindView(R2.id.tv_survey_detail_time_average)
    TextView tv_survey_detail_time_average;
    @BindView(R2.id.tv_survey_detail_time_whole)
    TextView tv_survey_detail_time_whole;
    @BindView(R2.id.tv_survey_detail_data_average)
    TextView tv_survey_detail_data_average;
    @BindView(R2.id.tv_survey_detail_data_whole)
    TextView tv_survey_detail_data_whole;

    @BindView(R2.id.line_chart)
    LineChartView lineChartView;
    @BindView(R2.id.ll_survey_detail_group_data)
    LinearLayout ll_survey_detail_group_data;
    @BindView(R2.id.ll_survey_detail_data_whole)
    LinearLayout ll_survey_detail_data_whole;
    @BindView(R2.id.view_survey_other_data)
    LinearLayout view_survey_other_data;

    private int currentUserId;
    private int currentSurveyId;
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisValues = new ArrayList<AxisValue>();
    private Survey currentSurvey;
    private String qrcCode;

    @Override
    public Object setLayout() {
        return R.layout.delegate_survey_detail;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        currentUserId = SPUtils.getInstance().getInt(SPKey.USERID);
        currentSurveyId = SPUtils.getInstance().getInt(SPKey.SURVEY_ID);
        currentSurvey = SurveyDao.queryById(currentSurveyId, currentUserId);

        refreshLayout = (SmartRefreshLayout) mRootView.findViewById(R.id.srl_survey_detail);
        refreshLayout.setDisableContentWhenLoading(true);
        refreshLayout.setDisableContentWhenRefresh(true);
        refreshLayout.setOnRefreshListener(this);

        getSurveyOtherDetail();
    }

    private void loadSurveyDetail(JSONObject surveyInfo) {
        tv_item_survey_title.setText(surveyInfo.getString("name"));
        tv_item_survey_description.setText(surveyInfo.getString("description"));
        tv_item_survey_role.setText("角色：" + surveyInfo.getString("roleName"));
        tv_item_survey_creator.setText("创建：" + currentSurvey.getCreateUserName());
        tv_item_survey_status.setText(AppUtil.getSurveyStatus(surveyInfo.getIntValue("status")));
        tv_item_survey_status.setTextColor(getResources().getColor(AppUtil.getSurveyStatusColor(surveyInfo.getIntValue("status"))));
        String createTimeStr = "";
        String updateTimeStr = "";
        String beginTimeStr = "";
        String endTimeStr = "";
        String pauseTimeStr = "";
        if (surveyInfo.getString("createTimeStr") != null) {
            createTimeStr = surveyInfo.getString("createTimeStr");
        } else {
            createTimeStr = surveyInfo.getString("createTime");
        }
        if (surveyInfo.getString("updateTimeStr") != null) {
            updateTimeStr = surveyInfo.getString("updateTimeStr");
        } else {
            updateTimeStr = surveyInfo.getString("updateTime");
        }
        if (surveyInfo.getString("beginDateStr") != null) {
            beginTimeStr = surveyInfo.getString("beginDateStr");
        } else {
            beginTimeStr = surveyInfo.getString("beginTime");
        }
        if (surveyInfo.getString("endDateStr") != null) {
            endTimeStr = surveyInfo.getString("endDateStr");
        } else {
            endTimeStr = surveyInfo.getString("endTime");
        }
        if (surveyInfo.getString("pauseTimeStr") != null) {
            pauseTimeStr = surveyInfo.getString("pauseTimeStr");
        } else {
            pauseTimeStr = surveyInfo.getString("pauseTime");
        }
        tv_item_survey_start_time.setText(AppUtil.getSurveyTime(surveyInfo.getIntValue("status"),
                createTimeStr, updateTimeStr, beginTimeStr, pauseTimeStr, endTimeStr));
        tv_item_survey_type_icon.setText(Html.fromHtml(AppUtil.getSurveyTypeIcon(surveyInfo.getString("type"))));
        tv_item_survey_type_text.setText(AppUtil.getSurveyTypeText(surveyInfo.getString("type")));

        if (ProjectConstants.CAWI_PROJECT.equals(surveyInfo.getString("type"))) {
            //网络调查隐藏样本相关，显示二维码
            tv_item_survey_cawi_icon.setVisibility(View.VISIBLE);
            ll_my_sample.setVisibility(View.GONE);
//            ll_survey_detail_sample_data.setVisibility(View.GONE);
        }

        if (!surveyInfo.getString("roleName").contains("管")) {
            //只有管理员可以查看更多监控报表，团队和数据总量
            view_survey_other_data.setVisibility(View.GONE);
            ll_survey_detail_group_data.setVisibility(View.GONE);
            ll_survey_detail_data_whole.setVisibility(View.GONE);
        }
    }

    private void getSurveyOtherDetail() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", currentSurveyId);
        RestClient.builder().url(App.orgHost + ApiUrl.GET_SURVEY_DETAIL)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (responseJson.getSuccess()) {
                            JSONObject data = responseJson.getDataAsObject();
                            JSONObject surveyInfo = data.getJSONObject("projectInfoDTO");
                            JSONObject sampleReport = data.getJSONObject("projectSampleReportDTO");
                            JSONObject surveySummary = data.getJSONObject("projectDataSummaryDTO");
                            JSONObject chartData = data.getJSONObject("answerProgressData");
                            loadSurveyDetail(surveyInfo);
                            tv_survey_sample_number.setText("我的分派样本("+sampleReport.getIntValue("numOfSample")+")");
                            tv_survey_sample_preparing_number.setText(sampleReport.getIntValue("numOfNotStarted")+"");
                            tv_survey_sample_executing_number.setText(sampleReport.getIntValue("numOfInProgress")+"");
                            tv_survey_sample_submit_number.setText(sampleReport.getIntValue("numOfFinish")+"");
                            tv_survey_sample_refuse_number.setText(sampleReport.getIntValue("numOfRefuse")+"");
                            tv_survey_sample_off_number.setText(sampleReport.getIntValue("numOfAuditInvalid")+"");

                            tv_survey_detail_access.setText(surveySummary.getIntValue("interviewerNum")+"");
                            tv_survey_detail_group.setText(surveySummary.getIntValue("numOfTeam")+"");
                            NumberFormat format = NumberFormat.getPercentInstance();
                            format.setMinimumFractionDigits(2);
                            String finishRate = format.format(surveySummary.getDoubleValue("finishRate"));
                            String successRate = format.format(surveySummary.getDoubleValue("successRate"));
                            tv_survey_detail_finish_percent.setText(finishRate);
                            tv_survey_detail_sample.setText(surveySummary.getIntValue("numOfSample")+"");
                            tv_survey_detail_success_percent.setText(successRate);
                            tv_survey_detail_questionnaire.setText(surveySummary.getIntValue("numOfAnswer")+"");
                            tv_survey_detail_time_average.setText(surveySummary.getString("avgTimeStr"));
                            tv_survey_detail_time_whole.setText(surveySummary.getString("timeStr"));
                            tv_survey_detail_data_average.setText(surveySummary.getString("avgFileSizeStr"));
                            tv_survey_detail_data_whole.setText(surveySummary.getString("fileSizeStr"));

                            getAxisLabels(chartData.getJSONArray("xData"));
                            getAxisPoints(chartData.getJSONArray("yData"));
                            initLineChart(chartData.getJSONArray("yData"));

                            qrcCode = surveyInfo.getString("publishCode");
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        Log.e("ERROR", msg);
                    }
                })
                .build()
                .post();
    }

    private void initLineChart(JSONArray yData) {
        Line line = new Line(mPointValues).setColor(R.color.primary).setCubic(false); //折线的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE); //折线图上每个数据点的形状，这里是圆形（有三种：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setCubic(true); //曲线是否平滑
        line.setFilled(false); //是否填充曲线的面积
//        line.setHasLabels(true); //曲线的数据坐标是否加上备注
        line.setHasLabelsOnlyForSelected(true); //点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true); //是否用直线显示。如果为false，则没有曲线只有点显示
        line.setHasPoints(true); //是否显示圆点。如果为false，则没有原点只有点显示
        line.setPointRadius(2); //坐标点大小
        line.setStrokeWidth(1); //设置线的宽度
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false); //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(R.color.color08); //设置字体颜色
        axisX.setTextSize(10); //设置字体大小
        axisX.setMaxLabelChars(10); //最多几个X轴坐标
        axisX.setValues(mAxisValues); //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x轴在底部

        Axis axisY = new Axis(); //Y轴
        axisY.setMaxLabelChars(7); //默认是3，只能看最后三个数字
        axisY.setTextSize(10); //设置字体大小
        axisY.setTextColor(R.color.color08);
        axisY.setHasLines(true);
        axisY.setLineColor(Color.parseColor("#AEC0C0C0"));
        // 这样添加y轴坐标 就可以固定y轴的数据
        List<AxisValue> values = new ArrayList<>();
        int maxValue = 0;
        for (int i = 0; i < yData.size(); i++) {
            if (yData.getIntValue(i) > maxValue) {
                maxValue = yData.getIntValue(i);
            }
        }
        for (int i = 0; i < maxValue + 1; i++) {
            AxisValue value = new AxisValue(i);
            values.add(value);
        }
        axisY.setValues(values);
        data.setAxisYLeft(axisY); //Y轴设置在左边

        // 设置行为属性，支持缩放、滑动以及平移
        lineChartView.setInteractive(true);
        lineChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.setScrollEnabled(true);
        lineChartView.setLineChartData(data);
        lineChartView.setVisibility(View.VISIBLE);

        //设置竖线
        Viewport v = new Viewport(lineChartView.getMaximumViewport());
        v.left = 0;
        v.right = 8;
        lineChartView.setCurrentViewport(v);
        lineChartView.moveTo(25, 0);
    }

    /**
     * X轴的显示
     */
    private void getAxisLabels(JSONArray dateList) {
        mAxisValues.clear();
        for (int i = 0; i < dateList.size(); i++) {
            mAxisValues.add(new AxisValue(i).setLabel(dateList.getString(i)));
        }
    }

    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints(JSONArray countList) {
        mPointValues.clear();
        for (int i = 0; i < countList.size(); i++) {
            mPointValues.add(new PointValue(i, countList.getInteger(i)));
        }
    }

    @OnClick(R2.id.tv_item_survey_cawi_icon)
    void onClickCAWI() {
        getSupportDelegate().start(QuestionnaireQRCodeDelegate.newInstance(qrcCode));
    }

    @OnClick(R2.id.ll_my_sample)
    void onClickMySample() {
        getSupportDelegate().start(new SampleDelegate());
    }

    @OnClick(R2.id.view_survey_other_data)
    void onClickOtherData() {
        getSupportDelegate().start(new SurveyChartDelegate());
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("网络不可用");
            refreshlayout.finishRefresh();
        } else {
            getSurveyOtherDetail();
            refreshlayout.finishRefresh();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            if (!NetworkUtil.isNetworkAvailable()) {
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort("网络不可用");
                refreshLayout.finishRefresh();
            } else {
                getSurveyOtherDetail();
                refreshLayout.finishRefresh();
            }
        }
        super.onHiddenChanged(hidden);
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }
}
