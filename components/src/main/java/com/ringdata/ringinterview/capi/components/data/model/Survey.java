package com.ringdata.ringinterview.capi.components.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.ringinterview.capi.components.ui.survey.SurveyItemType;

/**
 * Created by admin on 17/11/11.
 */

public class Survey implements MultiItemEntity {

    private Integer id;

    private Integer userId; //当前用户id

    private String name; //项目名称

    private String description; //项目简介

    private Integer status; //项目状态

    private String type; //项目类型

    private String labelText; //项目标签

    private String config; //项目配置项

    private String roleName; //角色（多种以逗号分隔）

    private Integer createUserId; //创建者id

    private String createUserName; //创建者用户名

    private String createTime; //创建时间（用于排序）

    private String createTimeStr; //创建时间（用于显示）

    private String updateTime; //更新时间

    private String beginTime; //启动时间

    private String endTime; //结束时间

    private String pauseTime; //停止时间

    private String useProperty; //使用属性

    private String markProperty; //样本标识

    private Integer numOfNotStarted; //未开始(初始化+已分派)

    private Integer numOfInProgress; //进行

    private Integer numOfFinish; //完成

    private Integer numOfRefuse; //拒访

    private Integer numOfIdentify; //甄别

    private Integer numOfAppointment; //预约

    private Integer numOfUnableToContact; //无效号码/无法联系

    private Integer numOfInTheCall; //通话中

    private Integer numOfNoAnswer; //无人接听

    private Integer numOfAuditInvalid; //审核无效

    private Integer numOfReturn; //审核退回

    private Integer numOfAuditSuccess; //审核成功

    private Integer remainInterval; //记录访问员轨迹定位剩余时间（几个5分钟）

    @Override
    public int getItemType() {
        return SurveyItemType.SURVEY;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(String pauseTime) {
        this.pauseTime = pauseTime;
    }

    public String getUseProperty() {
        return useProperty;
    }

    public void setUseProperty(String useProperty) {
        this.useProperty = useProperty;
    }

    public String getMarkProperty() {
        return markProperty;
    }

    public void setMarkProperty(String markProperty) {
        this.markProperty = markProperty;
    }

    public Integer getNumOfNotStarted() {
        return numOfNotStarted;
    }

    public void setNumOfNotStarted(Integer numOfNotStarted) {
        this.numOfNotStarted = numOfNotStarted;
    }

    public Integer getNumOfInProgress() {
        return numOfInProgress;
    }

    public void setNumOfInProgress(Integer numOfInProgress) {
        this.numOfInProgress = numOfInProgress;
    }

    public Integer getNumOfFinish() {
        return numOfFinish;
    }

    public void setNumOfFinish(Integer numOfFinish) {
        this.numOfFinish = numOfFinish;
    }

    public Integer getNumOfRefuse() {
        return numOfRefuse;
    }

    public void setNumOfRefuse(Integer numOfRefuse) {
        this.numOfRefuse = numOfRefuse;
    }

    public Integer getNumOfIdentify() {
        return numOfIdentify;
    }

    public void setNumOfIdentify(Integer numOfIdentify) {
        this.numOfIdentify = numOfIdentify;
    }

    public Integer getNumOfAppointment() {
        return numOfAppointment;
    }

    public void setNumOfAppointment(Integer numOfAppointment) {
        this.numOfAppointment = numOfAppointment;
    }

    public Integer getNumOfUnableToContact() {
        return numOfUnableToContact;
    }

    public void setNumOfUnableToContact(Integer numOfUnableToContact) {
        this.numOfUnableToContact = numOfUnableToContact;
    }

    public Integer getNumOfInTheCall() {
        return numOfInTheCall;
    }

    public void setNumOfInTheCall(Integer numOfInTheCall) {
        this.numOfInTheCall = numOfInTheCall;
    }

    public Integer getNumOfNoAnswer() {
        return numOfNoAnswer;
    }

    public void setNumOfNoAnswer(Integer numOfNoAnswer) {
        this.numOfNoAnswer = numOfNoAnswer;
    }

    public Integer getNumOfAuditInvalid() {
        return numOfAuditInvalid;
    }

    public void setNumOfAuditInvalid(Integer numOfAuditInvalid) {
        this.numOfAuditInvalid = numOfAuditInvalid;
    }

    public Integer getNumOfReturn() {
        return numOfReturn;
    }

    public void setNumOfReturn(Integer numOfReturn) {
        this.numOfReturn = numOfReturn;
    }

    public Integer getNumOfAuditSuccess() {
        return numOfAuditSuccess;
    }

    public void setNumOfAuditSuccess(Integer numOfAuditSuccess) {
        this.numOfAuditSuccess = numOfAuditSuccess;
    }

    public Integer getRemainInterval() {
        return remainInterval;
    }

    public void setRemainInterval(Integer remainInterval) {
        this.remainInterval = remainInterval;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", userId=" + userId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", labelText=" + labelText + '\'' +
                ", config='" + config + '\'' +
                ", roleName='" + roleName + '\'' +
                ", createUserId='" + createUserId + '\'' +
                ", createUserName='" + createUserName + '\'' +
                ", createTime=" + createTime + '\'' +
                ", updateTime=" + updateTime + '\'' +
                ", beginTime=" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", pauseTime='" + pauseTime + '\'' +
                ", useProperty='" + useProperty + '\'' +
                ", markProperty='" + markProperty + '\'' +
                ", numOfNotStarted='" + numOfNotStarted + '\'' +
                ", numOfInProgress='" + numOfInProgress + '\'' +
                ", numOfFinish='" + numOfFinish + '\'' +
                ", numOfRefuse='" + numOfRefuse + '\'' +
                ", numOfIdentify='" + numOfIdentify + '\'' +
                ", numOfAppointment='" + numOfAppointment + '\'' +
                ", numOfUnableToContact='" + numOfUnableToContact + '\'' +
                ", numOfInTheCall='" + numOfInTheCall + '\'' +
                ", numOfNoAnswer='" + numOfNoAnswer + '\'' +
                ", numOfAuditInvalid='" + numOfAuditInvalid + '\'' +
                ", numOfReturn='" + numOfReturn + '\'' +
                ", numOfAuditSuccess='" + numOfAuditSuccess + '\'' +
                '}';
    }
}
