package com.fanyiran.fcamera.camera.factory;

import com.fanyiran.fcamera.camera.CameraConfig;
import com.fanyiran.fcamera.camera.CameraImpl;
import com.fanyiran.fcamera.camera.ICamera;

public class CameraFactory implements ICameraFactory {
    @Override
    public ICamera createCamera(CameraConfig config, Class recorderClass, Class previewClass) {
        return new CameraImpl();
    }
}
