package com.fanyiran.fyrrecorder.recorder;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Size;
import android.view.Surface;

import java.io.File;

public class RecorderConfig {
    public int iFrameInterval = 1;
    public int outputFormat = MediaRecorder.OutputFormat.MPEG_4;
    public int encodingBitRate = 10000000;
    public int videoEncoder = MediaRecorder.VideoEncoder.MPEG_4_SP;
    public int audioEncoder = MediaRecorder.AudioEncoder.AAC;
    public int frameRate = 30;
    public File outputFile;
    public Camera camera;
    public int orientation;
    public Size videSize = new Size(320, 640);
    public Surface surface;
    public OnPlayErrorListener onPlayErrorListener;
}
