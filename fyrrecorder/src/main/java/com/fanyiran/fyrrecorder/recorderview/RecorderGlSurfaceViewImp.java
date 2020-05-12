package com.fanyiran.fyrrecorder.recorderview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.fanyiran.fcamera.camera.CameraConfig;
import com.fanyiran.fcamera.camera.ICamera;
import com.fanyiran.fcamera.camera.callback.OnTakePicCallBack;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//import com.fanyiran.fcamera.camera.RecorderManager;

public class RecorderGlSurfaceViewImp extends GLSurfaceView implements IRecorderView {
    private static final String TAG = "RecorderTextureViewImpl";
    private ICamera camera;
    private CameraConfig cameraConfig;
    private GlRender glRender;
    private int textureId = -1;
    private SurfaceTexture surfaceTexture;
    private DirectDrawer directDrawer;

    private class GlRender implements Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            textureId = createTextureID();
            surfaceTexture = new SurfaceTexture(textureId);
            surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    RecorderGlSurfaceViewImp.this.requestRender();
                }
            });
            directDrawer = new DirectDrawer(textureId);

//            cameraConfig.addSurfaceHolder(new Surface(surfaceTexture));
//            camera = RecorderManager.getInstance().createCamera(cameraConfig,SurfaceTexture.class);
//            camera.preview();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0,0,width,height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClearColor(1f,1f,1f,1f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            surfaceTexture.updateTexImage();
            float[] mtx = new float[16];
            surfaceTexture.getTransformMatrix(mtx);
            directDrawer.draw(mtx);
        }
    }

    private int createTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1,texture,0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GL10.GL_TEXTURE_WRAP_S,GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GL10.GL_TEXTURE_WRAP_T,GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    public RecorderGlSurfaceViewImp(Context context) {
        super(context);
        init();
    }

    public RecorderGlSurfaceViewImp(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        glRender = new GlRender();
        setRenderer(glRender);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
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
        camera.switchCamera();
    }

    @Override
    public void release() {
        camera.release();
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
