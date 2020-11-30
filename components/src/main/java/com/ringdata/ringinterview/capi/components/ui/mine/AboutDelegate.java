package com.ringdata.ringinterview.capi.components.ui.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.BuildConfig;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 17/11/7.
 */

public class AboutDelegate extends LatteDelegate {
    @BindView(R2.id.tv_version_code)
    TextView version_name;

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        back();
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_mine_about;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    @OnClick(R2.id.btn_monetware_web)
    void onClickBtnMonteWareWeb(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("http://www.ringdata.com/");
        intent.setData(content_url);
        startActivity(intent);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        String versionCode = BuildConfig.VERSION_NAME;
        version_name.setText("当前版本 "+versionCode);
    }
}
