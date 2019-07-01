package com.fanyiran.fyrrecorder;

import android.app.Application;

import com.fanyiran.fyrrecorder.camera.RecorderManager;
import com.fanyiran.fyrrecorder.camera.factory.Camera2Factory;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RecorderManager.getInstance().setCameraFactory(new Camera2Factory());
    }
}
