package com.fanyiran.fyrrecorder.recorder.factory;

import android.media.MediaCodec;

import com.fanyiran.fyrrecorder.recorder.IRecorder;
import com.fanyiran.fyrrecorder.recorder.mediacodec.MediaCodecRecorderImpl;

public class MediaCodecFactory implements IRecorderFactory {
    @Override
    public IRecorder createRecorder() {
        return new MediaCodecRecorderImpl();
    }

    @Override
    public Class getRecorderClass() {
        return MediaCodec.class;
    }
}
