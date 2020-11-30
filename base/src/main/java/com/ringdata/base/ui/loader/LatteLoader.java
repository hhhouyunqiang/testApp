package com.ringdata.base.ui.loader;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ringdata.base.R;

import java.util.ArrayList;

/**
 * Created by admin on 17/10/12.
 */

public class LatteLoader {

    private static final ArrayList<AppCompatDialog> LOADERS = new ArrayList<>();
    private static AppCompatDialog dialog;
    private static View view;
    private static TextView loadingText;

    public synchronized static void showLoading(Context context, String text) {
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        if (dialog == null) {
            view = LayoutInflater.from(context).inflate(R.layout.base_view_loading, null);
            loadingText = (TextView) view.findViewById(R.id.tv_loading);
            loadingText.setText(text);
            dialog = new AppCompatDialog(context, R.style.dialog);
            //final AVLoadingIndicatorView avLoadingIndicatorView = (AVLoadingIndicatorView)view.findViewById(R.id.view_AVLoadingIndicatorView);
            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        } else {
            loadingText.setText(text);
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public static synchronized void stopLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }
    }
}

