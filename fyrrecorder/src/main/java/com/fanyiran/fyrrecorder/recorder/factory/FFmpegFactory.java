package com.fanyiran.fyrrecorder.recorder.factory;

import android.media.MediaCodec;

import com.fanyiran.fyrrecorder.recorder.IRecorder;
import com.fanyiran.fyrrecorder.recorder.ffmpeg.FFmpegImpl;

public class FFmpegFactory implements IRecorderFactory {
    @Override
    public IRecorder createRecorder() {
        return new FFmpegImpl();
    }

    @Override
    public Class getRecorderClass() {
        // FIXME: 2019-07-05 修改？
        return MediaCodec.class;
    }
}
