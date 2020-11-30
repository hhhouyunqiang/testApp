package com.ringdata.ringinterview.capi.components.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * Created by admin on 17/11/14.
 */

public class Response implements Serializable, MultiItemEntity {

    private Integer itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(Integer itemType) {
        this.itemType = itemType;
    }

    private String id;

    private Integer userId;

    private Integer interviewerId; //访员的id,有可能是别的访员的id

    private Integer projectId;

    private String sampleGuid;

    private Integer moduleId;

    private String moduleCode;

    private String moduleName;

    private Integer dependencyId; //子问卷所依赖的主问卷的模块id

    private Integer questionnaireId;

    private String questionnaireName;

    private Integer questionnaireType;

    private String url;

    private String QuestionnaireUrl;

    private Integer version;

    private String subQuestionnaireJson; //app生成子问卷的json

    private Integer samplingStatus; //抽样状态

    private String lastDialNo; //做这份问卷时拨打的号码

    private Integer qrScanStatus; //二维码扫描状态

    private String lastModifyResource; //上次修改源

    private Integer isMonitor; //是否监听

    private Integer isSendRedPacket; //是否发红包

    private String explorerType; //浏览器类型

    private String explorerVersion; //浏览器版本

    private String lon; //经度

    private String lat; //纬度

    private String address;

    private Long startTime;

    private Long endTime;

    private Double score; //分数

    private String submitData;

    private String submitState;

    private Long responseDuration; //答卷实际作答时长

    private Integer responseType; //答卷类型

    private Integer responseStatus; //答卷状态
    //答卷状态null,0未开始 1.答题中（暂存到服务器）
    //6答题成功8审核合格，9审核无效,10审核退回

    private String responseData; //答卷数据，json格式

    private String questionData; //问题数据，json格式

    private String answeredCurrent; //当前答到第几题

    private String answeredQuestions; //已经回答过的问题，数组形式

    private Integer auditUser; //审核用户

    private Long auditTime; //审核时间

    private String auditResult; //审核结果

    private Double auditScore; //审核评分

    private String auditComments; //审核说明

    private Integer isExcellent; //是否优秀答卷

    //TODO 答卷标识？
    private String responseIdentifier;

    //TODO
    private String subJson; //子问卷数据
    private Integer isDownloadDetail;
    private Integer caxiWebStatus;

    private Long lastModifyTime;

    private Long createTime;

    private Integer isUpload; //1默认（上传失败），2已经上传，3上传失败

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getInterviewerId() {
        return interviewerId;
    }

