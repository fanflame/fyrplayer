package com.fanyiran.mediaplayer.fyrplayer.codec;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import androidx.annotation.NonNull;

import com.fanyiran.mediaplayer.fyrplayer.PlayerConfig;
import com.fanyiran.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;

public class CodecPlayerAsync extends CodecPlayer {
    private static final String TAG = "CodecPlayerAsync";
    private int sampleDataSize;
    private int renderCount;
    private int decodeCount;
    private ArrayList<OutputBufferInfo> bufferInfos;

    @Override
    protected void start() {
        repeatedCount = 0;
        mediaExtractor = new MediaExtractor();
        bufferInfos = new ArrayList<>();
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
                    mediaCodec.queueInputBuffer(index, 0, sampleDataSize, mediaExtractor.getSampleTime(), 0);
                    mediaExtractor.advance();
                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                    MediaCodec.BufferInfo infoClone = new MediaCodec.BufferInfo();
                    infoClone.flags = info.flags;
                    infoClone.size = info.size;
                    infoClone.presentationTimeUs = info.presentationTimeUs;
                    infoClone.offset = info.offset;
                    bufferInfos.add(new OutputBufferInfo(index,infoClone));
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
            playStatus = PLAY_STATUS_PLAYING;
            onStart();

            frameDuration = 1000 / videoInfo.getFrameRate();
            handleDecodeRender();
        } catch (IOException e) {
            e.printStackTrace();
            playStatus = PLAY_CODE_UNKNOW_ERROR;
            onError(PLAY_CODE_UNKNOW_ERROR);
        }
    }

    @Override
    protected void decodeRender() {
        if (bufferInfos.size() < 1) {
            return;
        }
        OutputBufferInfo outputBufferInfo = bufferInfos.remove(0);
        MediaCodec.BufferInfo info = outputBufferInfo.bufferInfo;
        int index = outputBufferInfo.index;
        if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            if (config.getPlayRepeatTime() == PlayerConfig.REPEATE_INFINITE) {
                playStatus = PLAY_STATUS_PLAYING;
                seekTo(0);
            } else if (repeatedCount++ == config.getPlayRepeatTime()) {
                playStatus = PLAY_STATUS_FINISHED;
                LogUtil.v(TAG, "renderFrameCount:" + renderCount);
                onFinish();
            } else {
                playStatus = PLAY_STATUS_PLAYING;
                seekTo(0);
            }
        } else {
            playStatus = PLAY_STATUS_PLAYING;
        }
        renderCount++;
        LogUtil.v(TAG,"presentationTImeUs:"+info.presentationTimeUs);
        mediaCodec.releaseOutputBuffer(index, info.presentationTimeUs);
    }

    @Override
    public void release() {
        super.release();
    }

    class OutputBufferInfo {
        public OutputBufferInfo(int index, MediaCodec.BufferInfo bufferInfo) {
            this.index = index;
            this.bufferInfo = bufferInfo;
        }

        int index;
        MediaCodec.BufferInfo bufferInfo;
    }

}
