package com.fanyiran.fyrrecorder.camera;

public interface ICameraListener {
    int ERROR = 1;
    void onCameraOpen();
    void onCameraDisconnected();
    void onCameraError(int error);
}