    public void setInterviewerId(Integer interviewerId) {
        this.interviewerId = interviewerId;
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

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Integer getDependencyId() {
        return dependencyId;
    }

    public void setDependencyId(Integer dependencyId) {
        this.dependencyId = dependencyId;
    }

    public Integer getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Integer questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getQuestionnaireName() {
        return questionnaireName;
    }

    public void setQuestionnaireName(String questionnaireName) {
        this.questionnaireName = questionnaireName;
    }

    public Integer getQuestionnaireType() {
        return questionnaireType;
    }

    public void setQuestionnaireType(Integer questionnaireType) {
        this.questionnaireType = questionnaireType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQuestionnaireUrl() {
        return QuestionnaireUrl;
    }

    public void setQuestionnaireUrl(String questionnaireUrl) {
        QuestionnaireUrl = questionnaireUrl;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getSubQuestionnaireJson() {
        return subQuestionnaireJson;
    }

    public void setSubQuestionnaireJson(String subQuestionnaireJson) {
        this.subQuestionnaireJson = subQuestionnaireJson;
    }

    public Integer getSamplingStatus() {
        return samplingStatus;
    }

    public void setSamplingStatus(Integer samplingStatus) {
        this.samplingStatus = samplingStatus;
    }

    public String getLastDialNo() {
        return lastDialNo;
    }

    public void setLastDialNo(String lastDialNo) {
        this.lastDialNo = lastDialNo;
    }

    public Integer getQrScanStatus() {
        return qrScanStatus;
    }

    public void setQrScanStatus(Integer qrScanStatus) {
        this.qrScanStatus = qrScanStatus;
    }

    public String getLastModifyResource() {
        return lastModifyResource;
    }

    public void setLastModifyResource(String lastModifyResource) {
        this.lastModifyResource = lastModifyResource;
    }

    public Integer getIsMonitor() {
        return isMonitor;
    }

    public void setIsMonitor(Integer isMonitor) {
        this.isMonitor = isMonitor;
    }

    public Integer getIsSendRedPacket() {
        return isSendRedPacket;
    }

    public void setIsSendRedPacket(Integer isSendRedPacket) {
        this.isSendRedPacket = isSendRedPacket;
    }

    public String getExplorerType() {
        return explorerType;
    }

    public void setExplorerType(String explorerType) {
        this.explorerType = explorerType;
    }

    public String getExplorerVersion() {
        return explorerVersion;
    }

    public void setExplorerVersion(String explorerVersion) {
        this.explorerVersion = explorerVersion;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getSubmitData() {
        return submitData;
    }

    public void setSubmitData(String submitData) {
        this.submitData = submitData;
    }

    public String getSubmitState() {
        return submitState;
    }

    public void setSubmitState(String submitState) {
        this.submitState = submitState;
    }

    public Long getResponseDuration() {
        return responseDuration;
    }

    public void setResponseDuration(Long responseDuration) {
        this.responseDuration = responseDuration;
    }

    public Integer getResponseType() {
        return responseType;
    }

    public void setResponseType(Integer responseType) {
        this.responseType = responseType;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getQuestionData() {
        return questionData;
    }

    public void setQuestionData(String questionData) {
        this.questionData = questionData;
    }

    public String getAnsweredCurrent() {
        return answeredCurrent;
    }

    public void setAnsweredCurrent(String answeredCurrent) {
        this.answeredCurrent = answeredCurrent;
    }

    public String getAnsweredQuestions() {
        return answeredQuestions;
    }

    public void setAnsweredQuestions(String answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }

    public Integer getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(Integer auditUser) {
        this.auditUser = auditUser;
    }

    public Long getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Long auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }

    public Double getAuditScore() {
        return auditScore;
    }

    public void setAuditScore(Double auditScore) {
        this.auditScore = auditScore;
    }

    public String getAuditComments() {
        return auditComments;
    }

    public void setAuditComments(String auditComments) {
        this.auditComments = auditComments;
    }

    public Integer getIsExcellent() {
        return isExcellent;
    }

    public void setIsExcellent(Integer isExcellent) {
        this.isExcellent = isExcellent;
    }

    public String getResponseIdentifier() {
        return responseIdentifier;
    }

    public void setResponseIdentifier(String responseIdentifier) {
        this.responseIdentifier = responseIdentifier;
    }

    public String getSubJson() {
        return subJson;
    }

    public void setSubJson(String subJson) {
        this.subJson = subJson;
    }

    public Integer getIsDownloadDetail() {
        return isDownloadDetail;
    }

    public void setIsDownloadDetail(Integer isDownloadDetail) {
        this.isDownloadDetail = isDownloadDetail;
    }

    public Integer getCaxiWebStatus() {
        return caxiWebStatus;
    }

    public void setCaxiWebStatus(Integer caxiWebStatus) {
        this.caxiWebStatus = caxiWebStatus;
    }

    public Long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(Integer isUpload) {
        this.isUpload = isUpload;
    }

    @Override
    public String toString() {
        return "Response{" +
                "itemType=" + itemType +
                ", id='" + id + '\'' +
                ", userId=" + userId +
                ", interviewerId=" + interviewerId +
                ", surveyId=" + projectId +
                ", responseIdentifier='" + responseIdentifier + '\'' +
                ", moduleId=" + moduleId +
                ", moduleCode='" + moduleCode + '\'' +
                ", version=" + version +
                ", questionnaireId=" + questionnaireId +
                ", sampleId='" + sampleGuid + '\'' +
                ", lastModifyTime=" + lastModifyTime +
                ", startTime=" + startTime +
                ", subJson='" + subJson + '\'' +
                ", endTime=" + endTime +
                ", responseStatus=" + responseStatus +
                ", submitData='" + submitData + '\'' +
                ", caxiWebStatus=" + caxiWebStatus +
                ", submitState='" + submitState + '\'' +
                ", createTime=" + createTime +
                ", isUpload=" + isUpload +
                '}';
    }
}
