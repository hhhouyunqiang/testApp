package com.ringdata.ringinterview.capi.components.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by admin on 17/10/18.
 */

public class RecordHelper {

    private static MediaPlayer mediaPlayer;

    // 录音类
    private static MediaRecorder mediaRecorder;

    private Context mcontext;

    private static RecordHelper instance = null;

    public static Boolean isRecoding = false;//是否正在录音

    public static RecordHelper getInstance(Context context) {
        if (instance == null) {
            instance = new RecordHelper(context);
        }
        return instance;
    }

    private RecordHelper(Context context) {
        this.mcontext = context;
    }

    // 播放录音文件
    public void playRecordFile(File file) {

        if (file.exists() && file != null) {
            if (mediaPlayer == null) {
                Uri uri = Uri.fromFile(file);
                mediaPlayer = MediaPlayer.create(mcontext, uri);
            }
            mediaPlayer.start();

            //监听MediaPlayer播放完成
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer paramMediaPlayer) {
                    // TODO Auto-generated method stub
                    //弹窗提示
//                    ToastUtils.showShort("播放完成");
                }
            });

        }
    }

    // 暂停播放录音
    public void pausePalyer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Log.e("TAG", "暂停播放");
        }

    }

    // 停止播放录音
    public void stopPalyer() {
        // 这里不调用stop()，调用seekto(0),把播放进度还原到最开始
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            Log.e("TAG", "停止播放");
        }
    }


    public void startRecording(File recordFile) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 判断，若当前文件已存在，则删除
        if (recordFile.exists()) {
            recordFile.delete();
        }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setOutputFile(recordFile.getAbsolutePath());

        try {
            // 准备好开始录音
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecoding = true;//标记正在录音
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            isRecoding = false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            isRecoding = false;
        }
    }
    public void stopRecording() {
        isRecoding = false;//标记录音结束
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}
