package com.fanyiran.fyrrecorder.recorderview.opengl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRender implements GLSurfaceView.Renderer {
    //    private Triangle triangle;
    private float[] projectionMatrix = new float[16];
    private float[] vPMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private int textureId;
    private OnSurfaceChangeListner onSurfaceChangeListner;
    private DirectDrawer directDrawer;
    private SurfaceTexture surfaceTexture;

    public GLRender(OnSurfaceChangeListner onSurfaceChangeListner) {
        this.onSurfaceChangeListner = onSurfaceChangeListner;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (onSurfaceChangeListner != null) {
            onSurfaceChangeListner.onSurfaceCreated();
        }
//        triangle = new Triangle();
        textureId = GLUtils.generateTexure();
        directDrawer = new DirectDrawer(textureId);
        GLES20.glClearColor(0.001f, 1, 1, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (onSurfaceChangeListner != null) {
            onSurfaceChangeListner.onSurfaceChanged();
        }
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -ratio, ratio, 3, 70);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (onSurfaceChangeListner != null) {
            onSurfaceChangeListner.onDrawFrame();
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
//
//        directDrawer.draw(vPMatrix);
        float[] mtx = new float[16];
        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(mtx);
        directDrawer.draw(vPMatrix);
    }

    public int getTextureId() {
        return textureId;
    }

    public SurfaceTexture getSurfaceTexture() {
        if (surfaceTexture == null) {
            int textureId = getTextureId();
            surfaceTexture = new SurfaceTexture(textureId);
            surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    if (onSurfaceChangeListner != null) {
                        onSurfaceChangeListner.onFrameAvailable();
                    }
                }
            });
        }
        return surfaceTexture;
    }

    public interface OnSurfaceChangeListner {
        void onSurfaceCreated();

        void onSurfaceChanged();

        void onDrawFrame();

        void onFrameAvailable();
    }
}
