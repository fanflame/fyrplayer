package com.fanyiran.mediaplayer.fyrplayer.ffmpeg;

import com.fanyiran.mediaplayer.fyrplayer.IFyrPlayer;
import com.fanyiran.mediaplayer.fyrplayer.PlayerConfig;

public class FFmpegPlayer implements IFyrPlayer {
    @Override
    public void readVideoInfo(String videoFile) {
        nativeReadVideoInfo(videoFile);
    }

    @Override
    public void setConfig(PlayerConfig config) {
        nativeSetConfig(config);
    }

    @Override
    public boolean isPlaying() {
        return nativeIsPlaying();
    }

    @Override
    public boolean play() {
        return nativePlay();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void release() {

    }

    @Override
    public void seekTo(long seekTime) {

    }

    native void nativeReadVideoInfo(String videoFile);
    native void nativeSetConfig(PlayerConfig config);
    native boolean nativeIsPlaying();
    native boolean nativePlay();
    native void nativePause();
    native void nativeResume();
    native void nativeRelease();
    native void nativeSeekTo(long seekTimes);
}
