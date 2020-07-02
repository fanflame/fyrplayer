package com.fanyiran.fyrrecorder.recorderview;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;

import com.fanyiran.fcamera.camera.CameraConfig;
import com.fanyiran.fcamera.camera.callback.OnTakePicCallBack;
import com.fanyiran.fyrrecorder.recorder.irecordermanager.IRecorderManager;
import com.fanyiran.fyrrecorder.recorder.irecordermanager.MediaRecorderCameraManager;
import com.fanyiran.utils.LogUtil;

import java.io.File;

public class RecorderTextureViewImpl extends TextureView implements IRecorderView {
    private static final String TAG = "RecorderTextureViewImpl";
    private CameraConfig cameraConfig;
    private IRecorderManager recorderManager;
    private boolean initCamera;

    public RecorderTextureViewImpl(Context context) {
        super(context);
        init();
    }

    public RecorderTextureViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        recorderManager = new MediaRecorderCameraManager();
        recorderManager.init((Activity) getContext());
        setSurfaceTextureListener(surfaceTextureListener);
    }

    private SurfaceTextureListener surfaceTextureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            LogUtil.v(TAG, "onSurfaceTextureAvailable");
            recorderManager.openCamera(true, cameraConfig);
            recorderManager.startPreview(getSurfaceTexture());
            initCamera = true;
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            LogUtil.v(TAG, "surfaceChanged");
            //todo 修改大小
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            LogUtil.v(TAG, "surfaceDestroyed");
            recorderManager.stopPreview();
            recorderManager.release();
            initCamera = false;
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            LogUtil.v(TAG, "onSurfaceTextureUpdated");
        }
    };

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
        recorderManager.startPreview(getSurfaceTexture());
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
