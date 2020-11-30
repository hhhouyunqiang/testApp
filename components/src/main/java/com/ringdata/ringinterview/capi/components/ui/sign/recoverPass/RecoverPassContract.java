package com.ringdata.ringinterview.capi.components.ui.sign.recoverPass;

import com.ringdata.base.BasePresenter;
import com.ringdata.base.BaseView;

/**
 * Created by bella_wang on 2020/3/23.
 */

public interface RecoverPassContract {
    interface Presenter extends BasePresenter {

        void sendCheckNum(String mobile);

        void resetPassByMobile(String phone, String code, String pwd);

        void matchUserAndEmail(String name, String email, String code);
    }

    interface View extends BaseView {

        void showError(String msg);

        void disableCheckBtn();

        void enableCheckBtn();

        void reEnableCheckBtn();

        void countDownCheckBtn(int second);

        void showSignInDelegateUI();
    }
}
