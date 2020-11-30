package com.ringdata.ringinterview.capi.components.ui.survey.sample;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.utils.TimeUtil;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.constant.SampleStatus;
import com.ringdata.ringinterview.capi.components.data.model.Sample;
import com.ringdata.ringinterview.capi.components.ui.survey.sample.detail.DetailDelegate;

import java.util.List;

/**
 * Created by admin on 17/10/26.
 */

public class SampleAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    private final LatteDelegate delegate;

    public SampleAdapter(List<MultiItemEntity> data, LatteDelegate delegate) {
        super(data);
        this.delegate = delegate;
        addItemType(SampleItemType.SAMPLE, R.layout.item_sample);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case SampleItemType.SAMPLE:
                Sample sample = (Sample) item;
                final String sampleId = sample.getSampleGuid();
                //显示拼接好的样本标识或样本名称
                helper.setText(R.id.tv_item_sample_code, TextUtils.isEmpty(sample.getDisplayName()) ? sample.getName() : sample.getDisplayName());
                helper.setTextColor(R.id.tv_item_sample_code, sample.getIsUpload() == 1 ? ContextCompat.getColor(delegate.getContext(), R.color.color06) : ContextCompat.getColor(delegate.getContext(), R.color.color08));
                //显示样本的开始时间
                if (sample.getStatus() != SampleStatus.INIT && sample.getStatus() != SampleStatus.ASSIGN) {
                    helper.setText(R.id.tv_item_sample_time, TimeUtil.question(sample.getStartTime()) + "--" + TimeUtil.question(sample.getEndTime()));
                } else {
                    helper.setText(R.id.tv_item_sample_time, "未开始");
                }
                //根据样本状态显示不同标签和时间
                TextView sampleProgress = helper.getView(R.id.tv_item_sample_progress);
                TextView visitTime = helper.getView(R.id.tv_item_sample_visit_time);
                String sampleDuration = "0秒";
                if (sample.getDuration() != null && sample.getDuration() > 0) {
                    sampleDuration = TimeUtil.second2Time(sample.getDuration().intValue());
                }
                switch (sample.getStatus()) {
                    case 1:
                        sampleProgress.setText("未开始");
                        sampleProgress.setBackgroundResource(R.drawable.view_init);
                        visitTime.setVisibility(View.GONE);
                        break;
                    case 2:
                        sampleProgress.setText("进行中");
                        sampleProgress.setBackgroundResource(R.drawable.view_executing);
                        visitTime.setVisibility(View.VISIBLE);
                        visitTime.setText(sampleDuration);
                        break;
                    case 3:
                        sampleProgress.setText("已完成");
                        sampleProgress.setBackgroundResource(R.drawable.view_success);
                        visitTime.setVisibility(View.VISIBLE);
                        visitTime.setText(sampleDuration);
                        break;
                    case 4:
                        sampleProgress.setText("拒访");
                        sampleProgress.setBackgroundResource(R.drawable.view_refuse);
                        //visitTime.setVisibility(View.GONE);
                        visitTime.setVisibility(View.VISIBLE);
                        visitTime.setText(sampleDuration);
                        break;
                    case 5:
                        sampleProgress.setText("甄别");
                        sampleProgress.setBackgroundResource(R.drawable.view_qualified);
                        visitTime.setVisibility(View.GONE);
                        break;
                    case 6:
                        sampleProgress.setText("预约");
                        sampleProgress.setBackgroundResource(R.drawable.view_revisit);
                        visitTime.setVisibility(View.VISIBLE);
                        visitTime.setText(TimeUtil.question(sample.getAppointVisitTime()));
                        break;
                    case 7:
                        sampleProgress.setText("无效号码");
                        sampleProgress.setBackgroundResource(R.drawable.view_error);
                        visitTime.setVisibility(View.GONE);
                        break;
                    case 8:
                        sampleProgress.setText("通话中");
                        sampleProgress.setBackgroundResource(R.drawable.view_error);
                        visitTime.setVisibility(View.GONE);
                        break;
                    case 9:
                        //sampleProgress.setText("无人接听");
                        sampleProgress.setText("无法联系");
                        sampleProgress.setBackgroundResource(R.drawable.view_error);
                        //visitTime.setVisibility(View.GONE);
                        visitTime.setVisibility(View.VISIBLE);
                        visitTime.setText(sampleDuration);
                        break;
                    case 10:
                        sampleProgress.setText("审核无效");
                        sampleProgress.setBackgroundResource(R.drawable.view_error);
                        visitTime.setVisibility(View.VISIBLE);
                        visitTime.setText(sampleDuration);
                        break;
                    case 11:
                        sampleProgress.setText("审核退回");
                        sampleProgress.setBackgroundResource(R.drawable.view_repulse);
                        visitTime.setVisibility(View.VISIBLE);
                        visitTime.setText(sampleDuration);
                        break;
                    case 12:
                        sampleProgress.setText("审核成功");
                        sampleProgress.setBackgroundResource(R.drawable.view_success);
                        visitTime.setVisibility(View.VISIBLE);
                        visitTime.setText(sampleDuration);
                        break;
                    case 13:
                        sampleProgress.setText("进行中");
                        sampleProgress.setBackgroundResource(R.drawable.view_executing);
                        visitTime.setVisibility(View.VISIBLE);
                        visitTime.setText(sampleDuration);
                        break;
                    default:
                        sampleProgress.setText("未开始");
                        sampleProgress.setBackgroundResource(R.drawable.view_init);
                        visitTime.setVisibility(View.GONE);
                        break;
                }

                helper.getView(R.id.tv_item_sample_code).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPUtils.getInstance().put(SPKey.SAMPLE_ID, sampleId);
                        delegate.start(new DetailDelegate());
                    }
                });

                helper.getView(R.id.icTv_sample_index).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SPUtils.getInstance().put(SPKey.SAMPLE_ID, sampleId);
                        delegate.start(new DetailDelegate());
                    }
                });

                break;
            default:
                break;
        }
    }
}
