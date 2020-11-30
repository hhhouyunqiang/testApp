package com.ringdata.ringinterview.capi.components.constant;

/**
 * Created by bella_wang on 2020/3/26.
 */

public class ProjectConstants {

    /**
     * 项目类别
     * 0-全部项目
     * 1-我创建的项目
     * 2-我参与的项目
     */
    public static final Integer PROJECT_ALL = 0;
    public static final Integer PROJECT_CREATED_BY_ME = 1;
    public static final Integer PROJECT_DONE_BY_ME = 2;

    /**
     * 项目排序
     * 1-创建时间
     * 2-更新时间
     */
    public static final Integer PROJECT_SORT_BY_CREATE_TIME = 1;
    public static final Integer PROJECT_SORT_BY_UPDATE_TIME = 2;

    /**
     * 项目项目状态
     * 0-准备中
     * 1-已启动
     * 2-已暂停
     * 3-已结束
     */
    public static final Integer STATUS_WAIT = 0;
    public static final Integer STATUS_RUN = 1;
    public static final Integer STATUS_PAUSE = 2;
    public static final Integer STATUS_FINISH = 3;

    /**
     * 项目类型
     * 1-在线录入
     * 2-面访调查
     * 3-电话调查
     * 4-网络调查
     * 5-混合调查
     */
    public static final Integer PROJECT_TYPE_CADI = 1;
    public static final Integer PROJECT_TYPE_CAPI = 2;
    public static final Integer PROJECT_TYPE_CATI = 3;
    public static final Integer PROJECT_TYPE_CAWI = 4;
    public static final Integer PROJECT_TYPE_CAXI = 5;
    public static final String CADI_PROJECT = "CADI";
    public static final String CAPI_PROJECT = "CAPI";
    public static final String CATI_PROJECT = "CATI";
    public static final String CAWI_PROJECT = "CAWI";
    public static final String CAXI_PROJECT = "CAXI";

    /**
     * 配置项是否开启
     * 0-关闭
     * 1-开启
     */
    public static final Integer PROJECT_CONFIG_OFF = 0;
    public static final Integer PROJECT_CONFIG_ON = 1;

    /**
     * 样本分配方式
     * 1-预先分配
     * 2-动态分配
     */
    public static final Integer SAMPLE_PRE_ASSIGN = 1;
    public static final Integer SAMPLE_DYNAMIC_ASSIGN = 2;

}
