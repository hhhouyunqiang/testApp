package com.ringdata.ringinterview.capi.components.ui.mine;

import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.R;

import java.util.List;

/**
 * Created by admin on 17/10/26.
 */

public class SyncDataAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    private final LatteDelegate delegate;

    public SyncDataAdapter(List<MultiItemEntity> data, LatteDelegate delegate) {
        super(data);
        this.delegate = delegate;
        addItemType(SyncDataItemType.LIST_BEAN, R.layout.item_list);
        addItemType(SyncDataItemType.TEXT, R.layout.item_text);
        addItemType(SyncDataItemType.DIVIDER_40, R.layout.item_divider);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case SyncDataItemType.LIST_BEAN:
                final ListBean listBean = (ListBean) item;
                TextView left = helper.getView(R.id.tv_item_left);
                left.setTextColor(ContextCompat.getColor(delegate.getContext(), listBean.getIconLeftColor()));
                helper.setText(R.id.tv_item_text, listBean.getText());
                helper.setText(R.id.tv_item_time, listBean.getTime());
                if (listBean.getRightNum() > 0) {
                    helper.setText(R.id.tv_item_right, "本地待同步：" + listBean.getRightNum());
                }
                break;
            case SyncDataItemType.TEXT:
                final ListBean listBean2 = (ListBean) item;
                helper.setText(R.id.btn_item_text, listBean2.getText());
                break;
            default:
                break;
        }
    }
}
