package com.fanyiran.fyrrecorder.camera.factory;

import com.fanyiran.fyrrecorder.camera.CameraConfig;
import com.fanyiran.fyrrecorder.camera.ICamera;

public interface ICameraFactory {
    ICamera createCamera(CameraConfig config,Class recorderClass,Class previewClass);
}
