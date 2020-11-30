package com.ringdata.ringinterview.capi.components.ui.survey.questionnaire;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.ui.loader.LatteLoader;
import com.ringdata.base.ui.view.BottomMenuBean;
import com.ringdata.base.util.AsyncTaskUtil;
import com.ringdata.base.util.callback.AsyncCallBack;
import com.ringdata.base.utils.TimeUtil;
import com.ringdata.ringinterview.capi.components.constant.Msg;
import com.ringdata.ringinterview.capi.components.constant.OrgType;
import com.ringdata.ringinterview.capi.components.constant.ProjectConstants;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.constant.SampleStatus;
import com.ringdata.ringinterview.capi.components.data.DaoUtil;
import com.ringdata.ringinterview.capi.components.data.MessageEvent;
import com.ringdata.ringinterview.capi.components.data.dao.QuestionnaireDao;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleDao;
import com.ringdata.ringinterview.capi.components.data.dao.SurveyDao;
import com.ringdata.ringinterview.capi.components.data.model.Questionnaire;
import com.ringdata.ringinterview.capi.components.data.model.Response;
import com.ringdata.ringinterview.capi.components.data.model.Sample;
import com.ringdata.ringinterview.capi.components.data.model.Survey;
import com.ringdata.ringinterview.capi.components.data.remote.SyncManager;
import com.ringdata.ringinterview.survey.SurveyActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/6/30.
 */

public class QuestionnairePresenter implements QuestionnaireContract.Presenter {
    private final QuestionnaireContract.View mView;
    private Sample sample;
    private List<Questionnaire> cats = new ArrayList<>(); //分组目录
    private SyncManager syncManager;

    private final int mainType = 1;
    private final int subType = 2;
    public int currentType = mainType;

    ArrayList optionsA = new ArrayList();
    ArrayList optionsB = new ArrayList();
    ArrayList options = new ArrayList();
    private String dependencyQuesName;//未完成依赖问卷的名称
    private int dependency;
    Response latestResponse;

    private int currentUserId;
    private int currentSurveyId;
    private String currentSampleId;
    private int currentGroupId;

    private boolean SYNC_QUESTION_SUCCESS = false;
    private boolean SYNC_RESPONSE_SUCCESS = false;
    private boolean SYNC_PICTURE_SUCCESS = false;
    private boolean SYNC_VOICE_SUCCESS = false;

    public QuestionnairePresenter(QuestionnaireContract.View view, Context context) {
        this.mView = view;
        EventBus.getDefault().register(this);
        syncManager = new SyncManager(this, context);
    }

    @Override
    public void start() {
        currentUserId = SPUtils.getInstance().getInt(SPKey.USERID);
        currentSurveyId = SPUtils.getInstance().getInt(SPKey.SURVEY_ID);
        currentSampleId = SPUtils.getInstance().getString(SPKey.SAMPLE_ID);

        cats.clear();
        cats.addAll(QuestionnaireDao.getGroupList(currentUserId, currentSurveyId));
        mView.initMyView(cats);

        //判断问卷版本是否更新
        //2种情况 a --->>1查看（新版），2查看（旧版），3编辑（新版），4编辑（旧版）
        //       b --->>5查看，6编辑

        optionsA.add(new BottomMenuBean(1, "查看（新版）"));
        optionsA.add(new BottomMenuBean(2, "查看（旧版）"));
        optionsA.add(new BottomMenuBean(3, "填报（新版）"));
        optionsA.add(new BottomMenuBean(4, "填报（旧版）"));

        optionsB.add(new BottomMenuBean(2, "查看"));
        optionsB.add(new BottomMenuBean(4, "填报"));
    }

    private void initView() {
        sample = SampleDao.getById(currentUserId, currentSampleId);

        mView.initRightView(sample.getAssignmentType() == 1 &&
                (sample.getStatus() == SampleStatus.APPOINTMENT || sample.getStatus() == SampleStatus.EXECUTION));
        mView.initSampleInfoView(sample.getStatus(), sample.getDescription(), TimeUtil.question(sample.getAppointVisitTime()));
        int countSub = QuestionnaireDao.countSubQuesBySurveyId(currentUserId, currentSurveyId);
        if (countSub > 0) {
            mView.initTitleTableView();
        } else {
            mView.initTitleView();
        }
    }

    @Override
    public void destroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void syncData() {
        SYNC_QUESTION_SUCCESS = false;
        SYNC_RESPONSE_SUCCESS = false;
        SYNC_PICTURE_SUCCESS = false;
        SYNC_VOICE_SUCCESS = false;
        syncManager.syncQuestionnaire();
    }

