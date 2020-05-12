package com.fanyiran.fyrrecorder.recorder.mediarecorder;

import android.media.MediaRecorder;
import android.view.Surface;

import com.fanyiran.fyrrecorder.recorder.IRecorderAbstract;
import com.fanyiran.fyrrecorder.recorder.RecorderConfig;
import com.fanyiran.utils.LogUtil;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class MediaRecorderImpl extends IRecorderAbstract {
    private static final String TAG = "MediaRecorderImpl";
    private MediaRecorder mediaRecorder;

    @Override
    public void init(RecorderConfig config) {
        super.init(config);
        setupMediaRecorder();
    }

    @Override
    public void startRecord() {
        mediaRecorder.start();
        super.startRecord();
    }

    @Override
    public void stopRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
        }
        recorderConfig.camera.unlock();//需要unlock，否则预览界面会卡住
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
        mediaRecorder.reset();
        mediaRecorder.release();
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
            recorderConfig.camera.unlock();
            mediaRecorder.setCamera(recorderConfig.camera);
            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    LogUtil.v(TAG, String.format("error what:%d;extra:%d", what, extra));
                }
            });
            mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    LogUtil.v(TAG, String.format("onInfo what:%d;extra:%d", what, extra));
                }
            });
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setOutputFormat(recorderConfig.outputFormat);
            mediaRecorder.setVideoEncoder(recorderConfig.videoEncoder);
            mediaRecorder.setAudioEncoder(recorderConfig.audioEncoder);
            mediaRecorder.setVideoFrameRate(recorderConfig.frameRate);
            File outputFile = recorderConfig.outputFile;
            mediaRecorder.setOutputFile(outputFile.getAbsolutePath());
//            mediaRecorder.setVideoSize(recorderConfig.videSize.getWidth(), recorderConfig.videSize.getHeight());
            mediaRecorder.setVideoEncodingBitRate(recorderConfig.encodingBitRate);
//            int rotation = ((Activity) recorderConfig.getContext()).getWindowManager().getDefaultDisplay().getRotation();
        }
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            LogUtil.v(TAG, e.getMessage());
            e.printStackTrace();
//            onCameraError(2);
            setStatusError();
            return;
        }
    }
}
