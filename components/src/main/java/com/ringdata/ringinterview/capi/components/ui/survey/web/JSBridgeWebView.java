package com.ringdata.ringinterview.capi.components.ui.survey.web;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.ringdata.ringinterview.survey.jsbridge.JSBridge;
import com.ringdata.ringinterview.survey.jsbridge.JSBridgePlugin;

/**
 * @Author: bella_wang
 * @Description:
 * @Date: Create in 2020/4/16 11:45
 */
public class JSBridgeWebView extends WebView {
    private JSBridge jsbridge;

    public JSBridgeWebView(Context context) {
        super(context);
        initJSBridge((Activity) context);
    }

    public JSBridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initJSBridge((Activity) context);
    }

    public JSBridgeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initJSBridge((Activity) context);
    }

    protected void initJSBridge(Activity activity) {
        jsbridge = new JSBridge(activity, this);
    }

    public void setPlugin(JSBridgePlugin plugin) {
        jsbridge.setPlugin(plugin);
    }

    public void execJS(String expr) {
        loadUrl("javascript:"+expr);
    }

    public void execJSFunc(String func, Object[] params) throws Exception {
        jsbridge.execJSFunc(func, params);
    }
}
