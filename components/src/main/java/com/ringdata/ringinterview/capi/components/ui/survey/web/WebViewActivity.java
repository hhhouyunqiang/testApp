package com.ringdata.ringinterview.capi.components.ui.survey.web;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author: bella_wang
 * @Description:
 * @Date: Create in 2020/4/16 11:37
 */
public class WebViewActivity extends AppCompatActivity {
    @BindView(R2.id.web_view)
    JSBridgeWebView _webview;
    @BindView(R2.id.tv_topbar_title)
    TextView topTitle;
    public KProgressHUD _hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_web_view);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        ButterKnife.bind(this);
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        boolean cache = getIntent().getBooleanExtra("use-cache", true);
        topTitle.setText(title);
        _hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍候")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.3f);
        final CookieManager cookieManager = CookieManager.getInstance();
        _webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                if (!_hud.isShowing()) {
//                    _hud.show();
//                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String CookieStr = cookieManager.getCookie(url);
                cookieManager.setCookie(url, CookieStr);
//                if(_hud.isShowing()) {
//                    _hud.dismiss();
//                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (errorResponse.getStatusCode() == 401){
                        ToastUtils.setBgColor(Color.GRAY);
                        ToastUtils.showShort("登录信息已失效，请退出重新登录！");
                    }
                }
            }
        });
        _webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings settings = _webview.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(cache ? WebSettings.LOAD_DEFAULT : WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);

        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(_webview, true);
        }

        _webview.setWebChromeClient(new WebChromeClient() {
            /**
             * 处理JavaScript Alert事件
             */
            @Override
            public boolean onJsAlert(WebView view, String url,
                                     String message, final JsResult result) {
                ToastUtils.setBgColor(Color.GRAY);
                ToastUtils.showShort(message);
                result.confirm();
                return true;
            }
        });

        if(url != null) {
            Log.i("SW", "Loading " + url);
            _webview.loadUrl(url);
        }
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.parseColor("#EFEFEF"));
            //设置状态栏文字颜色及图标为深色，当状态栏为白色时候，改变其颜色为深色
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //处理WebView跳转返回
        if ((keyCode == KeyEvent.KEYCODE_BACK) && _webview.canGoBack()) {
            _webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onBackClicked() {
        if(_webview.canGoBack()) {
            _webview.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
//        _webview.clearCache(true);
        _webview.clearHistory();
        _webview.clearFormData();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        super.onDestroy();
    }
}
