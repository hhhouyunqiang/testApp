package com.ringdata.ringinterview.capi.components.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.blankj.utilcode.util.SPUtils;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.base.ui.launcher.LauncherHolderCreator;
import com.ringdata.base.ui.launcher.ScrollLauncherTag;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.ui.sign.signIn.SignInDelegate;

import java.util.ArrayList;

/**
 * Created by admin on 17/10/15.
 */
public class LauncherScrollDelegate extends LatteDelegate implements OnItemClickListener {

    private ConvenientBanner<Integer> mConvenientBanner = null;
    private final ArrayList<Integer> INTEGERS = new ArrayList<>();


    private void initBanner() {
        INTEGERS.add(R.drawable.launcher04);
        INTEGERS.add(R.drawable.launcher04);
        mConvenientBanner
                .setPages(new LauncherHolderCreator(), INTEGERS)
                .setPageIndicator(new int[]{R.drawable.dot_normal, R.drawable.dot_focus})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setOnItemClickListener(this)
                .setCanLoop(false);
    }


    @Override
    public Object setLayout() {
        mConvenientBanner = new ConvenientBanner<>(getContext());
        //mConvenientBanner = (ConvenientBanner)findViewById(R.layout.delegate_launcher);
        return mConvenientBanner;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        initBanner();
    }

    @Override
    public void onItemClick(int position) {
        //如果点击的是最后一个
        if (position == INTEGERS.size() - 1) {
            SPUtils.getInstance().put(ScrollLauncherTag.HAS_FIRST_LAUNCHER_APP.name(), true);
            //检查用户是否已经登录
            getSupportDelegate().startWithPop(new SignInDelegate());
        }
    }
}

