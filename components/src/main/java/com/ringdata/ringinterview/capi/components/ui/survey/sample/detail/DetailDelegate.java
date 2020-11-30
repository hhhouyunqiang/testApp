package com.ringdata.ringinterview.capi.components.ui.survey.sample.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.ui.recycler.BaseDecoration;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.dao.SampleAddressDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleContactDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleTouchDao;
import com.ringdata.ringinterview.capi.components.utils.AppUtil;

import java.util.HashMap;
import java.util.LinkedList;

import butterknife.OnClick;

/**
 * Created by admin on 2018/6/3.
 */

public class DetailDelegate extends LatteDelegate implements BaseQuickAdapter.OnItemClickListener {

    private int currentUserId;
    private int currentSurveyId;
    private String currentSampleId;
    private LinkedList<MultiItemEntity> list = new LinkedList<>();
    private JSONArray sampleIdentifierJsonArray;
    private JSONArray samplePropertyJsonArray;

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @OnClick(R2.id.bt_top_edit)
    void onClickEdit() {
        getSupportDelegate().start(new EditDetailDelegate());
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        currentUserId = SPUtils.getInstance().getInt(SPKey.USERID);
        currentSurveyId = SPUtils.getInstance().getInt(SPKey.SURVEY_ID);
        currentSampleId = SPUtils.getInstance().getString(SPKey.SAMPLE_ID);
        sampleIdentifierJsonArray = JSON.parseArray(SPUtils.getInstance().getString(SPKey.SURVEY_SAMPLE_IDENTIFIER));
        samplePropertyJsonArray = JSON.parseArray(SPUtils.getInstance().getString(SPKey.SURVEY_SAMPLE_PROPERTY));
        if (sampleIdentifierJsonArray != null) {
            final int size = sampleIdentifierJsonArray.size();
            for (int i = 0; i < size; i++) {
                String key = sampleIdentifierJsonArray.getString(i);
                if (samplePropertyJsonArray.contains(key)) {
                    samplePropertyJsonArray.remove(key);
                }
            }
        }
        initView();
        initData();
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_sample_detail;
    }

    private void initView() {
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.rv_sample_property_list);
        recycleAdapter = new SampleDetailAdapter(list, this);
        recycleAdapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration
                (BaseDecoration.create(ContextCompat.getColor(getContext(), R.color.divider_line), 1));
        recyclerView.setAdapter(recycleAdapter);
    }

    private void initData() {
        list.clear();
        PropertyListBean divider = new PropertyListBean(SampleDetailItemType.DIVIDER_40);
        //顶部显示当前样本标识
        HashMap<String, String> sample = SampleDao.queryByIdAndSurveyId(SPUtils.getInstance().getInt(SPKey.USERID), currentSampleId, currentSurveyId);
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
        //显示样本的使用属性
        if (samplePropertyJsonArray != null) {
            int usePropertySize = samplePropertyJsonArray.size();
            for (int i = 0; i < usePropertySize; i++) {
                String key = samplePropertyJsonArray.getString(i);
                String value = sample.get(key);
                if (key != null) {
                    PropertyListBean item = new PropertyListBean(SampleDetailItemType.LIST_BEAN, AppUtil.getSampleProperty(key), value, false, i+3);
                    list.add(item);
                }
            }
        }
        boolean ifMore = SPUtils.getInstance().getBoolean(SPKey.SURVEY_IS_SUPPORT_SAMPLE_CONTACT_RECORD);
        if (ifMore) {
            if (samplePropertyJsonArray != null && samplePropertyJsonArray.size() > 0) {
                list.add(divider);
            }
            int contactNum = SampleContactDao.countContactBySample(currentUserId, currentSurveyId, currentSampleId);
            int addressNum = SampleAddressDao.countAddressBySample(currentUserId, currentSurveyId, currentSampleId);
            int touchNum = SampleTouchDao.countTouchBySample(currentUserId, currentSurveyId, currentSampleId);
            PropertyListBean item11 = new PropertyListBean(SampleDetailItemType.LIST_BEAN, "更多联系人", contactNum + "个", true, 11);
            PropertyListBean item12 = new PropertyListBean(SampleDetailItemType.LIST_BEAN, "更多地址", addressNum + "个", true, 12);
            PropertyListBean item13 = new PropertyListBean(SampleDetailItemType.LIST_BEAN, "接触记录", touchNum + "个", true, 13);
            list.add(item11);
            list.add(item12);
            list.add(divider);
            list.add(item13);
        }

        recycleAdapter.setNewData(list);
        recycleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        PropertyListBean item = (PropertyListBean) list.get(i);
        int id = item.getId();
        switch (id) {
            case 11:
                start(new ContactDelegate());
                break;
            case 12:
                start(new AddressDelegate());
                break;
            case 13:
                start(new TouchDelegate());
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initData();
        }
    }
}
