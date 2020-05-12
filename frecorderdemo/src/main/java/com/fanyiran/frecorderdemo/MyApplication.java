package com.fanyiran.frecorderdemo;

import android.app.Application;

//import com.fanyiran.fcamera.camera.RecorderManager;
//import com.fanyiran.fcamera.camera.factory.Camera2Factory;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        RecorderManager.getInstance().setCameraFactory(new Camera2Factory());
//        RecorderManager.getInstance().setiRecorderFactory(new MediaCodecFactory());
    }
}
