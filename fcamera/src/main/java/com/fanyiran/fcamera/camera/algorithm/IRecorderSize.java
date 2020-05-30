package com.fanyiran.fcamera.camera.algorithm;

import android.hardware.Camera;

import java.util.List;

// TODO: 2020/5/13
public interface IRecorderSize {

    Camera.Size getOptimalVideoSize(List<Camera.Size> supportedVideoSizes,
                                    List<Camera.Size> previewSizes, int w, int h);
}
