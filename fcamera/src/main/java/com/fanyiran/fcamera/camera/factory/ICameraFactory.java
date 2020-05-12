package com.fanyiran.fcamera.camera.factory;

import com.fanyiran.fcamera.camera.CameraConfig;
import com.fanyiran.fcamera.camera.ICamera;

public interface ICameraFactory {
    ICamera createCamera(CameraConfig config,Class recorderClass,Class previewClass);
}
