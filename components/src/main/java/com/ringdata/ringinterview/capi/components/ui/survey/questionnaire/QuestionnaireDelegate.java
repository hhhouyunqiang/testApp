package com.ringdata.ringinterview.capi.components.ui.survey.questionnaire;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bigkoo.pickerview.TimePickerView;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.ui.recycler.BaseDecoration;
import com.ringdata.base.ui.view.BottomMenuDialog;
import com.ringdata.base.util.NetworkUtil;
import com.ringdata.base.util.callback.IGlobalCallback;
import com.ringdata.base.utils.TimeUtil;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.constant.SampleStatus;
import com.ringdata.ringinterview.capi.components.data.FileData;
import com.ringdata.ringinterview.capi.components.data.dao.SampleDao;
import com.ringdata.ringinterview.capi.components.data.model.Questionnaire;
import com.ringdata.ringinterview.capi.components.data.model.Response;
import com.ringdata.ringinterview.capi.components.data.model.Sample;
import com.ringdata.ringinterview.capi.components.ui.survey.response.LeadResponseDelegate;
import com.ringdata.ringinterview.capi.components.ui.survey.response.ResponseActivity;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.SampleDelegate;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.detail.PropertyListBean;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.detail.SampleDetailAdapter;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.detail.SampleDetailItemType;
import com.ringdata.ringinterview.capi.components.utils.AppUtil;
import com.ringdata.ringinterview.survey.SurveyAccess;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 17/11/14.
 */

public class QuestionnaireDelegate extends LatteDelegate implements OnRefreshListener, QuestionnaireContract.View, BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemLongClickListener {
    private Integer userId; //当前用户id
    private Integer surveyId; //当前项目id
    private String sampleId; //当前样本id
    private QuestionnairePresenter mPresenter;
    private Questionnaire _selectedCat;
    private TimePickerView pickerView;
    android.support.v7.app.AlertDialog sampleSubmitDialog;
    EditText et_remarks;
    TextView tv_time_choice;
    BottomMenuDialog bottomMenuDialog;
    private String displayName;

    @BindView(R2.id.ll_topbar_tab)
    LinearLayout ll_topbar_tab;
    @BindView(R2.id.btn_main)
    Button btn_main;
    @BindView(R2.id.btn_sub)
    TextView btn_sub;
    @BindView(R2.id.tv_topbar_title)
    TextView tv_topbar_title;
    @BindView(R2.id.btn_topbar_right)
    TextView btn_topbar_right;
    @BindView(R2.id.cl_head)
    LinearLayout cl_head;
    @BindView(R2.id.tv_01)
    TextView tv_01;
    @BindView(R2.id.tv_revisit_time)
    TextView tv_revisit_time;
    @BindView(R2.id.tv_revisit_remark)
    TextView tv_revisit_remark;
    @BindView(R2.id.ll_questionnaire_group)
    LinearLayout ll_questionnaire_group;
    @BindView(R2.id.tl_questionnaire_group)
    TabLayout tl_questionnaire_group;
    @BindView(R2.id.rv_sample_detail)
    RecyclerView rv_sample_detail;

    @Override
    public Object setLayout() {
        return R.layout.delegate_survey_questionnaire;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        userId = SPUtils.getInstance().getInt(SPKey.USERID);
        surveyId = SPUtils.getInstance().getInt(SPKey.SURVEY_ID);
        sampleId = SPUtils.getInstance().getString(SPKey.SAMPLE_ID);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mPresenter = new QuestionnairePresenter(this, getActivity());
        mPresenter.syncData();
        mPresenter.start();
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        popTo(SampleDelegate.class, false);
    }

    @OnClick(R2.id.btn_topbar_right)
    void onClickTopRight() {
        mPresenter.openSampleStatus();
    }

    @OnClick(R2.id.btn_main)
    public void onClickMain() {
        if (_selectedCat != null) {
            mPresenter.getMainQuestionnaireListFromLocal(_selectedCat.getGroupId());
        } else {
            mPresenter.getMainQuestionnaireListFromLocal(0);
        }
    }

