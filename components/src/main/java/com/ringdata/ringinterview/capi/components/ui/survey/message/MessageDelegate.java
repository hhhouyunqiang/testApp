package com.ringdata.ringinterview.capi.components.ui.survey.message;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.ui.recycler.BaseDecoration;
import com.ringdata.base.util.NetworkUtil;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.data.model.Message;
import com.ringdata.ringinterview.capi.components.ui.survey.message.detail.MessageDetailDelegate;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 17/10/26.
 */

public class MessageDelegate extends LatteDelegate implements MessageContract.View, BaseQuickAdapter.OnItemClickListener, OnRefreshListener, BaseQuickAdapter.OnItemLongClickListener {
    @BindView(R2.id.sv_message)
    SearchView sv_message;

    private MessageContract.Presenter mPresenter;

    @Override
    public Object setLayout() {
        return R.layout.delegate_message;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @OnClick(R2.id.icTv_topbar_back)
    public void onClickBack() {
        back();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        mPresenter = new MessagePresenter(this, getActivity());
        mPresenter.start();
    }

    @Override
    public void initMyView() {
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.rv_message);
        refreshLayout = (SmartRefreshLayout) mRootView.findViewById(R.id.srl_message);
        recycleAdapter = new MessageAdapter(new ArrayList<MultiItemEntity>());
        recycleAdapter.setOnItemClickListener(this);
        recycleAdapter.setOnItemLongClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration
                (BaseDecoration.create(ContextCompat.getColor(getContext(), R.color.divider_line), 1));
        recyclerView.setAdapter(recycleAdapter);
        refreshLayout.setOnRefreshListener(this);

        // 设置搜索文本监听
        sv_message.onActionViewExpanded();
        int id = sv_message.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) sv_message.findViewById(id);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//字体、提示字体大小
        LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) textView.getLayoutParams();
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER_VERTICAL);

        //去掉搜索框的下划线
        if (sv_message != null) {
            try {        //--拿到字节码
                Class<?> argClass = sv_message.getClass();
                //--指定某个私有属性,mSearchPlate是搜索框父布局的名字
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                //--暴力反射,只有暴力反射才能拿到私有属性
                ownField.setAccessible(true);
                View mView = (View) ownField.get(sv_message);
                //--设置背景
                mView.setBackgroundColor(Color.TRANSPARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sv_message.clearFocus();
        sv_message.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mPresenter.queryFromLocal(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mPresenter.queryFromLocal(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        final Message message = (Message) adapter.getData().get(position);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("确定")
                .setMessage("确定删除该消息？")
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.deleteMessageFromLocal(message.getId());
                    }
                }).show();
        return false;
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtils.showShort("网络不可用");
            mPresenter.getMessageListFromLocal();
        } else {
            mPresenter.getMessageListFromNet();
            refreshlayout.finishRefresh();
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Message message = (Message) adapter.getData().get(position);
        mPresenter.openMessageDetails(message);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showMessageList(List<MultiItemEntity> messageList) {
        recycleAdapter.setNewData(messageList);
        recycleAdapter.notifyDataSetChanged();
        refreshLayout.finishRefresh();
    }

    @Override
    public void showNetErrorView(String msg) {
        showErrorView(msg);
    }

    @Override
    public void showDetailDelegateUI(Message message) {
        MessageDetailDelegate delegate = MessageDetailDelegate.newInstance(message);
        getSupportDelegate().start(delegate);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            mPresenter.getMessageListFromLocal();
        }
        super.onHiddenChanged(hidden);
    }
}
