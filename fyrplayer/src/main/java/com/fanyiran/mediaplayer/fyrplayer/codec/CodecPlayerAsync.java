package com.fanyiran.mediaplayer.fyrplayer.codec;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import androidx.annotation.NonNull;

import com.fanyiran.mediaplayer.fyrplayer.PlayerConfig;
import com.fanyiran.utils.LogUtil;

import java.io.IOException;

public class CodecPlayerAsync extends CodecPlayer {
    private static final String TAG = "CodecPlayerAsync";
    private long frameDuration;
    private int sampleDataSize;
    private long lastRenderTime;
    private int renderCount;
    private int decodeCount;
    private long startTime;

    @Override
    protected void startRun() {
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
            MediaFormat mediaFormat = mediaExtractor.getTrackFormat(videoTrack);
            onGetVideoInfo(mediaFormat, config.getUrl());
//            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 10);
            mediaCodec = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME));
            mediaCodec.setCallback(new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                    sampleDataSize = mediaExtractor.readSampleData(codec.getInputBuffer(index), 0);
                    if (sampleDataSize == -1) {
                        mediaCodec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        LogUtil.v(TAG, "decodeCount:" + decodeCount);
                        return;
                    }
                    decodeCount++;
//                    while (System.nanoTime() - startTime < mediaExtractor.getSampleTime()) {
//                        try {
//                            Thread.sleep((mediaExtractor.getSampleTime() - (System.nanoTime() - startTime))/1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    mediaCodec.queueInputBuffer(index, 0, sampleDataSize, mediaExtractor.getSampleTime(), 0);
                    mediaExtractor.advance();
                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
//                    if (System.currentTimeMillis() - lastRenderTime < frameDuration) {
//                        LogUtil.v(TAG,"jump render");
//                        mediaCodec.releaseOutputBuffer(index, false);
//                        return;
//                    }
                    lastRenderTime = System.currentTimeMillis();
                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        if (config.getPlayRepeatTime() == PlayerConfig.REPEATE_INFINITE) {
                            // TODO: 2019-06-24 继续从头开始播放
                            playStatus = PLAY_STATUS_PLAYING;
                            seekTo(0);
                        } else if (repeatedCount++ == config.getPlayRepeatTime()) {
                            playStatus = PLAY_STATUS_FINISHED;
                            LogUtil.v(TAG, "renderFrameCount:" + renderCount);
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
                    mediaCodec.releaseOutputBuffer(index, info.presentationTimeUs);
                }

                @Override
                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
                    switch (e.getErrorCode()) {
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
                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

                }
            });
            try {
                mediaCodec.configure(mediaFormat, config.getSurface(), null, 0);
            } catch (IllegalArgumentException e) {
                onError(PLAY_CODE_CONFIGUR_FAILED);
                release();
                return;
            }
            mediaCodec.start();
            startTime = System.nanoTime();
            playStatus = PLAY_STATUS_PLAYING;
            onStart();

            frameDuration = 1000 / videoInfo.getFrameRate();
        } catch (IOException e) {
            e.printStackTrace();
            playStatus = PLAY_CODE_UNKNOW_ERROR;
            onError(PLAY_CODE_UNKNOW_ERROR);
        }
    }

    @Override
    public void release() {

    }
}
