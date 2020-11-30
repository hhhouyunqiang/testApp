package com.ringdata.ringinterview.capi.components.ui.survey;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.ui.recycler.BaseDecoration;
import com.ringdata.base.ui.recycler.SpaceItemDecoration;
import com.ringdata.base.util.NetworkUtil;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.ProjectConstants;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.AppVersion;
import com.ringdata.ringinterview.capi.components.ui.ScanActivity;
import com.ringdata.ringinterview.capi.components.ui.mine.MineDelegate;
import com.ringdata.ringinterview.capi.components.ui.survey.message.MessageDelegate;
import com.ringdata.ringinterview.capi.components.utils.AppUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import bakerj.backgrounddarkpopupwindow.BackgroundDarkPopupWindow;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Created by admin on 17/10/20.
 */
public class SurveyDelegate extends LatteDelegate implements OnRefreshListener, SurveyContract.View {

    @BindView(R2.id.tv_topbar_title)
    TextView tv_topbar_title;
    @BindView(R2.id.btn_topbar_mes)
    ImageView btn_topbar_mes;
    @BindView(R2.id.sv_survey)
    SearchView sv_survey;
    @BindView(R2.id.ll_survey_tab)
    LinearLayout ll_survey_tab;
    @BindView(R2.id.tab_survey_type)
    TextView text_type_down;
    @BindView(R2.id.tab_survey_status)
    TextView text_status_down;
    @BindView(R2.id.tab_survey_sort)
    TextView text_sort_down;

    private SurveyContract.Presenter mPresenter;
    private BackgroundDarkPopupWindow surveyPopWindow;
    private BackgroundDarkPopupWindow typePopWindow;
    private BackgroundDarkPopupWindow statusPopWindow;
    private BackgroundDarkPopupWindow sortPopWindow;

    private String currentType; //当前项目类型
    private int currentStatus = 4; //当前项目状态
    private int currentSort = ProjectConstants.PROJECT_SORT_BY_CREATE_TIME; //当前排序类型

