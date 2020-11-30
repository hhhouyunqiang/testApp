package com.ringdata.ringinterview.capi.components.ui.survey;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
import android.text.Html;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.data.model.Survey;
import com.ringdata.ringinterview.capi.components.utils.AppUtil;

import java.util.List;

/**
 * Created by admin on 17/10/26.
 */

public class SurveyAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    private final LatteDelegate delegate;

    public SurveyAdapter(List<MultiItemEntity> data, LatteDelegate delegate) {
        super(data);
        this.delegate = delegate;
        addItemType(SurveyItemType.SURVEY, R.layout.item_survey);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case SurveyItemType.SURVEY:
                final Survey survey = (Survey) item;
                helper.setText(R.id.tv_item_survey_title, survey.getName() + "");
                if (survey.getDescription()==null){
                    helper.setText(R.id.tv_item_survey_description, "");
                }else {
                    helper.setText(R.id.tv_item_survey_description, survey.getDescription() + "");
                }
                //helper.setText(R.id.tv_item_survey_description, survey.getDescription() + "");
                helper.setText(R.id.tv_item_survey_role, "角色：" + survey.getRoleName());
                helper.setText(R.id.tv_item_survey_creator, "创建：" + survey.getCreateUserName());
                helper.setText(R.id.tv_item_survey_status, AppUtil.getSurveyStatus(survey.getStatus()));
                helper.setTextColor(R.id.tv_item_survey_status, ContextCompat.getColor(delegate.getContext(), AppUtil.getSurveyStatusColor(survey.getStatus())));
                helper.setText(R.id.tv_item_survey_start_time, "更新：" + survey.getUpdateTime());
                helper.setText(R.id.tv_item_survey_type_icon, Html.fromHtml(AppUtil.getSurveyTypeIcon(survey.getType())));
                helper.setText(R.id.tv_item_survey_type_text, AppUtil.getSurveyTypeText(survey.getType()));
                break;

            default:
                break;
        }
    }
}
