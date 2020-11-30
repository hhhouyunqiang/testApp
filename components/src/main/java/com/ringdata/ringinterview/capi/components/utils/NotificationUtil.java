package com.ringdata.ringinterview.capi.components.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.ringdata.base.app.Latte;
import com.ringdata.ringinterview.capi.components.R;

/**
 * Created by admin on 2018/1/22.
 */

public class NotificationUtil {
    public static final int downloadFileId = 10000;
    public static final int uploadFileId = 20000;
    private static NotificationManager manager;
    private static NotificationCompat.Builder mBuilder;

    public static void showDownloadFileNotification(Context context,String title,
                                        String content,int id) {
    }


    public static void showUploadFileNotification(Context context,String title,
                                                    String content,int id) {

        if(manager == null){
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if(mBuilder == null){
            mBuilder = new NotificationCompat.Builder(context);
            //显示在通知栏上的小图标
            mBuilder.setSmallIcon(R.mipmap.app_icon);
        }

        //系统收到通知时，通知栏上面显示的文字。
        mBuilder.setTicker(content);
        //通知标题
        mBuilder.setContentTitle(title);
        //通知内容
        mBuilder.setContentText(content);
        //显示通知，id必须不重复，否则新的通知会覆盖旧的通知（利用这一特性，可以对通知进行更新）
        manager.notify(id, mBuilder.build());
    }

    public static void showNotification(String title,
                                 String content,int id) {

        if(manager == null){
            manager = (NotificationManager) Latte.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if(mBuilder == null){
            mBuilder = new NotificationCompat.Builder(Latte.getApplicationContext());
            //显示在通知栏上的小图标
            mBuilder.setSmallIcon(R.mipmap.app_icon);
        }

        //系统收到通知时，通知栏上面显示的文字。
        mBuilder.setTicker(content);
        //通知标题
        mBuilder.setContentTitle(title);
        //通知内容
        mBuilder.setContentText(content);
        //显示通知，id必须不重复，否则新的通知会覆盖旧的通知（利用这一特性，可以对通知进行更新）
        manager.notify(id, mBuilder.build());
    }
    public static void dismissNotification(Integer id) {
        if(manager != null && mBuilder != null){
            manager.cancel(id);
        }
    }

}
