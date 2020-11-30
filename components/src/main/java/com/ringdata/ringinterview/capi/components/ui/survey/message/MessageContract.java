package com.ringdata.ringinterview.capi.components.ui.survey.message;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.BasePresenter;
import com.ringdata.base.BaseView;
import com.ringdata.ringinterview.capi.components.data.model.Message;

import java.util.List;

/**
 * Created by admin on 2018/5/31.
 */

public interface MessageContract {
    interface Presenter extends BasePresenter {
        void getMessageListFromNet();

        void getMessageListFromLocal();

        void deleteMessageFromLocal(Integer messageId);

        void openMessageDetails(Message message);

        void queryFromLocal(String query);

    }

    interface View extends BaseView {

        void initMyView();

        void showMessageList(List<MultiItemEntity> messageList);

        void showNetErrorView(String msg);

        void showDetailDelegateUI(Message message);

    }
}
