package com.ringdata.ringinterview.capi.components.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.ringinterview.capi.components.ui.survey.message.MessageItemType;

import java.io.Serializable;

/**
 * Created by admin on 17/11/11.
 */
public class Message implements MultiItemEntity, Serializable {

    private Integer id;

    private Integer userId;

    private String title;

    private String surveyName;

    private String moduleName;

    private String context;

    private Integer status;

    private Integer isRead;

    private String interviewerCellphone;

    private Integer isDelete;

    private String interviewerUsername;

    private String responseStatusText;

    private String interviewerName;

    private String auditQuestions;

    private String auditReturnReason;

    private String samplesMap;

    private String sampleIdentifier;

    private Long createTime;

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getInterviewerUsername() {
        return interviewerUsername;
    }

    public String getSamplesMap() {
        return samplesMap;
    }

    public String getSampleIdentifier() {
        return sampleIdentifier;
    }

    public void setSampleIdentifier(String sampleIdentifier) {
        this.sampleIdentifier = sampleIdentifier;
    }

    public String getInterviewerCellphone() {
        return interviewerCellphone;
    }

    public void setInterviewerCellphone(String interviewerCellphone) {
        this.interviewerCellphone = interviewerCellphone;
    }

    public void setSamplesMap(String samplesMap) {
        this.samplesMap = samplesMap;
    }

    public void setInterviewerUsername(String interviewerUsername) {
        this.interviewerUsername = interviewerUsername;
    }

    public String getResponseStatusText() {
        return responseStatusText;
    }

    public void setResponseStatusText(String responseStatusText) {
        this.responseStatusText = responseStatusText;
    }

    public String getInterviewerName() {
        return interviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    public String getAuditQuestions() {
        return auditQuestions;
    }

    public void setAuditQuestions(String auditQuestions) {
        this.auditQuestions = auditQuestions;
    }

    public String getAuditReturnReason() {
        return auditReturnReason;
    }

    public void setAuditReturnReason(String auditReturnReason) {
        this.auditReturnReason = auditReturnReason;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public int getItemType() {
        return MessageItemType.MESSAGE;
    }

}
