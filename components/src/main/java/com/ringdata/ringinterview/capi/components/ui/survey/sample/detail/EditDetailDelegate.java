package com.ringdata.ringinterview.capi.components.ui.survey.sample.detail;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.ui.recycler.BaseDecoration;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.dao.SampleDao;
import com.ringdata.ringinterview.capi.components.utils.AppUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 2018/6/3.
 */

public class EditDetailDelegate extends LatteDelegate {
    @BindView(R2.id.tv_topbar_title)
    TextView titleView;
    @BindView(R2.id.bt_top_edit)
    Button btnAdd;
    private int currentUserId;
    private String currentSampleId;
    private int currentSurveyId;
    private LinkedList<MultiItemEntity> list = new LinkedList<>();
    private JSONArray samplePropertyJsonArray;

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @OnClick(R2.id.bt_top_edit)
    void onClickEdit() {
        //修改样本
        HashMap<String, String> hm = new HashMap<>();
        for (int i = 0; i < samplePropertyJsonArray.size(); i++) {
            String key = samplePropertyJsonArray.getString(i);
            TextView tv = (TextView) recyclerView.getChildAt(i).findViewById(R.id.tv_item_left);
            EditText item = (EditText) recyclerView.getChildAt(i).findViewById(R.id.tv_item_text);
            if (tv.getText().toString().contains("样本编号")) {
                if (TextUtils.isEmpty(item.getText())) {
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("请输入" + AppUtil.getSampleProperty(key));
                    return;
                } else {
                    hm.put(key, item.getText().toString());
                }
            }else {
                hm.put(key, item.getText().toString());
            }
        }
        hm.put("is_upload", "1");
        hm.put("last_modify_time", new Date().getTime() + "");
        if (SampleDao.countExistSampleCode(currentUserId, currentSurveyId, hm.get("code")) > 0) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("该样本编号已存在，请重新输入！");
        } else {
            boolean isUpdate = SampleDao.update(hm, currentSampleId);
            if (isUpdate) {
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort("修改样本成功");
                SPUtils.getInstance().put("IF_ADD_SAMPLE", 1);
                back();
            } else {
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort("修改样本失败");
            }
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        currentUserId = SPUtils.getInstance().getInt(SPKey.USERID);
        currentSampleId = SPUtils.getInstance().getString(SPKey.SAMPLE_ID);
        currentSurveyId = SPUtils.getInstance().getInt(SPKey.SURVEY_ID);
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
        titleView.setText("修改样本");
        btnAdd.setText("保存");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration
                (BaseDecoration.create(ContextCompat.getColor(getContext(), R.color.divider_line), 1));
        recyclerView.setAdapter(recycleAdapter);
    }

    private void initData() {
        list.clear();
        HashMap<String, String> sample = SampleDao.queryByIdAndSurveyId(SPUtils.getInstance().getInt(SPKey.USERID), currentSampleId, currentSurveyId);
        //显示样本的使用属性
        samplePropertyJsonArray = JSON.parseArray(SPUtils.getInstance().getString(SPKey.SURVEY_SAMPLE_PROPERTY));
        if (samplePropertyJsonArray != null) {
            for (int i = 0; i < samplePropertyJsonArray.size(); i++) {
                String key = samplePropertyJsonArray.getString(i);
                String value = sample.get(key);
                if (key != null) {
                    PropertyListBean item = new PropertyListBean(SampleDetailItemType.LIST_EDIT_BEAN, AppUtil.getSampleProperty(key), value, false, i);
                    list.add(item);
                }
            }
        }

        recycleAdapter.setNewData(list);
        recycleAdapter.notifyDataSetChanged();
    }
}
