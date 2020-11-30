package com.ringdata.ringinterview.capi.components.ui.survey.message.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.utils.TimeUtil;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.dao.MessageDao;
import com.ringdata.ringinterview.capi.components.data.model.Message;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 17/11/3.
 */

public class MessageDetailDelegate extends LatteDelegate {

    @BindView(R2.id.tv_message_title)
    public TextView tv_message_title;

    @BindView(R2.id.tv_message_time)
    public TextView tv_message_time;

    @BindView(R2.id.tv_message_content)
    TextView tv_message_content;

    private Message message;

    public static MessageDetailDelegate newInstance(Message message) {
        Bundle args = new Bundle();
        args.putSerializable("message", message);

        MessageDetailDelegate fragment = new MessageDetailDelegate();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_message_detail;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        this.message = (Message) getArguments().getSerializable("message");
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        MessageDao.read(message.getId(), SPUtils.getInstance().getInt(SPKey.USERID));
        tv_message_title.setText(message.getTitle());
        tv_message_time.setText(TimeUtil.question(message.getCreateTime()));
        tv_message_content.setText(message.getContext());
    }

    @OnClick(R2.id.icTv_topbar_back)
    public void goBack(View view) {
        back();
    }

}