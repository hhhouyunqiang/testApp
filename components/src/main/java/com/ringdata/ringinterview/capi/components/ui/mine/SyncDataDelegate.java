package com.ringdata.ringinterview.capi.components.ui.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.ui.loader.LatteLoader;
import com.ringdata.base.ui.recycler.BaseDecoration;
import com.ringdata.base.utils.TimeUtil;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.Msg;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.constant.SyncType;
import com.ringdata.ringinterview.capi.components.data.DaoUtil;
import com.ringdata.ringinterview.capi.components.data.MessageEvent;
import com.ringdata.ringinterview.capi.components.data.dao.SyncLogDao;
import com.ringdata.ringinterview.capi.components.data.remote.SyncManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.OnClick;

/**
 * Created by admin on 17/10/26.
 */

public class SyncDataDelegate extends LatteDelegate implements BaseQuickAdapter.OnItemClickListener {
    private int currentUserId;
    private LinkedList<MultiItemEntity> list = new LinkedList<>();
    private SyncManager syncManager;
    private boolean SYNC_SURVEY_SUCCESS = false;
    private boolean SYNC_SAMPLE_SUCCESS = false;
    private boolean SYNC_QUESTIONNAIRE_SUCCESS = false;
    private boolean SYNC_RESPONSE_SUCCESS = false;
    private boolean SYNC_PICTURE_SUCCESS = false;
    private boolean SYNC_VOICE_SUCCESS = false;
    private boolean SYNC_ALL = false;

    @Override
    public Object setLayout() {
        return R.layout.delegate_mine_syncdata;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        currentUserId = SPUtils.getInstance().getInt(SPKey.USERID);
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        initView();
        initData();
        syncManager = new SyncManager(this, getActivity());
    }

