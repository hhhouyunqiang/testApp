package com.ringdata.ringinterview.capi.components.ui.sign.signIn;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.ui.loader.LatteLoader;
import com.ringdata.base.util.ClickUtil;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.ui.sign.recoverPass.RecoverPassDelegate;
import com.ringdata.ringinterview.capi.components.ui.sign.setting.SettingDelegate;
import com.ringdata.ringinterview.capi.components.ui.sign.signUp.SignUpDelegate;
import com.ringdata.ringinterview.capi.components.ui.survey.SurveyDelegate;

import butterknife.BindView;
import butterknife.OnClick;

public class SignInDelegate extends LatteDelegate implements SignInContract.View {
    private SignInContract.Presenter mPresenter;
    @BindView(R2.id.et_username)
    EditText etUsername = null;
    @BindView(R2.id.et_password)
    EditText etPassword = null;
    @BindView(R2.id.iv_log)
    ImageView iv_logo;

    @OnClick(R2.id.cl_signup)
    void onClickSignUp() {
        getSupportDelegate().start(new SignUpDelegate());
    }

    @OnClick(R2.id.cl_forget)
    void onClickForget() {
        getSupportDelegate().start(new RecoverPassDelegate());
    }

    @OnClick(R2.id.cl_setting)
    void onClickSetting() {
        getSupportDelegate().start(new SettingDelegate());
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_sign_in;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mPresenter = new SignInPresenter(this, getActivity());
        mPresenter.start();
    }

    @Override
    public boolean onBackPressedSupport() {
        _mActivity.moveTaskToBack(true);
        return true;
    }

    @OnClick(R2.id.btn_sign_in)
    void onClickSignIn() {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        mPresenter.signIn(username, password);
    }

    @Override
    public void showError(String msg) {
        LatteLoader.stopLoading();
        ToastUtils.setBgColor(Color.GRAY);
        ToastUtils.showShort(msg);
    }

    @Override
    public void showHomeDelegateUI() {
        ToastUtils.setBgColor(Color.GRAY);
        ToastUtils.showShort("登录成功");
        startWithPop(new SurveyDelegate());
    }

    @Override
    public void refreshLogoView(String username, String password) {
        String lurl = SPUtils.getInstance().getString(SPKey.LOGOURL);
        if(lurl != null && lurl.length() > 0) {
            Glide.with(getContext())
                    .load(App.orgHost + "/capi-api-test/" + lurl)
                    //.error(R.drawable.logo)//失败
                    .priority(Priority.HIGH)//设置下载优先级
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail(0.1f)
                    .into(iv_logo);
        }
        etUsername.setText(username);
        etPassword.setText(password);
    }

    @Override
    public void refreshDescription(String info) {
//        tvDescript.setText(info);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
