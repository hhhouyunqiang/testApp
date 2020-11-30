package com.ringdata.ringinterview.capi.components.ui.survey.sample;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.TimePickerView;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.base.ui.recycler.BaseDecoration;
import com.ringdata.base.util.NetworkUtil;
import com.ringdata.base.utils.TimeUtil;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.ProjectConstants;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.constant.SampleStatus;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.SampleContactDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleDao;
import com.ringdata.ringinterview.capi.components.data.model.Sample;
import com.ringdata.ringinterview.capi.components.data.model.SampleContact;
import com.ringdata.ringinterview.capi.components.ui.survey.questionnaire.QuestionnaireDelegate;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.detail.PropertyListBean;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.detail.SampleDetailAdapter;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.detail.SampleDetailItemType;
import com.ringdata.ringinterview.capi.components.utils.AppUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ringdata.base.app.Latte.getApplicationContext;

/**
 * Created by admin on 17/11/1.
 */

public class CATICallDelegate extends LatteDelegate {

    @BindView(R2.id.tv_topbar_title)
    TextView tv_topbarTitle;
    @BindView(R2.id.sp_contact_name)
    Spinner sp_contact_name;
    @BindView(R2.id.sp_contact_phone)
    Spinner sp_contact_phone;
    @BindView(R2.id.rv_sample_property)
    RecyclerView rv_sample_identifier;
    @BindView(R2.id.ll_cati_setting)
    LinearLayout ll_cati_setting;
    @BindView(R2.id.tv_cati_appoint_time)
    TextView tv_cati_appoint_time;
    @BindView(R2.id.tv_cati_error_type)
    TextView tv_cati_error_type;
    @BindView(R2.id.ll_cati_next_sample)
    LinearLayout ll_cati_next_sample;

    android.support.v7.app.AlertDialog sampleSubmitDialog;
    private EditText et_remarks;
    private TextView tv_time_choice;
    private TimePickerView pickerView;

    private Integer currentUserId;
    private Integer currentSurveyId;
    private String currentSampleId;
    private Sample currentSample;
    private String tName;
    private String tPhone;
    private LinkedList<SampleContact> contactList;
    private List<String> nameList = new ArrayList<>();
    private boolean isMultiQues = false;

    @Override
    public Object setLayout() {
        return R.layout.delegate_cati_call;
    }

