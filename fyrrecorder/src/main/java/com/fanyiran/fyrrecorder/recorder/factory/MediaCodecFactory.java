package com.fanyiran.fyrrecorder.recorder.factory;

import com.fanyiran.fyrrecorder.recorder.IRecorder;
import com.fanyiran.fyrrecorder.recorder.mediacodec.MediaCodecImpl;

public class MediaCodecFactory implements IRecorderFactory {
    @Override
    public IRecorder createRecorder() {
        return new MediaCodecImpl();
    }
}
