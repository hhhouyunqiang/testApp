package com.ringdata.ringinterview.capi.components.ui.sign.setting;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Patterns;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ringdata.base.util.NetworkUtil;
import com.ringdata.ringinterview.capi.components.App;
import com.ringdata.ringinterview.capi.components.constant.SPKey;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by admin on 2018/5/23.
 */

public class SettingPresenter implements SettingContract.Presenter {
    private final SettingContract.View mView;
    private final Context mContext;

    public SettingPresenter(SettingContract.View view, Context context) {
        this.mView = view;
        this.mContext = context;
    }

    @Override
    public void start() {
        initData();
    }

    @Override
    public void setting(final String host) {
        if (host.isEmpty() || !Patterns.WEB_URL.matcher(host).matches()) {
            mView.showError("服务器地址格式不对！");
            return;
        }
        if (!NetworkUtil.isNetworkAvailable()) {
            mView.showError("无网络连接");
            return;
        }
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection conn = null;
                try {
                    url = new URL(host);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setUseCaches(false);
                    conn.setInstanceFollowRedirects(true);
                    conn.setConnectTimeout(1000 * 5);
                    conn.setReadTimeout(1000 * 5);
                    try {
                        conn.connect();
                    } catch(Exception e) {
                        e.printStackTrace();

                    }
                    int code = conn.getResponseCode();
                    if ((code >= 100) && (code < 400)){
                        App.orgHost = host;
                        ToastUtils.setBgColor(Color.GRAY);
                        ToastUtils.showShort("验证成功");
                        SPUtils.getInstance().put(SPKey.HOST, host);
                        mView.showSignInDelegateUI();
                    }else {
                         ToastUtils.setBgColor(Color.GRAY);
                         ToastUtils.showShort("无法连接到服务器");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("URL格式错误");
                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtils.setBgColor(Color.GRAY);
                    ToastUtils.showShort("无法连接到服务器");
                } finally {
                    conn.disconnect();
                }
            }
        });
        thread.start();
//        if (isConnByHttp(host)) {
//            App.orgHost = host;
//            ToastUtils.setBgColor(Color.GRAY);
//            ToastUtils.showShort("验证成功");
//            SPUtils.getInstance().put(SPKey.HOST, host);
//            mView.showSignInDelegateUI();
//        } else {
//            ToastUtils.setBgColor(Color.GRAY);
//            ToastUtils.showShort("无法连接到服务器");
//        }
    }

    @Override
    public void initData() {
        String host = SPUtils.getInstance().getString(SPKey.HOST);
        if (!TextUtils.isEmpty(host)) {
            mView.refreshView(host);
        } else {
            mView.refreshView(App.orgHost);
        }
    }

    /**
     * 判断是否连接到服务器
     * @return
     */
    private static boolean isConnByHttp(String host){
        URL url;
        HttpURLConnection conn = null;
        try {
            url = new URL(host);
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(1000 * 5);
            conn.setReadTimeout(1000 * 5);

            //HTTP connect
            try {
                conn.connect();
            } catch(Exception e) {
                e.printStackTrace();
                return false;
            }

            int code = conn.getResponseCode();
            if ((code >= 100) && (code < 400)){
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            conn.disconnect();
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
