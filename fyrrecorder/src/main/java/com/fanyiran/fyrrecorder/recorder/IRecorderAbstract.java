package com.fanyiran.fyrrecorder.recorder;

import androidx.annotation.CallSuper;

public abstract class IRecorderAbstract implements IRecorder {
    int RECORD_STATUS_DEFAULT = 0;
    int RECORD_STATUS_INIT = 0;
    int RECORD_STATUS_START = 0;
    int RECORD_STATUS_STOP = 0;
    int RECORD_STATUS_PAUSE = 2;
    int RECORD_STATUS_PLAYING = 3;
    int RECORD_STATUS_ERROR = 4;
    int RECORD_STATUS_RELEASE = 4;

    private int status = RECORD_STATUS_DEFAULT;

    @CallSuper
    @Override
    public void init(RecorderConfig config) {
        status = RECORD_STATUS_INIT;
    }

    @CallSuper
    @Override
    public void startRecord() {
        status = RECORD_STATUS_START;
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

    protected void setStatusError() {
        status = RECORD_STATUS_ERROR;
    }
}
