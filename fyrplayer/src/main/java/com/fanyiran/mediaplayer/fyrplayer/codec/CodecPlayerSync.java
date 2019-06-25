package com.fanyiran.mediaplayer.fyrplayer.codec;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.fanyiran.mediaplayer.fyrplayer.PlayerConfig;
import com.fanyiran.utils.LogUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

public class CodecPlayerSync extends CodecPlayer {
    private static final String TAG = "CodecPlayerSync";
    private static final int WHAT_START = 1;
    private static final int WHAT_DECODE_RENDER = 2;
    private VideoHandler handler;

    ///////////////////////////////////////////////////////////////////////////
    // decode & render start
    ///////////////////////////////////////////////////////////////////////////
    private ByteBuffer[] inputBuffers;
    private MediaCodec.BufferInfo info;
    private int sampleDataSize;
    private int index;
    protected boolean inputAvilable = true;
    protected long frameDuration;
    private int renderCount;
    ///////////////////////////////////////////////////////////////////////////
    // decode & render end
    ///////////////////////////////////////////////////////////////////////////

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
                    decode();
                    render();
                    nextDecodeRender();
                    break;
                default:

            }
        }
    }

    @Override
    protected void startRun() {
        try {
            Looper.prepare();
        } catch (RuntimeException e) {
            onError(PLAY_STATUS_THREAD_LOOPER_ERROR);
            return;
        }
        inputAvilable = true;
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
    private void nextDecodeRender() {
        if (playStatus != PLAY_STATUS_RELEASE) {
            handler.sendEmptyMessageDelayed(WHAT_DECODE_RENDER, frameDuration);
        }
    }

    private void start() {
        repeatedCount = 0;
        mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(config.getUrl());
            int videoTrack = getVideoTrack(mediaExtractor);
            if (videoTrack == PLAY_CODE_NO_VOIDE_TRACK) {
                onError(PLAY_CODE_NO_VOIDE_TRACK);
                playStatus = PLAY_STATUS_ERRORED;
                return;
            }
            mediaExtractor.selectTrack(videoTrack);
            mediaFormat = mediaExtractor.getTrackFormat(videoTrack);
            mediaFormat.setInteger(MediaFormat.KEY_WIDTH, 16);
            mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, 32);
//            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,0);
            onGetVideoInfo(mediaFormat, config.getUrl());
//            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 10);
            mediaCodec = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME));
            try {
                mediaCodec.configure(mediaFormat, config.getSurface(), null, 0);
            } catch (IllegalArgumentException e) {
                onError(PLAY_CODE_CONFIGUR_FAILED);
                release();
                return;
            }
            mediaCodec.start();
            playStatus = PLAY_STATUS_PLAYING;
            onStart();

            inputBuffers = mediaCodec.getInputBuffers();
            info = new MediaCodec.BufferInfo();
            frameDuration = 1000 / videoInfo.getFrameRate();
            handler.sendEmptyMessage(WHAT_DECODE_RENDER);
        } catch (IOException e) {
            e.printStackTrace();
            playStatus = PLAY_CODE_UNKNOW_ERROR;
            onError(PLAY_CODE_UNKNOW_ERROR);
        }
    }

    private void decode() {
        if (inputAvilable) {
            index = mediaCodec.dequeueInputBuffer(TIMEOUT_US);
            if (index == -1) {
                inputAvilable = false;
                return;
            }
            inputBuffers[index].clear();
            sampleDataSize = mediaExtractor.readSampleData(inputBuffers[index], 0);
            if (sampleDataSize == -1) {
                mediaCodec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                inputAvilable = false;
                return;
            }
            mediaCodec.queueInputBuffer(index, 0, sampleDataSize, mediaExtractor.getSampleTime(), 0);
            mediaExtractor.advance();
        }
    }

    private void render() {
        int outputBufferStatus = mediaCodec.dequeueOutputBuffer(info, TIMEOUT_US);
        if (outputBufferStatus < 0) {//error
            switch (outputBufferStatus) {
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    LogUtil.v(TAG, "MediaCodec.INFO_OUTPUT_FORMAT_CHANGED");
                    break;
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    LogUtil.v(TAG, "MediaCodec.INFO_TRY_AGAIN_LATER");
                    break;
                case MediaCodec.BUFFER_FLAG_CODEC_CONFIG:
                    LogUtil.v(TAG, "MediaCodec.BUFFER_FLAG_CODEC_CONFIG");
                    break;
                default:
                    LogUtil.v(TAG, "default");
            }
        } else {
            if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                if (config.getPlayRepeatTime() == PlayerConfig.REPEATE_INFINITE) {
                    // TODO: 2019-06-24 继续从头开始播放
                    playStatus = PLAY_STATUS_PLAYING;
                    seekTo(0);
                } else if (repeatedCount++ == config.getPlayRepeatTime()) {
                    playStatus = PLAY_STATUS_FINISHED;
                    onFinish();
                } else {
                    // TODO: 2019-06-24 继续从头开始播放
                    playStatus = PLAY_STATUS_PLAYING;
                    seekTo(0);
                }
            } else {
                playStatus = PLAY_STATUS_PLAYING;
            }
            renderCount++;
            mediaCodec.releaseOutputBuffer(outputBufferStatus, info.size != 0);
            inputAvilable = true;
            if (renderCount > 90) {
                seekTo(6000 * 1000);
                renderCount = 0;
            }
        }
    }

    @Override
    public void release() {
        if (mediaCodec != null) {
            mediaCodec.stop();
        }
        if (handler != null) {
            handler.removeMessages(WHAT_START);
            handler.removeMessages(WHAT_DECODE_RENDER);
            handler.getLooper().quit();
        }
    }
}
