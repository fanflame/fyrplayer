package com.fanyiran.fyrrecorder.recorder;

import android.view.Surface;

public interface IRecorder {
    void init(RecorderConfig config);
    void startRecord();
    void stopRecord();
    void pause();
    void resume();
    void release();
    int getStatus();

    Surface getSurface();
}
