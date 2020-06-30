package com.fanyiran.fyrrecorder.recorder.irecordermanager;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.opengl.EGLSurface;
import android.os.Environment;
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

    @Override
    protected ICamera createCamera() {
        return new CameraImpl();
    }

    @Override
    protected IRecorder createRecorder() {
        return new MediaCodecRecorderImpl();
    }

    @Override
    public void startRecord(SetOnFrameAvailable onFrameAvailable) {
        RecorderConfig recorderConfig = createRecorderConfig(activityWeakReference.get());
        recorder.init(recorderConfig);
        initEgl(recorder.getSurface());
        recorder.startRecord();
        if (onFrameAvailable != null) {
            onFrameAvailable.setOnFrameAvailableCallBack(new OnFrameAvailableCallBack() {
                @Override
                public void onFrameAvaiable(DirectDrawer directDrawer, float[] mtx, int x, int y, int width, int height) {
//                    EGLHelper.getInstance().makeCurrent(eglSurface);
//                    GLES10.glViewport(0,0,width,height);
//                    directDrawer.draw(mtx);
//                    EGLHelper.getInstance().swapBuffers(eglSurface);
                }
            });
        }
    }

    @Override
    protected RecorderConfig createRecorderConfig(Activity activity) {
        RecorderConfig recorderConfig = new RecorderConfig();
        recorderConfig.outputFile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                , String.format("%d.mp4", System.currentTimeMillis()));
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

    private void initEgl(Surface surface) {
        eglSurface = EGLHelper.getInstance().genEglSurface(surface);
    }
}
