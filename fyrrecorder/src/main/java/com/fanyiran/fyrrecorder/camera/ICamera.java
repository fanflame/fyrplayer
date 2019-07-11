package com.fanyiran.fyrrecorder.camera;

public interface ICamera {
    int CAMERA_FRONT = 1;
    int CAMREA_BACK = 2;
    void setConfig(CameraConfig cameraConfig);
    void preview();
    void switchCamera();
    void release();
    void startRecord();
    void pauseRecord();
    void resumeRecord();
    void stopRecord();
    int getPreviewFps();
}
