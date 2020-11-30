package com.ringdata.ringinterview.capi.components.ui.survey.sample.detail;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by bella_wang on 2020/03/30.
 */

public class PropertyListBean implements MultiItemEntity {
    private int itemType = 0;
    private int titleType = 0;
    private MultiItemEntity item;
    private String left = null;
    private String text = null;
    private boolean right = false;
    private int id = 0;

    public PropertyListBean(int itemType){
        this.itemType = itemType;
    }

    public PropertyListBean(int itemType, String left, String text, boolean right, int id) {
        this.itemType = itemType;
        this.left = left;
        this.text = text;
        this.right = right;
        this.id = id;
    }

    public PropertyListBean(int itemType, int titleType, MultiItemEntity item, String left, boolean right, int id) {
        this.itemType = itemType;
        this.titleType = titleType;
        this.item = item;
        this.left = left;
        this.right = right;
        this.id = id;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getTitleType() {
        return titleType;
    }

    public void setTitleType(int titleType) {
        this.titleType = titleType;
    }

    public MultiItemEntity getItem() {
        return item;
    }

    public void setItem(MultiItemEntity item) {
        this.item = item;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}