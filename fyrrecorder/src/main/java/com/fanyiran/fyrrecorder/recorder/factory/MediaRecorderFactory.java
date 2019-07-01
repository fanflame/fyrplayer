package com.fanyiran.fyrrecorder.recorder.factory;

import com.fanyiran.fyrrecorder.recorder.IRecorder;
import com.fanyiran.fyrrecorder.recorder.mediarecorder.MediaRecorderImpl;

public class MediaRecorderFactory implements IRecorderFactory {
    @Override
    public IRecorder createRecorder() {
        return new MediaRecorderImpl();
    }
}
