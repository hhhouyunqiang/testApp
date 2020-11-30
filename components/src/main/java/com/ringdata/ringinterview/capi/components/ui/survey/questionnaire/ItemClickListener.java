package com.ringdata.ringinterview.capi.components.ui.survey.questionnaire;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.data.model.Response;

/**
 * Created by admin on 2018/6/25.
 */

public class ItemClickListener extends SimpleClickListener {
    private final LatteDelegate DELEGATE;

    ItemClickListener(LatteDelegate delegate) {
        this.DELEGATE = delegate;
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MultiItemEntity item = (MultiItemEntity) adapter.getData().get(position);
        Integer itemType = item.getItemType();
        if (itemType == QuestionnaireItemType.QUESTIONNAIRE_SUB) {
            return;
        }
        Response response = (Response) item;
    }

    @Override
    public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

    }
}
