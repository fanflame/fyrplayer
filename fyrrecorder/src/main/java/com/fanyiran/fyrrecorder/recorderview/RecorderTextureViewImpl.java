package com.fanyiran.fyrrecorder.recorderview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.fanyiran.fyrrecorder.camera.CameraConfig;
import com.fanyiran.fyrrecorder.camera.ICamera;
import com.fanyiran.fyrrecorder.camera.RecorderManager;

public class RecorderTextureViewImpl extends TextureView implements IRecorderView {
    private static final String TAG = "RecorderTextureViewImpl";
    private ICamera camera;
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
        setSurfaceTextureListener(new SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                cameraConfig.addSurfaceHolder(new Surface(getSurfaceTexture()));
                camera = RecorderManager.getInstance().createCamera(cameraConfig);
                camera.preview();
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
    public void autoPreview(CameraConfig cameraConfig) {
        if (cameraConfig == null) {
            throw new IllegalArgumentException("config can't be null");
        }
        this.cameraConfig = cameraConfig;
    }

    @Override
    public void switchCamera() {
        camera.switchCamera();
    }

    @Override
    public void release() {
        camera.release();
    }

    @Override
    public void startRecord() {
        camera.startRecord();
    }

    @Override
    public void pauseRecord() {
        camera.pauseRecord();
    }

    @Override
    public void resumeRecord() {
        camera.resumeRecord();
    }

    @Override
    public void stopRecord() {
        camera.stopRecord();
    }
}
