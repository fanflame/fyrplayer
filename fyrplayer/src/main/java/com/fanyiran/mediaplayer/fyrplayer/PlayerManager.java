package com.fanyiran.mediaplayer.fyrplayer;

import com.fanyiran.mediaplayer.fyrplayer.factory.FyrplayerFactory;
import com.fanyiran.mediaplayer.fyrplayer.factory.iFyrplayerFactory;
import com.fanyiran.utils.LogUtil;

public class PlayerManager {
    private static final PlayerManager ourInstance = new PlayerManager();
    private static final String TAG = "PlayerManager";

    private iFyrplayerFactory iFyrplayerFactory;
    private IFyrPlayer IFyrPlayer;
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
        IFyrPlayer = iFyrplayerFactory.createFryPlayer();
    }

    public void setPlayerConfig(PlayerConfig config) {
        IFyrPlayer.setConfig(config);
    }

    public boolean play() {
        boolean play = IFyrPlayer.play();
        LogUtil.v(TAG,"play result:"+play);
        return play;
    }

    public boolean isPlaying() {
        return IFyrPlayer.isPlaying();
    }

    public void resume() {
        IFyrPlayer.resume();
    }

    public void pause() {
        IFyrPlayer.pause();
    }

    public void release() {
        IFyrPlayer.release();
    }
}
