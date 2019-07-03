package com.fanyiran.fyrrecorder.camera.factory;

import com.fanyiran.fyrrecorder.camera.CameraConfig;
import com.fanyiran.fyrrecorder.camera.Camera2Impl;
import com.fanyiran.fyrrecorder.camera.ICamera;

public class Camera2Factory implements ICameraFactory {

    @Override
    public ICamera createCamera(CameraConfig config,Class recorderClass,Class previewClass) {
        config.setRecorderClass(recorderClass);
        config.setPreviewClass(previewClass);

        Camera2Impl cameraX = new Camera2Impl();
        cameraX.setConfig(config);
        return cameraX;
    }
}
