package com.ringdata.ringinterview.capi.components.constant;

/**
 * Created by admin on 2018/5/8.
 */

public class ResponseStatus {
    public static final int EXECUTION = 1;//答题中 进行中
    public static final int RETURN_VISIT = 2;//预约回访
    public static final int REFUSED = 3;//拒绝回访
    public static final int DIS_FAILED = 4;//甄别失败
    public static final int OUT_OF_QUOTA = 5;//不满足配额
    public static final int SUCCESS = 6;//答题成功 已完成
    public static final int QUOTA_OVERFLOW = 7;//配额溢出
    public static final int AUDIT_FIRST_SUCCESS = 8;//一审合格
    public static final int AUDIT_SECOND_SUCCESS = 9;//二审合格
    public static final int AUDIT_THIRD_SUCCESS = 10;//终审合格
    public static final int AUDIT_FAIL = 11;//审核无效
    public static final int AUDIT_BACK = 12;//打回
    public static final int SECOND_EXECUTION= 13;//允许第二次填答
    public static  int SECOND= 1;//测试2次提交  1提交一次了 2提交俩次了


}
