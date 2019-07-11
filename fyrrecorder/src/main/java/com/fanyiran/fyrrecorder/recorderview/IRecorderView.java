package com.fanyiran.fyrrecorder.recorderview;

import com.fanyiran.fyrrecorder.camera.CameraConfig;

public interface IRecorderView {
    void autoPreview(CameraConfig cameraConfig);
    void switchCamera();
    void release();
    void startRecord();
    void pauseRecord();
    void resumeRecord();
    void stopRecord();
    // for debug
    int getPreviewFps();
}
