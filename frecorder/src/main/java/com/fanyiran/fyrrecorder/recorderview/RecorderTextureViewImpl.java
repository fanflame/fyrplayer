package com.fanyiran.fyrrecorder.recorderview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;

import com.fanyiran.fcamera.camera.CameraConfig;
import com.fanyiran.fcamera.camera.callback.OnTakePicCallBack;

import java.io.File;
//import com.fanyiran.fcamera.camera.RecorderManager;

public class RecorderTextureViewImpl extends TextureView implements IRecorderView {
    private static final String TAG = "RecorderTextureViewImpl";
    private CameraConfig cameraConfig;

    public RecorderTextureViewImpl(Context context) {
        super(context);
        init();
    }

    public RecorderTextureViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
//        CameraManager.getInstance().init((Activity) getContext());
//        CameraManager.getInstance().open(true);
//        CameraManager.getInstance().setPreviewOrientation(10);
        // TODO: 2019-07-03 需要开启硬件加速？
        setSurfaceTextureListener(new SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                // TODO: 2019-07-03 surface这么构造出来，什么原理？
//                cameraConfig.addSurfaceHolder(new Surface(getSurfaceTexture()));
//                camera = RecorderManager.getInstance().createCamera(cameraConfig,SurfaceTexture.class);
//                CameraManager.getInstance().preview(getSurfaceTexture());
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                // TODO: 2019-07-03 大小判断
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
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
//        CameraManager.getInstance().switchCamera();
    }

    @Override
    public void release() {
//        CameraManager.getInstance().release();
    }

    @Override
    public void startRecord() {
//        camera.startRecord();
    }

    @Override
    public void startPreview() {

    }

    @Override
    public void pauseRecord() {
//        camera.pauseRecord();
    }

    @Override
    public void resumeRecord() {
//        camera.resumeRecord();
    }

    @Override
    public void stopRecord() {
//        camera.stopRecord();
    }

    @Override
    public int getPreviewFps() {
//        return camera.getPreviewFps();
        return 0;
    }

    @Override
    public void takePicture(File file, OnTakePicCallBack onTakePicCallBack) {

    }

}
