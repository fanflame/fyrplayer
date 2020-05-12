package com.fanyiran.fyrrecorder.recorder;

//import com.fanyiran.fcamera.camera.factory.Camera2Factory;

import com.fanyiran.fcamera.camera.CameraConfig;
import com.fanyiran.fcamera.camera.CameraManager;
import com.fanyiran.fyrrecorder.recorder.factory.IRecorderFactory;
import com.fanyiran.fyrrecorder.recorder.factory.MediaRecorderFactory;

public class RecorderManager {
    private static final RecorderManager ourInstance = new RecorderManager();

    public static RecorderManager getInstance() {
        return ourInstance;
    }

    public IRecorderFactory iRecorderFactory;


    private RecorderManager() {
    }


    public void setiRecorderFactory(IRecorderFactory iRecorderFactory) {
        this.iRecorderFactory = iRecorderFactory;
    }


    /**
     * @param cameraConfig
     * @param previewClass previewClass如何传参：
     *                     GLSurfaceView使用SurfaceTexture.class
     *                     TextureView使用SurfaceTexture.class
     *                     SurfaceView使用SurfaceHolder.class
     *
     * @return
     */

    // TODO: 2019-07-03  previewClass的检测使用注解 | 编译时检测
    public void createCamera(CameraConfig cameraConfig, Class previewClass) {
        CameraManager.getInstance().init(null);
    }

    public IRecorder createRecorder() {
        return getiRecorderFactory().createRecorder();
    }

    private IRecorderFactory getiRecorderFactory() {
        if (iRecorderFactory == null) {
            iRecorderFactory = new MediaRecorderFactory();
        }
        return iRecorderFactory;
    }
}
