package com.ringdata.ringinterview.survey.jsbridge;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xch on 2017/5/15.
 */


public class JSBridge {
    public String _stubName = "JSBridgeStub";
    public String _clientName = "jsbridge";

    static public class JSParams {
        private JSONArray array = new JSONArray();

        public JSParams(String paramString) {
            try {
                if(paramString != null && paramString.length() > 0) {
                    array = new JSONArray(paramString);
                }
            } catch (Exception e) {
                Log.e("JSBridge", "Failed to parse js params to JSONArray", e);
            }
        }

        public Object getObject(int index) {
            Object r = null;
            try {
                r = array.get(index);
            } catch (Exception e) {
                Log.e("JSBridge", "Failed to read js params at " + index, e);
            }
            return r;
        }

        public int getInt(int index) {
            int v = 0;
            try {
                v = array.getInt(index);
            } catch (Exception e) {
                Log.e("JSBridge", "Failed to read js params at " + index, e);
            }
            return v;
        }

        public double getDouble(int index) {
            double v = 0;
            try {
                v = array.getDouble(index);
            } catch (Exception e) {
                Log.e("JSBridge", "Failed to read js params at " + index, e);
            }
            return v;
        }

        public boolean getBool(int index) {
            boolean v = false;
            try {
                v = array.getBoolean(index);
            } catch (Exception e) {
                Log.e("JSBridge", "Failed to read js params at " + index, e);
            }
            return v;
        }

        public String getString(int index) {
            String v = null;
            try {
                v = array.getString(index);
            } catch (Exception e) {
                Log.e("JSBridge", "Failed to read js params at " + index, e);
            }
            return v;
        }

        public JSONObject jsonObject(int index) {
            JSONObject v = null;
            try {
                v = array.getJSONObject(index);
            } catch (Exception e) {
                Log.e("JSBridge", "Failed to read js params at " + index, e);
            }
            return v;
        }

        public JSONArray jsonArray(int index) {
            JSONArray v = null;
            try {
                v = array.getJSONArray(index);
            } catch (Exception e) {
                Log.e("JSBridge", "Failed to read js params at " + index, e);
            }
            return v;
        }
    }

    public class Callback {
        private String _callbackId;
        //private int _result = 0;
        //private Object _param;

        public Callback(String callbackId) {
            _callbackId = callbackId;
        }

        /*public void success(Object param) {
            _result = 1;
            _param = param;
        }

        public void error(Object param) {
            _result = 2;
            _param = param;
        }*/

        public void callbackOnUIThread(final Boolean success,
                                        final Object params) {
            _activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        callback(success, params);
                    } catch (Exception e) {
                        Log.e("JSBridge", "Error in exec JSBridge action", e);
                    }
                }
            });
        }

        public void callback(final Boolean success,
                             final Object params) {
            /*_activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        execClientFunc("callback", new Object[]{
                                _callbackId,
                                success,
                                params
                        });
                    } catch (Exception e) {
                        Log.e("JSBridge", "Error in exec JSBridge action", e);
                    }
                }
            });*/
            try {
                execClientFunc("callback", new Object[]{
                        _callbackId,
                        success,
                        params
                });
            } catch (Exception e) {
                Log.e("JSBridge", "Error in exec JSBridge action", e);
            }
        }

        public void callback(final Boolean success) {
            callback(success, new Object[]{});
        }
    }

    private WebView _webView;
    private Activity _activity;
    private JSBridgePlugin _plugin;

    public JSBridge(Activity act,
                    WebView webView,
                    String stubName,
                    String clientName) {
        _activity = act;
        _webView = webView;
        _stubName = stubName;
        _clientName = clientName;
        _webView.addJavascriptInterface(this, _stubName);
    }

    public JSBridge(Activity act, WebView webView) {
        _activity = act;
        _webView = webView;
        _webView.addJavascriptInterface(this, _stubName);
    }

    public void setPlugin(JSBridgePlugin plugin) {
        _plugin = plugin;
        if(_plugin != null) {
            _plugin.setActivity(_activity);
        }
    }

    public void setClientName(String name) {
        _clientName = name;
    }

    @JavascriptInterface
    public void postMessage(final String callbackid, final String action, final String params) {
        Log.d("JSBridge", "Exec  " + callbackid + " " +  action + " '" + params+"'");
        if(_plugin != null) {
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    final Callback callback = new Callback(callbackid);
                    try {
                        _plugin.exec(action, new JSParams(params), callback);
                    } catch (RuntimeException e) {
                        Log.e("JSBridge", "Error in exec JSBridge action", e);
                    }
                }
            }).start();*/
            final Callback callback = new Callback(callbackid);
            try {
                _plugin.exec(action, new JSParams(params), callback);
            } catch (RuntimeException e) {
                Log.e("JSBridge", "Error in exec JSBridge action", e);
            }
        }
    }

    //protected abstract void exec(String action, JSParams params, Callback callback);

    public void execJSFunc(String func, Object[] params) {
        try {
        String jsexpr = JSUtils.JSCallFuncString(func, params);
        Log.d("JSBridge", "execJSFunc '"+jsexpr+"'");
        _webView.loadUrl("javascript:"+jsexpr);
        } catch (Exception e) {
            Log.e("JSBridge", "Error in exec execJSFunc", e);
        }

    }

    public void execClientFunc(String func, Object[] params) {
        execJSFunc(_clientName+"."+func, params);
    }

    public void execJS(String expr) {
        _webView.loadUrl("javascript:" + expr);
    }

    public void injectScriptFile(String file) {
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
             injectScriptFile(input);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void injectScriptFile(InputStream input) throws Exception {
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();
            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            _webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "script.innerHTML = decodeURIComponent(escape(window.atob('" + encoded + "')));" +
                    "parent.appendChild(script)" +
                    "})()");
    }
}
