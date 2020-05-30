package com.fanyiran.fyrrecorder.recorderview;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.fanyiran.fcamera.camera.CameraConfig;
import com.fanyiran.fcamera.camera.callback.OnTakePicCallBack;
import com.fanyiran.fyrrecorder.recorder.irecordermanager.IRecorderManager;
import com.fanyiran.fyrrecorder.recorder.irecordermanager.MediaCodecCameraManager;
import com.fanyiran.fyrrecorder.recorderview.opengl.GLRender;

import java.io.File;

//import com.fanyiran.fcamera.camera.RecorderManager;

public class RecorderGlSurfaceViewImpl extends GLSurfaceView implements IRecorderView {
    private static final String TAG = "RecorderGlSurfaceViewImp";
    private GLRender glRender;
    private CameraConfig cameraConfig;
    private IRecorderManager recorderManager;

    public RecorderGlSurfaceViewImpl(Context context) {
        super(context);
        init();
    }

    public RecorderGlSurfaceViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        recorderManager = new MediaCodecCameraManager();
        recorderManager.init((Activity) getContext());
        setEGLContextClientVersion(2);
        glRender = new GLRender(new GLRender.OnSurfaceChangeListner() {

            @Override
            public void onSurfaceCreated() {
                recorderManager.openCamera(true, cameraConfig);
            }

            @Override
            public void onSurfaceChanged() {
                startPreview();
            }

            @Override
            public void onDrawFrame() {

            }

            @Override
            public void onFrameAvailable() {
                RecorderGlSurfaceViewImpl.this.requestRender();
            }
        });
        setRenderer(glRender);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public int getOrientation(int cameraId) {
        return 0;
    }

    @Override
    public int getCameraCount(int orientation) {
        return 0;
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
    public void startRecord() {
        recorderManager.startRecord();
    }

    @Override
    public void startPreview() {
        recorderManager.startPreview(glRender.getSurfaceTexture());
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
        return 0;
    }

    @Override
    public void takePicture(File file, OnTakePicCallBack onTakePicCallBack) {

    }
}
