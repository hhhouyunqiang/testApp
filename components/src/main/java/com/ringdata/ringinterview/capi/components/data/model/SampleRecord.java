package com.ringdata.ringinterview.capi.components.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * @Author: bella_wang
 * @Description: 样本变更记录实体类
 * @Date: Create in 2020/4/21 10:46
 */

public class SampleRecord implements Serializable, MultiItemEntity {

    private Integer id;

    private Integer userId;

    private Integer projectId;

    private String sampleGuid;

    private Integer status;

    private Integer isUpload;

    private Long createTime;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(Integer isUpload) {
        this.isUpload = isUpload;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
