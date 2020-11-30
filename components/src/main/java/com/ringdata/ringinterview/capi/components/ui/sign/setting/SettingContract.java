package com.ringdata.ringinterview.capi.components.ui.sign.setting;

import com.ringdata.base.BasePresenter;
import com.ringdata.base.BaseView;

/**
 * Created by admin on 2018/5/23.
 */

public interface SettingContract {
    interface Presenter extends BasePresenter {
        
        void setting(String host);

        void initData();
    }

    interface View extends BaseView {

        void showError(String msg);

        void refreshView(String host);

        void showSignInDelegateUI();
    }
}
