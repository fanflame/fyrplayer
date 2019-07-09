package com.fanyiran.mediaplayer.fyrplayer.factory;

import com.fanyiran.mediaplayer.fyrplayer.IFyrPlayer;
import com.fanyiran.mediaplayer.fyrplayer.codec.CodecPlayerSync;

public class FyrplayerFactory implements iFyrplayerFactory{

    @Override
    public IFyrPlayer createFryPlayer() {
        return new CodecPlayerSync();
    }
}
