package com.ringdata.ringinterview.capi.components.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.detail.SampleDetailItemType;

import java.io.Serializable;

/**
 * Created by admin on 2018/6/4.
 */

public class SampleAddress implements MultiItemEntity, Serializable {

    private Integer id;

    private Integer userId;

    private Integer projectId;

    private String sampleGuid;

    private String name; //地址名称

    private String province; //省

    private String city; //市

    private String district; //区

    private String town; //镇

    private String village; //村

    private String address; //详细地址

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String provice) {
        this.province = provice;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        return SampleDetailItemType.ADDRESS;
    }

    @Override
    public String toString() {
        return "SampleAddress{" +
                "id=" + id + '\'' +
                ", userId=" + userId + '\'' +
                ", projectId=" + projectId + '\'' +
                ", sampleGuid=" + sampleGuid + '\'' +
                ", name='" + name + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", town=" + town + '\'' +
                ", village=" + village + '\'' +
                ", address=" + address + '\'' +
                ", description=" + description + '\'' +
                ", createTime=" + createTime + '\'' +
                ", createUser=" + createUser +
                '}';
    }
}