    private void initView() {
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.rv_mine_syncdata_list);
        recycleAdapter = new SyncDataAdapter(list, this);
        recycleAdapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration
                (BaseDecoration.create(ContextCompat.getColor(getContext(), R.color.divider_line), 1));
        recyclerView.setAdapter(recycleAdapter);
    }

    private void initData() {
        List<HashMap<String, Object>> syncTimeList = SyncLogDao.queryList(currentUserId);
        list.clear();
        long time1 = 0;
        long time2 = 0;
        long time3 = 0;
        long time4 = 0;
        long time5 = 0;
        long time6 = 0;
        for (HashMap<String, Object> a : syncTimeList) {
            int type = (int) a.get("type");
            switch (type) {
                case SyncType.SYNC_SURVEY:
                    time1 = (long) a.get("time");
                    break;
                case SyncType.SYNC_SAMPLE:
                    time2 = (long) a.get("time");
                    break;
                case SyncType.SYNC_QUESTIONNAIRE:
                    time3 = (long) a.get("time");
                    break;
                case SyncType.SYNC_RESPONSE:
                    time4 = (long) a.get("time");
                    break;
                case SyncType.SYNC_PICTURE:
                    time5 = (long) a.get("time");
                    break;
                case SyncType.SYNC_VOICE:
                    time6 = (long) a.get("time");
                    break;
            }
        }
        int no_upload_sample = DaoUtil.countNoUploadSampleByUserId(currentUserId);
        int no_upload_response = DaoUtil.countNoUploadResponseByUserId(currentUserId);
        int no_upload_pic = DaoUtil.countNoUploadPicByUserId(currentUserId);
        int no_upload_audio = DaoUtil.countNoUploadAudioByUserId(currentUserId);
        ListBean item1 = new ListBean(SyncDataItemType.LIST_BEAN, R.color.color01, "同步项目数据", TimeUtil.downloadTime(time1), 0, 1);
        ListBean item2 = new ListBean(SyncDataItemType.LIST_BEAN, R.color.color01, "同步样本数据", TimeUtil.downloadTime(time2), no_upload_sample, 2);
        ListBean item3 = new ListBean(SyncDataItemType.LIST_BEAN, R.color.color01, "同步问卷数据", TimeUtil.downloadTime(time3), 0, 3);
        ListBean item4 = new ListBean(SyncDataItemType.LIST_BEAN, R.color.color01, "同步答卷数据", TimeUtil.downloadTime(time4), no_upload_response, 4);
        ListBean divider = new ListBean(SyncDataItemType.DIVIDER_40);
        ListBean item5 = new ListBean(SyncDataItemType.LIST_BEAN, R.color.color03, "同步图片数据", TimeUtil.downloadTime(time5), no_upload_pic, 5);
        ListBean item6 = new ListBean(SyncDataItemType.LIST_BEAN, R.color.color03, "同步音频数据", TimeUtil.downloadTime(time6), no_upload_audio, 6);
        ListBean item7 = new ListBean(SyncDataItemType.TEXT, 7);
        item7.setText("立即同步所有");
        list.add(item1);
        list.add(item2);
        list.add(item3);
        list.add(item4);
        list.add(divider);
        list.add(item5);
        list.add(item6);
        list.add(divider);
        list.add(item7);

        recycleAdapter.setNewData(list);
        recycleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ListBean item = (ListBean) list.get(position);
        int id = item.getId();
        SYNC_ALL = false;
        SYNC_SURVEY_SUCCESS = false;
        SYNC_SAMPLE_SUCCESS = false;
        SYNC_QUESTIONNAIRE_SUCCESS = false;
        SYNC_RESPONSE_SUCCESS = false;
        SYNC_PICTURE_SUCCESS = false;
        SYNC_VOICE_SUCCESS = false;

        switch (id) {
            case 1:
                LatteLoader.showLoading(getActivity(), "同步项目");
                syncManager.syncSurvey();
                break;
            case 2:
                LatteLoader.showLoading(getActivity(), "同步样本");
                syncManager.syncSample();
                break;
            case 3:
                LatteLoader.showLoading(getActivity(), "同步问卷");
                syncManager.syncQuestionnaire();
                break;
            case 4:
                LatteLoader.showLoading(getActivity(), "同步答卷");
                syncManager.syncResponse();
                break;
            case 5:
                LatteLoader.showLoading(getActivity(), "同步图片");
                syncManager.syncPicture();
                break;
            case 6:
                LatteLoader.showLoading(getActivity(), "同步音频");
                syncManager.syncVoice();
                break;
            case 7:
                LatteLoader.showLoading(getActivity(), "同步所有数据");
                SYNC_ALL = true;
                syncManager.syncSurvey();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (!this.equals(event.getBindTag())) {
            return;
        }
        int tag = event.getTag();
        if (!SYNC_ALL) {
            initData();
            LatteLoader.stopLoading();
        }
        if (tag == Msg.SYNC_ERROR) {
            showErrorView(event.getMsg());
        }
        switch (tag) {
            case Msg.SYNC_SURVEY:
                SYNC_SURVEY_SUCCESS = true;
                if (SYNC_ALL) {
                    syncManager.syncSample();
                }
                break;
            case Msg.SYNC_SAMPLE:
                SYNC_SAMPLE_SUCCESS = true;
                if (SYNC_ALL) {
                    syncManager.syncQuestionnaire();
                }
                break;
            case Msg.SYNC_QUESTIONNAIRE:
                SYNC_QUESTIONNAIRE_SUCCESS = true;
                if (SYNC_ALL) {
                    syncManager.syncResponse();
                }
                break;
            case Msg.SYNC_RESPONSE:
                SYNC_RESPONSE_SUCCESS = true;
                if (SYNC_ALL) {
                    syncManager.syncPicture();
                }
                break;
            case Msg.SYNC_PICTURE:
                SYNC_PICTURE_SUCCESS = true;
                if (SYNC_ALL) {
                    syncManager.syncVoice();
                }
                break;
            case Msg.SYNC_VOICE:
                SYNC_VOICE_SUCCESS = true;
                break;
        }

        if (SYNC_ALL) {
            if (SYNC_SURVEY_SUCCESS && SYNC_SAMPLE_SUCCESS && SYNC_QUESTIONNAIRE_SUCCESS && SYNC_RESPONSE_SUCCESS && SYNC_PICTURE_SUCCESS && SYNC_VOICE_SUCCESS) {
                initData();
                LatteLoader.stopLoading();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
