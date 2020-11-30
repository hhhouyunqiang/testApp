package com.ringdata.ringinterview.survey.jsbridge;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by xch on 2017/9/2.
 */

public class JSBridgeHelper {
    public JSBridge jsbridge;
    public WebView webView;
    public KProgressHUD hud;

    public JSBridgeHelper(Activity activity,
                          View view, int res,
                          String stubName,
                          String clientName) {
        webView = (WebView) view.findViewById(res);
        init(activity, webView, stubName, clientName);
    }


    public JSBridgeHelper(Activity activity,
                          int res,
                          String stubName,
                          String clientName) {
        webView = (WebView) activity.findViewById(res);
        init(activity, webView, stubName, clientName);
    }

    public JSBridgeHelper(Activity activity,
                          WebView webView,
                          String stubName,
                          String clientName) {
        this.webView = webView;
        init(activity, webView, stubName, clientName);
    }


    public void init(final Activity activity,
                     final WebView webView,
                     String stubName,
                     String clientName) {
        jsbridge = new JSBridge(activity, webView, stubName, clientName);
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍候")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.3f);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("CAPI", "onPageStarted " + url);
                if (!hud.isShowing()) {
                    hud.show();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("CAPI", "onPageFinished " + url);
                if (hud.isShowing()) {
                    hud.dismiss();
                }
                JSBridgeHelper.this.onPageStarted();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            /**
             * 处理JavaScript Alert事件
             */
            @Override
            public boolean onJsAlert(WebView view, String url,
                                     String message, final JsResult result) {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                result.confirm();
                return true;
            }



        });
    }

    protected void onPageStarted() {


    }

    public void setCacheEnabled(boolean enabled) {
        webView.getSettings().setCacheMode(enabled ? WebSettings.LOAD_DEFAULT
                : WebSettings.LOAD_NO_CACHE);
    }

    public void setPlugin(JSBridgePlugin plugin) {
        jsbridge.setPlugin(plugin);
    }

    public void execJS(String expr) {
        webView.loadUrl("javascript:" + expr);
    }

    public void execJSFunc(String func, Object[] params) {
        try {
            jsbridge.execJSFunc(func, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execClientFunc(String func, Object[] params) {
        try {
            jsbridge.execClientFunc(func, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadURL(String url) {
        webView.loadUrl(url);
    }

    public void loadJSFile(Activity activity, String file) {
        //String myHtmlString= readHtml(file);
        //Log.d("CAPI", "loadJSFile " + myHtmlString);
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            injectScriptFile(activity, webView, input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }


        //webView.loadData(myHtmlString, "text/html", "utf-8");
        //webView.loadUrl("javascript:function(){"+readHtml(file)+"}()");

    }

    private void injectScriptFile(Activity activity, WebView view, InputStream input) {
        try {
            //input =  activity.getAssets().open(scriptFile);
            //input = new FileInputStream("file:///android_asset/rs2-survey.js");
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "script.innerHTML = decodeURIComponent(escape(window.atob('" + encoded + "')));" +
                    "parent.appendChild(script)" +
                    "})()");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String readHtml(String remoteUrl) {
        String out = "";
        BufferedReader in = null;
        try {
            URL url = new URL(remoteUrl);
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                out += str;
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return out;
    }

    public void closeLoading() {
        if (hud.isShowing()) {
            hud.dismiss();
        }
    }
}
