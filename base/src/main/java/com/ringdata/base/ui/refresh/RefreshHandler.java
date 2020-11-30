package com.ringdata.base.ui.refresh;

import android.support.v7.widget.RecyclerView;

import com.ringdata.base.ui.recycler.DataConverter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


public class RefreshHandler implements
        OnRefreshListener,OnLoadmoreListener
{

    private final SmartRefreshLayout REFRESH_LAYOUT;
    private final PagingBean PAGEBEAN;
    private final RecyclerView RECYCLERVIEW;
    private final DataConverter CONVERTER;

    private RefreshHandler(SmartRefreshLayout smartRefreshLayout,
                           RecyclerView recyclerView,
                           DataConverter converter, PagingBean bean) {
        this.REFRESH_LAYOUT = smartRefreshLayout;
        this.RECYCLERVIEW = recyclerView;
        this.CONVERTER = converter;
        this.PAGEBEAN = bean;
        REFRESH_LAYOUT.setOnRefreshListener(this);
    }

    public static RefreshHandler create(SmartRefreshLayout smartRefreshLayout,
                                        RecyclerView recyclerView, DataConverter converter) {
        return new RefreshHandler(smartRefreshLayout, recyclerView, converter, new PagingBean());
    }

    private void refresh() {

    }

    public void firstPage(String url) {

    }

//    private void paging(final String url) {
//        final int pageSize = PAGEBEAN.getPageSize();
//        final int currentCount = PAGEBEAN.getCurrentCount();
//        final int total = PAGEBEAN.getTotal();
//        final int index = PAGEBEAN.getPageIndex();
//
//        if (mAdapter.getData().size() < pageSize || currentCount >= total) {
//            mAdapter.loadMoreEnd(true);
//        } else {
//            Latte.getHandler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    RestClient.builder()
//                            .url(url + index)
//                            .success(new ISuccess() {
//                                @Override
//                                public void onSuccess(String response) {
//                                    LatteLogger.json("paging", response);
//                                    CONVERTER.clearData();
//                                    mAdapter.addData(CONVERTER.setJsonData(response).convert());
//                                    //累加数量
//                                    PAGEBEAN.setCurrentCount(mAdapter.getData().size());
//                                    mAdapter.loadMoreComplete();
//                                    PAGEBEAN.addIndex();
//                                }
//                            })
//                            .build()
//                            .get();
//                }
//            }, 1000);
//        }
//    }
    @Override
    public void onRefresh(RefreshLayout refreshlayout) {

    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
    }
}
