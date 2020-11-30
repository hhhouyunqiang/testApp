package com.ringdata.ringinterview.capi.components.ui.survey.questionnaire;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.utils.TimeUtil;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.constant.ProjectConstants;
import com.ringdata.ringinterview.capi.components.constant.ResponseStatus;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.model.Questionnaire;
import com.ringdata.ringinterview.capi.components.data.model.Response;

import java.util.List;

/**
 * Created by admin on 17/10/26.
 */

public class QuestionnaireAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    private final LatteDelegate delegate;

    public QuestionnaireAdapter(List<MultiItemEntity> data, LatteDelegate delegate) {
        super(data);
        this.delegate = delegate;
        addItemType(QuestionnaireItemType.QUESTIONNAIRE_MAIN, R.layout.item_questionnaire);
        addItemType(QuestionnaireItemType.QUESTIONNAIRE_HEAD, R.layout.item_questionnaire_head);
        addItemType(QuestionnaireItemType.QUESTIONNAIRE_SUB, R.layout.item_questionnaire_sub);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case QuestionnaireItemType.QUESTIONNAIRE_MAIN:
                final Response response = (Response) item;
                String name = response.getQuestionnaireName();
                Long startTime = response.getStartTime();
                Long endTime = response.getEndTime();
                Long duration = response.getResponseDuration();
                Integer status = response.getResponseStatus();
                Integer isUpload = response.getIsUpload();
                TextView tv_title = (TextView) helper.getView(R.id.tv_questionnaire_title);
                tv_title.setText(TextUtils.isEmpty(name) ? "随机问卷" : name);
                helper.setText(R.id.tv_questionnaire_duration, TimeUtil.second2Time(duration.intValue()) + "");
                TextView quesProgress = helper.getView(R.id.tv_questionnaire_progress);
                helper.setText(R.id.tv_questionnaire_time, TimeUtil.question(startTime) + " -- " + TimeUtil.question(endTime));
                if (isUpload == null || isUpload == 1) {
                    tv_title.setTextColor(ContextCompat.getColor(delegate.getContext(), R.color.color06));
                } else if (status == ResponseStatus.AUDIT_BACK) {
                    tv_title.setTextColor(ContextCompat.getColor(delegate.getContext(), R.color.color15));
                } else {
                    tv_title.setTextColor(ContextCompat.getColor(delegate.getContext(), R.color.color18));
                }
                if (status == ResponseStatus.EXECUTION) {
                    quesProgress.setText("进行中");
                    quesProgress.setBackgroundResource(R.drawable.view_executing);
                } else if (status == ResponseStatus.RETURN_VISIT) {
                    quesProgress.setText("预约回访");
                    quesProgress.setBackgroundResource(R.drawable.view_revisit);
                } else if (status == ResponseStatus.REFUSED) {
                    quesProgress.setText("拒绝回访");
                    quesProgress.setBackgroundResource(R.drawable.view_error);
                } else if (status == ResponseStatus.DIS_FAILED) {
                    quesProgress.setText("甄别失败");
                    quesProgress.setBackgroundResource(R.drawable.view_error);
                } else if (status == ResponseStatus.OUT_OF_QUOTA) {
                    quesProgress.setText("不满足配额");
                    quesProgress.setBackgroundResource(R.drawable.view_error);
                } else if (status == ResponseStatus.SUCCESS) {
                    quesProgress.setText("已完成");
                    quesProgress.setBackgroundResource(R.drawable.view_success);
                } else if (status == ResponseStatus.QUOTA_OVERFLOW) {
                    quesProgress.setText("配额溢出");
                    quesProgress.setBackgroundResource(R.drawable.view_error);
                } else if (status == ResponseStatus.AUDIT_FIRST_SUCCESS) {
                    quesProgress.setText("一审合格");
                    quesProgress.setBackgroundResource(R.drawable.view_success);
                } else if (status == ResponseStatus.AUDIT_SECOND_SUCCESS) {
                    quesProgress.setText("二审合格");
                    quesProgress.setBackgroundResource(R.drawable.view_success);
                } else if (status == ResponseStatus.AUDIT_THIRD_SUCCESS) {
                    quesProgress.setText("终审合格");
                    quesProgress.setBackgroundResource(R.drawable.view_success);
                } else if (status == ResponseStatus.AUDIT_FAIL) {
                    quesProgress.setText("审核无效");
                    quesProgress.setBackgroundResource(R.drawable.view_repulse);
                } else if (status == ResponseStatus.AUDIT_BACK) {
                    quesProgress.setText("打回");
                    quesProgress.setBackgroundResource(R.drawable.view_error);
                }else if (status == ResponseStatus.SECOND_EXECUTION) {
                    quesProgress.setText("已完成");
                    quesProgress.setBackgroundResource(R.drawable.view_success);
                }else {
                    quesProgress.setText("未开始");
                    quesProgress.setBackgroundResource(R.drawable.view_init);
                }
                TextView qrc = helper.getView(R.id.itv_questionnaire_qrc);
                //混合调查有二维码
                qrc.setVisibility(SPUtils.getInstance().getString(SPKey.SURVEY_TYPE).equals(ProjectConstants.CAXI_PROJECT) ? View.VISIBLE : View.GONE);
//                final Integer caxiStatus = response.getCaxiWebStatus();
//                if (caxiStatus == null || caxiStatus == 0) {
//                    qrc.setTextColor(ContextCompat.getColor(delegate.getContext(), R.color.color08));
//                } else if (caxiStatus == 1) {
//                    qrc.setTextColor(ContextCompat.getColor(delegate.getContext(), R.color.color12));
//                } else if (caxiStatus == 2) {
//                    qrc.setTextColor(ContextCompat.getColor(delegate.getContext(), R.color.color04));
//                }
//
//                final String myQrcUrl = response.getQuestionnaireUrl();
//                qrc.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String qrc = response.getQuestionnaireName();
//                        int isupload = response.getIsUpload();
//                        if (TextUtils.isEmpty(qrc) && isupload == 1) {
//                            ToastUtils.showShort("请先上传本答卷");
//                            return;
//                        }
//                        delegate.start(QuestionnaireQRCodeDelegate.newInstance(""));
//                    }
//                });
                break;
            case QuestionnaireItemType.QUESTIONNAIRE_HEAD:
                final Response myResponse = (Response) item;
                TextView head = helper.getView(R.id.tv_questionnaire_head);
                head.setText(myResponse.getQuestionnaireName());
                break;
            case QuestionnaireItemType.QUESTIONNAIRE_SUB:
                TextView title = helper.getView(R.id.tv_questionnaire_sub_title);
                Button add = helper.getView(R.id.tv_questionnaire_sub_add);
                helper.addOnClickListener(R.id.tv_questionnaire_sub_add);
                final Questionnaire subQuestionnaire = (Questionnaire) item;
                title.setText(subQuestionnaire.getName());
                Integer type = subQuestionnaire.getIsAllowedManualAdd();
                if (type == 1) {
                    add.setVisibility(View.VISIBLE);
                } else {
                    add.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                break;

        }
    }
}
