package com.ringdata.ringinterview.capi.components.ui.sign.recoverPass;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

public class RecoverPassDelegate extends LatteDelegate implements RecoverPassContract.View {

    @BindView(R2.id.top_tab)
    TabLayout myTab;
    @BindView(R2.id.code_panel)
    LinearLayout codeLayout;
    @BindView(R2.id.text_phone)
    EditText phoneField;
    @BindView(R2.id.btn_verify)
    Button verifyBtn;
    @BindView(R2.id.text_code)
    EditText codeField;
    @BindView(R2.id.text_password)
    EditText pwdField;
    @BindView(R2.id.text_password2)
    EditText pwd2Field;
    @BindView(R2.id.text_user)
    EditText userField;
    @BindView(R2.id.text_email)
    EditText emailField;
    @BindView(R2.id.pic_panel)
    LinearLayout picLayout;
    @BindView(R2.id.text_pic_code)
    EditText picField;
    @BindView(R2.id.img_code)
    ImageView codeImg;

    private RecoverPassContract.Presenter mPresenter;
    private boolean byMobile = true;

    @Override
    public Object setLayout() {
        return R.layout.delegate_recover_pass;
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
                codeField.setVisibility(byMobile ? View.VISIBLE : View.GONE);
                pwdField.setVisibility(byMobile ? View.VISIBLE : View.GONE);
                pwd2Field.setVisibility(byMobile ? View.VISIBLE : View.GONE);
                userField.setVisibility(byMobile ? View.GONE : View.VISIBLE);
                emailField.setVisibility(byMobile ? View.GONE : View.VISIBLE);
                picLayout.setVisibility(byMobile ? View.GONE : View.VISIBLE);
                if (!byMobile) {
                    String code = getPicUrl();
//                    String url = "https://i.ringdata.com/uums/code/v1/createKaptcha?code=" + code;
                    String url = "http://175.102.15.229:18900/uums/code/v1/createKaptcha?code=" + code;
                    Glide.with(getContext())
                            .load(url)
                            .priority(Priority.HIGH)//设置下载优先级
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .thumbnail(0.1f)
                            .into(codeImg);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mPresenter = new RecoverPassPresenter(this, getActivity());
        mPresenter.start();
    }

    @OnClick(R2.id.btn_verify)
    void onClickGetCode() {
        //获取验证码
        String mobile = phoneField.getText().toString();
        mPresenter.sendCheckNum(mobile);
    }

    private String getPicUrl() {
        String str = "";
        String[] arr = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        for (int i = 0;i < 4;i++) {
            int pos = (int) Math.round(Math.random() * (arr.length - 1));
            str = str.concat(arr[pos]);
        }
        return str;
    }

    @OnClick(R2.id.btn_recover_pass)
    void onClickRecover() {
        String password = pwdField.getText().toString();
        String password2 = pwd2Field.getText().toString();
        if (!password.equals(password2)) {
            showError("两次输入的密码不一致");
        } else {
            if (byMobile) {
                String mobile = phoneField.getText().toString();
                String code = codeField.getText().toString();
                mPresenter.resetPassByMobile(mobile, code, password);
            } else {
                String name = userField.getText().toString();
                String email = emailField.getText().toString();
                String pic = picField.getText().toString();
                mPresenter.matchUserAndEmail(name, email, pic);
            }
        }
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
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
    public void showSignInDelegateUI() {
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
