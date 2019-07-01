package com.fanyiran.fyrrecorder.camera;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.fanyiran.fyrrecorder.recorder.RecorderConfig;

import java.io.File;
import java.util.ArrayList;

public class CameraConfig {

    private final CameraConfigBuilder builder;

    public CameraConfig(CameraConfigBuilder cameraConfigBuilder) {
        this.builder = cameraConfigBuilder;
    }

    public int getCameraId() {
        return builder.cameraId;
    }

    public boolean isUseFlash() {
        return builder.useFlash;
    }

    public Size getTargetResolution() {
        return builder.targetResolution;
    }

    public Rational getTargetAspectRatio() {
        return builder.targetAspectRatio;
    }

    public Context getContext() {
        return builder.context;
    }

    public ICameraListener getiCameraListener() {
        return builder.iCameraListener;
    }

    public ArrayList<SurfaceHolder> getSurface() {
        return builder.surfaces;
    }

//    public int getOutputFormat() {
//        return builder.recorderConfig.outputFormat;
//    }
//
//    public File getOutputFile() {
//        return builder.recorderConfig.outputFile;
//    }
//
//    public int getEncodingBitRate() {
//        return builder.recorderConfig.encodingBitRate;
//    }
//
//    public int getFrameRate() {
//        return builder.recorderConfig.frameRate;
//    }
//
//    public int getVideoEncoder() {
//        return builder.recorderConfig.videoEncoder;
//    }
//
//    public int getAudioEncoder() {
//        return builder.recorderConfig.audioEncoder;
//    }

    public RecorderConfig getRecorderConfig() {
        return builder.recorderConfig;
    }

    public static class CameraConfigBuilder {
        private int cameraId = ICamera.CAMREA_BACK;
        private boolean useFlash;
        private Size targetResolution;
        private Rational targetAspectRatio;
        private Context context;
        private ICameraListener iCameraListener;
        private ArrayList<SurfaceHolder> surfaces;
        private RecorderConfig recorderConfig;

        public CameraConfigBuilder() {
            recorderConfig = new RecorderConfig();
        }

        public CameraConfig build() {
            // TODO: 2019-06-27 checkout params
            if (recorderConfig.outputFile == null) {
                throw new IllegalArgumentException("output file is null");
            }
            return new CameraConfig(this);
        }

        public CameraConfigBuilder setCameraId(int cameraId) {
            this.cameraId = cameraId;
            return this;
        }

        public CameraConfigBuilder setUseFlash(boolean useFlash) {
            this.useFlash = useFlash;
            return this;
        }

        public CameraConfigBuilder setTargetResolution(Size targetResolution) {
            this.targetResolution = targetResolution;
            return this;
        }

        public CameraConfigBuilder setTargetAspectRatio(Rational targetAspectRatio) {
            this.targetAspectRatio = targetAspectRatio;
            return this;
        }

        public CameraConfigBuilder setContext(Context context) {
            this.context = context;
            return this;
        }

        public CameraConfigBuilder setiCameraListener(ICameraListener iCameraListener) {
            this.iCameraListener = iCameraListener;
            return this;
        }

        public CameraConfigBuilder setSurfaces(ArrayList<SurfaceHolder> surfaces) {
            this.surfaces = surfaces;
            return this;
        }

        public void setOutputFormat(int outputFormat) {
            this.recorderConfig.outputFormat = outputFormat;
        }

        public CameraConfigBuilder setOutputFile(File outputFile) {
            this.recorderConfig.outputFile = outputFile;
            return this;
        }

        public CameraConfigBuilder setEncodingBitRate(int encodingBitRate) {
            this.recorderConfig.encodingBitRate = encodingBitRate;
            return this;
        }

        public CameraConfigBuilder setFrameRate(int frameRate) {
            this.recorderConfig.frameRate = frameRate;
            return this;
        }

        public CameraConfigBuilder setVideoEncoder(int videoEncoder) {
            this.recorderConfig.videoEncoder = videoEncoder;
            return this;
        }
    }

}