    // 二级联动adapter
    class spinnerItemSelected implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            tName = sp_contact_name.getSelectedItem().toString();
            List<String> phoneList = new ArrayList<>();
            if (position == 0) {
                if (currentSample.getPhone() != null) {
                    phoneList.add("手机：" + currentSample.getPhone());
                } else {
                    phoneList.add("手机：");
                }
                if (currentSample.getMobile() != null) {
                    phoneList.add("电话：" + currentSample.getMobile());
                } else {
                    phoneList.add("电话：");
                }
            } else {
                SampleContact item = (SampleContact) contactList.get(position - 1);
                phoneList.add("电话：" + item.getMobile());
                phoneList.add("手机：" + item.getPhone());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_contact_list, phoneList);
            sp_contact_phone.setAdapter(adapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        currentUserId = SPUtils.getInstance().getInt(SPKey.USERID);
        currentSurveyId = SPUtils.getInstance().getInt(SPKey.SURVEY_ID);
        currentSampleId = SPUtils.getInstance().getString(SPKey.SAMPLE_ID);
        isMultiQues = SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_SUPPORT_MULTI_QUESTIONNAIRE);
        if (SPUtils.getInstance().getInt(SPKey.SURVEY_SAMPLE_ASSIGN_TYPE) == ProjectConstants.SAMPLE_DYNAMIC_ASSIGN && (currentSampleId == null || "".equals(currentSampleId))) {
            getSampleFromNet();
        } else {
            currentSample = SampleDao.getById(currentUserId, currentSampleId);
            refreshSampleView();
            contactList = SampleContactDao.queryBySampleGuid(currentSampleId, currentUserId);
            nameList.clear();
            nameList.add("样本");
            for (MultiItemEntity item : contactList) {
                SampleContact contact = (SampleContact) item;
                nameList.add(contact.getName());
            }
        }
        if (SPUtils.getInstance().getInt(SPKey.SURVEY_SAMPLE_ASSIGN_TYPE) == ProjectConstants.SAMPLE_PRE_ASSIGN) {
            //样本若为预先分配，则隐藏"下一样本"
            ll_cati_next_sample.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        tv_topbarTitle.setText(SPUtils.getInstance().getString(SPKey.SURVEY_NAME));

        ArrayAdapter<String> phoneAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_contact_list, nameList);
        sp_contact_name.setAdapter(phoneAdapter);
        sp_contact_name.setOnItemSelectedListener(new spinnerItemSelected());
        sp_contact_phone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tPhone = sp_contact_phone.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @OnClick(R2.id.btn_cati_call_local)
    void onClickLocalCall() {
        if (tPhone != null && tPhone.length() > 3) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tPhone.substring(3)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("无联系人可拨号！");
        }
    }

    @OnClick(R2.id.btn_cati_call_behalf)
    void onClickBehalfCall() {

    }

    @OnClick(R2.id.view_cati_appoint)
    void onClickAppoint() {
        if (currentSample != null) {
            showDialog(1);
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("未获取到样本，不能进行预约回访！");
        }
    }

    @OnClick(R2.id.view_cati_error)
    void onClickError() {
        if (currentSample != null) {
            showDialog(2);
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("未获取到样本，不能进行失败设定！");
        }
    }

    private void showDialog(final int type) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        View view = View
                .inflate(getActivity(), R.layout.view_cati_sample_submit_pop, null);
        builder.setView(view);
        builder.setCancelable(false);
        et_remarks = (EditText) view.findViewById(R.id.et_remarks);
        tv_time_choice = (TextView) view.findViewById(R.id.tv_time_choice);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        TextView tv_remarks = (TextView) view.findViewById(R.id.tv_remarks);
        RadioGroup rg_sample_error = (RadioGroup) view.findViewById(R.id.rg_sample_error);
        if (type == 1) {
            currentSample.setStatus(SampleStatus.APPOINTMENT);
            tv_time.setVisibility(View.VISIBLE);
            tv_time_choice.setVisibility(View.VISIBLE);
            tv_remarks.setVisibility(View.VISIBLE);
            et_remarks.setVisibility(View.VISIBLE);
            rg_sample_error.setVisibility(View.GONE);
        } else {
            currentSample.setStatus(SampleStatus.REFUSE_VISIT);
            tv_time.setVisibility(View.INVISIBLE);
            tv_time_choice.setVisibility(View.INVISIBLE);
            tv_remarks.setVisibility(View.VISIBLE);
            et_remarks.setVisibility(View.VISIBLE);
            rg_sample_error.setVisibility(View.VISIBLE);
        }
        final ConstraintLayout cl_pick_view = (ConstraintLayout) view.findViewById(R.id.cl_pick_view);
        final Button btn_cancel = (Button) view
                .findViewById(R.id.btn_cancel);//取消按钮
        final Button btn_confirm = (Button) view
                .findViewById(R.id.btn_confirm);//确定按钮
        tv_time_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickerView == null || !pickerView.isShowing()) {
                    showTimePick(cl_pick_view, currentSample);
                }
            }
        });
        rg_sample_error.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int i1 = radioGroup.getCheckedRadioButtonId();
                if (i1 == R.id.rb_sample_refuse) {
                    currentSample.setStatus(SampleStatus.REFUSE_VISIT);
                } else if (i1 == R.id.rb_sample_contact_failed) {
                    currentSample.setStatus(SampleStatus.NO_ANSWER);
                } else if (i1 == R.id.rb_sample_contact_invalid) {
                    currentSample.setStatus(SampleStatus.INVALID);
                }
            }
        });
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
                String remarks = et_remarks.getText().toString().trim();
                if (remarks.length() > 100) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("不能超过100字符");
                    return;
                }
                currentSample.setDescription(remarks);
                currentSample.setIsUpload(1);
                if (SampleDao.insertOrUpdate(currentSample)) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("更新本地样本状态成功");
                } else {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("更新本地样本状态失败");
                }
                sampleSubmitDialog.dismiss();
                if (type == 1) {
                    tv_cati_appoint_time.setText(TimeUtil.question(currentSample.getAppointVisitTime()));
                    tv_cati_error_type.setText("");
                } else {
                    if (currentSample.getStatus() == SampleStatus.REFUSE_VISIT) {
                        tv_cati_error_type.setText("拒访");
                    } else if (currentSample.getStatus() == SampleStatus.NO_ANSWER) {
                        tv_cati_error_type.setText("无人接听");
                    } else if (currentSample.getStatus() == SampleStatus.INVALID) {
                        tv_cati_error_type.setText("无效号码");
                    }
                    tv_cati_appoint_time.setText("");
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

    @OnClick(R2.id.view_cati_start)
    void onClickGoDescribe() {
        if (currentSample != null) {
            start(new QuestionnaireDelegate());
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("未获取到样本，不能开始填答！");
        }
    }

    @OnClick(R2.id.ll_cati_next_sample)
    void onClickNextSample() {
        startWithPop(new CATICallDelegate());
    }

    private void getSampleFromNet() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", currentSurveyId);
        RestClient.builder().url(App.orgHost + ApiUrl.GET_NEXT_SAMPLE)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (responseJson.getSuccess()) {
                            JSONObject data = responseJson.getDataAsObject();
                            currentSample = SampleDao.getSampleFromNet(data.getJSONObject("sample"), currentUserId, currentSurveyId);
                            currentSampleId = data.getJSONObject("sample").getString("sampleGuid");
                            refreshSampleView();
                            contactList = SampleContactDao.getListFromNet(data.getJSONArray("contactList"), currentUserId, currentSurveyId, currentSampleId);
                            SampleContactDao.insertOrUpdateList(contactList);
                            nameList.clear();
                            nameList.add("样本");
                            for (MultiItemEntity item : contactList) {
                                SampleContact contact = (SampleContact) item;
                                nameList.add(contact.getName());
                            }
                        } else {
                            ToastUtils.setBgColor(Color.GRAY);
                            ToastUtils.showShort(responseJson.getMsg());
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

    private void refreshSampleView() {
        LinkedList<MultiItemEntity> list = new LinkedList<>();
        BaseMultiItemQuickAdapter adapter = new SampleDetailAdapter(list, this);
        rv_sample_identifier.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_sample_identifier.addItemDecoration
                (BaseDecoration.create(ContextCompat.getColor(getContext(), R.color.divider_line), 1));
        rv_sample_identifier.setAdapter(adapter);
        //顶部显示当前样本标识
        HashMap<String, String> sample = SampleDao.queryByIdAndSurveyId(currentUserId, currentSampleId, currentSurveyId);
        JSONArray sampleIdentifierJsonArray = JSON.parseArray(SPUtils.getInstance().getString(SPKey.SURVEY_SAMPLE_IDENTIFIER));
        int num = 0;
        if (sampleIdentifierJsonArray != null) {
            final int size = sampleIdentifierJsonArray.size();
            for (int i = 0; i < size; i++) {
                String key = sampleIdentifierJsonArray.getString(i);
                String value = sample.get(key);
                if (num < 3) {
                    PropertyListBean item = new PropertyListBean(SampleDetailItemType.LIST_BEAN, AppUtil.getSampleProperty(key), value, false, num);
                    list.add(item);
                    num++;
                }
            }
        }
        adapter.setNewData(list);
        adapter.notifyDataSetChanged();

        if (isMultiQues || currentSample.getAssignmentType() != 1 ||
                (currentSample.getStatus() != SampleStatus.APPOINTMENT && currentSample.getStatus() != SampleStatus.EXECUTION)) {
            //只有单问卷项目的主访问员在样本进行中或预约时才可在此页面修改样本状态
            ll_cati_setting.setVisibility(View.GONE);
        } else {
            switch (currentSample.getStatus()) {
                case SampleStatus.APPOINTMENT:
                    if (currentSample.getAppointVisitTime() != null) {
                        tv_cati_appoint_time.setText(TimeUtil.question(currentSample.getAppointVisitTime()));
                    }
                    break;
                case SampleStatus.IN_CALL:
                    tv_cati_error_type.setText("通话中");
                    break;
                case SampleStatus.REFUSE_VISIT:
                    tv_cati_error_type.setText("拒访");
                    break;
                case SampleStatus.NO_ANSWER:
                    tv_cati_error_type.setText("无人接听");
                    break;
                case SampleStatus.INVALID:
                    tv_cati_error_type.setText("无效号码");
                    break;
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            if (!NetworkUtil.isNetworkAvailable()) {
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort("网络不可用");
                return;
            }
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
