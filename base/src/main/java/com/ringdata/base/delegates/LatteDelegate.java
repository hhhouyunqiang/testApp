package com.ringdata.base.delegates;

import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.ringdata.base.net.RestClient;
import com.ringdata.base.ui.loader.LatteLoader;
import com.ringdata.base.util.ClickUtil;
import com.ringdata.base.utils.DialogUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * Created by admin on 17/10/10.
 */

public abstract class LatteDelegate extends BaseDelegate {

    public <T extends LatteDelegate> T getParentDelegate() {
        return (T) getParentFragment();
    }

    public BaseMultiItemQuickAdapter recycleAdapter;
    public SmartRefreshLayout refreshLayout;
    public RecyclerView recyclerView;


    public void back() {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        getSupportDelegate().pop();
    }

    public void showErrorView(String text) {
        if (!isAdded()) {
            return;
        }
        DialogUtil.showErrorDialog(getActivity(), text);
        LatteLoader.stopLoading();
        RestClient.cancelWithTag(this);
    }

    public void showErrorView(String title, String content) {
        if (!isAdded()) {
            return;
        }
        DialogUtil.showErrorDialog(getActivity(), title, content);
        RestClient.cancelWithTag(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RestClient.cancelWithTag(this);
    }
}
