package com.fanyiran.fyrrecorder.recorder.mediarecorder;

import android.media.MediaRecorder;
import android.view.Surface;

import com.fanyiran.fyrrecorder.recorder.IRecorderAbstract;
import com.fanyiran.fyrrecorder.recorder.RecorderConfig;

import java.io.File;
import java.io.IOException;

public class MediaRecorderImpl extends IRecorderAbstract {
    private MediaRecorder mediaRecorder;

    @Override
    public void init(RecorderConfig config) {
        super.init(config);
        setupMediaRecorder();
    }

    @Override
    public void startRecord() {
        record();
        super.startRecord();
    }

    @Override
    public void stopRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        super.stopRecord();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void release() {
        super.release();
    }

    @Override
    public Surface getSurface() {
        return mediaRecorder.getSurface();
    }

    @Override
    public void receiveData(byte[] dataY, byte[] dataU, byte[] dataV) {

    }

    private void setupMediaRecorder() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(recorderConfig.outputFormat);
            mediaRecorder.setVideoEncoder(recorderConfig.videoEncoder);
            mediaRecorder.setAudioEncoder(recorderConfig.audioEncoder);
            mediaRecorder.setVideoSize(recorderConfig.videSize.getWidth(), recorderConfig.videSize.getHeight());
            mediaRecorder.setVideoFrameRate(recorderConfig.frameRate);
            File outputFile = recorderConfig.outputFile;
            mediaRecorder.setOutputFile(outputFile.getAbsolutePath());
            mediaRecorder.setVideoEncodingBitRate(recorderConfig.encodingBitRate);
//            int rotation = ((Activity) recorderConfig.getContext()).getWindowManager().getDefaultDisplay().getRotation();
        }
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
//            onCameraError(2);
            setStatusError();
            return;
        }
    }

    private void record() {
        mediaRecorder.start();
    }
}
