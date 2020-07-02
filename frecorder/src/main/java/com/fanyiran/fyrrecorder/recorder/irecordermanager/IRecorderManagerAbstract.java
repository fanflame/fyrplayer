package com.fanyiran.fyrrecorder.recorder.irecordermanager;

import android.app.Activity;

import com.fanyiran.fcamera.camera.CameraConfig;
import com.fanyiran.fcamera.camera.ICamera;
import com.fanyiran.fcamera.camera.callback.OnTakePicCallBack;
import com.fanyiran.fyrrecorder.recorder.IRecorder;
import com.fanyiran.fyrrecorder.recorder.RecorderConfig;
import com.fanyiran.fyrrecorder.recorderview.callback.SetOnFrameAvailable;

import java.io.File;
import java.lang.ref.WeakReference;

public abstract class IRecorderManagerAbstract implements IRecorderManager {
    protected ICamera camera;
    protected int openCameraId;
    protected IRecorder recorder;
    protected WeakReference<Activity> activityWeakReference;

    protected abstract ICamera createCamera();

    protected abstract IRecorder createRecorder();

    protected abstract RecorderConfig createRecorderConfig(Activity activity);

    @Override
    public void init(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
        camera = createCamera();
        camera.init(activity);
        recorder = createRecorder();
    }

    @Override
    public void openCamera(boolean front, CameraConfig cameraConfig) {
        openCameraId = camera.open(front);
        camera.setPreviewSize(cameraConfig.getPreviewSize());
        camera.setPreviewFps(cameraConfig.getPreviewMinFps(), cameraConfig.getPreviewMaxFps());
    }

    @Override
    public void stopPreview() {
        camera.stopPreview();
    }

    @Override
    public void takePicture(File picFile, OnTakePicCallBack onTakePicCallBack) {
        camera.takePicture(picFile, onTakePicCallBack);
    }

    @Override
    public void startRecord() {
        recorder.init(createRecorderConfig(activityWeakReference.get()));
        recorder.startRecord();
    }

    @Override
    public void stopRecord() {
        recorder.stopRecord();
    }

    @Override
    public int getCameraCount(int orientation) {
        return camera.getCameraCount(orientation);
    }

    @Override
    public int getOrientation(int cameraId) {
        return camera.getOrientation(cameraId);
    }

    @Override
    public int getCurrentPreviewFps() {
        return camera.getCurrentPreviewFps();
    }

    @Override
    public void switchCamera() {
        camera.switchCamera();
    }

    @Override
    public void release() {
        recorder.release();
        camera.release();
    }

    @Override
    public void startRecord(SetOnFrameAvailable onFrameAvailable, File file) {

    }
}
