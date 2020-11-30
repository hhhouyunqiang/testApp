package com.ringdata.ringinterview.capi.components.ui.sign.setting;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.helper.EditClearHelper;
import com.ringdata.ringinterview.capi.components.ui.sign.signIn.SignInDelegate;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 17/11/3.
 */

public class SettingDelegate extends LatteDelegate implements SettingContract.View {
    @BindView(R2.id.et_serverHost)
    EditText etHost;
    @BindView(R2.id.tv_clear_host)
    TextView tv_clear_host;

    private SettingContract.Presenter mPresenter;

    @Override
    public Object setLayout() {
        return R.layout.delegate_sign_in_setting;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        setStatusBar();
        EditClearHelper.init(etHost, tv_clear_host);
        mPresenter = new SettingPresenter(this, getActivity());
        mPresenter.start();
    }

    @OnClick(R2.id.bt_validate_server)
    void onClickCheckHost() {
        String host = etHost.getText().toString().trim();
        mPresenter.setting(host);
    }

    @OnClick(R2.id.bt_reset_server)
    void onClickReset() {
        etHost.setText(App.orgHost);
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
        ToastUtils.showLong(msg + "");
    }

    @Override
    public void refreshView(String host) {
        etHost.setText(host);
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    public void showSignInDelegateUI() {
        startWithPop(new SignInDelegate());
    }
}
