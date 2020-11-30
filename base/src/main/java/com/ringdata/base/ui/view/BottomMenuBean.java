package com.ringdata.base.ui.view;

/**
 * Created by admin on 2018/3/30.
 */

public class BottomMenuBean {
    private int tag;
    private String text;

    public BottomMenuBean(int tag, String text) {
        this.tag = tag;
        this.text = text;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