    @OnClick(R2.id.btn_sub)
    public void onClickSub() {
        if (_selectedCat != null) {
            mPresenter.getSubQuestionnaireListFromLocal(_selectedCat.getGroupId());
        } else {
            mPresenter.getSubQuestionnaireListFromLocal(0);
        }
    }

    @Override
    public void refreshTitleView(int tabType) {
        if (tabType == 1) {
            btn_main.setEnabled(false);
            btn_sub.setEnabled(true);
            btn_main.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            btn_sub.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary));
        } else {
            btn_main.setEnabled(true);
            btn_sub.setEnabled(false);
            btn_main.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary));
            btn_sub.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
    }

    @Override
    public void initSampleInfoView(int status, String remarks, String visitTime) {
        if (status == SampleStatus.APPOINTMENT) {
            cl_head.setVisibility(View.VISIBLE);
            tv_revisit_remark.setText(remarks);
            tv_revisit_time.setText(visitTime);
        } else if (status == SampleStatus.REFUSE_VISIT || status == SampleStatus.AUDIT_INVALID || status == SampleStatus.AUDIT_RETURN) {
            cl_head.setVisibility(View.VISIBLE);
            tv_revisit_time.setVisibility(View.GONE);
            tv_01.setVisibility(View.GONE);
            tv_revisit_remark.setText(remarks);
        } else {
            cl_head.setVisibility(View.GONE);
        }
    }

    @Override
    public void initMyView(final List<Questionnaire> cats) {
        btn_main.setText(SPUtils.getInstance().getString(SPKey.APP_QUESTIONNAIRE_LIFT_TITLE, "主问卷"));
        btn_sub.setText(SPUtils.getInstance().getString(SPKey.APP_QUESTIONNAIRE_RIGHT_TITLE, "子问卷"));
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.rv_questionnaire);
        refreshLayout = (SmartRefreshLayout) mRootView.findViewById(R.id.srl_questionnaire);

        //顶部显示当前样本标识
        final LinkedList<MultiItemEntity> list = new LinkedList<>();
        BaseMultiItemQuickAdapter adapter = new SampleDetailAdapter(list, this);
        rv_sample_detail.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_sample_detail.addItemDecoration
                (BaseDecoration.create(ContextCompat.getColor(getContext(), R.color.divider_line), 1));
        rv_sample_detail.setAdapter(adapter);

        HashMap<String, String> sample = SampleDao.queryByIdAndSurveyId(userId, sampleId, surveyId);
        JSONArray jsonArray = JSON.parseArray(SPUtils.getInstance().getString(SPKey.SURVEY_SAMPLE_IDENTIFIER));
        int num = 0;
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                String key = jsonArray.getString(i);
                String value = sample.get(key);
                if (num < 3) {
                    PropertyListBean item = new PropertyListBean(SampleDetailItemType.LIST_BEAN, AppUtil.getSampleProperty(key), value, false, num);
                    list.add(item);
                    num ++;
                }
            }
        }
        String province = propertyValue("province", sample);
        String city = propertyValue("city", sample);
        String district = propertyValue("district", sample);
        String town = propertyValue("town", sample);
        String address = propertyValue("address", sample);
        final String sample_address = province + city + district + town + address;
        if (ifUseProperty("province") || ifUseProperty("city") || ifUseProperty("district") || ifUseProperty("town") || ifUseProperty("address")) {
            if ("".equals(sample_address) || "null".equals(sample_address)) {
                list.add(new PropertyListBean(SampleDetailItemType.LIST_BEAN, "样本地址", "", false, 4));
            } else {
                list.add(new PropertyListBean(SampleDetailItemType.LIST_BEAN, "样本地址", sample_address, true, 4));
            }
        }
        adapter.setNewData(list);
        adapter.notifyDataSetChanged();

        if (cats.size() > 1) {
            tl_questionnaire_group.removeAllTabs();
            int index = 0;
            for (int i = 0; i < cats.size(); i++) {
                Questionnaire t = cats.get(i);
                tl_questionnaire_group.addTab(tl_questionnaire_group.newTab().setText(t.getGroupName()));
                if (_selectedCat != null && _selectedCat.getGroupId() == t.getGroupId()) {
                    index = i;
                }
            }
            _selectedCat = cats.get(index);
            tl_questionnaire_group.getTabAt(index).select();
        } else {
            ll_questionnaire_group.setVisibility(View.GONE);
        }

        tl_questionnaire_group.setTabMode(TabLayout.MODE_SCROLLABLE);
        tl_questionnaire_group.setTabGravity(TabLayout.GRAVITY_FILL);
        tl_questionnaire_group.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int index = tab.getPosition();
                _selectedCat = cats.get(index);
                mPresenter.getMainQuestionnaireListFromLocal(_selectedCat.getGroupId());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        recycleAdapter = new QuestionnaireAdapter(new ArrayList<MultiItemEntity>(), this);
        recycleAdapter.setOnItemClickListener(this);
        recycleAdapter.setOnItemChildClickListener(this);
        recycleAdapter.setOnItemLongClickListener(this);
        refreshLayout.setDisableContentWhenLoading(true);
        refreshLayout.setDisableContentWhenRefresh(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration
                (BaseDecoration.create(ContextCompat.getColor(getContext(), R.color.divider_line), 1));
        recyclerView.setAdapter(recycleAdapter);
        refreshLayout.setOnRefreshListener(this);
    }

    private boolean ifUseProperty(String property) {
        String sample_property = SPUtils.getInstance().getString(SPKey.SURVEY_SAMPLE_PROPERTY);
        return sample_property.contains(property);
    }

    private String propertyValue(String property, HashMap<String, String> sample) {
        if (ifUseProperty(property)) {
            if (sample.containsKey(property) && sample.get(property) != null && !"".equals(sample.get(property)) && !"null".equals(sample.get(property))) {
                return sample.get(property);
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    @Override
    public void initTitleView() {
        ll_topbar_tab.setVisibility(View.GONE);
        tv_topbar_title.setVisibility(View.VISIBLE);
    }

    @Override
    public void initTitleTableView() {
        ll_topbar_tab.setVisibility(View.VISIBLE);
        tv_topbar_title.setVisibility(View.GONE);
    }

    @Override
    public void initRightView(boolean visible) {
        if (visible) {
            btn_topbar_right.setVisibility(View.VISIBLE);
        } else {
            btn_topbar_right.setVisibility(View.GONE);
        }
    }

    @Override
    public void showQuestionnaireList(List<MultiItemEntity> list) {
        recycleAdapter.setNewData(list);
        recycleAdapter.notifyDataSetChanged();
        refreshLayout.finishRefresh();
    }

    @Override
    public void showNetErrorView(String msg) {
        showErrorView(msg);
    }

    @Override
    public void showDeleteAlertCommit(final Response response) {
        if (response.getIsUpload() == 1 && response.getResponseStatus() == 0) {
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle("删除子答卷")
                    .setMessage("确定删除？")
                    .setCancelable(false)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPresenter.deleteSubResponse(response);
                        }
                    }).show();
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("答卷状态已改变，不能删除！");
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("网络不可用");
            if (_selectedCat != null) {
                mPresenter.getCurrentListFromLocal(_selectedCat.getGroupId());
            } else {
                mPresenter.getCurrentListFromLocal(0);
            }
        } else {
            mPresenter.syncData();
        }
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        MultiItemEntity itemEntity = (MultiItemEntity) adapter.getData().get(position);
        if (itemEntity instanceof Response) {
            showDeleteAlertCommit((Response) itemEntity);
        }
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MultiItemEntity item = (MultiItemEntity) adapter.getData().get(position);
        Integer itemType = item.getItemType();
        if (itemType == QuestionnaireItemType.QUESTIONNAIRE_SUB) {
            return;
        }
        mPresenter.openResponse((Response) item);
    }

    @Override
    public void onItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
        if (view.getId() == R.id.tv_questionnaire_sub_add) {
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle("确定")
                    .setMessage("确定生成子问卷？")
                    .setCancelable(false)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("生成", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Questionnaire subQuestionnaire = (Questionnaire) adapter.getData().get(position);
                            mPresenter.addSubResponse(subQuestionnaire);
                        }
                    }).show();
        }
    }

    //样本状态提交确认框
    @Override
    public void showSampleCommitDialog(final Sample sample) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        View view = View
                .inflate(getActivity(), R.layout.view_sample_submit_pop, null);
        builder.setView(view);
        builder.setCancelable(false);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tl_tab_group);
        et_remarks = (EditText) view.findViewById(R.id.et_remarks);
        tv_time_choice = (TextView) view.findViewById(R.id.tv_time_choice);
        final TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        final TextView tv_remarks = (TextView) view.findViewById(R.id.tv_remarks);
        final RadioGroup rg_sample_error = (RadioGroup) view.findViewById(R.id.rg_sample_error);
        final RadioButton rb_sample_refuse = (RadioButton) view.findViewById(R.id.rb_sample_refuse);
        final RadioButton rb_sample_contact_failed = (RadioButton) view.findViewById(R.id.rb_sample_contact_failed);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        //sample.setStatus(SampleStatus.FINISH);
                        tv_time.setVisibility(View.GONE);
                        tv_time_choice.setVisibility(View.GONE);
                        tv_remarks.setVisibility(View.GONE);
                        et_remarks.setVisibility(View.GONE);
                        rg_sample_error.setVisibility(View.GONE);
                        break;
                    case 1:
                        //sample.setStatus(SampleStatus.REFUSE_VISIT);
                        tv_time.setVisibility(View.INVISIBLE);
                        tv_time_choice.setVisibility(View.INVISIBLE);
                        tv_remarks.setVisibility(View.VISIBLE);
                        et_remarks.setVisibility(View.VISIBLE);
                        rg_sample_error.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        //sample.setStatus(SampleStatus.APPOINTMENT);
                        tv_time.setVisibility(View.VISIBLE);
                        tv_time_choice.setVisibility(View.VISIBLE);
                        tv_remarks.setVisibility(View.VISIBLE);
                        et_remarks.setVisibility(View.VISIBLE);
                        rg_sample_error.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.addTab(tabLayout.newTab().setText("成功访问"));
        tabLayout.addTab(tabLayout.newTab().setText("失败设定"));
        tabLayout.addTab(tabLayout.newTab().setText("预约回访"));
        final ConstraintLayout cl_pick_view = (ConstraintLayout) view.findViewById(R.id.cl_pick_view);
        final Button btn_cancel = (Button) view
                .findViewById(R.id.btn_cancel);//取消按钮
        final Button btn_confirm = (Button) view
                .findViewById(R.id.btn_confirm);//确定按钮
        tv_time_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickerView == null || !pickerView.isShowing()) {
                    showTimePick(cl_pick_view, sample);
                }
            }
        });
        //为什么要这里写变化了就获取状态，要是人家不点取消了呢
