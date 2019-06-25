package com.fanyiran.mediaplayer.fyrplayer.factory;

import com.fanyiran.mediaplayer.fyrplayer.FyrPlayer;
import com.fanyiran.mediaplayer.fyrplayer.codec.CodecPlayerAsync;
import com.fanyiran.mediaplayer.fyrplayer.codec.CodecPlayerSync;

public class FyrplayerFactory implements iFyrplayerFactory{

    @Override
    public FyrPlayer createFryPlayer() {
        return new CodecPlayerSync();
    }
}
