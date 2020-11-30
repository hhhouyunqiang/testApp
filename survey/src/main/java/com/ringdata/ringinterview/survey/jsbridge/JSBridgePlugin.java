package com.ringdata.ringinterview.survey.jsbridge;

import android.app.Activity;

/**
 * Created by xch on 2017/5/16.
 */

public interface JSBridgePlugin {

    void setActivity(Activity  activity);

    Activity getActivity();

    void exec(String action,
              JSBridge.JSParams params,
              JSBridge.Callback callback);
}
