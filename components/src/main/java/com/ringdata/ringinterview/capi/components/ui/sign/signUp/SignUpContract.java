package com.ringdata.ringinterview.capi.components.ui.sign.signUp;

import com.ringdata.base.BasePresenter;
import com.ringdata.base.BaseView;

/**
 * Created by bella_wang on 2020/3/23.
 */

public interface SignUpContract {
    interface Presenter extends BasePresenter {
        
        void sendCheckNum(String mobile);

        void checkPhoneAvailable(String mobile);

        void registerByMobile(String phone, String code, String pwd);

        void registerByEmail(String email, String pwd);

        void initData();
    }

    interface View extends BaseView {

        void showError(String msg);

        void refreshView(String username);

        void disableCheckBtn();

        void enableCheckBtn();

        void reEnableCheckBtn();

        void countDownCheckBtn(int second);

        void showSignInDelegate();
    }
}
