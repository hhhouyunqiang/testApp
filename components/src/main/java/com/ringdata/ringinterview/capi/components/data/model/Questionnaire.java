package com.ringdata.ringinterview.capi.components.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by admin on 17/11/11.
 */

/**
 * 问卷
 */
public class Questionnaire implements MultiItemEntity {

    private Integer itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(Integer itemType) {
        this.itemType = itemType;
    }

    private Integer id;

    private Integer userId;

    private Integer surveyId;

    private Integer moduleId;

    private Integer type; //模块类型（主/子）

    private String name; //问卷名称

    private String code; //问卷模块编号（A-Z）

    private Integer version;

    private Integer moduleDependency; //依赖关系(依赖的模块Id)

    private String sampleDependency; //依赖样本的某一属性 逻辑运算符 值(例：name = "张三")

    private Integer isAllowedManualAdd; //子问卷是否允许手动新增答卷

    private Integer quotaMin; //最小子问卷数量

    private Integer quotaMax; //最大子问卷数量

    private Integer groupId;

    private String groupName;

    private String url; //问卷js存放路径

    private String qrcUrl; //网络调查生成二维码url前缀

    private Long createTime; //创建时间

    private Integer isFileDownloadSuccess;

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

    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getModuleDependency() {
        return moduleDependency;
    }

    public void setModuleDependency(Integer moduleDependency) {
        this.moduleDependency = moduleDependency;
    }

    public String getSampleDependency() {
        return sampleDependency;
    }

    public void setSampleDependency(String sampleDependency) {
        this.sampleDependency = sampleDependency;
    }

    public Integer getIsAllowedManualAdd() {
        return isAllowedManualAdd;
    }

    public void setIsAllowedManualAdd(Integer isAllowedManualAdd) {
        this.isAllowedManualAdd = isAllowedManualAdd;
    }

    public Integer getQuotaMin() {
        return quotaMin;
    }

    public void setQuotaMin(Integer quotaMin) {
        this.quotaMin = quotaMin;
    }

    public Integer getQuotaMax() {
        return quotaMax;
    }

    public void setQuotaMax(Integer quotaMax) {
        this.quotaMax = quotaMax;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQrcUrl() {
        return qrcUrl;
    }

    public void setQrcUrl(String qrcUrl) {
        this.qrcUrl = qrcUrl;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getIsFileDownloadSuccess() {
        return isFileDownloadSuccess;
    }

    public void setIsFileDownloadSuccess(Integer isFileDownloadSuccess) {
        this.isFileDownloadSuccess = isFileDownloadSuccess;
    }

    @Override
    public String toString() {
        return "Questionnaire{" +
                "itemType=" + itemType + '\'' +
                ", id=" + id + '\'' +
                ", userId=" + userId + '\'' +
                ", surveyId=" + surveyId + '\'' +
                ", moduleId=" + moduleId + '\'' +
                ", type=" + type + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", version=" + version + '\'' +
                ", moduleDependency='" + moduleDependency + '\'' +
                ", sampleDependency='" + sampleDependency + '\'' +
                ", isAllowedManualAdd=" + isAllowedManualAdd + '\'' +
                ", groupId='" + groupId + '\'' +
                ", url='" + url + '\'' +
                ", qrcUrl='" + qrcUrl + '\'' +
                ", createTime=" + createTime + '\'' +
                ", isFileDownloadSuccess=" + isFileDownloadSuccess + '\'' +
                '}';
    }
}
