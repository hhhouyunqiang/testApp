package com.ringdata.ringinterview.capi.components.ui.survey.response;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.czt.mp3recorder.MP3Recorder;
import com.ringdata.base.ui.camera.CameraImageBean;
import com.ringdata.base.ui.camera.LatteCamera;
import com.ringdata.base.ui.camera.RequestCodes;
import com.ringdata.base.util.GUIDUtil;
import com.ringdata.base.util.callback.CallbackManager;
import com.ringdata.base.util.callback.CallbackType;
import com.ringdata.base.util.callback.IGlobalCallback;
import com.ringdata.base.util.file.FileUtil;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.ProjectConstants;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.constant.SampleStatus;
import com.ringdata.ringinterview.capi.components.data.FileData;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseAudioFileDao;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseDao;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseQuestionFileDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleDao;
import com.ringdata.ringinterview.capi.components.data.model.Response;
import com.ringdata.ringinterview.capi.components.data.model.Sample;
import com.ringdata.ringinterview.capi.components.helper.BDLocationHelper;
import com.ringdata.ringinterview.capi.components.utils.LocationTool;
import com.ringdata.ringinterview.survey.SoftKeyboardLsnedRelativeLayout;
import com.ringdata.ringinterview.survey.SurveyActivity;
import com.ringdata.ringinterview.survey.jsbridge.JSBridge;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * Created by admin on 17/11/4.
 */
public class ResponseActivity extends SurveyActivity {
    private int currentUserId;
    private int currentSurveyId;
    private String currentSampleId;
    private String title;
    private String display;
    AlertDialog dialog;
    private Unbinder mUnbinder;
    private Response response;
    private Sample sample;

    private HashMap<String, Object> responseAudioFile = new HashMap<>();//答卷录音对应名称数据
    private long QRecordBeginPos = 0L;
    private long QRecordEndPos = 0L;
    private long mCountBeginTime;
    private long responseBeginRecordTime;
    private long questionnaireBeginRecordTime;
    private String responseRecordFilename;
    private String questionnaireRecordFilename;
    private String currentSurveyType;

    private boolean isSubmitAndExit = false;//是否是提交退出情况,
    private boolean isAlertExit = false;//是否是退出情况,
    private boolean isQuestionnaireRecording = false;
    private Boolean showToast = false;

    private JSBridge.Callback signatureCallback;
    private MP3Recorder mRecorder;
    private MediaRecorder recorder; //录音的一个实例
    private PopupWindow myPopWindow;
    private PopupWindow titlePopWindow;

    private static int UPLOAD_PIC_FAILED = 20;//上传答卷图片失败
    private static final int UPLOAD_VOICE_FAILED = 21;//上传答卷录音失败
    private Boolean Bback = true;
    @BindView(R2.id.tv_topbar_title)
    public TextView tv_title;
    @BindView(R2.id.btn_font_size)
    public Button btnFont;
    @BindView(R2.id.ll_bottom_bar)
    public ViewGroup ll_bottomBar;
    @BindView(R2.id.rl_response_prev)
    public RelativeLayout rl_response_prev;
    @BindView(R2.id.rl_response_next)
    public RelativeLayout rl_response_next;
    @BindView(R2.id.tv_next_response)
    public TextView tv_next_response;
    @BindView(R2.id.itv_next_response)
    public TextView itv_next_response;
    @BindView(R2.id.itv_prev_response)
    public TextView itv_prev_response;
    @BindView(R2.id.rl_response_save)
    public RelativeLayout rl_save;
    Context context;
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        context=ResponseActivity.this;
        //测试打电话时候能监听录音保存
//        Intent intent1=new Intent(ResponseActivity.this,MonitorRecordingService.class);
//        startService(intent1);
        //获得电话管理器
//        TelephonyManager tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        //启动监听.传入一个listener和监听的事件,
//        tm.listen(new MyListener(), PhoneStateListener.LISTEN_CALL_STATE);