    @Override
    public Object setLayout() {
        return R.layout.delegate_survey;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        setStatusBar();
        mPresenter = new SurveyPresenter(this,getActivity());
        mPresenter.start();
        RxPermissions rxPermission = new RxPermissions(getActivity());
        rxPermission
                .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean permission) throws Exception {
                        if (permission) {
                            getActivity().startService(App.locationService);
                            AppVersion.checkAppVersion(getActivity());
                        }
                    }
                });
        SPUtils.getInstance().put(SPKey.USERTYPE, ProjectConstants.PROJECT_ALL);

        //设置搜索文本监听
        int id = sv_survey.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) sv_survey.findViewById(id);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//字体、提示字体大小
        LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) textView.getLayoutParams();
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER_VERTICAL);

        //去掉搜索框的下划线
        if (sv_survey != null) {
            try {        //--拿到字节码
                Class<?> argClass = sv_survey.getClass();
                //--指定某个私有属性,mSearchPlate是搜索框父布局的名字
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                //--暴力反射,只有暴力反射才能拿到私有属性
                ownField.setAccessible(true);
                View mView = (View) ownField.get(sv_survey);
                //--设置背景
                mView.setBackgroundColor(Color.TRANSPARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sv_survey.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!NetworkUtil.isNetworkAvailable()) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("网络不可用，即将查询本地数据...");
                    mPresenter.queryListByKeyword(query);
                } else {
                    mPresenter.querySurveyListFromNet(query, currentType, currentStatus, currentSort);
                    refreshLayout.finishRefresh();
                }
                sv_survey.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!NetworkUtil.isNetworkAvailable()) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("网络不可用，即将查询本地数据...");
                    mPresenter.queryListByKeyword(newText);
                } else {
                    mPresenter.querySurveyListFromNet(newText, currentType, currentStatus, currentSort);
                    refreshLayout.finishRefresh();
                }
                return false;
            }
        });
    }

    @OnClick(R2.id.btn_topbar_left)
    void onClickMine() {
        getSupportDelegate().start(new MineDelegate());
    }

    @OnClick(R2.id.btn_topbar_down)
    void onClickSurveyMenu() {
        sv_survey.clearFocus();
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_survey_menu, null);
        if (surveyPopWindow == null) {
            surveyPopWindow = new BackgroundDarkPopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            surveyPopWindow.setFocusable(true);
            surveyPopWindow.setBackgroundDrawable(new BitmapDrawable());
            surveyPopWindow.setDarkStyle(-1);
            surveyPopWindow.setDarkColor(Color.parseColor("#99000000"));
            surveyPopWindow.resetDarkPosition();
            surveyPopWindow.darkBelow(tv_topbar_title);
        }
        surveyPopWindow.showAsDropDown(tv_topbar_title);
        Button btn_all_survey = (Button) contentView.findViewById(R.id.item_survey_all);
        Button btn_create_survey = (Button) contentView.findViewById(R.id.item_survey_create);
        Button btn_mine_survey = (Button) contentView.findViewById(R.id.item_survey_mine);
        btn_all_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surveyPopWindow.dismiss();
                tv_topbar_title.setText(getResources().getString(R.string.all_survey));
                querySurveyListTopbar(ProjectConstants.PROJECT_ALL);
            }
        });
        btn_create_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surveyPopWindow.dismiss();
                tv_topbar_title.setText(getResources().getString(R.string.create_survey));
                querySurveyListTopbar(ProjectConstants.PROJECT_CREATED_BY_ME);
            }
        });
        btn_mine_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surveyPopWindow.dismiss();
                tv_topbar_title.setText(getResources().getString(R.string.mine_survey));
                querySurveyListTopbar(ProjectConstants.PROJECT_DONE_BY_ME);
            }
        });
    }

    private void querySurveyListTopbar(int topType) {
        SPUtils.getInstance().put(SPKey.USERTYPE, topType);
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("网络不可用，即将查询本地数据...");
            mPresenter.getSurveyListFromLocal();
        } else {
            mPresenter.querySurveyListFromNet("", currentType, currentStatus, currentSort);
            refreshLayout.finishRefresh();
        }
    }

    @OnClick(R2.id.btn_topbar_scan)
    void onClickScan() {
        if (AppUtil.hasPermission(getContext(), Manifest.permission.CAMERA)) {
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
            // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setCaptureActivity(ScanActivity.class); //设置打开摄像头的Activity
            integrator.setPrompt("请扫描二维码"); //底部的提示文字，设为""可以置空
            integrator.setCameraId(0); //前置或者后置摄像头
            integrator.setBeepEnabled(true); //扫描成功的「哔哔」声，默认开启
            integrator.setBarcodeImageEnabled(true);
            integrator.initiateScan();
        } else {
            String[] requests = new String[]{Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(getActivity(), requests, 1001);
        }
    }

    @OnClick(R2.id.btn_topbar_mes)
    void onClickMessage() {
        getSupportDelegate().start(new MessageDelegate());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            if (scanResult.getContents() == null) {
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort("取消扫描");
            } else {
                if (scanResult.getContents().length() > 8) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("扫码登录")
                            .setMessage("确认登录？")
                            .setCancelable(false)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mPresenter.scanLogIn(scanResult.getContents());
                                }
                            }).show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("申请加入项目")
                            .setMessage("确认加入？")
                            .setCancelable(false)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mPresenter.joinProject(scanResult.getContents());
                                }
                            }).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @OnClick(R2.id.survey_type_down)
    void onClickType() {
        sv_survey.clearFocus();
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_survey_type_menu, null);
        if (typePopWindow == null) {
            typePopWindow = new BackgroundDarkPopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            typePopWindow.setFocusable(true);
            typePopWindow.setBackgroundDrawable(new BitmapDrawable());
            typePopWindow.setDarkStyle(-1);
            typePopWindow.setDarkColor(Color.parseColor("#99000000"));
            typePopWindow.resetDarkPosition();
            typePopWindow.darkBelow(ll_survey_tab);
        }
        typePopWindow.showAsDropDown(ll_survey_tab);
        Button btn_survey_all = (Button) contentView.findViewById(R.id.item_survey_all);
        Button btn_survey_cawi = (Button) contentView.findViewById(R.id.item_survey_cawi);
        Button btn_survey_capi = (Button) contentView.findViewById(R.id.item_survey_capi);
        Button btn_survey_cati = (Button) contentView.findViewById(R.id.item_survey_cati);
        Button btn_survey_cadi = (Button) contentView.findViewById(R.id.item_survey_cadi);
        Button btn_survey_caxi = (Button) contentView.findViewById(R.id.item_survey_caxi);
        btn_survey_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typePopWindow.dismiss();
                text_type_down.setText("全部");
                querySurveyListByType(null);
            }
        });
        btn_survey_cawi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typePopWindow.dismiss();
                text_type_down.setText("网络调查");
                querySurveyListByType(ProjectConstants.CAWI_PROJECT);
            }
        });
        btn_survey_capi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typePopWindow.dismiss();
                text_type_down.setText("面访调查");
                querySurveyListByType(ProjectConstants.CAPI_PROJECT);
            }
        });
        btn_survey_cati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typePopWindow.dismiss();
                text_type_down.setText("电话调查");
                querySurveyListByType(ProjectConstants.CATI_PROJECT);
            }
        });
        btn_survey_cadi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typePopWindow.dismiss();
                text_type_down.setText("在线录入");
                querySurveyListByType(ProjectConstants.CADI_PROJECT);
            }
        });
        btn_survey_caxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typePopWindow.dismiss();
                text_type_down.setText("混合调查");
                querySurveyListByType(ProjectConstants.CAXI_PROJECT);
            }
        });
    }

    private void querySurveyListByType(String type) {
        currentType = type;
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("网络不可用，即将查询本地数据...");
            mPresenter.queryListByType(type);
        } else {
            mPresenter.querySurveyListFromNet("", currentType, currentStatus, currentSort);
            refreshLayout.finishRefresh();
        }
    }

    @OnClick(R2.id.survey_status_down)
    void onClickStatus() {
        sv_survey.clearFocus();
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_survey_status_menu, null);
        if (statusPopWindow == null) {
            statusPopWindow = new BackgroundDarkPopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            statusPopWindow.setFocusable(true);
            statusPopWindow.setBackgroundDrawable(new BitmapDrawable());
            statusPopWindow.setDarkStyle(-1);
            statusPopWindow.setDarkColor(Color.parseColor("#99000000"));
            statusPopWindow.resetDarkPosition();
            statusPopWindow.darkBelow(ll_survey_tab);
        }
        statusPopWindow.showAsDropDown(ll_survey_tab);
        Button btn_survey_all = (Button) contentView.findViewById(R.id.item_survey_all);
        Button btn_survey_prepare = (Button) contentView.findViewById(R.id.item_survey_prepare);
        Button btn_survey_start = (Button) contentView.findViewById(R.id.item_survey_start);
        Button btn_survey_finish = (Button) contentView.findViewById(R.id.item_survey_finish);
        Button btn_survey_stop = (Button) contentView.findViewById(R.id.item_survey_stop);
        btn_survey_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPopWindow.dismiss();
                text_status_down.setText("全部");
                querySurveyListByStatus(4);
            }
        });
        btn_survey_prepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPopWindow.dismiss();
                text_status_down.setText("准备中");
                querySurveyListByStatus(ProjectConstants.STATUS_WAIT);
            }
        });
        btn_survey_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPopWindow.dismiss();
                text_status_down.setText("已启动");
                querySurveyListByStatus(ProjectConstants.STATUS_RUN);
            }
        });
        btn_survey_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPopWindow.dismiss();
                text_status_down.setText("已结束");
                querySurveyListByStatus(ProjectConstants.STATUS_FINISH);
            }
        });
        btn_survey_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPopWindow.dismiss();
                text_status_down.setText("已暂停");
                querySurveyListByStatus(ProjectConstants.STATUS_PAUSE);
            }
        });
    }

    private void querySurveyListByStatus(int status) {
        currentStatus = status;
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("网络不可用，即将查询本地数据...");
            mPresenter.queryListByStatus(status);
        } else {
            mPresenter.querySurveyListFromNet("", currentType, currentStatus, currentSort);
            refreshLayout.finishRefresh();
        }
    }

    @OnClick(R2.id.survey_sort_down)
    void onClickSort() {
        sv_survey.clearFocus();
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_survey_sort_menu, null);
        if (sortPopWindow == null) {
            sortPopWindow = new BackgroundDarkPopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            sortPopWindow.setFocusable(true);
            sortPopWindow.setBackgroundDrawable(new BitmapDrawable());
            sortPopWindow.setDarkStyle(-1);
            sortPopWindow.setDarkColor(Color.parseColor("#99000000"));
            sortPopWindow.resetDarkPosition();
            sortPopWindow.darkBelow(ll_survey_tab);
        }
        sortPopWindow.showAsDropDown(ll_survey_tab);
        Button btn_create_time = (Button) contentView.findViewById(R.id.item_survey_create_time);
        Button btn_update_time = (Button) contentView.findViewById(R.id.item_survey_update_time);
        btn_create_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPopWindow.dismiss();
                text_sort_down.setText("创建时间");
                querySurveyListByOrder(ProjectConstants.PROJECT_SORT_BY_CREATE_TIME);
            }
        });
        btn_update_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPopWindow.dismiss();
                text_sort_down.setText("更新时间");
                querySurveyListByOrder(ProjectConstants.PROJECT_SORT_BY_UPDATE_TIME);
            }
        });
    }

    private void querySurveyListByOrder(int sort) {
        currentSort = sort;
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("网络不可用，即将查询本地数据...");
            mPresenter.queryListByOrder(sort);
        } else {
            mPresenter.querySurveyListFromNet("", currentType, currentStatus, currentSort);
            refreshLayout.finishRefresh();
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("网络不可用！即将加载本地数据...");
            mPresenter.getSurveyListFromLocal();
        } else {
            mPresenter.getSurveyListFromNet();
            text_type_down.setText("全部");
            currentType = null;
            text_status_down.setText("状态");
            currentSort = 4;
            text_sort_down.setText("排序");
            currentSort = ProjectConstants.PROJECT_SORT_BY_CREATE_TIME;
            refreshlayout.finishRefresh();
        }
    }

    @Override
    public void initMyView() {
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.rv_survey);
        refreshLayout = (SmartRefreshLayout) mRootView.findViewById(R.id.srl_survey);

        recycleAdapter = new SurveyAdapter(new ArrayList<MultiItemEntity>(), this);
        recyclerView.addOnItemTouchListener(new ItemClickListener(this));
        refreshLayout.setDisableContentWhenLoading(true);
        refreshLayout.setDisableContentWhenRefresh(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration
                (BaseDecoration.create(ContextCompat.getColor(getContext(), R.color.divider_line), 1));
        recyclerView.addItemDecoration(new SpaceItemDecoration(30));
        recyclerView.addItemDecoration
                (BaseDecoration.create(ContextCompat.getColor(getContext(), R.color.divider_line), 1));
        recyclerView.setAdapter(recycleAdapter);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void refreshTopRightMessageView(int count) {
        btn_topbar_mes.setImageResource(count > 0 ? R.drawable.message_read : R.drawable.message);
    }

    @Override
    public void showSurveyList(List<MultiItemEntity> list) {
        recycleAdapter.setNewData(list);
        recycleAdapter.notifyDataSetChanged();
        refreshLayout.finishRefresh();
    }

    @Override
    public void showSyncErrorView(String msg) {
        showErrorView(msg);
    }

    @Override
    public void showNetErrorView(String title, String msg) {
        showErrorView(title, msg);
    }

    @Override
    public void initTitleView(String title) {
        tv_topbar_title.setText(title);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setStatusBar();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setStatusBar();
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public final boolean onBackPressedSupport() {
        _mActivity.moveTaskToBack(true);
        return true;
    }

    @Override
    public void onDestroy() {
        mPresenter.destroy();
        super.onDestroy();
    }
}