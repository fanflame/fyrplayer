package com.fanyiran.fyrrecorder.recorderview;

import com.fanyiran.fcamera.camera.CameraConfig;

import java.io.File;

public interface IRecorderView {
    int getOrientation(int cameraId);

    int getCameraCount(int orientation);

    void autoPreview(CameraConfig cameraConfig);
    void switchCamera();
    void release();
    void startRecord();
    void pauseRecord();
    void resumeRecord();
    void stopRecord();
    // for debug
    int getPreviewFps();

    void takePicture(File file);
}
