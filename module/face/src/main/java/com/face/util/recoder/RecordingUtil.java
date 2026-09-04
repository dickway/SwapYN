package com.face.util.recoder;


import android.media.MediaRecorder;

import com.face.key.Constants;

import java.io.File;
import java.io.IOException;

public class RecordingUtil implements RecorderManager {
    private String path = null;
    private MediaRecorder mRecorder = null;
    public RecordingUtil(String path) {
        this.path = path;
    }

    @Override
    public boolean start() {
        File dir =new File(Constants.INSTANCE.getVoicePath());
        if(!dir.exists()) {
            dir.mkdirs();
        }

        mRecorder = new MediaRecorder();
        //设置音源为Micphone
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置封装格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(path);
        //设置编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
            mRecorder.start();   //录音
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean stop() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        return false;
    }

}
