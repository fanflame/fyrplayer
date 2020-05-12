package com.fanyiran.fyrrecorder.recorder.mediacodec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.fanyiran.fyrrecorder.recorder.IRecorderAbstract;
import com.fanyiran.fyrrecorder.recorder.RecorderConfig;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;


public class MediaCodecImpl extends IRecorderAbstract {
    private static final String TAG = "MediaCodecImpl";
    private MediaCodec mediaCodec;
    private MediaMuxer mediaMuxer;
    private HandlerThread handlerThread;
    private Handler handler;
    private int trackId;
    private boolean muxerStarted = false;
    private SynchronousQueue<byte[]> cameraData;

    @Override
    public void init(RecorderConfig config) {
        super.init(config);
        MediaFormat format = new MediaFormat();
        format.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_VIDEO_AVC);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, config.frameRate);
        format.setInteger(MediaFormat.KEY_WIDTH, config.videSize.getWidth());
        format.setInteger(MediaFormat.KEY_HEIGHT, config.videSize.getHeight());
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, config.encodingBitRate);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, config.iFrameInterval);
        //以上参数必须设置否则crash,KEY_I_FRAME_INTERVAL如果不设置，视频录制结果会比较模糊

        String type = format.getString(MediaFormat.KEY_MIME);
        // TODO: 2019-07-02 以下代码支持
//        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
//        String supportCodec = null;
//        for (MediaCodecInfo codecInfo : mediaCodecList.getCodecInfos()) {
//            LogUtil.v(TAG,codecInfo.getName());
//            if (!codecInfo.isEncoder()) {
//                continue;
//            }
//            try {
//                if (codecInfo.getCapabilitiesForType(type).isFormatSupported(format)) {
//                    supportCodec = codecInfo.getName();
//                    break;
//                }
//            } catch (IllegalArgumentException e) {
//                continue;
//            }
//        }
//        if (supportCodec == null) {
//            onError(2,"format not support");
//            return;
//        }
        try {
            mediaCodec = MediaCodec.createEncoderByType(type);
            mediaMuxer = new MediaMuxer(config.outputFile.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
            onError(2, "mediacodec or mediaMuxer create failed");
            return;
        }
        handlerThread = new HandlerThread("MediaCodecImpl thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mediaCodec.setCallback(callback, handler);
        trackId = mediaMuxer.addTrack(format);
    }

    private MediaCodec.Callback callback = new MediaCodec.Callback() {
//        private byte[] lastBytes;
//        private int offSet;

        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
//            ByteBuffer inputBuffer = codec.getInputBuffer(index);
//            if (lastBytes == null || offSet >= lastBytes.length) {
//                try {
//                    lastBytes = cameraData.take();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            int size = lastBytes.length - offSet;
//            int queueSize;
//            if (size - inputBuffer.remaining() > 0) {
//                inputBuffer.put(lastBytes, offSet, queueSize = inputBuffer.remaining());
//                offSet += inputBuffer.remaining();
//            } else {
//                inputBuffer.put(lastBytes, offSet, queueSize = size);
//                offSet = lastBytes.length;
//            }
//            LogUtil.v("TAGGGGGG",""+queueSize);
//            codec.queueInputBuffer(index, 0, queueSize, 0, MediaCodec.BUFFER_FLAG_CODEC_CONFIG);
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            if (muxerStarted) {
                mediaMuxer.writeSampleData(trackId, codec.getOutputBuffer(index), info);
            }
            codec.releaseOutputBuffer(index, false);
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
            MediaCodecImpl.this.onError(2, "codec callback onError");
        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
//            trackId = mediaMuxer.addTrack(format);
//            mediaMuxer.start();
//            muxerStarted = true;
        }
    };

    @Override
    public void startRecord() {
        super.startRecord();
        if (cameraData == null) {
            cameraData = new SynchronousQueue<>();
        }
        muxerStarted = true;
        mediaMuxer.start();
        mediaCodec.start();
        cameraData.clear();
    }

    @Override
    public void stopRecord() {
        super.stopRecord();
        mediaCodec.signalEndOfInputStream();
        mediaCodec.stop();
        mediaMuxer.stop();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void release() {
        super.release();
        mediaCodec.release();
    }

    @Override
    public Surface getSurface() {
        if (getStatus() != RECORD_STATUS_INIT) {
            onError(2, "init method must be called first");
            return null;
        }
        if (mediaCodec == null) {
            return null;
        }

        return mediaCodec.createInputSurface();
    }

    @Override
    public void receiveData(byte[] dataY, byte[] dataU, byte[] dataV) {
//        if (muxerStarted) {
//            LogUtil.v("TAGssssss",""+dataY.length);
//            cameraData.add(dataY);
//        }

    }
}
