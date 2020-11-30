package com.ringdata.ringinterview.survey;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.ringdata.ringinterview.survey.jsbridge.JSBridge;
import com.ringdata.ringinterview.survey.jsbridge.JSBridgePlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class SurveyActivity extends Activity implements JSBridgePlugin {
    final Logger logger = LoggerFactory.getLogger(SurveyActivity.class);

    public static final int RS2_I_EDIT_INTERVIEW = 1;
    public static final int RS2_I_EDIT_VIEW = 2;
    public static final int RS2_I_EDIT_CHECK = 3;
    public static final int RS2_I_EDIT_PREVIEW = 4;

    public static final String INTENT_SURVEY_STATE = "SURVEY_STATE";
    public static final String INTENT_SURVEY_DATA = "SURVEY_DATA";
    public static final String INTENT_SURVEY_VARS = "SURVEY_VARIABLES";
    public static final String INTENT_SURVEY_EXTERNS = "SURVEY_EXTERNS";
    private static final String RESPONSE_IDENTIFIER = "答卷标识";
    private static final String SAMPLE_IDENTIFIER = "样本标识";
    public static String survey_state, survey_data;
    public static String[] survey_variables, survey_externs;
    //protected JSBridgeHelper jshelper;
    private String _remote_survey;
    private String _panel_page;
    private String responseIdentifier = "";
    private String sampleIdentifier = "";
    protected boolean _exit = false;
    protected boolean _lastStep = false;
    protected boolean _alerting = false;
    protected SurveyHistoryItem _item;
    protected int editType = RS2_I_EDIT_INTERVIEW;
    protected WebView _webview;
    public JSBridge jsbridge;
    public KProgressHUD hud;
    //protected String _sfile;
    protected boolean _loaded = false;
    protected boolean _started = false;
    private GestureDetector _gd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createView();
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍候")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.3f);
        _remote_survey = getIntent().getStringExtra("REMOTE_SURVEY");
        _panel_page = getIntent().getStringExtra("SURVEY_PANEL_PAGE");
        if (getIntent().getStringExtra("responseIdentifier") != null) {
            responseIdentifier = getIntent().getStringExtra("responseIdentifier");
        }
        sampleIdentifier = getIntent().getStringExtra("sampleIdentifier");
        //_sfile = getIntent().getStringExtra("SURVEY_FILE");
        jsbridge = new JSBridge(this, _webview, "jsbridge", "rs2_plugin");
        initWebView(_webview);
        jsbridge.setPlugin(this);
        _gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                float x = e2.getX() - e1.getX();
                float y = e2.getY() - e1.getY();
                if (Math.abs(y) < 30) {
                    if (x > 20) {
                        jsbridge.execClientFunc("on_swipe", new Object[]{1});
                    } else if (x < 0) {
                        jsbridge.execClientFunc("on_swipe", new Object[]{0});
                    }
                }
                return false;
            }
        });
        _webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return _gd.onTouchEvent(motionEvent);
            }
        });
    }

    abstract protected void createView();

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        survey_state = savedInstanceState.getString(INTENT_SURVEY_STATE);
//        survey_data = savedInstanceState.getString(INTENT_SURVEY_DATA);
//        survey_variables = savedInstanceState.getStringArray(INTENT_SURVEY_VARS);
//        survey_externs = savedInstanceState.getStringArray(INTENT_SURVEY_EXTERNS);
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(INTENT_SURVEY_STATE, survey_state);
//        outState.putString(INTENT_SURVEY_DATA, survey_data);
//        outState.putStringArray(INTENT_SURVEY_VARS, survey_variables);
//        outState.putStringArray(INTENT_SURVEY_EXTERNS, survey_externs);
//    }

    protected void initWebView(WebView webView) {
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("CAPI", "onPageStarted " + url);
                logger.debug("onPageStarted " + url);
                if (!hud.isShowing()) {
                    hud.show();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("CAPI", "onPageFinished " + url);
                logger.debug("onPageFinished " + url);
                if (hud.isShowing()) {
                    hud.dismiss();
                }
                onPageLoaded();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            /**
             * 处理JavaScript Alert事件
             */
            @Override
            public boolean onJsAlert(WebView view, String url,
                                     String message, final JsResult result) {
                Toast.makeText(SurveyActivity.this, message, Toast.LENGTH_SHORT).show();
                result.confirm();
                return true;
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("CAPI", consoleMessage.message());
                logger.debug(consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return _gd.onTouchEvent(ev);
    }

    protected void onPageLoaded() {
        /*if(_sfile != null) {
            jsbridge.injectScriptFile(_sfile);
            jsbridge.execJS("onload();");
        }*/
    }

    public void setCacheEnabled(boolean enabled) {
        _webview.getSettings().setCacheMode(enabled ? WebSettings.LOAD_DEFAULT
                : WebSettings.LOAD_NO_CACHE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!_loaded) {
            _loaded = true;
            _webview.loadUrl(_panel_page);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void setActivity(Activity activity) {
    }

    @Override
    public void exec(String action,
                     JSBridge.JSParams params,
                     JSBridge.Callback callback) {
        if ("nextPageLoaded".equals(action)) {
            nextPageLoaded(params, callback);
        } else if ("prevPageLoaded".equals(action)) {
            prevPageLoaded(params, callback);
        } else if ("pageEdited".equals(action)) {
            callback.callbackOnUIThread(true, null);
            pageEdited();
        } else if ("exit".equals(action)) {
            exit(params, callback);
        } else if ("submitData".equals(action)) {
            submitData(params, callback);
        } else if ("saveData".equals(action)) {
            saveData(params, callback);
        } else if ("onload".equals(action)) {
            onload(params, callback);
        } else if ("onstart".equals(action)) {
            onstart(params, callback);
        } else if ("beginLocation".equals(action)) {
            beginLocation(callback);
        } else if ("getResource".equals(action)) {
            getResource(callback, params.getInt(0), params.getString(1));
        } else if ("removeResource".equals(action)) {
            callback.callbackOnUIThread(true, null);
            removeResource(params.getInt(0),
                    params.getString(1),
                    params.getString(2));
        } else if ("picture".equals(action)) {
            picture(callback, params.getString(0));
        } else if ("signature".equals(action)) {
            signature(callback, params.getString(0));
        } else if ("alert".equals(action)) {
            Object msg = params.getObject(0);
            if (msg != null) {
                if (msg instanceof String) {
                    alert(callback, (String) msg);
                } else if (msg instanceof JSONArray) {
                    JSONArray array = (JSONArray) msg;
                    if (array.length() > 0) {
                        try {
                            alert(callback, array.getString(0));
                        } catch (JSONException e) {
                        }
                    }
                }
            }
        } else if ("ask".equals(action)) {
            String msg = params.getString(0);
            if (msg != null) {
                ask(callback, msg);
            }
        } else if ("beginRecording".equals(action)) {
            beginRecording(callback, params.getString(0));
        } else if ("endRecording".equals(action)) {
            endRecording(callback, params.getString(0));
        }
    }


    public void endEditing() {
        jsbridge.execClientFunc("end_editing", new Object[]{});
    }


    public void onload(final JSBridge.JSParams params,
                       final JSBridge.Callback callback) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.callback(true);
                StringBuilder interview = new StringBuilder("{");
                interview.append("\"edit_type\":\"").append(editType);
                interview.append("\",");
                interview.append("\"menu_props\":[");
//                interview.append("{\\\"name\\\":\\\"").append(RESPONSE_IDENTIFIER).append("\",\"value\":\"").append(responseIdentifier).append("\"},");
                interview.append("{\"name\":\"").append(SAMPLE_IDENTIFIER).append("\",\"value\":\"").append(sampleIdentifier).append("\"}]}");
                StringBuilder vars = new StringBuilder();
                vars.append("[");
                if (survey_variables != null) {
                    for (int i = 0; i < survey_variables.length; ++i) {
                        if (!TextUtils.isEmpty(survey_variables[i])) {
                            if (i > 0) {
                                vars.append(",");
                            }
                            vars.append(survey_variables[i]);
                        }
                    }
                }
                vars.append("]");
                StringBuilder externs = new StringBuilder();
                externs.append("[");
                if (survey_externs != null) {
                    for (int i = 0; i < survey_externs.length; ++i) {
                        if (!TextUtils.isEmpty(survey_externs[i])) {
                            if (i > 0) {
                                externs.append(",");
                            }
                            externs.append(survey_externs[i]);
                        }
                    }
                }
                externs.append("]");
                if (_remote_survey != null) {
                    jsbridge.execClientFunc("load_remote",
                            new Object[]{"\"" + _remote_survey + "\"",
                                    survey_state,
                                    survey_data,
                                    interview.toString(),
                                    vars.toString(),
                                    externs.toString()});
                } else {
                    jsbridge.execClientFunc("start", new Object[]{survey_state,
                            survey_data,
                            interview.toString(),
                            vars.toString(),
                            externs.toString()});
                }
            }
        });
    }

    public void onstart(final JSBridge.JSParams params,
                        final JSBridge.Callback callback) {
        final JSONObject state = params.jsonObject(0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.callback(true);
                updateViewState(state);

            }
        });

    }

    public void updateViewState(JSONObject state) {
        if (state != null) {
            try {
                boolean hasRemark = state.getBoolean("has_remark");
                Log.d("DEV", "has_remark " + hasRemark);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void prevPageLoaded(final JSBridge.JSParams params,
                               final JSBridge.Callback callback) {
        final JSONObject p = params.jsonObject(0);
        _item = null;
        if (p != null) {
            _item = new SurveyHistoryItem(p);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //查看模式下
                    updateNavState(_item);
                    callback.callback(true);
                }
            });
        }

    }

    public void nextPageLoaded(final JSBridge.JSParams params,
                               final JSBridge.Callback callback) {
        _started = true;
        final JSONObject p = params.jsonObject(0);
        _item = null;
        if (p != null) {
            _item = new SurveyHistoryItem(p);
            try {
                onBeginEditingPage(_item);
            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //加载下一页数据，
                    updateNavState(_item);
                    callback.callback(true);
                }
            });
        }
    }

    protected void onBeginEditingPage(SurveyHistoryItem item) throws Exception {

    }

    public void updateNavState(SurveyHistoryItem item) {
        _alerting = false;
        if (item != null) {
            updateTitle(item.index, item.total);
            updatePrev(item.hasPrev);
            _exit = item.exit;
            _lastStep = item.lastStep;
            //显示下一页 上一页
            updateNext(_exit, _lastStep);
        }
    }

    public void pageEdited() {
        if (_exit) {
            _exit = false;
            updateNext(_exit, _lastStep);
        }
        if (_item != null) {
            try {
                onBeginEditingPage(_item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void exit(final JSBridge.JSParams params,
                     final JSBridge.Callback callback) {
        if (_exit) {
            confirmSubmit();
        } else {
            _exit = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateNext(_exit, _lastStep);
                    callback.callback(true);
                }
            });
        }
    }

    public void submitData(final JSBridge.JSParams params,
                           final JSBridge.Callback callback) {
        onSubmitData(params.getString(0), params.getString(1), params.jsonObject(2));
    }

    public void saveData(final JSBridge.JSParams params,
                         final JSBridge.Callback callback) {
        onSaveData(params.getString(0), params.getString(1), params.jsonObject(2));
    }

    protected void onSaveData(String state, String data, JSONObject extra) {
        Log.d("CAPI", "saveData state = " + state);
        Log.d("CAPI", "saveData data = " + data);
    }

    protected void onSubmitData(String state, String data, JSONObject extra) {
        Log.d("CAPI", "submitData state = " + state);
        Log.d("CAPI", "submitData data = " + data);
        Log.d("CAPI", "submitData extra = " + extra);
    }

    protected void doPrev() {
        jsbridge.execClientFunc("page_event", new Object[]{"'prev'"});
    }

    protected void doNext() {
        jsbridge.execClientFunc("page_event", new Object[]{"'next'"});
    }

    public void doSave() {
        jsbridge.execClientFunc("save", new Object[]{});
    }

    public void toggleMenu() {
        jsbridge.execClientFunc("toggle_menu", new Object[]{});
    }

    public void toggleRemark() {
        jsbridge.execClientFunc("toggle_remark", new Object[]{});
    }

    public void confirmSubmit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("提交问卷");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doSubmit();
                    }
                });
            }
        });
        builder.show();
    }

    protected void doSubmit() {
        jsbridge.execClientFunc("submit", new Object[]{});
    }

    protected void beginLocation(final JSBridge.Callback callback) {

    }

    protected void endLocation(JSBridge.Callback callback,
                               String lat,
                               String lng,
                               String poi,
                               String error) {
        JSONObject obj = new JSONObject();
        try {
            if (error == null) {
                obj.put("lat", lat);
                obj.put("lng", lng);
                obj.put("poi", poi);
            } else {
                obj.put("message", error);
            }
        } catch (JSONException e) {
        }
        callback.callbackOnUIThread(error == null, obj);
    }

    private void getResource(JSBridge.Callback callback, int type, String name) {
        endPicture(callback, resourceURI(type, name), name, "");
    }

    protected void removeResource(int type, String name, String qid) {

    }

    protected String resourceURI(int type, String name) {
        return name;
    }

    protected void picture(JSBridge.Callback callback, String qid) {

    }

    protected void signature(JSBridge.Callback callback, String qid) {

    }

    protected void endPicture(JSBridge.Callback callback,
                              String uri,
                              String name,
                              String error) {
        JSONObject obj = new JSONObject();
        try {
            if (uri != null) {
                obj.put("uri", uri);
                obj.put("name", name);
            } else {
                obj.put("message", error);
            }
        } catch (JSONException e) {
        }
        callback.callbackOnUIThread(uri != null, obj);
    }

    protected void alert(final JSBridge.Callback callback, final String message) {
        Log.d("DEV", "Survey Alert " + message + " " + _alerting);
        if (!_alerting) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage(message);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            _alerting = false;
                            callback.callback(true);
                        }
                    });
                    builder.setCancelable(false);
                    _alerting = true;
                    builder.show();
                }
            });
        }
    }

    protected void ask(final JSBridge.Callback callback, final String message) {
        if (!_alerting) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage(message);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            _alerting = false;
                            callback.callback(true);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            _alerting = false;
                            callback.callback(false);
                        }
                    });
                    builder.setCancelable(false);
                    _alerting = true;
                    builder.show();
                }
            });
        }
    }

    protected void beginRecording(final JSBridge.Callback callback, String qid) {

    }

    protected void endRecording(final JSBridge.Callback callback, String qid) {

    }

    protected void updateTitle(int index, int total) {
    }

    protected void updatePrev(boolean hasPrev) {
    }

    protected void updateNext(boolean exit, boolean _lastStep) {
    }


}
