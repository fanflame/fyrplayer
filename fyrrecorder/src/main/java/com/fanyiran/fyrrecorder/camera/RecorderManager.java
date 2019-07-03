package com.fanyiran.fyrrecorder.camera;

import com.fanyiran.fyrrecorder.camera.factory.Camera2Factory;
import com.fanyiran.fyrrecorder.camera.factory.ICameraFactory;
import com.fanyiran.fyrrecorder.recorder.IRecorder;
import com.fanyiran.fyrrecorder.recorder.factory.IRecorderFactory;
import com.fanyiran.fyrrecorder.recorder.factory.MediaRecorderFactory;

public class RecorderManager {
    private static final RecorderManager ourInstance = new RecorderManager();

    public static RecorderManager getInstance() {
        return ourInstance;
    }

    public ICameraFactory cameraFactory;
    public IRecorderFactory iRecorderFactory;


    private RecorderManager() {
    }

    public void setCameraFactory(ICameraFactory cameraFactory) {
        this.cameraFactory = cameraFactory;
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
    public ICamera createCamera(CameraConfig cameraConfig,Class previewClass) {
        return getCameraFactory().createCamera(cameraConfig,getiRecorderFactory().getRecorderClass(),
                previewClass);
    }

    public IRecorder createRecorder() {
        return getiRecorderFactory().createRecorder();
    }

    private ICameraFactory getCameraFactory() {
        if (cameraFactory == null) {
            cameraFactory = new Camera2Factory();
        }
        return cameraFactory;
    }

    private IRecorderFactory getiRecorderFactory() {
        if (iRecorderFactory == null) {
            iRecorderFactory = new MediaRecorderFactory();
        }
        return iRecorderFactory;
    }
}
