package com.fanyiran.fyrrecorder.recorder.irecordermanager;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.opengl.EGLSurface;
import android.opengl.GLES10;
import android.view.Surface;

import com.fanyiran.fcamera.camera.CameraImpl;
import com.fanyiran.fcamera.camera.ICamera;
import com.fanyiran.fyrrecorder.recorder.IRecorder;
import com.fanyiran.fyrrecorder.recorder.RecorderConfig;
import com.fanyiran.fyrrecorder.recorder.mediacodec.MediaCodecRecorderImpl;
import com.fanyiran.fyrrecorder.recorderview.callback.OnFrameAvailableCallBack;
import com.fanyiran.fyrrecorder.recorderview.callback.SetOnFrameAvailable;
import com.fanyiran.fyrrecorder.recorderview.opengl.DirectDrawer;
import com.fanyiran.fyrrecorder.recorderview.opengl.EGLHelper;

import java.io.File;

/**
 * 使用camera、codec,
 */
public class MediaCodecCameraManager extends IRecorderManagerAbstract {
    private EGLSurface eglSurface;
    private boolean isRecording;
    private RecorderConfig recorderConfig;

    @Override
    protected ICamera createCamera() {
        return new CameraImpl();
    }

    @Override
    protected IRecorder createRecorder() {
        return new MediaCodecRecorderImpl();
    }

    @Override
    public void startRecord(SetOnFrameAvailable onFrameAvailable, File file) {
        RecorderConfig recorderConfig = createRecorderConfig(activityWeakReference.get());
        recorderConfig.outputFile = file;
        recorder.init(recorderConfig);
        initEgl(recorder.getSurface());
        recorder.startRecord();
        isRecording = true;
        if (onFrameAvailable != null) {
            onFrameAvailable.setOnFrameAvailableCallBack(new OnFrameAvailableCallBack() {
                @Override
                public void onFrameAvaiable(DirectDrawer directDrawer, float[] mtx, int x, int y, int width, int height, long time) {
                    if (isRecording) {
                        EGLHelper.getInstance().makeCurrent(eglSurface);
                        GLES10.glViewport(0, 0, width, height);
                        directDrawer.draw(mtx);
                        // TODO: 2020/7/1 下边这行不影响用android自带播放器播放
//                        EGLHelper.getInstance().setPresentationTime(eglSurface, time);
                        EGLHelper.getInstance().swapBuffers(eglSurface);
                    }
                }
            });
        }
    }

    @Override
    protected RecorderConfig createRecorderConfig(Activity activity) {
        recorderConfig = new RecorderConfig();
        recorderConfig.camera = ((CameraImpl) camera).getCurrentCamera();
        recorderConfig.orientation = camera.getOrientation(openCameraId);
        return recorderConfig;
    }

    @Override
    public void startPreview(Object object) {
        if (object instanceof SurfaceTexture) {
            camera.preview((SurfaceTexture) object);
        } else {
            throw new IllegalArgumentException("object must be SurfaceTexture?");
        }
    }

    @Override
    public void stopRecord() {
        isRecording = false;
        super.stopRecord();
    }

    private void initEgl(Surface surface) {
        eglSurface = EGLHelper.getInstance().genEglSurface(surface);
        if (eglSurface == null) {
            throw new IllegalStateException("eglSurface can't be null");
        }
    }
}