        mCountBeginTime = new Date().getTime();
        currentUserId = SPUtils.getInstance().getInt(SPKey.USERID);
        currentSurveyId = SPUtils.getInstance().getInt(SPKey.SURVEY_ID);
        currentSampleId = SPUtils.getInstance().getString(SPKey.SAMPLE_ID);
        currentSurveyType = SPUtils.getInstance().getString(SPKey.SURVEY_TYPE);
        //问卷标题
        title = intent.getStringExtra("name");
        display = title;
        editType = intent.getIntExtra("modeType", 1);
        String msg = intent.getStringExtra("msg");
        Integer version = intent.getIntExtra("version", 0);
        String id = intent.getStringExtra("id");
        int moduleId = intent.getIntExtra("moduleId", 0);
        int questionnaireId = intent.getIntExtra("questionnaireId", 0);
        String modelCode = intent.getStringExtra("moduleCode");
        int type = intent.getIntExtra("type", 0);
        String newVersionResponseId = intent.getStringExtra("newVersionResponseId");
        sample = SampleDao.getById(currentUserId, currentSampleId);
        if (TextUtils.isEmpty(id)) {
            response = new Response();
            response.setId(GUIDUtil.getGuidStr());
            if (!TextUtils.isEmpty(newVersionResponseId)) {
                response.setId(newVersionResponseId);
            }
            response.setInterviewerId(currentUserId);
            response.setVersion(version);
            response.setUserId(currentUserId);
            response.setSampleGuid(currentSampleId);
            response.setProjectId(currentSurveyId);
            response.setQuestionnaireId(questionnaireId);
            response.setModuleId(moduleId);
            response.setModuleCode(modelCode);
            response.setCreateTime(new Date().getTime());
            response.setStartTime(new Date().getTime());
//            response.setType(type);
            response.setIsUpload(1);
        } else {
            response = ResponseDao.queryById(currentUserId, id);
        }
        if (response.getStartTime() == null || response.getStartTime() == 0) {
            response.setStartTime(new Date().getTime());
        }
        String outerData = sample.getExtraData();
        sample.setExtraData(null);
        String[] vars = new String[]{
                response.getSubJson(),
                App.surveyVariable,
        };

        sample.setExtraData(outerData);
        String[] surveyExterns = new String[]{
                sample.getResponseVariable(),
                outerData,
                JSON.toJSONString(sample)
        };

        SurveyActivity.survey_variables = vars;
        SurveyActivity.survey_externs = surveyExterns;

        survey_data = response.getSubmitData();
        if ("".equals(survey_data)) {
            survey_data = null;
        }
        survey_state = response.getSubmitState();
        if ("{}".equals(survey_state)) {
            survey_state = null;
        }
        if (!TextUtils.isEmpty(msg)) {
            //ToastUtils.showShort(msg);
            Toast toast=Toast.makeText(getApplicationContext(), msg+"", Toast.LENGTH_SHORT);
            toast.show();
        }
        //答卷标识若存在，则拼接在问卷标题后面（限制长度）
        if (response.getResponseIdentifier() != null && !response.getResponseIdentifier().equals("")) {
            title = title + "（" + response.getResponseIdentifier() + "）";
            display = title;
        }
        if (title != null && title.length() > 10) {
            display = title.substring(0, 9) + "...";
        }
    }

    @OnClick(R2.id.tv_topbar_title)
    void onClickTitle() {
        if (title.length() > 10) {
            if (titlePopWindow != null && titlePopWindow.isShowing()) {
                titlePopWindow.dismiss();
            } else {
                initTitlePop(title);
            }
        }
    }

    private void initTitlePop(String whole) {
        TextView textView = new TextView(getActivity());
        textView.setBackgroundColor(Color.parseColor("#90000000"));
        textView.setPadding(20, 20, 20, 20);
        textView.setTextSize(16);
        textView.setTextColor(Color.WHITE);
        textView.setText(whole);
        titlePopWindow = new PopupWindow(textView, ConvertUtils.dp2px(200), ConvertUtils.dp2px(60), true);
        titlePopWindow.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
        titlePopWindow.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        titlePopWindow.showAsDropDown(tv_title);
    }

    @Override
    protected void createView() {
        setStatusBar();
        setContentView(R.layout.activity_survey_response);
        mUnbinder = ButterKnife.bind(this);
        _webview = (WebView) findViewById(R.id.wv_response);
        _webview.getSettings().setSupportZoom(true);
        _webview.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        SoftKeyboardLsnedRelativeLayout layout = (SoftKeyboardLsnedRelativeLayout) findViewById(R.id.view_root);
        layout.addSoftKeyboardLsner(new SoftKeyboardLsnedRelativeLayout.SoftKeyboardLsner() {
            @Override
            public void onSoftKeyboardShow() {
                ll_bottomBar.setVisibility(View.GONE);
            }

            @Override
            public void onSoftKeyboardHide() {
                ll_bottomBar.setVisibility(View.VISIBLE);
                endEditing();
            }
        });
    }

    @Override
    public void updateViewState(JSONObject state) {
        rl_response_next.setVisibility(View.VISIBLE);
        rl_response_prev.setVisibility(View.VISIBLE);
        rl_save.setVisibility(View.VISIBLE);

        //查看模式
        if (editType == SurveyActivity.RS2_I_EDIT_VIEW || editType == SurveyActivity.RS2_I_EDIT_PREVIEW) {
            btnFont.setVisibility(View.VISIBLE);
            rl_save.setVisibility(View.INVISIBLE);
        } else { //编辑模式
            if (SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_NEED_RESPONSE_POS)) {
                //openLocationService(context);
                beginResponseLocation(); //定位
            }
            if (sample.getStatus() != 2) { //不是进行中状态，未开始状态 、2次答卷已完成状态
                if(SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_SUPPORT_TWICE_SUBMIT) && sample.getStatus() == 6){
                    //二次答卷 已完成进去没点提交退出编辑模式，状态码不变
                }else {
                    SampleDao.updateSampleStatus(currentUserId, currentSampleId, 2);
                }
            }
            if ((super._item == null || super._item.recording) && currentSurveyType.equals(ProjectConstants.CAPI_PROJECT)) {
                responseBeginRecord();
            }
        }
