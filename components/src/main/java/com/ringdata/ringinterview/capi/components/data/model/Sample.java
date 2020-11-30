package com.ringdata.ringinterview.capi.components.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.SampleItemType;

import java.io.Serializable;

/**
 * Created by admin on 17/11/11.
 */

public class Sample implements Serializable, MultiItemEntity {

    private Integer id;

    private String sampleGuid;

    private String name;

    private String code;

    private Integer userId;

    private Integer surveyId;

    private String gender;

    private String email; //邮箱

    private String phone; //手机

    private String mobile; //电话

    private String province; //省

    private String city; //市

    private String district; //区

    private String town; //镇

    private String address; //详细地址

    private String organization; //单位

    private Float lon; //经度

    private Float lat; //纬度

    private String extraData; //附加数据

    private String responseVariable; //答卷变量

//    private String samplePoint;

    private String displayName; //样本标识拼接

    private Long startTime; //开始访问时间

    private Long endTime; //结束访问时间

    private Long duration; //答卷总时长

    private Integer interviewerId;

    private Integer assignmentType; //当前用户对当前样本拥有的角色权限

    private Integer status; //样本状态

    private Long appointVisitTime; //预约回访时间

    private String description; //备注

//    private Integer responseSubmitCount; //已提交问卷数量
//
//    private Integer responseAllCount; //

    private String custom1, custom2, custom3, custom4, custom5;

    private Long createTime;

    private Long lastModifyTime;

    private Integer isAdd; //1本地新增样本；2否

    private Integer isUpload;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public String getResponseVariable() {
        return responseVariable;
    }

    public void setResponseVariable(String responseVariable) {
        this.responseVariable = responseVariable;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getInterviewerId() {
        return interviewerId;
    }

    public void setInterviewerId(Integer interviewerId) {
        this.interviewerId = interviewerId;
    }

    public Integer getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(Integer assignmentType) {
        this.assignmentType = assignmentType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getAppointVisitTime() {
        return appointVisitTime;
    }

    public void setAppointVisitTime(Long appointVisitTime) {
        this.appointVisitTime = appointVisitTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    public String getCustom2() {
        return custom2;
    }

    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    public String getCustom3() {
        return custom3;
    }

    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    public String getCustom4() {
        return custom4;
    }

    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    public String getCustom5() {
        return custom5;
    }

    public void setCustom5(String custom5) {
        this.custom5 = custom5;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Integer getIsAdd() {
        return isAdd;
    }

    public void setIsAdd(Integer isAdd) {
        this.isAdd = isAdd;
    }

    public Integer getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(Integer isUpload) {
        this.isUpload = isUpload;
    }

    public String getStatusString() {
        switch (status) {
            case 0:
                return "未开始";
            case 1:
                return "已分派";
            case 2:
                return "进行中";
            case 3:
                return "已完成";
            case 4:
                return "拒访";
            case 5:
                return "甄别";
            case 6:
                return "预约";
            case 7:
                return "无效号码";
            case 8:
                return "通话中";
            case 9:
                return "无人接听";
            case 10:
                return "审核无效";
            case 11:
                return "审核退回";
            case 12:
                return "审核成功";
        }
        return "--";
    }

    @Override
    public int getItemType() {
        return SampleItemType.SAMPLE;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", userId=" + userId +
                ", surveyId=" + surveyId +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", town='" + town + '\'' +
                ", address='" + address + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                ", extraData='" + extraData + '\'' +
                ", responseVariable='" + responseVariable + '\'' +
                ", displayName='" + displayName + '\'' +
                ", interviewerId=" + interviewerId +
                ", status=" + status +
                ", appointVisitTime=" + appointVisitTime +
                ", custom1='" + custom1 + '\'' +
                ", custom2='" + custom2 + '\'' +
                ", custom3='" + custom3 + '\'' +
                ", custom4='" + custom4 + '\'' +
                ", custom5='" + custom5 + '\'' +
                ", createTime=" + createTime +
                ", isUpload=" + isUpload +
                '}';
    }
}
