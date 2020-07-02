package com.fanyiran.fyrrecorder.recorderview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fanyiran.fcamera.camera.CameraConfig;
import com.fanyiran.fcamera.camera.callback.OnTakePicCallBack;
import com.fanyiran.fyrrecorder.recorder.irecordermanager.IRecorderManager;
import com.fanyiran.fyrrecorder.recorder.irecordermanager.MediaRecorderCameraManager;
import com.fanyiran.utils.LogUtil;

import java.io.File;

public class RecorderSurfaceViewImpl extends SurfaceView implements IRecorderView {
    private static final String TAG = "RecorderSurfaceViewImpl";
    protected CameraConfig cameraConfig;
    protected IRecorderManager recorderManager;
    private boolean initCamera;

    public RecorderSurfaceViewImpl(Context context) {
        super(context);
        init();
    }

    public RecorderSurfaceViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        recorderManager = genRecorderManager();
        recorderManager.init((Activity) getContext());
        getHolder().addCallback(holderCallBack);
    }

    protected IRecorderManager genRecorderManager() {
        return new MediaRecorderCameraManager();
    }

    private SurfaceHolder.Callback holderCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtil.v(TAG, "surfaceCreated");
            recorderManager.openCamera(true, cameraConfig);
            initCamera = true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtil.v(TAG, "surfaceChanged");
            //todo 修改大小
//            cameraConfig.addSurfaceHolder();
//            cameraConfig.getRecorderConfig().surface = getHolder().getSurface();
//            CameraManager.getInstance().preview(getHolder().getSurface());
            startPreviewInner();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtil.v(TAG, "surfaceDestroyed");
            recorderManager.stopPreview();
            recorderManager.release();
            initCamera = false;
        }
    };

    protected void startPreviewInner() {
        recorderManager.startPreview(getHolder());
    }

    @Override
    public void autoPreview(CameraConfig cameraConfig) {
        if (cameraConfig == null) {
            throw new IllegalArgumentException("config can't be null");
        }
        this.cameraConfig = cameraConfig;
    }

    @Override
    public void switchCamera() {
        recorderManager.switchCamera();
    }

    @Override
    public void release() {
        recorderManager.release();
    }

    @Override
    public void startRecord(File file) {
        recorderManager.startRecord();
    }

    @Override
    public void startPreview() {
        startPreviewInner();
    }

    @Override
    public void pauseRecord() {
//        recorderManager.pauseRecord();
    }

    @Override
    public void resumeRecord() {
//        camera.resumeRecord();
    }

    @Override
    public void stopRecord() {
        recorderManager.stopRecord();
    }

    @Override
    public int getPreviewFps() {
        return initCamera ? recorderManager.getCurrentPreviewFps() : 0;
    }

    @Override
    public void takePicture(File file, OnTakePicCallBack callBack) {
        if (initCamera) {
            recorderManager.takePicture(file, callBack);
        }
    }

    @Override
    public int getOrientation(int cameraId) {
        return recorderManager.getOrientation(cameraId);
    }

    @Override
    public int getCameraCount(int orientation) {
        return initCamera ? recorderManager.getCameraCount(orientation) : 0;
    }
}
