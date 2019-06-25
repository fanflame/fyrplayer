package com.fanyiran.mediaplayer.fyrplayer;

import com.fanyiran.mediaplayer.fyrplayer.factory.FyrplayerFactory;
import com.fanyiran.mediaplayer.fyrplayer.factory.iFyrplayerFactory;
import com.fanyiran.utils.LogUtil;

public class PlayerManager {
    private static final PlayerManager ourInstance = new PlayerManager();
    private static final String TAG = "PlayerManager";

    private iFyrplayerFactory iFyrplayerFactory;
    private FyrPlayer fyrPlayer;
    public static PlayerManager getInstance() {
        return ourInstance;
    }

    private PlayerManager() {
    }

    public void initPlayerFactory(iFyrplayerFactory playter) {
        this.iFyrplayerFactory = playter;
    }

    public void createFyrPlayer() {
        if (iFyrplayerFactory == null) {
            iFyrplayerFactory = new FyrplayerFactory();
        }
        fyrPlayer = iFyrplayerFactory.createFryPlayer();
    }

    public void setPlayerConfig(PlayerConfig config) {
        fyrPlayer.setConfig(config);
    }

    public boolean play() {
        boolean play = fyrPlayer.play();
        LogUtil.v(TAG,"play result:"+play);
        return play;
    }

    public boolean isPlaying() {
        return fyrPlayer.isPlaying();
    }

    public void resume() {
        fyrPlayer.resume();
    }

    public void pause() {
        fyrPlayer.pause();
    }

    public void release() {
        fyrPlayer.release();
    }
}
