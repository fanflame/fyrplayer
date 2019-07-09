package com.fanyiran.mediaplayer.fyrplayer.codec;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.CallSuper;
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
    protected static final int WHAT_START = 1;
    protected static final int WHAT_DECODE_RENDER = 2;

    protected PlayerConfig config;
    protected volatile int playStatus = PLAY_STATUS_NOT_PLAYING;
    protected VideoInfo videoInfo;
    protected MediaCodec mediaCodec;
    protected MediaExtractor mediaExtractor;
    protected int repeatedCount = 0;
    protected MediaFormat mediaFormat;
    VideoHandler handler;
    protected long frameDuration;

    private class VideoHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_START:
                    start();
                    break;
                case WHAT_DECODE_RENDER:
                    if (playStatus == PLAY_STATUS_RELEASE) {
                        return;
                    }
                    if (PLAY_STATUS_PAUSE == playStatus) {
                        nextDecodeRender();
                        return;
                    }
                    decodeRender();
                    nextDecodeRender();
                    break;
                default:

            }
        }
    }

    private void nextDecodeRender() {
        if (playStatus != PLAY_STATUS_RELEASE) {
            handler.sendEmptyMessageDelayed(WHAT_DECODE_RENDER, frameDuration);
        }
    }

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
                try {
                    Looper.prepare();
                } catch (RuntimeException e) {
                    onError(PLAY_STATUS_THREAD_LOOPER_ERROR);
                    return;
                }
//                inputAvilable = true;
                handler = new VideoHandler();
                handler.sendEmptyMessage(WHAT_START);
                Looper.loop();
                if (mediaExtractor != null) {
                    mediaExtractor.release();
                }
                mediaExtractor = null;
                if (mediaCodec != null) {
                    mediaCodec.release();
                }
                mediaCodec = null;
                playStatus = PLAY_STATUS_RELEASE;
            }
        });
        return true;
    }

    protected abstract void start();
    protected abstract void decodeRender();

    public void handleDecodeRender() {
        handler.sendEmptyMessage(WHAT_DECODE_RENDER);
    }
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
        mediaExtractor.seekTo(seekTime * 1000, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
//        mediaCodec.stop();
//        mediaCodec.configure(mediaFormat, config.getSurface(), null, 0);
//        mediaCodec.start();
        mediaCodec.flush();
    }

    @CallSuper
    @Override
    public void release() {
        if (handler != null) {
            handler.removeMessages(WHAT_START);
            handler.removeMessages(WHAT_DECODE_RENDER);
            handler.getLooper().quit();
        }
    }
}