//        rg_sample_error.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                int i1 = radioGroup.getCheckedRadioButtonId();
//                if (i1 == R.id.rb_sample_refuse) {
//                    sample.setStatus(SampleStatus.REFUSE_VISIT);
//                } else if (i1 == R.id.rb_sample_contact_failed) {
//                    sample.setStatus(SampleStatus.NO_ANSWER);
//                }
//            }
//        });
        //取消或确定按钮监听事件处理
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleSubmitDialog.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tabLayout.getTabAt(0).isSelected()) {
                    String remarks = et_remarks.getText().toString().trim();
                    if (remarks.length() > 100) {
                        ToastUtils.setBgColor(Color.GRAY);
                        ToastUtils.showShort("不能超过100字符");
                        return;
                    }
                    if (tabLayout.getTabAt(2).isSelected()){
                        sample.setStatus(SampleStatus.APPOINTMENT);
                    }else{
                        if (rb_sample_refuse.isChecked()==true){
                            sample.setStatus(SampleStatus.REFUSE_VISIT);
                        }else if (rb_sample_contact_failed.isChecked()==true){
                            sample.setStatus(SampleStatus.NO_ANSWER);
                        }
                    }
                    sample.setDescription(remarks);
                    mPresenter.updateSample(sample);
                    SPUtils.getInstance().put("EDIT_MODE", 1);

                    sampleSubmitDialog.dismiss();
                    popTo(SampleDelegate.class, false);
                } else {
                    sample.setDescription("");
                    sample.setStatus(SampleStatus.FINISH);
                    if (mPresenter.setSampleStatusFinish()) {
                        mPresenter.updateSample(sample);
                        SPUtils.getInstance().put("EDIT_MODE", 1);

                        sampleSubmitDialog.dismiss();
                        popTo(SampleDelegate.class, false);
                    }else {
                        sampleSubmitDialog.dismiss();
                    }

                }
            }
        });
        sampleSubmitDialog = builder.create();
        sampleSubmitDialog.show();
    }

    private void showTimePick(ViewGroup v, final Sample sample) {
        Calendar startDate = Calendar.getInstance();
        pickerView = new TimePickerView.Builder(getContext(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                tv_time_choice.setText(TimeUtil.timePick(date));
                sample.setAppointVisitTime(date.getTime());
            }
        }).setRangDate(startDate, null)
                .setType(new boolean[]{true, true, true, true, true, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(18)//滚轮文字大小
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(ContextCompat.getColor(getContext(), R.color.primary))//标题文字颜色
                .setSubmitColor(ContextCompat.getColor(getContext(), R.color.primary))//确定按钮文字颜色
                .setCancelColor(ContextCompat.getColor(getContext(), R.color.primary))//取消按钮文字颜色
                .setTitleBgColor(ContextCompat.getColor(getContext(), R.color.white))//标题背景颜色 Night mode
                .setBgColor(ContextCompat.getColor(getContext(), R.color.white))//滚轮背景颜色 Night mode
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                //.isDialog(true)//是否显示为对话框样式
                //.setDecorView((ViewGroup) sampleSubmitDialog.getWindow().getDecorView())
                .setDecorView(v)
                .build();
        pickerView.show(false);
    }

    @Override
    public void showBottomMenu(ArrayList options, final Response response) {
        bottomMenuDialog = new BottomMenuDialog.Builder(getActivity())
                .setTitle("请选择模式")
                .addMenus(options, new IGlobalCallback() {
                    @Override
                    public void executeCallback(@Nullable Object args) {
                        int tag = (int) args;
                        bottomMenuDialog.dismiss();
                        mPresenter.openResponseWithType(response, tag);
                    }
                }).create();
        bottomMenuDialog.show();
    }

    public void showResponseActivity(Integer modeType, String msg, Response response, String newVersionResponseId) {
        openSurvey(modeType, msg, response, newVersionResponseId);
    }

    private void openSurvey(final Integer modeType, final String msg, final Response response, final String newVersionResponseId) {
        String jsFilename = response.getQuestionnaireUrl();
        if (StringUtils.isEmpty(jsFilename)) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("没有找到问卷文件！");
            return;
        }
        final File jsFile = new File(FileData.getQuestionnaireDir(SPUtils.getInstance().getInt(SPKey.USERID)) + jsFilename);
        if (!jsFile.exists()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("问卷文件没有成功下载！");
            return;
        }

        Intent intent = new Intent(getContext(), ResponseActivity.class);
        intent.putExtra("msg", msg);
        intent.putExtra("id", response.getId());
        intent.putExtra("name", response.getQuestionnaireName());
        intent.putExtra("responseIdentifier", response.getResponseIdentifier());
        intent.putExtra("sampleIdentifier", displayName);
        intent.putExtra("moduleId", response.getModuleId());
        intent.putExtra("moduleCode", response.getModuleCode());
        intent.putExtra("modeType", modeType);
        intent.putExtra("version", response.getVersion());
        intent.putExtra("questionnaireId", response.getQuestionnaireId());
        intent.putExtra("newVersionResponseId", newVersionResponseId);
        SurveyAccess.instance.showSurvey(getContext(), intent, jsFile);
    }

    @Override
    public void showLeadResponseDelegate(Integer modeType, String msg, Response response, String newVersionResponseId) {
        int resultCode = 1;
        start(LeadResponseDelegate.newInstance(modeType, msg, response, newVersionResponseId), resultCode);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SPUtils.getInstance().getInt("EDIT_MODE") == 1) {
            if (!NetworkUtil.isNetworkAvailable()) {
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort("网络不可用");
            } else {
                if(mPresenter != null){
                    mPresenter.syncData();
                }
            }
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

}
