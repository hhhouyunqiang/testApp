package com.ringdata.ringinterview.capi.components.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.detail.SampleDetailItemType;

import java.io.Serializable;

/**
 * Created by admin on 2018/6/4.
 */

public class SampleTouch implements MultiItemEntity, Serializable {

    private Integer id;

    private Integer userId;

    private Integer projectId;

    private String sampleGuid;

    private String type; //接触方式

    private Integer status; //接触状态

    private String description; //备注

    private Long createTime;

    private Integer createUser;

    private Integer isDelete; //0 未删除；1 已删除

    private Integer deleteUser;

    private Integer isUpload;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getSampleGuid() {
        return sampleGuid;
    }

    public void setSampleGuid(String sampleGuid) {
        this.sampleGuid = sampleGuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Integer createUser) {
        this.createUser = createUser;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getDeleteUser() {
        return deleteUser;
    }

    public void setDeleteUser(Integer deleteUser) {
        this.deleteUser = deleteUser;
    }

    public Integer getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(Integer isUpload) {
        this.isUpload = isUpload;
    }

    @Override
    public int getItemType() {
        return SampleDetailItemType.TOUCH;
    }

    @Override
    public String toString() {
        return "SampleTouch{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", sampleGuid='" + sampleGuid + '\'' +
                ", type=" + type + '\'' +
                ", status=" + status + '\'' +
                ", description=" + description +
                ", createTime=" + createTime +
                '}';
    }
}
