package com.ringdata.ringinterview.capi.components.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HYQ on 2020/10/23.
 */

public class MonitorRecordingService extends Service {
    private MediaRecorder recorder; //录音的一个实例
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获得电话管理器
        TelephonyManager tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //启动监听.传入一个listener和监听的事件,
        tm.listen(new MyListener(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    class  MyListener extends PhoneStateListener {
        //在电话状态改变的时候调用
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态
                    if (recorder != null) {
                        recorder.stop();//停止录音
                        recorder.release();//释放资源
                        recorder = null;
                    }
                    break;

                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃状态  需要在响铃状态的时候初始化录音服务
                    if (recorder == null) {
                        recorder = new MediaRecorder();//初始化录音对象
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置录音的输入源(麦克)
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);//设置音频格式(3gp)
                        createRecorderFile();//创建保存录音的文件夹

                        recorder.setOutputFile(Environment.getExternalStorageDirectory() + "/recorder/" + getCurrentTime() + ".mp3"); //设置录音保存的文件
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置音频编码
                        try {
                            recorder.prepare();//准备录音
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:

                    //摘机状态（接听）
                    if (recorder != null) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                recorder.start(); //接听的时候开始录音
                            }
                        }).start();

                    }
                    break;
            }
        }

        //创建保存录音的目录
        private void createRecorderFile() {
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String filePath = absolutePath + "/recorder";
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        }

        //获取当前时间，以其为名来保存录音
        private String getCurrentTime() {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            String str = format.format(date);
            return str;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
