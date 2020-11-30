package com.ringdata.ringinterview.capi.components.constant;

/**
 * Created by admin on 2018/5/8.
 */

public class SampleStatus {
    public static final int INIT = 0; //未开始
    public static final int ASSIGN = 1; //已分派
    public static final int EXECUTION = 2; //进行中
    public static final int FINISH = 3; //已完成
    public static final int REFUSE_VISIT = 4; //拒访
    public static final int IDENTIFY = 5; //甄别
    public static final int APPOINTMENT = 6; //预约
    public static final int INVALID = 7; //无效号码
    public static final int IN_CALL = 8; //通话中
    public static final int NO_ANSWER = 9; //无人接听
    public static final int AUDIT_INVALID = 10; //审核无效
    public static final int AUDIT_RETURN = 11; //审核退回
    public static final int AUDIT_SUCCESS = 12; //审核成功
}
