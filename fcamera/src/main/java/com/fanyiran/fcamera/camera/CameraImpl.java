package com.fanyiran.fcamera.camera;


import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.fanyiran.utils.LogUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

@SuppressWarnings("deprecation")
public class CameraImpl extends CameraBase {
    private static final String TAG = "CameraImpl";
    private int cameraNum;
    private int cameraFrontCount;
    private int cameraBackCount;
    private WeakReference<Object> surfaceReference;

    private Camera currentCamera;
    private int currentCameraId = -1;
    private int fps;

    @Override
    public void init(Activity activity) {
        super.init(activity);
        LogUtil.v(TAG, "init");
        cameraNum = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo;
        for (int i = 0; i < cameraNum; i++) {
            cameraInfo = new Camera.CameraInfo();
            try {
                Camera.getCameraInfo(cameraNum, cameraInfo);
            } catch (RuntimeException e) {
                e.printStackTrace();
                continue;
            }
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraFrontCount++;
            } else if ((cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                cameraBackCount++;
            }
        }
        LogUtil.v(TAG, "cameraNum:" + cameraNum);
    }

    @Override
    public int getCameraCount(@ICameraNumber int orientation) {
        switch (orientation) {
            case ICamera.CAMERA_ALL:
                return cameraNum;
            case ICamera.CAMERA_FRONT:
                return cameraFrontCount;
            case ICamera.CAMREA_BACK:
                return cameraBackCount;
        }
        return 0;
    }

    @Override
    public int getOrientation(int cameraId) {
        if (cameraId < 0 || cameraId > cameraNum - 1) {
            return -1;
        }

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        try {
            Camera.getCameraInfo(cameraId, cameraInfo);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return -1;
        }
        return cameraInfo.orientation;
    }

//    @Override
//    public void setPreviewOrientation(int degree) {
//        setCameraDisplayOrientation();
//    }

    private void setCameraDisplayOrientation() {
        Activity context = getActivity();
        if (context == null) {
            return;
        }
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(currentCameraId, info);
        int rotation = context.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        currentCamera.setDisplayOrientation(result);
    }

    @Override
    public boolean open(boolean isFront) {
        release();
        Camera.CameraInfo cameraInfo;
        currentCameraId = -1;
        for (int i = 0; i < cameraNum; i++) {
            cameraInfo = new Camera.CameraInfo();
            try {
                Camera.getCameraInfo(i, cameraInfo);
            } catch (RuntimeException e) {
                e.printStackTrace();
                continue;
            }
            if (isFront && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                currentCameraId = i;
                break;
            }
            if (!isFront && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                currentCameraId = i;
                break;
            }
        }
        if (currentCameraId != -1) {
            openInner(currentCameraId);
        }
        LogUtil.v(TAG, "currentCamera:" + currentCamera);
        return currentCamera == null;
    }

    @Override
    public void setPreviewSize(Size size) {
//        Size previewSize = getPreviewSize(size);
        Size previewSize = getProperPreviewSize(size);
        LogUtil.v(TAG, previewSize + "");
        if (previewSize == null) {
            return;
        }
        Camera.Parameters parameters = currentCamera.getParameters();
        parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
        try {
            currentCamera.setParameters(parameters);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private Size getProperPreviewSize(Size exceptSize) {
        int orientation = getActivity().getWindowManager().getDefaultDisplay().getOrientation();
        if (orientation == 0 || orientation == 180) {

        } else {

        }
        List<Camera.Size> supportedPreviewSizes = currentCamera.getParameters().getSupportedPreviewSizes();
//        Arrays.sort(objects);
        for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
            if (exceptSize.getWidth() == supportedPreviewSize.width
                    && exceptSize.getHeight() == supportedPreviewSize.height) {
                return exceptSize;
            }
            if (exceptSize.getWidth() == supportedPreviewSize.width) {
                return exceptSize;
            }
        }
        return null;
    }

    private boolean openInner(int currentCameraId) {
        try {
            //On some devices, this method may take a long time to complete
            currentCamera = Camera.open(currentCameraId);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        currentCamera.setErrorCallback(new Camera.ErrorCallback() {

            @Override
            public void onError(int error, Camera camera) {

            }
        });
        currentCamera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

            }
        });
        currentCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

            }
        });
        return true;
    }

    @Override
    public void setConfig(CameraConfig cameraConfig) {

    }


    @Override
    public boolean preview(SurfaceTexture surface) {
        surfaceReference = new WeakReference<Object>(surface);
        try {
            currentCamera.setPreviewTexture(surface);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        currentCamera.startPreview();
        setCameraDisplayOrientation();
        return true;
    }

    @Override
    public boolean preview(SurfaceHolder holder) {
        surfaceReference = new WeakReference<Object>(holder);
        try {
            currentCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        currentCamera.startPreview();
        setCameraDisplayOrientation();
        return true;
    }

    @Override
    public boolean switchCamera() {
        if (currentCamera != null) {
            currentCamera.release();
        }
        if (currentCameraId == -1) {
            if (!open(true)) {
                return false;
            }
            return previewInner();
        } else {
            currentCameraId = (currentCameraId + 1) % cameraNum;
            if (!openInner(currentCameraId)) {
                return false;
            }
            return previewInner();
        }
    }

    private boolean previewInner() {
        if (surfaceReference == null) {
            return false;
        }
        Object surface = surfaceReference.get();
        if (surface == null) {
            return false;
        }
        if (surface instanceof SurfaceTexture) {
            return preview((SurfaceTexture) surface);
        } else if (surface instanceof SurfaceHolder) {
            return preview((SurfaceHolder) surface);
        }
        return false;
    }

    @Override
    public void release() {
        if (currentCamera != null) {
            currentCamera.release();
        }
        currentCameraId = -1;
    }
}
