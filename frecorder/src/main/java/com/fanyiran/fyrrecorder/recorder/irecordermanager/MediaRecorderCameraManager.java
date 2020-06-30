package com.fanyiran.fyrrecorder.recorder.irecordermanager;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Environment;
import android.view.SurfaceHolder;

import com.fanyiran.fcamera.camera.CameraImpl;
import com.fanyiran.fcamera.camera.ICamera;
import com.fanyiran.fyrrecorder.recorder.IRecorder;
import com.fanyiran.fyrrecorder.recorder.RecorderConfig;
import com.fanyiran.fyrrecorder.recorder.mediarecorder.MediaRecorderImpl;

import java.io.File;

/**
 * 使用camera，MediaRecorder
 */
public class MediaRecorderCameraManager extends IRecorderManagerAbstract {
    @Override
    protected ICamera createCamera() {
        return new CameraImpl();
    }

    @Override
    protected IRecorder createRecorder() {
        return new MediaRecorderImpl();
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
        if (object instanceof SurfaceHolder) {
            camera.preview((SurfaceHolder) object);
        } else if (object instanceof SurfaceTexture) {
            camera.preview((SurfaceTexture) object);
        }
    }

    @Override
    public void startRecord() {
        super.startRecord();
    }
}
