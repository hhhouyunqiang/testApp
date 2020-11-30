package com.ringdata.ringinterview.capi.components.ui.survey;

import android.content.Context;
import android.graphics.Color;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.net.callback.IError;
import com.ringdata.base.net.callback.ISuccess;
import com.ringdata.base.util.NetworkUtil;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.constant.Msg;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.ApiUrl;
import com.ringdata.ringinterview.capi.components.data.MessageEvent;
import com.ringdata.ringinterview.capi.components.data.ResponseJson;
import com.ringdata.ringinterview.capi.components.data.dao.MessageDao;
import com.ringdata.ringinterview.capi.components.data.dao.SurveyDao;
import com.ringdata.ringinterview.capi.components.data.remote.SyncManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/6/25.
 */

public class SurveyPresenter implements SurveyContract.Presenter {
    private final SurveyContract.View mView;
    private SyncManager syncManager;
    private int currentUserId;
    private List<MultiItemEntity> list = new ArrayList<>();

    public SurveyPresenter(SurveyContract.View mView, Context context) {
        this.mView = mView;
        EventBus.getDefault().register(this);
        currentUserId = SPUtils.getInstance().getInt(SPKey.USERID);
        syncManager = new SyncManager(this, context);
    }

    @Override
    public void start() {
        mView.initMyView();
        String appTitle = SPUtils.getInstance().getString(SPKey.APP_TITLE);
        mView.initTitleView(appTitle);
        if (StringUtils.isEmpty(appTitle)) {
            mView.initTitleView("全部项目");
        }
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("网络不可用");
        } else {
            syncManager.syncSurvey();
        }
    }

    @Override
    public void getSurveyListFromNet() {
        syncManager.syncSurvey();
        getSurveyListFromLocal();
    }

    @Override
    public void querySurveyListFromNet(String keyword, String type, int status, int sort) {
        int userType = SPUtils.getInstance().getInt(SPKey.USERTYPE);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", currentUserId);
        jsonObject.put("userType", userType);
        jsonObject.put("keyword", keyword);
        jsonObject.put("type", type);
        if (status == 0 || status == 1 || status == 2 || status == 3) {
            jsonObject.put("status", status);
        }
        jsonObject.put("sortType", sort);
        RestClient.builder().url(App.orgHost + ApiUrl.DOWNLOAD_SURVEY)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (!responseJson.getSuccess()) {
                            mView.showNetErrorView("查询调查项目", responseJson.getMsg());
                            return;
                        }
                        JSONObject data = responseJson.getDataAsObject();
                        list = SurveyDao.getListFromNet(data.getJSONArray("projectList"), currentUserId);
                        mView.showSurveyList(list);
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        mView.showNetErrorView("查询调查项目", msg);
                    }
                })
                .build()
                .post();
    }
    //刚进入主页面更新list数据
    @Override
    public void getSurveyListFromLocal() {
        int userType = SPUtils.getInstance().getInt(SPKey.USERTYPE);
        List<MultiItemEntity> list = SurveyDao.queryListAll(currentUserId, userType);
        Integer messageNoRead = MessageDao.countNoRead(currentUserId);
        mView.refreshTopRightMessageView(messageNoRead);
        mView.showSurveyList(list);
    }

    @Override
    public void queryListByKeyword(String keyword) {
        List<MultiItemEntity> list = SurveyDao.queryListByKeyword(currentUserId, keyword);
        Integer messageNoRead = MessageDao.countNoRead(currentUserId);
        mView.refreshTopRightMessageView(messageNoRead);
        mView.showSurveyList(list);
    }

    @Override
    public void queryListByType(String type) {
        List<MultiItemEntity> list = SurveyDao.queryListByType(currentUserId, type);
        Integer messageNoRead = MessageDao.countNoRead(currentUserId);
        mView.refreshTopRightMessageView(messageNoRead);
        mView.showSurveyList(list);
    }

    @Override
    public void queryListByStatus(int status) {
        List<MultiItemEntity> list = SurveyDao.queryListByStatus(currentUserId, status);
        Integer messageNoRead = MessageDao.countNoRead(currentUserId);
        mView.refreshTopRightMessageView(messageNoRead);
        mView.showSurveyList(list);
    }

    @Override
    public void queryListByOrder(int sort) {
        List<MultiItemEntity> list = SurveyDao.queryListByOrder(currentUserId, sort);
        Integer messageNoRead = MessageDao.countNoRead(currentUserId);
        mView.refreshTopRightMessageView(messageNoRead);
        mView.showSurveyList(list);
    }

    @Override
    public void scanLogIn(String scanKey) {
        String token = SPUtils.getInstance().getString(SPKey.REFRESH_TOKEN);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("scanKey", scanKey);
        jsonObject.put("token", token);
        RestClient.builder().url(App.orgHost + ApiUrl.SCAN_LOGIN)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (!responseJson.getSuccess()) {
                            mView.showNetErrorView("扫码登录", responseJson.getMsg());
                            return;
                        }
                        ToastUtils.setBgColor(Color.GRAY);
                        ToastUtils.showShort(responseJson.getMsg());
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        mView.showNetErrorView("扫码登录", msg);
                    }
                })
                .build()
                .post();
    }

    @Override
    public void joinProject(String scanKey) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("inviteCode", scanKey);
        RestClient.builder().url(App.orgHost + ApiUrl.APPLY_PROJECT)
                .raw(jsonObject.toJSONString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        ResponseJson responseJson = new ResponseJson(response);
                        if (!responseJson.getSuccess()) {
                            mView.showNetErrorView("申请加入项目", responseJson.getMsg());
                            return;
                        }
                        ToastUtils.setBgColor(Color.GRAY);
                        ToastUtils.showShort(responseJson.getMsg());
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        mView.showNetErrorView("申请加入项目", msg);
                    }
                })
                .build()
                .post();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Object bindTag = event.getBindTag();
        if (!this.equals(bindTag)) {
            return;
        }
        int tag = event.getTag();
        if (tag == Msg.SYNC_ERROR) {
            mView.showSyncErrorView(event.getMsg());
        }
        getSurveyListFromLocal();
    }

    @Override
    public void destroy() {
        EventBus.getDefault().unregister(this);
    }
}
