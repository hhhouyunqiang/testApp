package com.ringdata.ringinterview.capi.components.ui.sign.signIn;

import com.ringdata.base.BasePresenter;
import com.ringdata.base.BaseView;

/**
 * Created by admin on 2018/5/23.
 */

public interface SignInContract {
    interface Presenter extends BasePresenter {
        void signIn(String username, String password);

        void checkJSVersion();

        void localCheckLogin(String username, String password);

        void initData();

        void destroy();
    }

    interface View extends BaseView {

        void showError(String msg);

        void showHomeDelegateUI();

        void refreshDescription(String info);

        void refreshLogoView(String username, String password);
    }
}
