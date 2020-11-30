package com.ringdata.ringinterview.capi.components;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.ringdata.base.activities.ProxyActivity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.ui.LauncherDelegate;

/**
 * Created by admin on 2018/6/18.
 */

public class MainActivity extends ProxyActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public LatteDelegate setRootDelegate() {
        return new LauncherDelegate();
    }
}
