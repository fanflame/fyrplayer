package com.fanyiran.fcamera.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.SurfaceHolder;

import com.fanyiran.fcamera.camera.factory.CameraFactory;
import com.fanyiran.fcamera.camera.factory.ICameraFactory;

import java.io.File;

public class CameraManager extends CameraBase {
    private ICamera cameraImpl;
    public ICameraFactory cameraFactory;

    private static final CameraManager ourInstance = new CameraManager();

    public static CameraManager getInstance() {
        return ourInstance;
    }

    public void setCameraFactory(ICameraFactory cameraFactory) {
        this.cameraFactory = cameraFactory;
    }

    private ICameraFactory getCameraFactory() {
        if (cameraFactory == null) {
            cameraFactory = new CameraFactory();
        }
        return cameraFactory;
    }


    @Override
    public void init(Activity activity) {
        super.init(activity);
        cameraImpl = getCameraFactory().createCamera(null, null, null);
        cameraImpl.init(activity);
    }

    @Override
    public int getCameraCount(@ICameraNumber int orientation) {
        return cameraImpl.getCameraCount(orientation);
    }

    @Override
    public int getCurrentPreviewFps() {
        return cameraImpl.getCurrentPreviewFps();
    }

    @Override
    public void setPreviewFps(int minFps, int maxFps) {
        cameraImpl.setPreviewFps(minFps, maxFps);
    }

    @Override
    public int getOrientation(int cameraId) {
        return cameraImpl.getOrientation(cameraId);
    }

//    @Override
//    public void setPreviewOrientation(int degree) {
//        cameraImpl.setPreviewOrientation(degree);
//    }


    @Override
    public boolean open(boolean isFront) {
        return cameraImpl.open(isFront);
    }

    @Override
    public void setPreviewSize(Size size) {
        cameraImpl.setPreviewSize(size);
    }

    @Override
    public void setConfig(CameraConfig cameraConfig) {
        cameraImpl.setConfig(cameraConfig);
    }

    @Override
    public boolean preview(SurfaceTexture surface) {
        return cameraImpl.preview(surface);
    }

    @Override
    public boolean preview(SurfaceHolder holder) {
        return cameraImpl.preview(holder);
    }

    @Override
    public boolean switchCamera() {
        return cameraImpl.switchCamera();
    }

    @Override
    public void takePicture(File picFile) {
        cameraImpl.takePicture(picFile);
    }

    @Override
    public void release() {
        cameraImpl.release();
    }
}
