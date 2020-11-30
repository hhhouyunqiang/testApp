package com.ringdata.base.delegates;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ringdata.base.activities.ProxyActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment;

/**
 * Created by admin on 17/10/10.
 */

public abstract class BaseDelegate extends SwipeBackFragment {

    private Unbinder mUnbinder = null;
    public View mRootView = null;

    public abstract Object setLayout();

    public abstract void onBindView(@Nullable Bundle savedInstanceState, View rootView);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView;
        final Object layout = setLayout();
        if (layout instanceof Integer) {
            rootView = inflater.inflate((int) setLayout(), container, false);
        } else if (layout instanceof View) {
            rootView = (View) setLayout();
        } else {
            throw new ClassCastException("type of setLayout() must be int or View!");
        }
        setStatusBar();
        mUnbinder = ButterKnife.bind(this, rootView);
        mRootView = rootView;
        onBindView(savedInstanceState, rootView);
        return rootView;
    }

    public final ProxyActivity getProxyActivity() {
        return (ProxyActivity) _mActivity;
    }

    @Override
    public void onDestroy() {
        if (mUnbinder != null) {
            try {
                mUnbinder.unbind();
            } catch (Exception e) {

            }
        }
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setStatusBar();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setStatusBar();
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            getActivity().getWindow().setStatusBarColor(Color.parseColor("#EFEFEF"));
            //设置状态栏文字颜色及图标为深色，当状态栏为白色时候，改变其颜色为深色
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
