package com.fanyiran.fcamera.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.SurfaceHolder;

import androidx.annotation.IntDef;

import com.fanyiran.fcamera.camera.callback.OnTakePicCallBack;

import java.io.File;

public interface ICamera {
    int CAMERA_ALL = 0;
    int CAMERA_FRONT = 1;
    int CAMREA_BACK = 2;

    @IntDef({CAMERA_ALL, CAMERA_FRONT, CAMREA_BACK})
    @interface ICameraNumber {
    }

    boolean checkCameraHardware(Context context);

    boolean checkAndRequestPermission(Context context);

    void init(Activity context);

    int getCameraCount(@ICameraNumber int orientation);

    int getCurrentPreviewFps();

    void setPreviewFps(int minFps, int maxFps);

    int getOrientation(int cameraId);

    //    void setPreviewOrientation(int degree);
    boolean open(boolean isFront);

    void setPreviewSize(Size size);
    void setConfig(CameraConfig cameraConfig);

    boolean preview(SurfaceTexture surface);

    boolean preview(SurfaceHolder holder);

    void stopPreview();

    boolean switchCamera();

    void takePicture(File picFile, OnTakePicCallBack onTakePicCallBack);
    void release();
}