//        btnRemark.setVisibility(hasRemark ? View.VISIBLE : View.GONE);
//        if (state != null) {
//            try {
//                hasRemark = state.getBoolean("has_remark");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }
     //判断是否开始定位服务
    public static void openLocationService(Context context) {

        LocationManager manager;
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPS && !isNetwork) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("请开启定位服务！");
//            Intent intent = new Intent();
//            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
        }
    }


    @Override
    protected void beginLocation(final JSBridge.Callback callback) {
        RxPermissions rxPermission = new RxPermissions(getActivity());
        rxPermission
                .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean permission) throws Exception {
                        if (permission) {
                            BDLocationHelper.initConfig(new BDAbstractLocationListener() {
                                @Override
                                public void onReceiveLocation(BDLocation location) {
                                    BDLocationHelper.stop();
                                    float radius = location.getRadius();    //获取定位精度，默认值为0.0f
                                    int code = location.getLocType();
                                    if (code == 61 || code == 161) {//定位成功
                                        location = LocationTool.BAIDU_to_WGS84(location);
                                        final float latitude = (float) location.getLatitude();    //获取纬度信息
                                        final float longitude = (float) location.getLongitude();    //获取经度信息
                                        final String address = location.getAddrStr();//获取位置
                                        endLocation(callback, latitude + "", longitude + "", address, null);
                                    } else {
                                        endLocation(callback, null, null, null, ",请开启定位服务!");
                                    }
                                }
                            });
                            BDLocationHelper.start();
                        }
                    }
                });
    }

    @Override
    protected void removeResource(int type, String name, String qid) {
        FileData.removeFile(currentUserId, name);
    }

    @Override
    protected String resourceURI(int type, String name) {
        return FileData.getSurveyDir(currentUserId) + name;
    }

    @Override
    protected void picture(final JSBridge.Callback callback, final String qid) {
        takePicture(qid, callback);
    }

    private void takePicture(final String qid, final JSBridge.Callback callback) {
        CallbackManager.getInstance()
                .addCallback(CallbackType.ON_CROP, new IGlobalCallback<Uri>() {
                    @Override
                    public void executeCallback(final Uri args) {
                        String filename = FileUtils.getFileName(args.getPath());
                        if (TextUtils.isEmpty(filename)) {
                            endPicture(callback, "", "", "文件保存失败");
                            response.setResponseStatus(UPLOAD_PIC_FAILED);
                            return;
                        }
                        String filepath = "/project-file/" + currentSurveyId + "/attach/" + filename;
                        Boolean isSuccess = ResponseQuestionFileDao.insertLocalFile(currentUserId, currentSurveyId, response.getId(), qid, filename, filepath,"png", new Date().getTime());
                        if (!isSuccess) {
                            endPicture(callback, "", "", "文件保存失败");
                            response.setResponseStatus(UPLOAD_PIC_FAILED);
                            return;
                        }
                        endPicture(callback, args.getPath(), filename, null);
                    }
                });
        RxPermissions rxPermission = new RxPermissions(getActivity());
        rxPermission
                .request(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean permission) throws Exception {
                        if (permission) {
                            SPUtils.getInstance().put(SPKey.CAMERA_IF_HEADIMAGE, false);
                            LatteCamera.start(getActivity(), FileData.getSurveyDir(currentUserId) + GUIDUtil.getGuidStr() + ".png");
                        }
                    }
                });
    }

    @Override
    protected void beginRecording(final JSBridge.Callback callback, String qid) {
        super.beginRecording(callback, qid);
        if (mRecorder != null && mRecorder.isRecording() && currentSurveyType.equals(ProjectConstants.CAPI_PROJECT)) {
            responseEndRecord();
        }
        RxPermissions rxPermission = new RxPermissions(getActivity());
        rxPermission
                .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.WAKE_LOCK)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean permission) throws Exception {
                        if (permission) {
                            questionnaireBeginRecordTime = new Date().getTime();
                            questionnaireRecordFilename = GUIDUtil.getGuidStr() + ".mp3";
                            File recordFile = FileUtil.createFile(FileData.getSurveyDir(currentUserId) + questionnaireRecordFilename);
                            //RecordHelper.getInstance(getApplicationContext()).startRecording(recordFile);
                            mRecorder = new MP3Recorder(recordFile);
                            mRecorder.start();
                            isQuestionnaireRecording = true;
                            callback.callbackOnUIThread(true, null);
                        } else {
                            callback.callbackOnUIThread(false, null);
                        }
                    }
                });
    }

    @Override
    protected void endRecording(JSBridge.Callback callback, String qid) {
        super.endRecording(callback, qid);
        isQuestionnaireRecording = false;
        if (mRecorder != null && mRecorder.isRecording()) {
            mRecorder.stop();
        }
        if (TextUtils.isEmpty(questionnaireRecordFilename)) {
            callback.callbackOnUIThread(false, null);
            return;
        }
        String filepath = "/project-file/" + currentSurveyId + "/attach/" + questionnaireRecordFilename;
        Boolean isSuccess = ResponseQuestionFileDao.insertLocalFile(currentUserId, currentSurveyId, response.getId(), qid, questionnaireRecordFilename, filepath,"mp3", new Date().getTime());
        JSONObject obj = new JSONObject();
        if (!isSuccess) {
            callback.callbackOnUIThread(false, null);
            return;
        }
        int length = (int) ((new Date().getTime() - questionnaireBeginRecordTime) / 1000);
        String uri = FileData.getSurveyDir(currentUserId) + questionnaireRecordFilename;
        File questionnaireRecordFile = new File(uri);
        try {
            if (questionnaireRecordFile != null && questionnaireRecordFile.length() > 0) {
                obj.put("uri", uri);
                obj.put("name", questionnaireRecordFilename);
                obj.put("length", length);
            } else {
                obj.put("message", "录音失败");
                response.setResponseStatus(UPLOAD_VOICE_FAILED);
            }
        } catch (JSONException e) {
        }
        callback.callbackOnUIThread(uri != null, obj);
        if (currentSurveyType.equals(ProjectConstants.CAPI_PROJECT)) {
            responseBeginRecord();
        }
    }

    @Override
    protected void updateTitle(int index, int total) {
        //tv_title.setText("第" + (index + 1) + "页 / 共" + total + "页");
        tv_title.setText(display);
    }

    @Override
    protected void doNext() {
        if (isQuestionnaireRecording) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("请先结束录音");
            return;
        }
        //去调取js方法并传参
        super.doNext();

        if (_started && editType == SurveyActivity.RS2_I_EDIT_INTERVIEW && !_exit) {
            showToast = false;
            doSave();
            //录音切分
            if (mRecorder != null && mRecorder.isRecording() && SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_NEED_RESPONSE_AUDIO, false)) {
                if (TextUtils.isEmpty(responseRecordFilename)) {
                    return;
                }
                QRecordEndPos = (new Date().getTime() - responseBeginRecordTime) / 1000;
                HashMap<String, Object> partAudio = new HashMap<>();
                partAudio.put("user_id", currentUserId);
                partAudio.put("project_id", currentSurveyId);
                partAudio.put("response_guid", response.getId());
                partAudio.put("local_filename", responseRecordFilename);
                partAudio.put("file_path", "/project-file/" + currentSurveyId + "/voice/" + responseRecordFilename);
                partAudio.put("type", 2); //录音类型 1:答卷录音 2:单题录音
                partAudio.put("begin_pos", QRecordBeginPos);
                partAudio.put("end_pos", QRecordEndPos);
                partAudio.put("audio_duration", QRecordEndPos - QRecordBeginPos);
                partAudio.put("create_time", new Date().getTime());
                Boolean isSuccess = ResponseAudioFileDao.insert(partAudio);
                if (!isSuccess) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("录音数据保存异常");
                }
                QRecordBeginPos = QRecordEndPos;
            }
        }
    }

    @Override
    protected void updatePrev(boolean hasPrev) {
        rl_response_next.setEnabled(true);
        itv_next_response.setTextColor(ContextCompat.getColor(this, R.color.primary));
        tv_next_response.setTextColor(ContextCompat.getColor(this, R.color.primary));
        itv_prev_response.setTextColor(hasPrev ? ContextCompat.getColor(this, R.color.primary) : ContextCompat.getColor(this, R.color.bottom_text));
    }

    protected void updateNext(boolean exit, boolean lastStep) {
        if ((exit || lastStep) && (editType == SurveyActivity.RS2_I_EDIT_VIEW || editType == SurveyActivity.RS2_I_EDIT_PREVIEW)) {
            rl_response_next.setEnabled(false);
            itv_next_response.setTextColor(ContextCompat.getColor(this, R.color.bottom_text));
            tv_next_response.setTextColor(ContextCompat.getColor(this, R.color.bottom_text));
        }
        itv_next_response.setText((exit || lastStep) ? "{icon-check}" : "{icon-right-black}");
        tv_next_response.setText((exit || lastStep) ? "提交" : "下一页");
    }

    @OnClick(R2.id.rl_response_save)
    public void onSaveClicked() {
        if (isQuestionnaireRecording) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("请先结束录音");
            return;
        }
        if (mRecorder != null && mRecorder.isRecording()) {
            mRecorder.stop();
            mRecorder = null;
        }
        Long duration = 0L;
        if (response.getResponseDuration() != null) {
            duration = response.getResponseDuration();
        }
        response.setResponseDuration(duration + (new Date().getTime() - mCountBeginTime) / 1000);
        showToast = true;
        Bback=false;
        doSave();
    }

    @Override
    protected void signature(JSBridge.Callback callback, String qid) {
        super.signature(callback, qid);
        this.signatureCallback = callback;
    }

    @OnClick(R2.id.rl_response_prev)
    public void onClickPrev() {
        doPrev();
    }

    @OnClick(R2.id.rl_response_next)
    public void onClickNext() {
        if (_exit || _lastStep) {
            confirmSubmit();
        } else {
            doNext();
        }
    }

    @OnClick(R2.id.btn_font_size)
    void onClickFontSize(View v) {
        if (myPopWindow != null && myPopWindow.isShowing()) {
            myPopWindow.dismiss();
        } else {
            initMyPopWindow();
            myPopWindow.showAsDropDown(v);
        }
    }

    @OnClick(R2.id.icBtn_topbar_history)
    void onClickHistory() {
        toggleMenu();
    }

