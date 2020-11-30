package com.ringdata.ringinterview.capi.components.ui.survey.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.ui.recycler.BaseDecoration;
import com.ringdata.base.util.NetworkUtil;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.ProjectConstants;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.constant.SampleStatus;
import com.ringdata.ringinterview.capi.components.data.model.Sample;
import com.ringdata.ringinterview.capi.components.data.model.Survey;
import com.ringdata.ringinterview.capi.components.ui.survey.questionnaire.QuestionnaireDelegate;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.map.MapActivity;
import com.ringdata.ringinterview.capi.components.utils.AppUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import bakerj.backgrounddarkpopupwindow.BackgroundDarkPopupWindow;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 17/11/1.
 */

public class SampleDelegate extends LatteDelegate implements SampleContract.View, OnRefreshListener, BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemLongClickListener {

    @BindView(R2.id.tv_topbar_title)
    TextView tv_topbarTitle;
    @BindView(R2.id.tv_survey_sample_title)
    TextView tv_surveyTitle;
    @BindView(R2.id.tv_survey_sample_status)
    TextView tv_surveyStatus;
    @BindView(R2.id.ll_sample_top)
    LinearLayout ll_sample_top;
    @BindView(R2.id.btn_survey_sample_add)
    Button btn_add_sample;
    @BindView(R2.id.btn_cati_sample)
    LinearLayout btn_cati_sample;
    @BindView(R2.id.sv_survey_sample)
    SearchView sv_survey_sample;
    @BindView(R2.id.ll_sample_tab)
    LinearLayout ll_sample_tab;
    @BindView(R2.id.tab_sample_sort)
    TextView tv_sample_sort;
    @BindView(R2.id.tab_sample_status)
    TextView tv_sample_status;

    private SamplePresenter mPresenter;
    private View contentView;
    private BackgroundDarkPopupWindow statusPopWindow;
    private BackgroundDarkPopupWindow sortPopWindow;
    private int currentStatus = -1; //当前样本状态
    private int currentSort = 1; //当前排序类型

    @Override
    public Object setLayout() {
        return R.layout.delegate_sample;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mPresenter = new SamplePresenter(this, getActivity());
        mPresenter.start();
    }

