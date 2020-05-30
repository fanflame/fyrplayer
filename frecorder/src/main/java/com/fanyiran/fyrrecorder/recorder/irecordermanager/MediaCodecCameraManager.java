package com.fanyiran.fyrrecorder.recorder.irecordermanager;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Environment;

import com.fanyiran.fcamera.camera.CameraImpl;
import com.fanyiran.fcamera.camera.ICamera;
import com.fanyiran.fyrrecorder.recorder.IRecorder;
import com.fanyiran.fyrrecorder.recorder.RecorderConfig;
import com.fanyiran.fyrrecorder.recorder.mediacodec.MediaCodecRecorderImpl;

import java.io.File;

public class MediaCodecCameraManager extends IRecorderManagerAbstract {
    @Override
    protected ICamera createCamera() {
        return new CameraImpl();
    }

    @Override
    protected IRecorder createRecorder() {
        return new MediaCodecRecorderImpl();
    }

    @Override
    public void startRecord() {
        RecorderConfig recorderConfig = createRecorderConfig(activityWeakReference.get());
        recorder.init(recorderConfig);
        recorder.getSurface();
        recorder.startRecord();
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
}
