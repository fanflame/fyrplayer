package com.fanyiran.fyrrecorder.recorderview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.util.AttributeSet;

import com.fanyiran.fyrrecorder.recorderview.callback.OnFrameAvailableCallBack;
import com.fanyiran.fyrrecorder.recorderview.callback.SetOnFrameAvailable;
import com.fanyiran.fyrrecorder.recorderview.opengl.DirectDrawer;
import com.fanyiran.fyrrecorder.recorderview.opengl.EGLHelper;
import com.fanyiran.fyrrecorder.recorderview.opengl.GLUtils;
import com.fanyiran.utils.LogUtil;

public class RecorderCodecSurfaceViewImpl extends RecorderSurfaceViewImpl implements SetOnFrameAvailable {
    // Android-specific extension.
    private static final int EGL_RECORDABLE_ANDROID = 0x3142;

    private static final String TAG = "RecorderCodecSurfaceViewImpl";
    private SurfaceTexture surfaceTexture;
    private DirectDrawer directDrawer;
    private float[] mtx;
    private EGLSurface eglSurface;
    private OnFrameAvailableCallBack onFrameAvailableCallBack;

    public RecorderCodecSurfaceViewImpl(Context context) {
        super(context);
    }

    public RecorderCodecSurfaceViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    protected IRecorderManager genRecorderManager() {
//        return new MediaCodecCameraManager();
//    }

    @Override
    protected void startPreviewInner() {
        final int textureId = GLUtils.generateTexure();
        surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                if (directDrawer == null) {
                    directDrawer = new DirectDrawer(textureId);
                    mtx = new float[16];
                }
                EGLHelper.getInstance().makeCurrent(eglSurface);
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(mtx);
                GLES20.glViewport(0, 0, getWidth(), getHeight());
                directDrawer.draw(mtx);
                if (!EGLHelper.getInstance().swapBuffers(eglSurface)) {
                    LogUtil.v(TAG, "swapBuffers failed");
                }

//                if (onFrameAvailableCallBack != null) {
//                    onFrameAvailableCallBack.onFrameAvaiable(directDrawer,mtx,100,100,200,200);
//                }

            }
        });
        initEGL();
        recorderManager.startPreview(surfaceTexture);
    }

    private void initEGL() {
        eglSurface = EGLHelper.getInstance().genEglSurface(getHolder());
        // TODO: 2020/6/30 在这makecurrent 才会在surfaceview中显示图像
        EGLHelper.getInstance().makeCurrent(eglSurface);
    }

    @Override
    public void startRecord() {
        recorderManager.startRecord(this);
    }

    public void setOnFrameAvailableCallBack(OnFrameAvailableCallBack onFrameAvailableCallBack) {
        this.onFrameAvailableCallBack = onFrameAvailableCallBack;
    }

}
