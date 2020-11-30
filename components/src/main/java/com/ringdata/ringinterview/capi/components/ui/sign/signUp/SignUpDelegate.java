package com.ringdata.ringinterview.capi.components.ui.sign.signUp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.ui.loader.LatteLoader;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.ui.sign.signIn.SignInDelegate;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by bella_wang on 2020/3/23.
 */

public class SignUpDelegate extends LatteDelegate implements SignUpContract.View {
    private SignUpContract.Presenter mPresenter;
    @BindView(R2.id.top_tab)
    TabLayout myTab;
    @BindView(R2.id.edit_sign_up_email)
    EditText emailField;
    @BindView(R2.id.edit_sign_up_phone)
    EditText mobileField;
    @BindView(R2.id.edit_sign_up_code_panel)
    LinearLayout codeLayout;
    @BindView(R2.id.edit_sign_up_code)
    EditText codeField;
    @BindView(R2.id.edit_sign_up_password)
    EditText passField;
    @BindView(R2.id.edit_sign_up_re_password)
    EditText pass2Field;
    @BindView(R2.id.btn_verify)
    Button verifyBtn;
    private boolean byMobile = true;
    private String editString = "";

    private Handler handler = new Handler();

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            mPresenter.checkPhoneAvailable(editString);
        }
    };

    @Override
    public Object setLayout() {
        return R.layout.delegate_sign_up;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        setStatusBar();
        myTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                byMobile = tab.getPosition() == 0;
                codeLayout.setVisibility(byMobile ? View.VISIBLE : View.GONE);
                mobileField.setVisibility(byMobile ? View.VISIBLE : View.GONE);
                emailField.setVisibility(byMobile ? View.GONE : View.VISIBLE);
                if (byMobile) {
                    mobileField.setText("");
                    codeField.setText("");
                } else {
                    emailField.setText("");
                }
                passField.setText("");
                pass2Field.setText("");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mobileField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (delayRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                editString = s.toString();

                //延迟1s，如果不再输入字符，则执行该线程的run方法
                handler.postDelayed(delayRun, 1000);
            }
        });
        mPresenter = new SignUpPresenter(this, getActivity());
        mPresenter.start();
    }

    @OnClick(R2.id.btn_verify)
    void onClickGetCode() {
        //获取验证码
        String mobile = mobileField.getText().toString();
        mPresenter.sendCheckNum(mobile);
    }

    @OnClick(R2.id.btn_sign_up)
    void onClickSignUp() {
        String password = passField.getText().toString();
        String password2 = pass2Field.getText().toString();
        if (!password.equals(password2)) {
            showError("两次输入的密码不一致");
        } else {
            if (byMobile) {
                String mobile = mobileField.getText().toString();
                String code = codeField.getText().toString();
                mPresenter.registerByMobile(mobile, code, password);
            } else {
                String email = emailField.getText().toString();
                mPresenter.registerByEmail(email, password);
            }
        }
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @OnClick(R2.id.tv_link_sign_in)
    void onClickSignIn() {
        startWithPop(new SignInDelegate());
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showError(String msg) {
        LatteLoader.stopLoading();
        ToastUtils.setBgColor(Color.GRAY);
        ToastUtils.showShort(msg);
    }

    @Override
    public void refreshView(String username) {
        if (byMobile) {
            mobileField.setText(username);
        } else {
            emailField.setText(username);
        }
    }

    @Override
    public void disableCheckBtn() {
        verifyBtn.setBackgroundResource(R.drawable.btn_unenabled);
        verifyBtn.setEnabled(false);
    }

    @Override
    public void enableCheckBtn() {
        verifyBtn.setBackgroundResource(R.drawable.btn_enabled);
        verifyBtn.setEnabled(true);
        verifyBtn.setText("获取验证码");
    }

    @Override
    public void reEnableCheckBtn() {
        verifyBtn.setBackgroundResource(R.drawable.btn_enabled);
        verifyBtn.setEnabled(true);
        verifyBtn.setText("重新发送");
    }

    @Override
    public void countDownCheckBtn(int second) {
        verifyBtn.setText(second + "秒后重发");
    }

    @Override
    public void showSignInDelegate() {
        startWithPop(new SignInDelegate());
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
