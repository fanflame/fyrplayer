package com.fanyiran.mediaplayer.fyrplayer;

public interface FyrPlayer {
    int PLAY_STATUS_NOT_PLAYING = 0;
    int PLAY_STATUS_PLAYING = 1;
    int PLAY_STATUS_FINISHED = 2;
    int PLAY_STATUS_ERRORED = 4;
    int PLAY_STATUS_PAUSE = 6;
    int PLAY_STATUS_RELEASE = 7;
    int PLAY_STATUS_THREAD_LOOPER_ERROR = 8;


    int PLAY_CODE_INIT_SUCCESS = 0;
    int PLAY_CODE_UNKNOW_ERROR = -1;
    int PLAY_CODE_NO_VOIDE_TRACK = -2;//没有视频轨道
    int PLAY_CODE_CONFIGUR_FAILED = -3;//没有视频轨道

    void readVideoInfo(String videoFile);
    void setConfig(PlayerConfig config);
    boolean isPlaying();
    boolean play();
    void pause();
    void resume();
    void release();
    void seekTo(long seekTime);
}
