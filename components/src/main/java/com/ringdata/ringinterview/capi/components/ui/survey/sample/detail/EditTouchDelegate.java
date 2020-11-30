package com.ringdata.ringinterview.capi.components.ui.survey.sample.detail;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.Msg;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.MessageEvent;
import com.ringdata.ringinterview.capi.components.data.dao.SampleTouchDao;
import com.ringdata.ringinterview.capi.components.data.model.SampleTouch;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Bella_wang on 2020/04/03.
 */

public class EditTouchDelegate extends LatteDelegate {
    private final static String SAMPLE_TOUCH = "SAMPLE_TOUCH";
    private SampleTouch sampleTouch;

    @BindView(R2.id.tv_topbar_title)
    TextView tvTitle;
    @BindView(R2.id.tv_sample_detail_touch_fail)
    RadioButton failBtn;
    @BindView(R2.id.tv_sample_detail_touch_success)
    RadioButton successBtn;
    @BindView(R2.id.et_sample_detail_touch_type)
    EditText etType;
    @BindView(R2.id.et_sample_detail_touch_description)
    EditText etDescription;

    @Override
    public Object setLayout() {
        return R.layout.delegate_sample_detail_touch_edit;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        sampleTouch = (SampleTouch) getArguments().getSerializable(SAMPLE_TOUCH);
        if (sampleTouch == null) {
            tvTitle.setText("添加地址");

            sampleTouch = new SampleTouch();
            sampleTouch.setUserId(SPUtils.getInstance().getInt(SPKey.USERID));
            sampleTouch.setProjectId(SPUtils.getInstance().getInt(SPKey.SURVEY_ID));
            sampleTouch.setSampleGuid(SPUtils.getInstance().getString(SPKey.SAMPLE_ID));
            sampleTouch.setCreateUser(SPUtils.getInstance().getInt(SPKey.USERID));
            sampleTouch.setCreateTime(new Date().getTime());
        } else {
            tvTitle.setText("更多地址");

            etType.setText(sampleTouch.getType() + "");
            if (sampleTouch.getStatus() == 0) {
                failBtn.setChecked(true);
            } else {
                successBtn.setChecked(true);
            }
            etDescription.setText(sampleTouch.getDescription() + "");
        }
        sampleTouch.setIsUpload(1);
        sampleTouch.setIsDelete(0);
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @OnClick(R2.id.btn_top_save)
    public void onClickSave() {
        String type = etType.getText().toString().trim();
        if (TextUtils.isEmpty(type)) {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("接触方式不能为空");
            return;
        }
        String description = etDescription.getText().toString().trim();
        if (failBtn.isChecked()) {
            sampleTouch.setStatus(0);
        } else if (successBtn.isChecked()) {
            sampleTouch.setStatus(1);
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("请设置接触状态");
            return;
        }
        sampleTouch.setType(type);
        sampleTouch.setDescription(description);
        boolean success = SampleTouchDao.insertOrUpdate(sampleTouch);
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
        EventBus.getDefault().post(new MessageEvent(Msg.REFRESH_TOUCH, this));
    }

    public static EditTouchDelegate newInstance(SampleTouch sampleTouch) {
        Bundle args = new Bundle();
        args.putSerializable(SAMPLE_TOUCH, sampleTouch);
        EditTouchDelegate fragment = new EditTouchDelegate();
        fragment.setArguments(args);
        return fragment;
    }
}
