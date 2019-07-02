package com.fanyiran.fyrrecorder.recorder;

import android.media.MediaRecorder;
import android.util.Size;

import java.io.File;

public class RecorderConfig {
    public int iFrameInterval = 1;
    public int outputFormat = MediaRecorder.OutputFormat.MPEG_4;
    public int encodingBitRate = 10000000;
    public int videoEncoder = MediaRecorder.VideoEncoder.H264;
    public int audioEncoder = MediaRecorder.AudioEncoder.AAC;
    public int frameRate = 30;
    public File outputFile;
    public Size videSize;
    public OnPlayErrorListener onPlayErrorListener;
}
