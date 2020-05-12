package com.fanyiran.fyrrecorder.recorder.ffmpeg;

import android.view.Surface;

import com.fanyiran.fyrrecorder.recorder.IRecorderAbstract;
import com.fanyiran.fyrrecorder.recorder.RecorderConfig;
import com.fanyiran.utils.LogUtil;

public class FFmpegImpl extends IRecorderAbstract {
    private static final String TAG = "FFmpegImpl";
    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ffmpegrender");
    }

    @Override
    public void init(RecorderConfig config) {
        super.init(config);
        int result = nativeInit(config);
        LogUtil.v(TAG,"result = "+result);
    }

    @Override
    public void startRecord() {
        super.startRecord();
        nativeStartRecord();
    }

    @Override
    public void stopRecord() {
        super.stopRecord();
        nativeStopRecord();
    }

    @Override
    public void resume() {
        super.resume();
        nativeResume();
    }

    @Override
    public void pause() {
        super.pause();
        nativePause();
    }

    @Override
    public void release() {
        super.release();
        nativeRelease();
    }

    @Override
    public Surface getSurface() {
        return null;
    }

    @Override
    public void receiveData(byte[] dataY, byte[] dataU, byte[] dataV) {
        encode(dataY,dataU,dataV);
    }

    native int nativeInit(RecorderConfig config);
    native void nativeStartRecord();
    native void encode(byte[] data,byte[] dataU, byte[] dataV);
    native void nativeStopRecord();
    native void nativeResume();
    native void nativePause();
    native void nativeRelease();
}
