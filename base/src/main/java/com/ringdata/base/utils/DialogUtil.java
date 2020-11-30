package com.ringdata.base.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by admin on 2018/1/27.
 */

public class DialogUtil {
    private static AlertDialog errorDialog;
    private static ProgressDialog progressDialog;
    private static Context context;

    public static void showProgressDialog(Context mContext, String content) {
        if (content != null) {
            context = mContext;
            if (progressDialog == null) {
                progressDialog = ProgressDialog.show(context, "提示", "正在同步数据");
            } else {
                progressDialog.setMessage(content);
            }
            progressDialog.show();
        }

    }

    public static void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            context = null;
        }
    }

    public static void showErrorDialog(Context mContext, String content) {
        if (mContext != null) {
            if (errorDialog == null) {
                errorDialog = new AlertDialog.Builder(mContext).setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                errorDialog.setTitle("数据同步失败");
                errorDialog.setMessage(content);
                errorDialog.setCancelable(false);
            } else {
                errorDialog.setMessage(content);
            }
            if (!errorDialog.isShowing()) {
                errorDialog.show();
            }
        }
    }

    public static void showErrorDialog(Context mContext, String title, String content) {
        if (mContext != null) {
            if (errorDialog == null) {
                errorDialog = new AlertDialog.Builder(mContext).setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                errorDialog.setTitle(title);
                errorDialog.setMessage(content);
                errorDialog.setCancelable(false);
            } else {
                errorDialog.setMessage(content);
            }
            if (!errorDialog.isShowing()) {
                errorDialog.show();
            }
        }
    }

}
