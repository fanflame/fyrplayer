package com.fanyiran.fyrrecorder.recorderview;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Rational;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fanyiran.fyrrecorder.camera.CameraConfig;
import com.fanyiran.fyrrecorder.camera.ICamera;
import com.fanyiran.fyrrecorder.camera.RecorderManager;
import com.fanyiran.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;

public class RecorderSurfaceViewImpl extends SurfaceView implements IRecorderView {
    private static final String TAG = "RecorderSurfaceViewImpl";
    private ICamera camera;
    private CameraConfig cameraConfig;

    public RecorderSurfaceViewImpl(Context context) {
        super(context);
        init();
    }

    public RecorderSurfaceViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        getHolder().addCallback(holderCallBack);
    }

    private SurfaceHolder.Callback holderCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtil.v(TAG, "surfaceCreated");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtil.v(TAG, "surfaceChanged");
            //todo 修改大小
            cameraConfig.addSurfaceHolder(getHolder().getSurface());
            camera = RecorderManager.getInstance().createCamera(cameraConfig,SurfaceHolder.class);
            camera.preview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtil.v(TAG, "surfaceDestroyed");
            if (camera != null) {
                camera.release();
            }
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

    @Override
    public int getPreviewFps() {
        if (camera == null) {
            return 0;
        }
        return camera.getPreviewFps();
    }
}
