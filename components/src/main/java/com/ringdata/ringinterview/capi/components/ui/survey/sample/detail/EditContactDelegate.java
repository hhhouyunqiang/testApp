package com.ringdata.ringinterview.capi.components.ui.survey.sample.detail;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.Msg;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.MessageEvent;
import com.ringdata.ringinterview.capi.components.data.dao.SampleContactDao;
import com.ringdata.ringinterview.capi.components.data.model.SampleContact;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 2018/6/4.
 */

public class EditContactDelegate extends LatteDelegate {
    private final static String SAMPLE_CONTACT = "SAMPLE_CONTACT";
    private SampleContact sampleContact;

    @BindView(R2.id.tv_topbar_title)
    TextView tvTitle;
    @BindView(R2.id.et_sample_detail_contact_name)
    EditText etName;
    @BindView(R2.id.et_sample_detail_contact_relation)
    EditText etRelation;
    @BindView(R2.id.et_sample_detail_contact_phone)
    EditText etPhone;
    @BindView(R2.id.et_sample_detail_contact_mobile)
    EditText etMobile;
    @BindView(R2.id.et_sample_detail_contact_email)
    EditText etEmail;
    @BindView(R2.id.et_sample_detail_contact_qq)
    EditText etQq;
    @BindView(R2.id.et_sample_detail_contact_weixin)
    EditText etWeixin;
    @BindView(R2.id.et_sample_detail_contact_weibo)
    EditText etWeibo;
    @BindView(R2.id.et_sample_detail_contact_description)
    EditText etDescription;

    @Override
    public Object setLayout() {
        return R.layout.delegate_sample_detail_contact_edit;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        sampleContact = (SampleContact) getArguments().getSerializable(SAMPLE_CONTACT);
        if (sampleContact == null) {
            tvTitle.setText("添加联系人");

            sampleContact = new SampleContact();
            sampleContact.setUserId(SPUtils.getInstance().getInt(SPKey.USERID));
            sampleContact.setProjectId(SPUtils.getInstance().getInt(SPKey.SURVEY_ID));
            sampleContact.setSampleGuid(SPUtils.getInstance().getString(SPKey.SAMPLE_ID));
            sampleContact.setCreateUser(SPUtils.getInstance().getInt(SPKey.USERID));
            sampleContact.setCreateTime(new Date().getTime());
        } else {
            tvTitle.setText("更多联系人");

            etName.setText(sampleContact.getName() + "");
            etRelation.setText(sampleContact.getRelation() + "");
            etPhone.setText(sampleContact.getPhone() + "");
            etMobile.setText(sampleContact.getMobile() + "");
            etEmail.setText(sampleContact.getEmail() + "");
            etQq.setText(sampleContact.getQq() + "");
            etWeixin.setText(sampleContact.getWeixin() + "");
            etWeibo.setText(sampleContact.getWeibo() + "");
            etDescription.setText(sampleContact.getDescription() + "");
        }
        sampleContact.setIsUpload(1);
        sampleContact.setIsDelete(0);
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @OnClick(R2.id.btn_top_save)
    public void onClickSave() {
        String name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("姓名不能为空");
            return;
        }
        String relation = etRelation.getText().toString().trim();
        if (TextUtils.isEmpty(relation)) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("关系不能为空");
            return;
        }
        String phone = etPhone.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String qq = etQq.getText().toString().trim();
        String weixin = etWeixin.getText().toString().trim();
        String weibo = etWeibo.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        sampleContact.setName(name);
        sampleContact.setRelation(relation);
        sampleContact.setPhone(phone);
        sampleContact.setMobile(mobile);
        sampleContact.setEmail(email);
        sampleContact.setQq(qq);
        sampleContact.setWeixin(weixin);
        sampleContact.setWeibo(weibo);
        sampleContact.setDescription(description);
        boolean success = SampleContactDao.insertOrUpdate(sampleContact);
        if (success) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("保存成功");
            pop();
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("保存失败");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new MessageEvent(Msg.REFRESH_CONTACT, this));
    }

    public static EditContactDelegate newInstance(SampleContact sampleContact) {
        Bundle args = new Bundle();
        args.putSerializable(SAMPLE_CONTACT, sampleContact);
        EditContactDelegate fragment = new EditContactDelegate();
        fragment.setArguments(args);
        return fragment;
    }
}
