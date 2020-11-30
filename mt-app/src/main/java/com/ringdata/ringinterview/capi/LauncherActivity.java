package com.ringdata.ringinterview.capi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.ringdata.base.activities.ProxyActivity;
import com.ringdata.base.delegates.LatteDelegate;


public class LauncherActivity extends ProxyActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }
    @Override
    public LatteDelegate setRootDelegate() {
        return new MTLauncherDelegate();
    }
}
