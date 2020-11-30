package com.ringdata.base.app;

import android.content.Context;
import android.os.Handler;

import java.util.HashMap;

/**
 * Created by admin on 17/10/7.
 */

public final class Latte {
    public static Configurator init(Context context) {
        Configurator.getInstance()
                .getLatteConfigs()
                .put(ConfigType.APPLICATION_CONTEXT, context.getApplicationContext());
        return Configurator.getInstance();
    }

    public static HashMap<Object, Object> getConfigurations() {
        return Configurator.getInstance().getLatteConfigs();
    }
    
    public static Context getApplicationContext(){
        return (Context) getConfigurations().get(ConfigType.APPLICATION_CONTEXT);
    }
    public static Handler getHandler() {
        return (Handler) getConfigurations().get(ConfigType.HANDLER);
    }

}