    @Override
    public void getCurrentListFromLocal(Integer groupId) {
        currentGroupId = groupId;
        if (currentType == mainType) {
            getMainQuestionnaireListFromLocal(groupId);
        } else {
            getSubQuestionnaireListFromLocal(groupId);
        }
    }

    @Override
    public void getSubQuestionnaireListFromLocal(Integer groupId) {
        currentType = subType;
        currentGroupId = groupId;
        if (cats.size() > 1) {
            mView.showQuestionnaireList(QuestionnaireDao.getSubQuestionListWithResponseList(currentSurveyId, currentUserId, currentSampleId, groupId));
        } else {
            mView.showQuestionnaireList(QuestionnaireDao.getSubQuestionListWithResponseList(currentSurveyId, currentUserId, currentSampleId, 0));
        }
        mView.refreshTitleView(subType);
        LatteLoader.stopLoading();
    }

    @Override
    public void getMainQuestionnaireListFromLocal(Integer groupId) {
        currentType = mainType;
        currentGroupId = groupId;
        if (cats.size() > 1) {
            mView.showQuestionnaireList(ResponseDao.getMainQuestionList(currentUserId, currentSurveyId, currentSampleId, groupId));
        } else {
            mView.showQuestionnaireList(ResponseDao.getMainQuestionList(currentUserId, currentSurveyId, currentSampleId, 0));//问卷进入时list展示
        }
        mView.refreshTitleView(mainType);
        LatteLoader.stopLoading();
    }

