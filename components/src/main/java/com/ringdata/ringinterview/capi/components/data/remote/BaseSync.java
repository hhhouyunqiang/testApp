package com.ringdata.ringinterview.capi.components.data.remote;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.ui.loader.LatteLoader;
import com.ringdata.ringinterview.capi.components.constant.Msg;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.constant.SyncType;
import com.ringdata.ringinterview.capi.components.data.MessageEvent;
import com.ringdata.ringinterview.capi.components.data.dao.SyncLogDao;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by admin on 2018/6/18.
 */

public abstract class BaseSync {
    private Object bindTag;
    private int syncType;
    private EventBus eventBus;
    protected Integer currentUserId;
    protected Integer currentSurveyId;
    protected long mSyncTime;
    private int mSyncId;
    public Context mContent;
    public String surveyName = "";

    BaseSync(Integer currentUserId, Integer currentSurveyId, Object bindTag, int syncType, Context context) {
        this.currentUserId = currentUserId;
        this.currentSurveyId = currentSurveyId;
        this.syncType = syncType;
        this.eventBus = EventBus.getDefault();
        this.bindTag = bindTag;
        this.mContent = context;
    }

    public void start() {
        if (syncType == SyncType.SYNC_VOICE || syncType == SyncType.SYNC_PICTURE) {
            if (SPUtils.getInstance().getBoolean(SPKey.SETTING_SYNC_MEDIA_WIFI) && !NetworkUtils.isWifiAvailable()) {
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort("如要提交文件，请在设置中关闭（WIFI模式下提交多媒体文件）");
                sendSuccessMsg();
                return;
            }
        }
        showLoading();
        HashMap<String, String> hm = new HashMap<>();
        hm.put("user_id", currentUserId + "");
        hm.put("project_id", currentSurveyId + "");
        hm.put("type", syncType + "");
        hm.put("success", "0");
        hm.put("sync_time", new Date().getTime() + "");
        SyncLogDao.insert(hm);
        mSyncId = getSyncId();
        mSyncTime = getSyncTime();
        beginSync();
    }

    protected abstract void beginSync();

    protected void endSync() {
        SyncLogDao.update(mSyncId, new Date().getTime(), 1);
        sendSuccessMsg();
    }

    private void sendSuccessMsg() {
        Log.i("sendSuccessMsg", syncType + "");
        stopLoading();
        eventBus.post(new MessageEvent(syncType, bindTag));
    }

    protected void sendErrorMsg() {
        stopLoading();
        String message = "同步" + getTypeStr() + "失败";
        if (!"".equals(surveyName)) {
            message = message.concat("，调查名称：" + surveyName);
        }
        eventBus.post(new MessageEvent(Msg.SYNC_ERROR, bindTag, message));
    }

    protected void sendMsgError(String msg) {
        stopLoading();
        eventBus.post(new MessageEvent(Msg.SYNC_ERROR, bindTag, msg));
    }

    private void showLoading() {
        LatteLoader.showLoading(mContent, getTypeStr()+"同步中");
    }

    private void stopLoading() {
        LatteLoader.stopLoading();
    }

    private long getSyncTime() {
        return SyncLogDao.queryLastSyncTime(syncType, currentUserId, currentSurveyId);
    }

    private int getSyncId() {
        return SyncLogDao.queryLastSyncId();
    }

    private String getTypeStr() {
        switch (syncType) {
            case SyncType.SYNC_SURVEY:
                return "项目";
            case SyncType.SYNC_SAMPLE:
                return "样本";
            case SyncType.SYNC_QUESTIONNAIRE:
                return "问卷";
            case SyncType.SYNC_RESPONSE:
                return "答卷";
            case SyncType.SYNC_PICTURE:
                return "图片";
            case SyncType.SYNC_VOICE:
                return "音频";
            case SyncType.SYNC_MESSAGE:
                return "消息";
            default:
                return "";
        }
    }

}
