package com.ringdata.ringinterview.capi.components.ui.survey.message;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.utils.TimeUtil;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.data.model.Message;

import java.util.List;

/**
 * Created by admin on 17/10/26.
 */

public class MessageAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public MessageAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(MessageItemType.MESSAGE, R.layout.item_message);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case MessageItemType.MESSAGE:
                Message message = (Message) item;
                helper.setVisible(R.id.iv_item_message_dot, message.getIsRead() == 1 ? false : true);
                helper.setText(R.id.tv_item_message_title, message.getTitle() + "");
                helper.setText(R.id.tv_item_message_time, TimeUtil.question(message.getCreateTime()));
                helper.setText(R.id.tv_item_message_content, message.getContext() + "");
                helper.setText(R.id.tv_item_message_survey, message.getSurveyName() + "");
                break;

            default:
                break;
        }
    }
}
