package com.fanyiran.fyrrecorder.recorderview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fanyiran.fcamera.camera.CameraConfig;
import com.fanyiran.fcamera.camera.CameraManager;
import com.fanyiran.utils.LogUtil;

public class RecorderSurfaceViewImpl extends SurfaceView implements IRecorderView {
    private static final String TAG = "RecorderSurfaceViewImpl";
    private CameraConfig cameraConfig;
    private boolean init;

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
            CameraManager.getInstance().init((Activity) getContext());
            CameraManager.getInstance().open(true);
            CameraManager.getInstance().setPreviewSize(cameraConfig.getPreviewSize());
//            CameraManager.getInstance().setPreviewOrientation(100);
            init = true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtil.v(TAG, "surfaceChanged");
            //todo 修改大小
//            cameraConfig.addSurfaceHolder();
//            cameraConfig.getRecorderConfig().surface = getHolder().getSurface();
//            CameraManager.getInstance().preview(getHolder().getSurface());
            CameraManager.getInstance().preview(getHolder());
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtil.v(TAG, "surfaceDestroyed");
            CameraManager.getInstance().release();
            init = false;
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
        CameraManager.getInstance().switchCamera();
    }

    @Override
    public void release() {
        CameraManager.getInstance().release();
    }

    @Override
    public void startRecord() {
//        CameraManager.getInstance().startRecord();
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
//        if (camera == null) {
            return 0;
//        }
//        return camera.getPreviewFps();
    }

    @Override
    public int getOrientation(int cameraId) {
        return CameraManager.getInstance().getOrientation(cameraId);
    }

    @Override
    public int getCameraCount(int orientation) {
        return init ? CameraManager.getInstance().getCameraCount(orientation) : 0;
    }
}
