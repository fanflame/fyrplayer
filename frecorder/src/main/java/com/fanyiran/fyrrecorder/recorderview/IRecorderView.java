package com.fanyiran.fyrrecorder.recorderview;

import com.fanyiran.fcamera.camera.CameraConfig;
import com.fanyiran.fcamera.camera.callback.OnTakePicCallBack;

import java.io.File;

public interface IRecorderView {
    int getOrientation(int cameraId);

    int getCameraCount(int orientation);

    void autoPreview(CameraConfig cameraConfig);
    void switchCamera();
    void release();

    void startRecord(File file);

    void startPreview();
    void pauseRecord();
    void resumeRecord();
    void stopRecord();
    // for debug
    int getPreviewFps();

    void takePicture(File file, OnTakePicCallBack onTakePicCallBack);
}
