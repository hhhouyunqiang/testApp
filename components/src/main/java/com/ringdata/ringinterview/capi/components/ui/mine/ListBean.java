package com.ringdata.ringinterview.capi.components.ui.mine;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by admin on 2018/2/27.
 */

public class ListBean implements MultiItemEntity {
    private int itemType = 0;
    private int iconLeftColor;
    private String text = null;
    private String time = null;
    private int rightNum = 0;
    private int id = 0;

    public ListBean(int itemType){
        this.itemType = itemType;
    }

    public ListBean(int itemType, int id){
        this.itemType = itemType;
        this.id = id;
    }

    public ListBean(int itemType, int iconLeftColor, String text, String time, int rightNum, int id) {
        this.itemType = itemType;
        this.iconLeftColor = iconLeftColor;
        this.text = text;
        this.time = time;
        this.rightNum = rightNum;
        this.id = id;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getIconLeftColor() {
        return iconLeftColor;
    }

    public void setIconLeftColor(int iconLeftColor) {
        this.iconLeftColor = iconLeftColor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRightNum() {
        return rightNum;
    }

    public void setRightNum(int rightText) {
        this.rightNum = rightText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}