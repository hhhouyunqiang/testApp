package com.ringdata.ringinterview.capi.components.ui.survey.sample;

import com.bigkoo.pickerview.model.IPickerViewData;

import java.util.ArrayList;

/**
 * Created by admin on 2018/6/5.
 */

public class RecordStatusBean implements IPickerViewData{
    private int code;
    private String name;
    private ArrayList<RecordStatusBean> detailStatus;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<RecordStatusBean> getDetailStatus() {
        return detailStatus;
    }

    public void setDetailStatus(ArrayList<RecordStatusBean> detailStatus) {
        this.detailStatus = detailStatus;
    }

    @Override
    public String toString() {
        return "RecordStatusBean{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", detailStatus=" + detailStatus +
                '}';
    }

    @Override
    public String getPickerViewText() {
        return name;
    }
}
