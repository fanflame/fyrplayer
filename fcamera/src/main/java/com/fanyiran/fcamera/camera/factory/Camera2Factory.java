//package com.fanyiran.fcamera.camera.factory;
//
//import com.fanyiran.fcamera.camera.CameraConfig;
//import com.fanyiran.fcamera.camera.Camera2Impl;
//import com.fanyiran.fcamera.camera.ICamera;
//
//public class Camera2Factory implements ICameraFactory {
//
//    @Override
//    public ICamera createCamera(CameraConfig config,Class recorderClass,Class previewClass) {
//        config.setRecorderClass(recorderClass);
//        config.setPreviewClass(previewClass);
//
//        Camera2Impl camera2 = new Camera2Impl();
//        camera2.setConfig(config);
//        return camera2;
//    }
//}
