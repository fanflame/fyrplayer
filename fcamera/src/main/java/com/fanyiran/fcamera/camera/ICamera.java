package com.fanyiran.fcamera.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.SurfaceHolder;

import androidx.annotation.IntDef;

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

    int getOrientation(int cameraId);

    //    void setPreviewOrientation(int degree);
    boolean open(boolean isFront);

    void setPreviewSize(Size size);
    void setConfig(CameraConfig cameraConfig);

    boolean preview(SurfaceTexture surface);

    boolean preview(SurfaceHolder holder);

    boolean switchCamera();
    void release();
}
