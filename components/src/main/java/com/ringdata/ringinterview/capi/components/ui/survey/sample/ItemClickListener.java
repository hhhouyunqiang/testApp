package com.ringdata.ringinterview.capi.components.ui.survey.sample;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.ringdata.base.delegates.LatteDelegate;

/**
 * Created by admin on 2018/6/25.
 */

public class ItemClickListener extends SimpleClickListener {
    private final LatteDelegate DELEGATE;
    private final SampleContract.Presenter mPresenter;

    ItemClickListener(LatteDelegate delegate,SampleContract.Presenter presenter) {
        this.DELEGATE = delegate;
        this.mPresenter = presenter;
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

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