//    @OnClick(R2.id.icBtn_topbar_mark)
//    void onClickMark() {
//        toggleRemark();
//    }

    private void initMyPopWindow() {
        //设置字体大小下拉框
        View customView = getActivity().getLayoutInflater().inflate(R.layout.pop_font_size, null, false);
        myPopWindow = new PopupWindow(customView, ConvertUtils.dp2px(60), ConvertUtils.dp2px(120), true);
        myPopWindow.setBackgroundDrawable(new ColorDrawable(0));
        final TextView bigTextView = (TextView) customView.findViewById(R.id.font_size_big);
        final TextView mediumTextView = (TextView) customView.findViewById(R.id.font_size_medium);
        final TextView smallTextView = (TextView) customView.findViewById(R.id.font_size_small);
        if (_webview.getSettings().getTextSize() == WebSettings.TextSize.NORMAL) {
            mediumTextView.setBackgroundColor(Color.parseColor("#5e000000"));
        } else if (_webview.getSettings().getTextSize() == WebSettings.TextSize.LARGER) {
            bigTextView.setBackgroundColor(Color.parseColor("#5e000000"));
        } else if (_webview.getSettings().getTextSize() == WebSettings.TextSize.SMALLER) {
            smallTextView.setBackgroundColor(Color.parseColor("#5e000000"));
        }
        bigTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bigTextView.setBackgroundColor(Color.parseColor("#5e000000"));
                mediumTextView.setBackgroundColor(Color.argb(0,0,0,0));
                smallTextView.setBackgroundColor(Color.argb(0,0,0,0));
                _webview.getSettings().setTextSize(WebSettings.TextSize.LARGER);
                myPopWindow.dismiss();
            }
        });
        mediumTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediumTextView.setBackgroundColor(Color.parseColor("#5e000000"));
                bigTextView.setBackgroundColor(Color.argb(0,0,0,0));
                smallTextView.setBackgroundColor(Color.argb(0,0,0,0));
                _webview.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
                myPopWindow.dismiss();
            }
        });
        smallTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smallTextView.setBackgroundColor(Color.parseColor("#5e000000"));
                bigTextView.setBackgroundColor(Color.argb(0,0,0,0));
                mediumTextView.setBackgroundColor(Color.argb(0,0,0,0));
                _webview.getSettings().setTextSize(WebSettings.TextSize.SMALLER);
                myPopWindow.dismiss();
            }
        });
    }

    //开始答卷录音
    private void responseBeginRecord() {
        if (!SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_NEED_RESPONSE_AUDIO)) {
            return;
        }
        RxPermissions rxPermission = new RxPermissions(getActivity());
        rxPermission
                .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.WAKE_LOCK)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean permission) throws Exception {
                        if (permission) {
                            responseBeginRecordTime = new Date().getTime();
                            responseRecordFilename = GUIDUtil.getGuidStr() + ".mp3";
                            File recordFile = FileUtil.createFile(FileData.getSurveyDir(currentUserId) + responseRecordFilename);
                            //RecordHelper.getInstance(getApplicationContext()).startRecording(recordFile);
                            mRecorder = new MP3Recorder(recordFile);
                            mRecorder.start();
                        }
                    }
                });
    }

    //停止答卷录音
    private void responseEndRecord() {
        if (!SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_NEED_RESPONSE_AUDIO)) {
            return;
        }
        if (mRecorder != null && mRecorder.isRecording()) {
            mRecorder.stop();
            mRecorder = null;
            Long newTime = new Date().getTime();
            responseAudioFile.put("user_id", currentUserId);
            responseAudioFile.put("project_id", currentSurveyId);
            responseAudioFile.put("response_guid", response.getId());
            responseAudioFile.put("local_filename", responseRecordFilename);
            responseAudioFile.put("file_path", "/project-file/" + currentSurveyId + "/voice/" + responseRecordFilename);
            responseAudioFile.put("type", 1); //录音类型 1:答卷录音 2:单题录音
            responseAudioFile.put("start_time", responseBeginRecordTime);
            responseAudioFile.put("end_time", newTime);
            responseAudioFile.put("audio_duration", newTime - responseBeginRecordTime);
            responseAudioFile.put("create_time", newTime);
            Boolean success = ResponseAudioFileDao.insert(responseAudioFile);
            if (!success) {
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort("录音数据保存失败");
                response.setResponseStatus(UPLOAD_VOICE_FAILED);
            }
        }
    }

    //答卷定位
    private void beginResponseLocation() {
        RxPermissions rxPermission = new RxPermissions(getActivity());
        rxPermission
                .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean permission) throws Exception {
                        if (permission) {
                            BDLocationHelper.initConfig(new BDAbstractLocationListener() {
                                @Override
                                public void onReceiveLocation(BDLocation location) {
                                    BDLocationHelper.stop();
                                    float radius = location.getRadius(); //获取定位精度，默认值为0.0f
                                    int code = location.getLocType();
                                    if (code == 61 || code == 161) { //定位成功
                                        location = LocationTool.BAIDU_to_WGS84(location);
                                        final float latitude = (float) location.getLatitude(); //获取纬度信息
                                        final float longitude = (float) location.getLongitude(); //获取经度信息
                                        final String address = location.getAddrStr(); //获取位置
                                        response.setLon(longitude + "");
                                        response.setLat(latitude + "");
                                        response.setAddress(address);
                                    }
                                    BDLocationHelper.stop();
                                }
                            });
                            BDLocationHelper.start();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 001) {
            String filename = data.getStringExtra("signatureFilename");
            String filepath = data.getStringExtra("signatureFilepath");
            endPicture(signatureCallback, filepath, filename, null);
            return;
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestCodes.TAKE_PHOTO:
                    final Uri resultUri = CameraImageBean.getInstance().getPath();
                    UCrop.of(resultUri, resultUri)
                            .useSourceImageAspectRatio()
                            .withMaxResultSize(1024, 960)
                            .start(this);
                    break;
                case RequestCodes.PICK_PHOTO:
                    if (data != null) {
                        final Uri pickPath = data.getData();
                        //从相册选择后需要有个路径存放剪裁过的图片
                        final String pickCropPath = FileData.getSurveyDir(currentUserId) + GUIDUtil.getGuidStr() + ".png";
                        UCrop.of(pickPath, Uri.parse(pickCropPath))
                                .useSourceImageAspectRatio()
                                .withMaxResultSize(1024, 960)
                                .start(this);
                    }
                    break;
                case RequestCodes.CROP_PHOTO:
                    final Uri cropUri = UCrop.getOutput(data);
                    //拿到剪裁后的数据进行处理
                    @SuppressWarnings("unchecked") final IGlobalCallback<Uri> callback = CallbackManager
                            .getInstance()
                            .getCallback(CallbackType.ON_CROP);
                    if (callback != null) {
                        callback.executeCallback(cropUri);
                    }
                    break;
                case RequestCodes.CROP_ERROR:
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("剪裁出错");
                    break;
                default:
                    break;
            }
        }
    }

    @OnClick(R2.id.icTv_topbar_back)
    void exitConfirm() {
        dialog = new AlertDialog.Builder(this)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!_started || editType == SurveyActivity.RS2_I_EDIT_VIEW || editType == SurveyActivity.RS2_I_EDIT_PREVIEW || isSubmitAndExit) {//答卷加载异常，预览模式直接退出,做完主动提交
                            dialog.dismiss();
                            finish();
                        } else {
                            if (isQuestionnaireRecording) {
                                ToastUtils.setBgColor(Color.GRAY);
                                ToastUtils.showShort("请先结束录音");
                                return;
                            }
                            Long duration = 0L;
                            if (response.getResponseDuration() != null) {
                                duration = response.getResponseDuration();
                            }
                            response.setResponseDuration(duration + (new Date().getTime() - mCountBeginTime) / 1000);
                            showToast = true;
                            doSave();
                        }
                        isAlertExit = true;
                        Bback=false;//界面不是从屏幕边界滑动返回
                    }
                }).create();

        dialog.setCancelable(false);
        dialog.setTitle("退出");
        dialog.setMessage("确定退出当前问卷？");
        dialog.show();
    }

    @Override
    protected void onSubmitData(String state, String data, final JSONObject extra) {
        response.setIsUpload(1);
        response.setResponseType(1);
        isSubmitAndExit = true;
        if (_exit || _lastStep) { //_exit为true表示问卷全部做完
            response.setResponseStatus(6);
        }
        if (currentSurveyType.equals(ProjectConstants.CAPI_PROJECT)) {
            responseEndRecord();
        }
        //保存跨模块传值的数据
        SampleDao.addResponseVariable(com.alibaba.fastjson.JSONObject.parseObject(data), currentSampleId, response.getModuleCode());
        try {
            if (extra.getJSONObject("submit_extra") != null) {
                //把数据先存在主答卷,防止数据丢失
                response.setSubJson(extra.toString());
                com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(extra.toString());
                com.alibaba.fastjson.JSONObject submitExtra = jsonObject.getJSONObject("submit_extra");
                com.alibaba.fastjson.JSONArray subQuestionnaireJsonArray = submitExtra.getJSONArray("sub_questionnaire");
                if (subQuestionnaireJsonArray != null) {
                    ResponseDao.insertSubResponse(subQuestionnaireJsonArray, currentSampleId, currentUserId, currentSurveyId);
                }
                com.alibaba.fastjson.JSONArray jsonArray = submitExtra.getJSONArray("sub_sample");
                if (jsonArray != null) {
                    SampleDao.addSamples(jsonArray, currentUserId, currentSurveyId);
                }
            }
        } catch (Exception e) {
            Log.i("exception", e.getMessage());
        }
        //统计答卷时长
        Long duration = 0L;
        if (response.getResponseDuration() != null) {
            duration = response.getResponseDuration();
        }
        response.setResponseDuration(duration + (new Date().getTime() - mCountBeginTime) / 1000);
        saveResponseData(state, data);
        if (sample.getAssignmentType() == 1 && !SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_SUPPORT_MULTI_QUESTIONNAIRE)&& !SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_SUPPORT_TWICE_SUBMIT)) {
            //具有访问员权限的 在提交答卷后，自动修改样本状态为已完成（单问卷项目）
            SampleDao.updateSampleStatus(currentUserId, currentSampleId, SampleStatus.FINISH);
        }
        finish();
    }

    @Override
    protected void onSaveData(String state, String data, JSONObject extra) {
        response.setIsUpload(1);
        response.setResponseStatus(1);
        response.setResponseType(2);
//        if (mRecorder != null && mRecorder.isRecording()) {
//            mRecorder.stop();
//            mRecorder = null;
//        }
        String name = "";
        if (extra != null) {
            try {
                JSONObject jsonObject = (JSONObject) extra.get("tags");
                if (jsonObject != null) {
                    response.setResponseIdentifier(jsonObject.toString());
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String value = "" + jsonObject.getString(key);
                        name += value;
                    }
                }
            } catch (Exception e) {
                Log.i("exception", e.getMessage());
            }
            if (!TextUtils.isEmpty(name)) {
//                response.setQuestionnaireName(name.trim());
            }
            saveResponseData(state, data);
        }
    }

    //保存答卷数据
    private void saveResponseData(String state, String data) {
        response.setEndTime(new Date().getTime());
        response.setSubmitState(state);
        response.setSubmitData(data);
        response.setResponseData(JSON.parseObject(data).getJSONObject("value_dict").toJSONString());
        response.setQuestionData(JSON.parseObject(data).getJSONObject("prop_dict").toJSONString());
        response.setIsDownloadDetail(2);
        boolean isSuccess = ResponseDao.replace(response);
        if (!isSuccess) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("保存失败");
        } else if (showToast) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("保存成功");
        }
        if (isAlertExit) {
            dialog.dismiss();
            finish();
        }
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.parseColor("#EFEFEF"));
            //设置状态栏文字颜色及图标为深色，当状态栏为白色时候，改变其颜色为深色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //exitConfirm();
            if(Bback){  //滑动退出时有数据要保存下，要不然答卷丢失
                //且不是查看模式下
                if (editType != SurveyActivity.RS2_I_EDIT_VIEW && editType != SurveyActivity.RS2_I_EDIT_PREVIEW) {
                    if (mRecorder != null && mRecorder.isRecording()) {
                        mRecorder.stop();
                        mRecorder = null;
                    }
                    Long duration = 0L;
                    if (response.getResponseDuration() != null) {
                        duration = response.getResponseDuration();
                    }
                    response.setResponseDuration(duration + (new Date().getTime() - mCountBeginTime) / 1000);
                    doSave();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    class  MyListener extends PhoneStateListener {
        //在电话状态改变的时候调用
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    if (mRecorder != null && mRecorder.isRecording()) {
                        mRecorder.stop();
                        mRecorder = null;
                    }
                    //响铃状态  需要在响铃状态的时候初始化录音服务
                    if (recorder == null) {
                        recorder = new MediaRecorder();//初始化录音对象
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置录音的输入源(麦克)
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//设置音频格式(3gp)
                        createRecorderFile();//创建保存录音的文件夹

                        recorder.setOutputFile("sdcard/recorder" + "/" + getCurrentTime() + ".3gp"); //设置录音保存的文件
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置音频编码
                        try {
                            recorder.prepare();//准备录音
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态（接听）
                    if (recorder != null) {
                        recorder.start(); //接听的时候开始录音
                    }
                    break;
            }
        }

        //创建保存录音的目录
        private void createRecorderFile() {
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String filePath = absolutePath + "/recorder";
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        }

        //获取当前时间，以其为名来保存录音
        private String getCurrentTime() {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            String str = format.format(date);
            return str;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (editType != SurveyActivity.RS2_I_EDIT_VIEW && editType != SurveyActivity.RS2_I_EDIT_PREVIEW) {
            if (mRecorder != null && mRecorder.isRecording()) {
                mRecorder.stop();
                mRecorder = null;
            }
            Long duration = 0L;
            if (response.getResponseDuration() != null) {
                duration = response.getResponseDuration();
            }
            response.setResponseDuration(duration + (new Date().getTime() - mCountBeginTime) / 1000);
            doSave();
        }
    }

    @Override
    protected void onDestroy() {
        response = null;
        mUnbinder.unbind();
        super.onDestroy();
    }

}