    @Override
    public void updateSample(Sample sample) {
        sample.setIsUpload(1);
        boolean success = SampleDao.insertOrUpdate(sample);
        if (success) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("更新本地样本状态成功");
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("更新本地样本状态失败");
        }
    }

    @Override
    public void deleteSubResponse(Response response) {
        ResponseDao.deleteById(currentUserId, response.getId());
        getSubQuestionnaireListFromLocal(currentGroupId);
    }

    @Override
    public void addSubResponse(Questionnaire questionnaire) {
        int sub_response_num = ResponseDao.countSubResponse(currentUserId, currentSampleId, currentSurveyId);
        if (sub_response_num < questionnaire.getQuotaMax()) {
            ResponseDao.insertRandomSubResponse2(questionnaire.getId(), questionnaire.getName(), currentUserId, currentSampleId, currentSurveyId, questionnaire.getModuleId(), questionnaire.getCode(), sub_response_num);
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("已达到子问卷数量上线，不能新增子问卷！");
        }
        getSubQuestionnaireListFromLocal(currentGroupId);
    }

    private boolean checkIsCanUpdateSampleStatus() {
        Integer status = sample.getStatus();
        if (status == SampleStatus.AUDIT_INVALID || status == SampleStatus.AUDIT_RETURN || status == SampleStatus.AUDIT_SUCCESS) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("样本状态已提交，不能修改");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean setSampleStatusFinish() {
        if (checkIsCanUpdateSampleStatus()) {
            Integer submit = ResponseDao.countAllSubmitResponse(currentUserId, currentSurveyId, currentSampleId);
            Integer all = DaoUtil.countAllResponse(currentUserId, currentSurveyId, currentSampleId);
            if (submit == all && all != 0) {
                if (QuestionnaireDao.ifSubQuestionListResponseQuota(currentSurveyId, currentUserId, currentSampleId)) {
                    return true;
                } else {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("子答卷数量未达到项目要求！");
                    return false;
                }
            } else {
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort("有未完成的问卷");
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void openSampleStatus() {
        int i = SPUtils.getInstance().getInt(SPKey.ORGANIZATION_TYPE, 0);
        if (i == OrgType.WHU) {
            setSampleStatusFinish();
        } else {
            mView.showSampleCommitDialog(sample);
        }
    }

    @Override
    public void openResponse(final Response response) {
        dependencyQuesName = "";
        Integer responseStatus = response.getResponseStatus();
        if (SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_SUPPORT_TWICE_SUBMIT)) {
            //二次填答配置下 已完成状态可以填报
            if (responseStatus == 8 || responseStatus == 9 || responseStatus == 10 || responseStatus == 11) {
                SPUtils.getInstance().put("EDIT_MODE", 0); //记录进入了查看模式
                if (TextUtils.isEmpty(response.getSubmitData())) {
                    mView.showResponseActivity(SurveyActivity.RS2_I_EDIT_PREVIEW, "答卷已提交，只能查看", response, null);
                } else {
                    mView.showResponseActivity(SurveyActivity.RS2_I_EDIT_VIEW, "答卷已提交，只能查看", response, null);
                }
                return;
            }
        }else {
            if (responseStatus == 6 || responseStatus == 8 || responseStatus == 9 || responseStatus == 10 || responseStatus == 11) {//已完成直接进入预览模式
                SPUtils.getInstance().put("EDIT_MODE", 0); //记录进入了查看模式
                if (TextUtils.isEmpty(response.getSubmitData())) {
                    //完成查看
                    mView.showResponseActivity(SurveyActivity.RS2_I_EDIT_PREVIEW, "答卷已提交，只能查看", response, null);
                } else {
                    mView.showResponseActivity(SurveyActivity.RS2_I_EDIT_VIEW, "答卷已提交，只能查看", response, null);
                }
                return;
            }
        }
        Integer sampleStatus = sample.getStatus();
        // 样本状态 3 已完成 4 拒访 9 无法联系 10 审核无效 11 审核退回 12 审核成功
        if (sampleStatus == 3 ||sampleStatus == 4 ||sampleStatus == 9 || sampleStatus == 10 || sampleStatus == 12) {
            SPUtils.getInstance().put("EDIT_MODE", 0); //记录进入了查看模式
            mView.showResponseActivity(SurveyActivity.RS2_I_EDIT_VIEW, "样本状态已改变，只能查看", response, null);
            return;
        }
        long systemTime = System.currentTimeMillis();
        //样本状态 6 预约  getAppointVisitTime 预约时间
        if (sample.getAppointVisitTime()!=null){
            if (sampleStatus == 6 && sample.getAppointVisitTime() >= systemTime) {
                SPUtils.getInstance().put("EDIT_MODE", 0); //记录进入了查看模式
                mView.showResponseActivity(SurveyActivity.RS2_I_EDIT_VIEW, "预约时间未到，只能查看", response, null);
                return;
            }
        }
        final Integer moduleId = response.getModuleId();

        AsyncTaskUtil.doAsync(new AsyncCallBack() {
            @Override
            public void before() {

            }

            @Override
            public void execute() {
                latestResponse = ResponseDao.queryLatestResponse(currentUserId, currentSurveyId, moduleId);
            }

            @Override
            public void after() {
                options = optionsB;
                String latestVersion = latestResponse.getQuestionnaireUrl();
                if (!latestVersion.equals(response.getQuestionnaireUrl())) {
                    options = optionsA;
                } else {
                    options = optionsB;
                }
                mView.showBottomMenu(options, response);
            }
        });
    }

    @Override
    public void openResponseWithType(final Response response, int type) {
        Survey project = SurveyDao.queryById(currentSurveyId, currentUserId);

        switch (type) {
            case 1://查看新版
                SPUtils.getInstance().put("EDIT_MODE", 0); //记录进入了查看模式
                if (TextUtils.isEmpty(latestResponse.getSubmitData())) {
                    mView.showResponseActivity(SurveyActivity.RS2_I_EDIT_PREVIEW, "您进入了查看模式", latestResponse, null);
                } else {
                    mView.showResponseActivity(SurveyActivity.RS2_I_EDIT_VIEW, "您进入了查看模式", latestResponse, null);
                }
                break;
            case 2://查看旧版
                SPUtils.getInstance().put("EDIT_MODE", 0); //记录进入了查看模式
                if (TextUtils.isEmpty(response.getSubmitData())) {
                    mView.showResponseActivity(SurveyActivity.RS2_I_EDIT_PREVIEW, "您进入了查看模式", response, null);
                } else {
                    mView.showResponseActivity(SurveyActivity.RS2_I_EDIT_VIEW, "您进入了查看模式", response, null);
                }
                break;
            case 3://填报新版
                if (response.getIsDownloadDetail() == 1) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("答卷没有成功下载");
                    return;
                }
                if (response.getResponseStatus() == 6 || response.getResponseStatus() == 8 || response.getResponseStatus() == 9 || response.getResponseStatus() == 10 || response.getResponseStatus() == 11) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("答卷已审核通过，不可再次填报");
                    return;
                }
//                if (!SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_SUPPORT_TWICE_SUBMIT)) {
//                    ToastUtils.setBgColor(Color.GRAY);
//                    ToastUtils.showShort("该项目不允许二次提交，不可再次填报");
//                    return;
//                }
                if (SPUtils.getInstance().getInt(SPKey.SURVEY_STATUS) != ProjectConstants.STATUS_RUN) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("项目未在启动中，不可填报");
                    return;
                }
                if (!project.getRoleName().contains("访")) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("您没有当前项目的访问员权限，不可填报");
                    return;
                }
                dependency = latestResponse.getDependencyId();
                int isUpload = response.getIsUpload();
                if (isUpload == 1) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("请先上传老版本");
                    return;
                }
                AsyncTaskUtil.doAsync(new AsyncCallBack() {
                    @Override
                    public void before() {

                    }

                    @Override
                    public void execute() {
                        if (dependency != 0) {
                            dependencyQuesName = QuestionnaireDao.getFinishDependencyName(currentSurveyId, currentUserId, currentSampleId, dependency);
                        }
                        ResponseDao.deleteById(currentUserId, response.getId());
                    }

                    @Override
                    public void after() {
                        //有依赖并且依赖问卷没有完成
                        if (dependency != 0 && TextUtils.isEmpty(dependencyQuesName)) {
                            ToastUtils.setBgColor(Color.GRAY);
                            ToastUtils.showShort("请先完成依赖问卷");
                            return;
                        }
                        latestResponse.setVersion(response.getVersion());
                        SPUtils.getInstance().put("EDIT_MODE", 1); //记录进入了填报模式
                        mView.showResponseActivity(SurveyActivity.RS2_I_EDIT_INTERVIEW, "您进入了填报模式", latestResponse, response.getId());
                    }
                });
                break;
            case 4://填报
                if (response.getIsDownloadDetail() == 1) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("答卷没有成功下载");
                    return;
                }
                if (response.getResponseStatus() == 8 || response.getResponseStatus() == 9 || response.getResponseStatus() == 10 || response.getResponseStatus() == 11) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("答卷已审核通过，不可再次填报");
                    return;
                }
