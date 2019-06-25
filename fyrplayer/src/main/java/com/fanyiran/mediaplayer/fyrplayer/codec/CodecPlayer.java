package com.fanyiran.mediaplayer.fyrplayer.codec;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.fanyiran.mediaplayer.fyrplayer.FyrPlayer;
import com.fanyiran.mediaplayer.fyrplayer.PlayerConfig;
import com.fanyiran.mediaplayer.fyrplayer.VideoInfo;
import com.fanyiran.utils.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class CodecPlayer implements FyrPlayer {
    private static final String TAG = "CodecPlayer";
    protected static final int TIMEOUT_US = 10000;

    protected PlayerConfig config;
    protected volatile int playStatus = PLAY_STATUS_NOT_PLAYING;
    protected VideoInfo videoInfo;
    protected MediaCodec mediaCodec;
    protected MediaExtractor mediaExtractor;
    protected int repeatedCount = 0;
    protected MediaFormat mediaFormat;

    @Override
    public void readVideoInfo(String videoFile) {

    }

    @Override
    public void setConfig(PlayerConfig config) {
        this.config = config;
    }

    @Override
    public boolean isPlaying() {
        return playStatus != PLAY_STATUS_NOT_PLAYING && playStatus != PLAY_STATUS_FINISHED
                && playStatus != PLAY_STATUS_RELEASE;
    }

    @Override
    public boolean play() {
        checkConfig();
        if (playStatus != PLAY_STATUS_NOT_PLAYING) {
            LogUtil.v(TAG, "not start");
            return false;
        }
        config.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                startRun();
            }
        });
        return true;
    }

    protected abstract void startRun();

    @Override
    public void pause() {
        playStatus = PLAY_STATUS_PAUSE;
    }

    @Override
    public void resume() {
        if (playStatus == PLAY_STATUS_PAUSE)
            playStatus = PLAY_STATUS_PLAYING;
    }

    protected void onFinish() {
        config.getOnPlayCallback().onFinish();
    }

    protected void onStart() {
        config.getOnPlayCallback().onStart();
    }

    protected void onError(int error) {
        onError(error,"");
    }
    protected void onError(int error,String errorContent) {
        playStatus = PLAY_STATUS_ERRORED;
        config.getOnPlayCallback().onError(error,errorContent);
    }

    protected void onGetVideoInfo(MediaFormat mediaFormat, String url) {
        if (config.getOnPlayCallback() != null) {
            videoInfo = new VideoInfo();
            videoInfo.setTrackId(String.valueOf(mediaFormat.getInteger(MediaFormat.KEY_TRACK_ID)));
            videoInfo.setMime(mediaFormat.getString(MediaFormat.KEY_MIME));
            videoInfo.setDisplayHeight(mediaFormat.getInteger("display-height"));
            videoInfo.setDisplayWidth(mediaFormat.getInteger("display-width"));
            videoInfo.setDuration(mediaFormat.getLong(MediaFormat.KEY_DURATION));
            videoInfo.setFrameRate(mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE));
            videoInfo.setHeight(mediaFormat.getInteger(MediaFormat.KEY_HEIGHT));
            videoInfo.setWidth(mediaFormat.getInteger(MediaFormat.KEY_WIDTH));
            videoInfo.setUrl(url);
            config.getOnPlayCallback().onGetVideoInfo(videoInfo);
        }
    }

    protected int getVideoTrack(MediaExtractor mediaExtractor) {
        int trackCount = mediaExtractor.getTrackCount();
        MediaFormat trackFormat;
        String mineType;
        for (int i = 0; i < trackCount; i++) {
            trackFormat = mediaExtractor.getTrackFormat(i);
            mineType = trackFormat.getString(MediaFormat.KEY_MIME);
            if (mineType.startsWith("video/")) {
                return i;
            }
        }
        return PLAY_CODE_NO_VOIDE_TRACK;
    }

    private void checkConfig() {
        if (config == null) {
            throw new IllegalArgumentException("config is null");
        }
    }

    @Override
    public void seekTo(long seekTime) {
        mediaExtractor.seekTo(seekTime, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        mediaCodec.stop();
        mediaCodec.configure(mediaFormat, config.getSurface(), null, 0);
        mediaCodec.start();
    }
}
