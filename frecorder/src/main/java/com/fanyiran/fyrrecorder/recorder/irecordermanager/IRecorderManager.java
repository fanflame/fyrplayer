package com.fanyiran.fyrrecorder.recorder.irecordermanager;

import android.app.Activity;

import com.fanyiran.fcamera.camera.CameraConfig;
import com.fanyiran.fcamera.camera.callback.OnTakePicCallBack;

import java.io.File;

public interface IRecorderManager {
    void init(Activity activity);

    void startPreview(Object object);

    void stopPreview();

    void takePicture(File picFile, OnTakePicCallBack onTakePicCallBack);

    void startRecord();

    void stopRecord();

    void release();

    int getCameraCount(int orientation);

    int getOrientation(int cameraId);

    int getCurrentPreviewFps();

    void switchCamera();

    void openCamera(boolean front, CameraConfig cameraConfig);
}
