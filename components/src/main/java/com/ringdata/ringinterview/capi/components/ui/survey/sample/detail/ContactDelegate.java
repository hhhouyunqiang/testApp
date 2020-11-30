package com.ringdata.ringinterview.capi.components.ui.survey.sample.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.ui.recycler.BaseDecoration;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.Msg;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.MessageEvent;
import com.ringdata.ringinterview.capi.components.data.dao.SampleContactDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleDao;
import com.ringdata.ringinterview.capi.components.data.model.SampleContact;
import com.ringdata.ringinterview.capi.components.utils.AppUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 2018/6/3.
 */

public class ContactDelegate extends LatteDelegate {
    @BindView(R2.id.btn_top_add)
    Button btnAdd;

    private LinkedList<MultiItemEntity> list = new LinkedList<>();
    private String currentSampleId;
    private Integer currentUserId;

    @Override
    public Object setLayout() {
        return R.layout.delegate_sample_detail_contact;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initView();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentSampleId = SPUtils.getInstance().getString(SPKey.SAMPLE_ID);
        currentUserId = SPUtils.getInstance().getInt(SPKey.USERID);
        EventBus.getDefault().register(this);
    }

    private void initView() {
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.rv_sample_contact);
        recycleAdapter = new SampleDetailAdapter(list, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration
                (BaseDecoration.create(ContextCompat.getColor(getContext(), R.color.divider_line), 1));
        recyclerView.setAdapter(recycleAdapter);

        if (SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_SUPPORT_SAMPLE_CONTACT_RECORD)) {
            btnAdd.setVisibility(View.VISIBLE);
        } else {
            btnAdd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initData() {
        list.clear();
        PropertyListBean divider = new PropertyListBean(SampleDetailItemType.DIVIDER_40);
        //顶部显示当前样本标识
        HashMap<String, String> sample = SampleDao.queryByIdAndSurveyId(SPUtils.getInstance().getInt(SPKey.USERID), currentSampleId, SPUtils.getInstance().getInt(SPKey.SURVEY_ID));
        JSONArray sampleIdentifierJsonArray = JSON.parseArray(SPUtils.getInstance().getString(SPKey.SURVEY_SAMPLE_IDENTIFIER));
        int num = 0;
        if (sampleIdentifierJsonArray != null) {
            final int size = sampleIdentifierJsonArray.size();
            for (int i = 0; i < size; i++) {
                String key = sampleIdentifierJsonArray.getString(i);
                String value = sample.get(key);
                PropertyListBean item = new PropertyListBean(SampleDetailItemType.LIST_BEAN, AppUtil.getSampleProperty(key), value, false, num);
                list.add(item);
                num ++;
            }
        }
        if (num > 0) {
            list.add(divider);
        }

        LinkedList<SampleContact> contactList = SampleContactDao.queryBySampleGuid(currentSampleId, SPUtils.getInstance().getInt(SPKey.USERID));
        for (int i = 0; i < contactList.size(); i++) {
            int position = i + 1;
            PropertyListBean title = new PropertyListBean(SampleDetailItemType.LIST_TITLE_BEAN, SampleDetailItemType.CONTACT, contactList.get(i), "联系人" + position, checkIsCanEdit(contactList.get(i)), i);
            list.add(title);
            list.add(contactList.get(i));
        }

        recycleAdapter.setNewData(list);
        recycleAdapter.notifyDataSetChanged();
    }

    private boolean checkIsCanEdit(MultiItemEntity item) {
        SampleContact sampleContact = (SampleContact) item;
        return sampleContact.getCreateUser().equals(currentUserId);
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @OnClick(R2.id.btn_top_add)
    void onClickAdd() {
        start(EditContactDelegate.newInstance(null));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getTag() == Msg.REFRESH_CONTACT) {
            initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
