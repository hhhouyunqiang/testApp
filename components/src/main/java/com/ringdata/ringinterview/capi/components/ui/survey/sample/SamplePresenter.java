package com.ringdata.ringinterview.capi.components.ui.survey.sample;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.constant.Msg;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.MessageEvent;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.ResponseDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleDao;
import com.ringdata.ringinterview.capi.components.data.dao.SurveyDao;
import com.ringdata.ringinterview.capi.components.data.model.Sample;
import com.ringdata.ringinterview.capi.components.data.model.Survey;
import com.ringdata.ringinterview.capi.components.data.remote.SyncManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/6/25.
 */

public class SamplePresenter implements SampleContract.Presenter {

    private final SampleContract.View mView;
    private int currentUserId;
    private int currentSurveyId;
    private String surveyName;
    private int surveyStatus;
    private String surveyType;
    private SyncManager syncManager;
    private List<MultiItemEntity> list = new ArrayList<>();

    private boolean SYNC_SAMPLE_SUCCESS = false;
    private boolean SYNC_SURVEY_SUCCESS = false;

    public SamplePresenter(SampleContract.View mView, Context context) {
        this.mView = mView;
        EventBus.getDefault().register(this);
        syncManager = new SyncManager(this, context);
        currentUserId = SPUtils.getInstance().getInt(SPKey.USERID);
        currentSurveyId = SPUtils.getInstance().getInt(SPKey.SURVEY_ID);
    }

    @Override
    public void start() {
        mView.initMyView();
        surveyName = SPUtils.getInstance().getString(SPKey.SURVEY_NAME);
        surveyStatus = SPUtils.getInstance().getInt(SPKey.SURVEY_STATUS);
        surveyType = SPUtils.getInstance().getString(SPKey.SURVEY_TYPE);
        mView.showSampleAddView(SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_CAN_ADD_SAMPLE, false),
                SPUtils.getInstance().getInt(SPKey.SURVEY_SAMPLE_ASSIGN_TYPE), surveyType);
        syncManager.syncSample();
    }

    @Override
    public void getSampleListFromNet() {
        if (SPUtils.getInstance().getInt("EDIT_MODE") == 1 || SPUtils.getInstance().getInt("IF_ADD_SAMPLE") == 1) {
            SPUtils.getInstance().put("EDIT_MODE", 0);
            SPUtils.getInstance().put("IF_ADD_SAMPLE", 0);
            syncManager.syncSample();
        } else {
            getSampleListFromLocal();
        }
    }

    @Override
    public void queryFromLocal(String query) {
        if (TextUtils.isEmpty(query)) {
            getSampleListFromLocal();
        } else {
            mView.showSampleList(SampleDao.queryByKeyword(currentSurveyId, currentUserId, query));
        }
    }

    @Override
    public void querySampleListFromNet(String query, int status, int sort) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", currentSurveyId);
        if (query != null && !"".equals(query)) {
            jsonObject.put("keyword", query);
        }
        if (status != -1) {
            jsonObject.put("sampleStatus", status);
        }
        jsonObject.put("sortType", sort);
        RestClient.builder().url(App.orgHost + ApiUrl.DOWNLOAD_SAMPLE)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (!responseJson.getSuccess()) {
                            mView.showNetErrorView(responseJson.getMsg());
                            return;
                        }
                        JSONObject data = responseJson.getDataAsObject();
                        list = SampleDao.getListFromNet(data.getJSONArray("sampleList"), currentUserId);
                        List<MultiItemEntity> listBy=SampleDao.selectListBySurveyId(currentSurveyId, currentUserId);
                        //数据库查询结果，没事开始时间结束时间
                        for (int i=0;i<list.size();i++){
                            Sample sample =(Sample)list.get(i);
                            //本地数据查询有时间，所以加进去要不然排序后没有时间数据
                            for (int j=0;j<listBy.size();j++){
                                Sample sampleBy =(Sample)listBy.get(j);
                                if (sample.getId() == sampleBy.getId()&&sample.getName().equals(sampleBy.getName())){
                                    sample.setStartTime(sampleBy.getStartTime());
                                    sample.setEndTime(sampleBy.getEndTime());
                                    sample.setDuration(sampleBy.getDuration());
                                    list.set(i,sample);
                                }
                            }
                        }
                        //查询完数据返回样本页面更新list列表展示
                        mView.showSampleList(list);
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        mView.showNetErrorView(msg);
                    }
                })
                .build()
                .post();
    }

    @Override
    public void checkIsCanDelete(Sample sample) {
        int count = ResponseDao.countResponse(currentUserId, currentSurveyId, SPUtils.getInstance().getString(SPKey.SAMPLE_ID));
        if (count > 0 || sample.getUserId() == currentUserId || sample.getIsUpload() == 2) {
            return;
        }
        mView.showDeleteAlertCommit(sample);
    }

    @Override
    public void deleteSample(Sample sample) {
        boolean success = SampleDao.deleteById(currentUserId, sample.getSampleGuid());
        if (success) {
            mView.toast("删除成功");
            getSampleListFromLocal();
        } else {
            mView.toast("删除失败");
        }
    }

    @Override
    public void getSampleListFromLocal() {
        //刚开始进入样本list数据展示,后答卷完返回
        mView.showSampleList(SampleDao.selectListBySurveyId(currentSurveyId, currentUserId));
    }
    
    //网络不可用查询使用本地数据
    @Override
    public void queryListByStatus(int status) {
        mView.showSampleList(SampleDao.queryByStatus(currentSurveyId, currentUserId, status));
    }

    //网络不可用查询使用排序本地数据返回
    @Override
    public void queryListByOrder(int sort) {
        mView.showSampleList(SampleDao.queryByOrder(currentSurveyId, currentUserId, sort));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (!this.equals(event.getBindTag())) {
            return;
        }
        int tag = event.getTag();
        if (tag == Msg.SYNC_ERROR) {
            mView.showNetErrorView(event.getMsg());
        }
        switch (tag) {
            case Msg.SYNC_SAMPLE:
                SYNC_SAMPLE_SUCCESS = true;
                syncManager.syncSurvey();
                break;
            case Msg.SYNC_SURVEY:
                SYNC_SURVEY_SUCCESS = true;
                break;
        }
        if (SYNC_SAMPLE_SUCCESS && SYNC_SURVEY_SUCCESS) {
            Survey currentSurvey = SurveyDao.queryById(currentSurveyId, currentUserId);
            mView.initSurveyView(currentSurveyId, surveyName, surveyStatus, surveyType, currentSurvey);
            getSampleListFromLocal();
        }
    }

    @Override
    public void destroy() {
        EventBus.getDefault().unregister(this);
    }
}
