package com.fanyiran.mediaplayer;

import android.app.Application;

import com.fanyiran.mediaplayer.fyrplayer.PlayerManager;
import com.fanyiran.mediaplayer.fyrplayer.factory.FyrplayerFactory;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PlayerManager.getInstance().initPlayerFactory(new FyrplayerFactory());
    }
}