//                if (!SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_SUPPORT_TWICE_SUBMIT)) {
//                    ToastUtils.setBgColor(Color.GRAY);
//                    ToastUtils.showShort("该项目不允许二次提交，不可再次填报");
//                    return;
//                }
                if (SPUtils.getInstance().getInt(SPKey.SURVEY_STATUS) != ProjectConstants.STATUS_RUN) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("项目未在启动中，不可填报");
                    return;
                }
                if (!project.getRoleName().contains("访")) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("您没有当前项目的访问员权限，不可填报");
                    return;
                }
                dependency = response.getDependencyId();
                AsyncTaskUtil.doAsync(new AsyncCallBack() {
                    @Override
                    public void before() {

                    }

                    @Override
                    public void execute() {
                        if (dependency != 0) {
                            dependencyQuesName = QuestionnaireDao.getFinishDependencyName(currentSurveyId, currentUserId, currentSampleId, dependency);
                        }
                    }

                    @Override
                    public void after() {
                        //有依赖并且依赖问卷没有完成
                        if (dependency != 0 && TextUtils.isEmpty(dependencyQuesName)) {
                            ToastUtils.setBgColor(Color.GRAY);
                            ToastUtils.showShort("请先完成依赖问卷");
                            return;
                        }
                        SPUtils.getInstance().put("EDIT_MODE", 1); //记录进入了填报模式
                        mView.showResponseActivity(SurveyActivity.RS2_I_EDIT_INTERVIEW, "您进入了填报模式", response, null);
                    }
                });
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (!this.equals(event.getBindTag())) {
            return;
        }
        int tag = event.getTag();
        if (tag == Msg.SYNC_ERROR) {
            mView.showNetErrorView(event.getMsg());
            initView();
            if (currentType == subType) {
                getSubQuestionnaireListFromLocal(currentGroupId);
            } else {
                getMainQuestionnaireListFromLocal(0);
            }
        }
        switch (tag) {
            case Msg.SYNC_QUESTIONNAIRE:
                SYNC_QUESTION_SUCCESS = true;
                syncManager.syncResponse();
                break;
            case Msg.SYNC_RESPONSE:
                SYNC_RESPONSE_SUCCESS = true;
                syncManager.syncPicture();
                break;
            case Msg.SYNC_PICTURE:
                SYNC_PICTURE_SUCCESS = true;
                syncManager.syncVoice();
                break;
            case Msg.SYNC_VOICE:
                SYNC_VOICE_SUCCESS = true;
                break;
        }
        if (SYNC_QUESTION_SUCCESS && SYNC_RESPONSE_SUCCESS && SYNC_PICTURE_SUCCESS && SYNC_VOICE_SUCCESS) {
            initView();
            if (currentType == subType) {
                getSubQuestionnaireListFromLocal(currentGroupId);
            } else {
                getMainQuestionnaireListFromLocal(0);
            }
        }
    }
}
