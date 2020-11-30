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
import com.ringdata.ringinterview.capi.components.data.dao.SampleAddressDao;
import com.ringdata.ringinterview.capi.components.data.model.SampleAddress;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 2018/6/4.
 */

public class EditAddressDelegate extends LatteDelegate {
    private final static String SAMPLE_ADDRESS = "SAMPLE_ADDRESS";
    private SampleAddress sampleAddress;

    @BindView(R2.id.tv_topbar_title)
    TextView tvTitle;
    @BindView(R2.id.et_sample_detail_address_name)
    EditText etName;
    @BindView(R2.id.et_sample_detail_address_province)
    EditText etProvince;
    @BindView(R2.id.et_sample_detail_address_city)
    EditText etCity;
    @BindView(R2.id.et_sample_detail_address_district)
    EditText etDistrict;
    @BindView(R2.id.et_sample_detail_address_town)
    EditText etTown;
    @BindView(R2.id.et_sample_detail_address_village)
    EditText etVillage;
    @BindView(R2.id.et_sample_detail_address_address)
    EditText etAddress;
    @BindView(R2.id.et_sample_detail_address_description)
    EditText etDescription;

    @Override
    public Object setLayout() {
        return R.layout.delegate_sample_detail_address_edit;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        sampleAddress = (SampleAddress) getArguments().getSerializable(SAMPLE_ADDRESS);
        if (sampleAddress == null) {
            tvTitle.setText("添加地址");

            sampleAddress = new SampleAddress();
            sampleAddress.setUserId(SPUtils.getInstance().getInt(SPKey.USERID));
            sampleAddress.setProjectId(SPUtils.getInstance().getInt(SPKey.SURVEY_ID));
            sampleAddress.setSampleGuid(SPUtils.getInstance().getString(SPKey.SAMPLE_ID));
            sampleAddress.setCreateUser(SPUtils.getInstance().getInt(SPKey.USERID));
            sampleAddress.setCreateTime(new Date().getTime());
        } else {
            tvTitle.setText("更多地址");

            etName.setText(sampleAddress.getName() + "");
            etProvince.setText(sampleAddress.getProvince() + "");
            etCity.setText(sampleAddress.getCity() + "");
            etDistrict.setText(sampleAddress.getDistrict() + "");
            etTown.setText(sampleAddress.getTown() + "");
            etVillage.setText(sampleAddress.getVillage() + "");
            etAddress.setText(sampleAddress.getAddress() + "");
            etDescription.setText(sampleAddress.getDescription() + "");
        }
        sampleAddress.setIsUpload(1);
        sampleAddress.setIsDelete(0);
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
            ToastUtils.showShort("地址名称不能为空");
            return;
        }
        String province = etProvince.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String town = etTown.getText().toString().trim();
        String village = etVillage.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        sampleAddress.setName(name);
        sampleAddress.setProvince(province);
        sampleAddress.setCity(city);
        sampleAddress.setDistrict(district);
        sampleAddress.setTown(town);
        sampleAddress.setVillage(village);
        sampleAddress.setAddress(address);
        sampleAddress.setDescription(description);
        boolean success = SampleAddressDao.insertOrUpdate(sampleAddress);
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
        EventBus.getDefault().post(new MessageEvent(Msg.REFRESH_ADDRESS, this));
    }

    public static EditAddressDelegate newInstance(SampleAddress sampleAddress) {
        Bundle args = new Bundle();
        args.putSerializable(SAMPLE_ADDRESS, sampleAddress);
        EditAddressDelegate fragment = new EditAddressDelegate();
        fragment.setArguments(args);
        return fragment;
    }
}