    @OnClick(R2.id.icTv_map)
    void onClickGoMap() {
        Intent intent = new Intent(getContext(), MapActivity.class);
        getActivity().startActivity(intent);
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @OnClick(R2.id.btn_survey_sample_add)
    void addRandomSample() {
        getSupportDelegate().start(new SampleAddDelegate());
    }

    @OnClick(R2.id.sample_status_down)
    void onClickStatusMenu() {
        sv_survey_sample.clearFocus();
        String surveyType = SPUtils.getInstance().getString(SPKey.SURVEY_TYPE);
        if (statusPopWindow == null) {
            switch (surveyType) {
                case ProjectConstants.CATI_PROJECT:
                    contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_cati_sample_status_menu, null);
                    statusPopWindow = new BackgroundDarkPopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT);
                    statusPopWindow.setFocusable(true);
                    statusPopWindow.setBackgroundDrawable(new BitmapDrawable());
                    statusPopWindow.setDarkStyle(-1);
                    statusPopWindow.setDarkColor(Color.parseColor("#99000000"));
                    statusPopWindow.resetDarkPosition();
                    statusPopWindow.darkBelow(ll_sample_tab);
                    break;
                case ProjectConstants.CAPI_PROJECT:
                case ProjectConstants.CAXI_PROJECT:
                    contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_capi_sample_status_menu, null);
                    statusPopWindow = new BackgroundDarkPopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT);
                    statusPopWindow.setFocusable(true);
                    statusPopWindow.setBackgroundDrawable(new BitmapDrawable());
                    statusPopWindow.setDarkStyle(-1);
                    statusPopWindow.setDarkColor(Color.parseColor("#99000000"));
                    statusPopWindow.resetDarkPosition();
                    statusPopWindow.darkBelow(ll_sample_tab);
                    break;
                case ProjectConstants.CADI_PROJECT:
                case ProjectConstants.CAWI_PROJECT:
                    contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_cawi_sample_status_menu, null);
                    statusPopWindow = new BackgroundDarkPopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT);
                    statusPopWindow.setFocusable(true);
                    statusPopWindow.setBackgroundDrawable(new BitmapDrawable());
                    statusPopWindow.setDarkStyle(-1);
                    statusPopWindow.setDarkColor(Color.parseColor("#99000000"));
                    statusPopWindow.resetDarkPosition();
                    statusPopWindow.darkBelow(ll_sample_tab);
                    break;
            }
        }
        statusPopWindow.showAsDropDown(ll_sample_tab);
        Button sample_all = (Button) contentView.findViewById(R.id.item_sample_all);
        Button sample_preparing = (Button) contentView.findViewById(R.id.item_sample_preparing);
        Button sample_executing = (Button) contentView.findViewById(R.id.item_sample_executing);
        Button sample_book = (Button) contentView.findViewById(R.id.item_sample_book);
        Button sample_finish = (Button) contentView.findViewById(R.id.item_sample_finish);
        Button sample_refuse = (Button) contentView.findViewById(R.id.item_sample_refuse);
        Button sample_identify = (Button) contentView.findViewById(R.id.item_sample_examine);
        Button sample_calling = (Button) contentView.findViewById(R.id.item_sample_calling);
        Button sample_unanswer = (Button) contentView.findViewById(R.id.item_sample_unanswer);
        Button sample_num_invalid = (Button) contentView.findViewById(R.id.item_sample_invalid_number);
        Button sample_audit_invalid = (Button) contentView.findViewById(R.id.item_sample_invalid);
        Button sample_audit_return = (Button) contentView.findViewById(R.id.item_sample_return);
        Button sample_audit_success = (Button) contentView.findViewById(R.id.item_sample_success);
        sample_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPopWindow.dismiss();
                tv_sample_status.setText("全部");
                querySampleListByStatus(-1);
            }
        });
        sample_preparing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPopWindow.dismiss();
                tv_sample_status.setText("未开始");
                querySampleListByStatus(SampleStatus.ASSIGN);
            }
        });
        sample_executing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPopWindow.dismiss();
                tv_sample_status.setText("进行");
                querySampleListByStatus(SampleStatus.EXECUTION);
            }
        });
        if (sample_book != null) {
            sample_book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    statusPopWindow.dismiss();
                    tv_sample_status.setText("预约");
                    querySampleListByStatus(SampleStatus.APPOINTMENT);
                }
            });
        }
        sample_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPopWindow.dismiss();
                tv_sample_status.setText("完成");
                querySampleListByStatus(SampleStatus.FINISH);
            }
        });
        if (sample_refuse != null) {
            sample_refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    statusPopWindow.dismiss();
                    tv_sample_status.setText("拒访");
                    querySampleListByStatus(SampleStatus.REFUSE_VISIT);
                }
            });
        }
        sample_identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPopWindow.dismiss();
                tv_sample_status.setText("甄别");
                querySampleListByStatus(SampleStatus.IDENTIFY);
            }
        });
        if (sample_calling != null) {
            sample_calling.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    statusPopWindow.dismiss();
                    tv_sample_status.setText("通话中");
                    querySampleListByStatus(SampleStatus.IN_CALL);
                }
            });
        }
        if (sample_unanswer != null) {
            sample_unanswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    statusPopWindow.dismiss();
                    tv_sample_status.setText("无人接听");
                    querySampleListByStatus(SampleStatus.NO_ANSWER);
                }
            });
        }
        if (sample_num_invalid != null) {
            sample_num_invalid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    statusPopWindow.dismiss();
                    tv_sample_status.setText("无效号码");
                    querySampleListByStatus(SampleStatus.INVALID);
                }
            });
        }
        sample_audit_invalid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPopWindow.dismiss();
                tv_sample_status.setText("审核无效");
                querySampleListByStatus(SampleStatus.AUDIT_INVALID);
            }
        });
        sample_audit_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPopWindow.dismiss();
                tv_sample_status.setText("审核退回");
                querySampleListByStatus(SampleStatus.AUDIT_RETURN);
            }
        });
        sample_audit_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPopWindow.dismiss();
                tv_sample_status.setText("审核成功");
                querySampleListByStatus(SampleStatus.AUDIT_SUCCESS);
            }
        });
    }

    private void querySampleListByStatus(int status) {
        currentStatus = status;
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("网络不可用，即将查询本地数据...");
            mPresenter.queryListByStatus(status);
        } else {
            mPresenter.querySampleListFromNet("", currentStatus, currentSort);
        }
    }

    @OnClick(R2.id.sample_sort_down)
    void onClickSortMenu() {
        sv_survey_sample.clearFocus();
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_sample_sort_menu, null);
        if (sortPopWindow == null) {
            sortPopWindow = new BackgroundDarkPopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            sortPopWindow.setFocusable(true);
            sortPopWindow.setBackgroundDrawable(new BitmapDrawable());
            sortPopWindow.setDarkStyle(-1);
            sortPopWindow.setDarkColor(Color.parseColor("#99000000"));
            sortPopWindow.resetDarkPosition();
            sortPopWindow.darkBelow(ll_sample_tab);
        }
        sortPopWindow.showAsDropDown(ll_sample_tab);
        Button name_desc = (Button) contentView.findViewById(R.id.item_sample_name_desc);
        Button name_asc = (Button) contentView.findViewById(R.id.item_sample_name_asc);
        Button code_desc = (Button) contentView.findViewById(R.id.item_sample_num_desc);
        Button code_asc = (Button) contentView.findViewById(R.id.item_sample_num_asc);
        Button time_desc = (Button) contentView.findViewById(R.id.item_sample_answer_desc);
        Button time_asc = (Button) contentView.findViewById(R.id.item_sample_answer_asc);
        name_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPopWindow.dismiss();
                tv_sample_sort.setText("按样本名称降序");
                querySampleListByOrder(1);
            }
        });
        name_asc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPopWindow.dismiss();
                tv_sample_sort.setText("按样本名称升序");
                querySampleListByOrder(2);
            }
        });
        code_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPopWindow.dismiss();
                tv_sample_sort.setText("按样本编号降序");
                querySampleListByOrder(3);
            }
        });
        code_asc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPopWindow.dismiss();
                tv_sample_sort.setText("按样本编号升序");
                querySampleListByOrder(4);
            }
        });
        time_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPopWindow.dismiss();
                tv_sample_sort.setText("按填答时间降序");
                querySampleListByOrder(5);
            }
        });
        time_asc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPopWindow.dismiss();
                tv_sample_sort.setText("按填答时间升序");
                querySampleListByOrder(6);
            }
        });
    }

    private void querySampleListByOrder(int sort) {
        currentSort = sort;
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("网络不可用，即将查询本地数据...");
            mPresenter.queryListByOrder(sort);
        } else {
            mPresenter.querySampleListFromNet("", currentStatus, currentSort);
        }
    }

    //下拉刷新
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (!NetworkUtil.isNetworkAvailable()) {
            mPresenter.queryFromLocal("");
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("网络不可用，即将加载本地数据...");
        } else {
            SPUtils.getInstance().put("EDIT_MODE", 1);
            //列表回刷上面名称也变化
            tv_sample_status.setText("全部");
            currentStatus=-1;
            tv_sample_sort.setText("排序");
            currentSort=1;
            mPresenter.getSampleListFromNet();
        }
    }

    @Override
    public void initMyView() {
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.rv_survey_sample);
        refreshLayout = (SmartRefreshLayout) mRootView.findViewById(R.id.srl_survey_sample);
        recycleAdapter = new SampleAdapter(new ArrayList<MultiItemEntity>(), this);
        recycleAdapter.setOnItemClickListener(this);
        recycleAdapter.setOnItemLongClickListener(this);
        refreshLayout.setDisableContentWhenLoading(true);
        refreshLayout.setDisableContentWhenRefresh(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration
                (BaseDecoration.create(ContextCompat.getColor(getContext(), R.color.divider_line), 1));
        recyclerView.setAdapter(recycleAdapter);
        refreshLayout.setOnRefreshListener(this);

        int id = sv_survey_sample.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) sv_survey_sample.findViewById(id);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//字体、提示字体大小
        LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) textView.getLayoutParams();
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER_VERTICAL);

        //去掉搜索框的下划线
        if (sv_survey_sample != null) {
            try {        //--拿到字节码
                Class<?> argClass = sv_survey_sample.getClass();
                //--指定某个私有属性,mSearchPlate是搜索框父布局的名字
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                //--暴力反射,只有暴力反射才能拿到私有属性
                ownField.setAccessible(true);
                View mView = (View) ownField.get(sv_survey_sample);
                //--设置背景
                mView.setBackgroundColor(Color.TRANSPARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sv_survey_sample.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!NetworkUtil.isNetworkAvailable()) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("网络不可用，即将查询本地数据...");
                    mPresenter.queryFromLocal(query);
                } else {
                    mPresenter.querySampleListFromNet(query, currentStatus, currentSort);
                    refreshLayout.finishRefresh();
                }
                sv_survey_sample.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!NetworkUtil.isNetworkAvailable()) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("网络不可用，即将查询本地数据...");
                    mPresenter.queryFromLocal(newText);
                } else {
                    mPresenter.querySampleListFromNet(newText, currentStatus, currentSort);
                    refreshLayout.finishRefresh();
                }
                return false;
            }
        });
    }

    @Override
    public void initSurveyView(int id, String name, int status, String type, Survey survey) {
        tv_surveyTitle.setText(name);
        tv_surveyStatus.setText(AppUtil.getSurveyStatus(status));
        tv_surveyStatus.setTextColor(getResources().getColor(AppUtil.getSurveyStatusColor(status)));
        ll_sample_top.removeAllViews();
        switch (type) {
            case ProjectConstants.CATI_PROJECT: {
                View contentView = LayoutInflater.from(getContext()).inflate(R.layout.item_cati_sample_data, null);
                TextView preparing_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_preparing_number);
                preparing_num.setText(survey.getNumOfNotStarted() + "");
                TextView executing_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_executing_number);
                executing_num.setText(survey.getNumOfInProgress() + "");
                TextView book_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_book_number);
                book_num.setText(survey.getNumOfAppointment() + "");
                TextView submit_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_submit_number);
                submit_num.setText(survey.getNumOfFinish() + "");
                TextView refuse_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_refuse_number);
                refuse_num.setText(survey.getNumOfRefuse() + "");
                TextView examine_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_examine_number);
                examine_num.setText(survey.getNumOfIdentify() + "");
                TextView calling_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_calling_number);
                calling_num.setText(survey.getNumOfInTheCall() + "");
                TextView unanswer_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_unanswer_number);
                unanswer_num.setText(survey.getNumOfNoAnswer() + "");
                TextView error_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_error_number);
                error_num.setText(survey.getNumOfUnableToContact() + "");
                TextView invalid_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_invalid_number);
                invalid_num.setText(survey.getNumOfAuditInvalid() + "");
                TextView return_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_return_number);
                return_num.setText(survey.getNumOfReturn() + "");
                TextView success_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_success_number);
                success_num.setText(survey.getNumOfAuditSuccess() + "");
                ll_sample_top.addView(contentView);
                break;
            }
            case ProjectConstants.CAPI_PROJECT:
            case ProjectConstants.CAXI_PROJECT: {
                View contentView = LayoutInflater.from(getContext()).inflate(R.layout.item_capi_sample_data, null);
                TextView preparing_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_preparing_number);
                preparing_num.setText(survey.getNumOfNotStarted() + "");
                TextView executing_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_executing_number);
                executing_num.setText(survey.getNumOfInProgress() + "");
                TextView book_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_book_number);
                book_num.setText(survey.getNumOfAppointment() + "");
                TextView submit_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_submit_number);
                submit_num.setText(survey.getNumOfFinish() + "");
                TextView refuse_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_refuse_number);
                refuse_num.setText(survey.getNumOfRefuse() + "");
                TextView examine_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_examine_number);
                examine_num.setText(survey.getNumOfIdentify() + "");
                TextView unanswer_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_unanswer_number);
                unanswer_num.setText(survey.getNumOfNoAnswer() + "");
                TextView invalid_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_invalid_number);
                invalid_num.setText(survey.getNumOfAuditInvalid() + "");
                TextView return_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_return_number);
                return_num.setText(survey.getNumOfReturn() + "");
                TextView success_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_success_number);
                success_num.setText(survey.getNumOfAuditSuccess() + "");
                ll_sample_top.addView(contentView);
                break;
            }
            case ProjectConstants.CADI_PROJECT:
            case ProjectConstants.CAWI_PROJECT: {
                View contentView = LayoutInflater.from(getContext()).inflate(R.layout.item_cawi_sample_data, null);
                TextView preparing_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_preparing_number);
                preparing_num.setText(survey.getNumOfNotStarted() + "");
                TextView executing_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_executing_number);
                executing_num.setText(survey.getNumOfInProgress() + "");
                TextView submit_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_submit_number);
                submit_num.setText(survey.getNumOfFinish() + "");
                TextView examine_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_examine_number);
                examine_num.setText(survey.getNumOfIdentify() + "");
                TextView invalid_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_invalid_number);
                invalid_num.setText(survey.getNumOfAuditInvalid() + "");
                TextView return_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_return_number);
                return_num.setText(survey.getNumOfReturn() + "");
                TextView success_num = (TextView) contentView.findViewById(R.id.tv_survey_sample_success_number);
                success_num.setText(survey.getNumOfAuditSuccess() + "");
                ll_sample_top.addView(contentView);
                break;
            }
        }
    }

    @OnClick(R2.id.btn_cati_sample)
    void onClickCATI() {
        if (SPUtils.getInstance().getInt(SPKey.SURVEY_SAMPLE_ASSIGN_TYPE) == ProjectConstants.SAMPLE_PRE_ASSIGN) {
            if (recycleAdapter.getData().size() > 0) {
                Sample sample = (Sample) recycleAdapter.getData().get(0);
                startSample(sample);
                start(new CATICallDelegate());
            } else {
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort("样本不足，不能开始电话调查！");
            }
        } else {
            SPUtils.getInstance().remove(SPKey.SAMPLE_ID);
            start(new CATICallDelegate());
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Sample sample = (Sample) adapter.getData().get(position);
        startSample(sample);
        String currentSurveyType = SPUtils.getInstance().getString(SPKey.SURVEY_TYPE);
        if (currentSurveyType.equals(ProjectConstants.CATI_PROJECT)) {
            start(new CATICallDelegate());
        } else {
            start(new QuestionnaireDelegate());
        }
    }

    private void startSample(Sample sample) {
        String sampleId = sample.getSampleGuid();
        String sampleName = TextUtils.isEmpty(sample.getDisplayName()) ? sample.getName() : sample.getDisplayName();
        SPUtils.getInstance().put(SPKey.SAMPLE_ID, sampleId);
        SPUtils.getInstance().put(SPKey.SAMPLE_NAME, sampleName);
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        mPresenter.checkIsCanDelete((Sample) adapter.getData().get(position));
        return false;
    }

    @Override
    public void showSampleAddView(boolean canAdd, int assign, String surveyType) {
        if (surveyType.equals(ProjectConstants.CATI_PROJECT)) {
            if (assign == ProjectConstants.SAMPLE_DYNAMIC_ASSIGN) {
                btn_cati_sample.setVisibility(View.VISIBLE);
                btn_add_sample.setVisibility(View.GONE);
            }
        } else {
            if (canAdd) {
                btn_add_sample.setVisibility(View.VISIBLE);
            } else {
                btn_add_sample.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showDeleteAlertCommit(final Sample sample) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("确定")
                .setMessage("确定删除样本-(" + sample.getName() + ")？")
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
                        mPresenter.deleteSample(sample);
                    }
                }).show();
    }

    @Override
    public void toast(String msg) {
        ToastUtils.setBgColor(Color.GRAY);
        ToastUtils.showShort(msg);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showSampleList(List<MultiItemEntity> list) {
        tv_topbarTitle.setText("样本(" + list.size() + ")");

        recycleAdapter.setNewData(list);
        recycleAdapter.notifyDataSetChanged();
        refreshLayout.finishRefresh();
    }

    @Override
    public void showNetErrorView(String msg) {
        showErrorView(msg);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            if (!NetworkUtil.isNetworkAvailable()) {
                mPresenter.getSampleListFromLocal();
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort("网络不可用");
            } else {
                //mPresenter.querySampleListFromNet("", currentStatus, currentSort);
               mPresenter.getSampleListFromNet();
            }
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    private void selectSort(){

    }
}
