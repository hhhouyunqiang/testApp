package com.ringdata.ringinterview.capi.components.ui.survey.message;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.ringinterview.capi.components.constant.Msg;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.MessageEvent;
import com.ringdata.ringinterview.capi.components.data.dao.MessageDao;
import com.ringdata.ringinterview.capi.components.data.model.Message;
import com.ringdata.ringinterview.capi.components.data.remote.SyncManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;

/**
 * Created by admin on 2018/5/31.
 */

public class MessagePresenter implements MessageContract.Presenter {
    private final MessageContract.View mView;
    private SyncManager syncManager;

    public MessagePresenter(MessageContract.View mView, Context context) {
        this.mView = mView;
        syncManager = new SyncManager(this, context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void start() {
        mView.initMyView();
        getMessageListFromLocal();
    }

    @Override
    public void getMessageListFromLocal() {
        LinkedList<MultiItemEntity> list = MessageDao.queryList(SPUtils.getInstance().getInt(SPKey.USERID));
        mView.showMessageList(list);
    }

    @Override
    public void openMessageDetails(Message message) {
        mView.showDetailDelegateUI(message);
    }

    @Override
    public void getMessageListFromNet() {
        syncManager.syncMessage();
    }

    @Override
    public void queryFromLocal(String query) {
        LinkedList<MultiItemEntity> list = null;
        if (TextUtils.isEmpty(query)) {
            list = MessageDao.queryList(SPUtils.getInstance().getInt(SPKey.USERID));
        } else {
            list = MessageDao.queryList(SPUtils.getInstance().getInt(SPKey.USERID), query);
        }
        mView.showMessageList(list);
    }

    @Override
    public void deleteMessageFromLocal(Integer messageId) {
        boolean success = MessageDao.delete(messageId, SPUtils.getInstance().getInt(SPKey.USERID));
        if (success) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("删除成功");
            getMessageListFromLocal();
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("删除失败");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Object bindTag = event.getBindTag();
        if (!this.equals(bindTag)) {
            return;
        }
        int tag = event.getTag();
        if (tag == Msg.SYNC_ERROR) {
            mView.showNetErrorView(event.getMsg());
        }
        if (tag == Msg.SYNC_MESSAGE) {
            getMessageListFromLocal();
        }
    }

    @Override
    public void destroy() {
        EventBus.getDefault().unregister(this);
    }
}
