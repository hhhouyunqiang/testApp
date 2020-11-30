package com.ringdata.ringinterview.capi.components.ui.survey.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.ui.recycler.BaseDecoration;
import com.ringdata.base.util.GUIDUtil;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.dao.SampleDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleRecordDao;
import com.ringdata.ringinterview.capi.components.data.model.Sample;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.detail.PropertyListBean;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.detail.SampleDetailAdapter;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.detail.SampleDetailItemType;
import com.ringdata.ringinterview.capi.components.utils.AppUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import butterknife.OnClick;

/**
 * Created by admin on 2018/1/23.
 */

public class SampleAddDelegate extends LatteDelegate {
    private int currentUserId;
    private int currentSurveyId;
    private String currentSampleId;
    private LinkedList<MultiItemEntity> list = new LinkedList<>();
    private JSONArray samplePropertyJsonArray;
    private JSONArray sampleIdentifierJsonArray;
    private HashMap<String, String> hm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserId = SPUtils.getInstance().getInt(SPKey.USERID);
        currentSurveyId = SPUtils.getInstance().getInt(SPKey.SURVEY_ID);
        currentSampleId = SPUtils.getInstance().getString(SPKey.SAMPLE_ID);
        hm = new HashMap<String, String>();
        SPUtils.getInstance().put("IF_ADD_SAMPLE", 0); //没有成功新增样本（默认）
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_sample_add;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initView();
        initData();
    }

    @OnClick(R2.id.bt_sample_save)
    public void onClickSave() {
        final int size = samplePropertyJsonArray.size();
        Long createTime = new Date().getTime();
        String displayName = "";

        hm.clear();
        String sampleId = GUIDUtil.getGuidStr();
        hm.put("sample_guid", sampleId);
        hm.put("user_id", currentUserId + "");
        hm.put("survey_id", currentSurveyId + "");
        hm.put("status", "1");
        hm.put("is_add", "1");

        HashMap<String, String> sr = new HashMap<String, String>();
        sr.put("user_id", currentUserId + "");
        sr.put("project_id", currentSurveyId + "");
        sr.put("sample_guid", sampleId);
        sr.put("status", "0");
        sr.put("create_time", createTime + "");

        for (int i = 0; i < size; i++) {
            String key = samplePropertyJsonArray.getString(i);
            TextView tv = (TextView) recyclerView.getChildAt(i).findViewById(R.id.tv_item_left);
            EditText item = (EditText) recyclerView.getChildAt(i).findViewById(R.id.tv_item_text);
            if (tv.getText().toString().contains("*")) {
                if (TextUtils.isEmpty(item.getText())) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("请输入" + AppUtil.getSampleProperty(key));
                    return;
                } else {
                    hm.put(key, item.getText().toString());
                    if (sampleIdentifierJsonArray.contains(key)) {
                        displayName = displayName.concat(item.getText().toString()).concat(",");
                    }
                }
            } else {
                hm.put(key, item.getText().toString());
            }
        }
        hm.put("display_name", displayName.substring(0, displayName.length()-1));
        hm.put("is_upload", "1");
        hm.put("create_time", createTime + "");
        if (SampleDao.countExistSampleCode(currentUserId, currentSurveyId, hm.get("code")) > 0) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("该样本编号已存在，请重新输入！");
        } else {
            boolean isAddSuccess = SampleDao.insert(hm);
            boolean addRecord = SampleRecordDao.insert(sr);
            sr.put("status", "1");
            boolean addRecord1 = SampleRecordDao.insert(sr);
            if (!isAddSuccess && addRecord && addRecord1) {
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort("新增样本异常");
                SPUtils.getInstance().put("IF_ADD_SAMPLE", 0); //没有成功新增样本
            } else {
                SPUtils.getInstance().put("IF_ADD_SAMPLE", 1); //成功新增样本
                back();
            }
        }
    }

    private void initView() {
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.rv_sample_property_list);
        recycleAdapter = new SampleDetailAdapter(list, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration
                (BaseDecoration.create(ContextCompat.getColor(getContext(), R.color.divider_line), 1));
        recyclerView.setAdapter(recycleAdapter);
    }

    private void initData() {
        list.clear();
        //显示样本的使用属性（只有样本编号必填）
        samplePropertyJsonArray = JSON.parseArray(SPUtils.getInstance().getString(SPKey.SURVEY_SAMPLE_PROPERTY));
        sampleIdentifierJsonArray = JSON.parseArray(SPUtils.getInstance().getString(SPKey.SURVEY_SAMPLE_IDENTIFIER));
        if (samplePropertyJsonArray != null) {
            int usePropertySize = samplePropertyJsonArray.size();
            for (int i = 0; i < usePropertySize; i++) {
                String key = samplePropertyJsonArray.getString(i);
                if (key != null) {
                    PropertyListBean item = null;
                    if (key.equals("code")) {
                        //自动生成样本编号
                        String name = SPUtils.getInstance().getString(SPKey.USERNAME);
                        Sample latestSample = SampleDao.getLatestSample(currentUserId, currentSurveyId);
                        if (latestSample != null && latestSample.getCode() != null && !"".equals(latestSample.getCode()) && latestSample.getCode().length() > 3) {
                            String index = latestSample.getCode().substring(latestSample.getCode().length()-2, latestSample.getCode().length());
                            String pre = latestSample.getCode().substring(0, latestSample.getCode().length()-2);
                            int newIndex = 1;
                            try {
                                if (index.startsWith("0")) {
                                    newIndex = Integer.parseInt(index.substring(1)) + 1;
                                } else {
                                    newIndex = Integer.parseInt(index) + 1;
                                }
                                if (newIndex > 9) {
                                    item = new PropertyListBean(SampleDetailItemType.LIST_EDIT_BEAN, "<font color='#EE8118'>* </font>" + AppUtil.getSampleProperty(key), pre + newIndex, false, i);
                                } else {
                                    item = new PropertyListBean(SampleDetailItemType.LIST_EDIT_BEAN, "<font color='#EE8118'>* </font>" + AppUtil.getSampleProperty(key), pre + "0" + newIndex, false, i);
                                }
                            } catch (NumberFormatException e) {
                                Log.e("PARSEINT", "error");
                                item = new PropertyListBean(SampleDetailItemType.LIST_EDIT_BEAN, "<font color='#EE8118'>* </font>" + AppUtil.getSampleProperty(key), name + "01", false, i);
                            }
                        } else {
                            item = new PropertyListBean(SampleDetailItemType.LIST_EDIT_BEAN, "<font color='#EE8118'>* </font>" + AppUtil.getSampleProperty(key), name + "01", false, i);
                        }
                    } else {
                        item = new PropertyListBean(SampleDetailItemType.LIST_EDIT_BEAN, AppUtil.getSampleProperty(key), null, false, i);
                    }
                    list.add(item);
                }
            }
        }

        recycleAdapter.setNewData(list);
        recycleAdapter.notifyDataSetChanged();
    }
}
