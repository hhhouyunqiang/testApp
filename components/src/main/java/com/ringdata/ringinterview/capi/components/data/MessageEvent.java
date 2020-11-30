package com.ringdata.ringinterview.capi.components.data;

/**
 * Created by admin on 17/11/30.
 */
public class MessageEvent {

    private int tag;//消息类型
    private String msg;//消息
    private Object bindTag;//消息

    public MessageEvent(int tag, Object bindTag) {
        this.tag = tag;
        this.bindTag = bindTag;
    }

    public MessageEvent(int tag, Object bindTag, String msg) {
        this.tag = tag;
        this.bindTag = bindTag;
        this.msg = msg;
    }

    public Object getBindTag() {
        return bindTag;
    }

    public void setBindTag(Object bindTag) {
        this.bindTag = bindTag;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
