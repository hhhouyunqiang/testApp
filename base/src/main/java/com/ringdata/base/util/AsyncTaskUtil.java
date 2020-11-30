package com.ringdata.base.util;

import android.os.AsyncTask;

import com.ringdata.base.util.callback.AsyncCallBack;

/**
 * Created by admin on 2018/3/30.
 */

public class AsyncTaskUtil {

    public static void doAsync(final AsyncCallBack callBack) {
        if (callBack == null) {
            return;
        }
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                callBack.before();
            }

            @Override
            protected Void doInBackground(Void... params) {
                callBack.execute();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                callBack.after();
            }
        }.execute();
    }

}
