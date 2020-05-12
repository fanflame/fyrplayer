package com.fanyiran.fyrrecorder.recorder;

import androidx.annotation.CallSuper;

public abstract class IRecorderAbstract implements IRecorder {
    public static int RECORD_STATUS_DEFAULT = 0;
    public static int RECORD_STATUS_INIT = 1;
    public static int RECORD_STATUS_START_RECORD = 2;
    public static int RECORD_STATUS_STOP = 3;
    public static int RECORD_STATUS_PAUSE = 4;
    public static int RECORD_STATUS_PLAYING = 5;
    public static int RECORD_STATUS_ERROR = 6;
    public static int RECORD_STATUS_RELEASE = 7;

    private volatile int status = RECORD_STATUS_DEFAULT;
    protected RecorderConfig recorderConfig;

    @CallSuper
    @Override
    public void init(RecorderConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config can't be null");
        }
        recorderConfig = config;
        status = RECORD_STATUS_INIT;
    }

    @CallSuper
    @Override
    public void startRecord() {
        status = RECORD_STATUS_START_RECORD;
    }

    @CallSuper
    @Override
    public void stopRecord() {
        status = RECORD_STATUS_STOP;
    }

    @CallSuper
    @Override
    public void resume() {
        status = RECORD_STATUS_PLAYING;
    }

    @CallSuper
    @Override
    public void pause() {
        status = RECORD_STATUS_PAUSE;

    }

    @CallSuper
    @Override
    public void release() {
        status = RECORD_STATUS_RELEASE;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public void onError(int error,String errorString) {
        if (recorderConfig.onPlayErrorListener != null) {
            recorderConfig.onPlayErrorListener.onError(error,errorString);
        }
    }

    protected void setStatusError() {
        status = RECORD_STATUS_ERROR;
    }
}
