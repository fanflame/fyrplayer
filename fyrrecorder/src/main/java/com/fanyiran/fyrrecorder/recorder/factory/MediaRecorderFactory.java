package com.fanyiran.fyrrecorder.recorder.factory;

import android.media.MediaRecorder;

import com.fanyiran.fyrrecorder.recorder.IRecorder;
import com.fanyiran.fyrrecorder.recorder.mediarecorder.MediaRecorderImpl;

public class MediaRecorderFactory implements IRecorderFactory {
    @Override
    public IRecorder createRecorder() {
        return new MediaRecorderImpl();
    }

    @Override
    public Class getRecorderClass() {
        return MediaRecorder.class;
    }
}
