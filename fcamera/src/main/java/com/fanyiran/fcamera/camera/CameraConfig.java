package com.fanyiran.fcamera.camera;

import android.content.Context;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

//import com.fanyiran.fyrrecorder.recorder.RecorderConfig;

public class CameraConfig {
    private int cameraId = ICamera.CAMREA_BACK;
    private boolean useFlash;
    private Size targetResolution;
    private Rational targetAspectRatio;
    private WeakReference<Context> context;
    private ICameraListener iCameraListener;
    private ArrayList<Surface> surfaces;
    private Size previewSize;

    private Class recorderClass;
    private Class previewClass;

    private CameraConfig(Builder builder) {
        cameraId = builder.cameraId;
        useFlash = builder.useFlash;
        targetResolution = builder.targetResolution;
        targetAspectRatio = builder.targetAspectRatio;
        context = builder.context;
        iCameraListener = builder.iCameraListener;
        surfaces = builder.surfaces;
        previewSize = builder.previewSize;
    }

    public int getCameraId() {
        return cameraId;
    }

    public boolean isUseFlash() {
        return useFlash;
    }

    public Size getTargetResolution() {
        return targetResolution;
    }

    public Rational getTargetAspectRatio() {
        return targetAspectRatio;
    }

    public WeakReference<Context> getContext() {
        return context;
    }

    public ICameraListener getiCameraListener() {
        return iCameraListener;
    }

    public ArrayList<Surface> getSurfaces() {
        return surfaces;
    }

    public Size getPreviewSize() {
        return previewSize;
    }

    public void setRecorderClass(Class recorderClass) {
        this.recorderClass = recorderClass;
    }

    public void setPreviewClass(Class previewClass) {
        this.previewClass = previewClass;
    }

    public Class getRecorderClass() {
        return recorderClass;
    }

    public Class getPreviewClass() {
        return previewClass;
    }

    public static class CameraConfigBuilder {
        private int cameraId = ICamera.CAMREA_BACK;
        private boolean useFlash;
        private Size targetResolution;
        private Rational targetAspectRatio;
        private WeakReference<Context> context;
        private ICameraListener iCameraListener;
        private ArrayList<Surface> surfaces;
        private Size previewSize;
//        private RecorderConfig recorderConfig;

        public CameraConfigBuilder() {
//            recorderConfig = new RecorderConfig();
        }

//        public CameraConfig build() {
//            // TODO: 2019-06-27 checkout params
////            if (recorderConfig.outputFile == null) {
////                throw new IllegalArgumentException("output file is null");
////            }
//            if (context == null) {
//                throw new IllegalArgumentException("context file is null");
//            }
////            if (!FileUtils.createFile(recorderConfig.outputFile)) {
////                throw new IllegalStateException("video file create failed!");
////            }
//            return new CameraConfig();
//        }

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
            this.context = new WeakReference<>(context);
            return this;
        }

        public CameraConfigBuilder setiCameraListener(ICameraListener iCameraListener) {
            this.iCameraListener = iCameraListener;
            return this;
        }

        public CameraConfigBuilder setSurfaces(ArrayList<Surface> surfaces) {
            this.surfaces = surfaces;
            return this;
        }

//        public void setOutputFormat(int outputFormat) {
//            this.recorderConfig.outputFormat = outputFormat;
//        }
//
//        public CameraConfigBuilder setOutputFile(File outputFile) {
//            this.recorderConfig.outputFile = outputFile;
//            return this;
//        }
//
//        public CameraConfigBuilder setEncodingBitRate(int encodingBitRate) {
//            this.recorderConfig.encodingBitRate = encodingBitRate;
//            return this;
//        }
//
//        public CameraConfigBuilder setFrameRate(int frameRate) {
//            this.recorderConfig.frameRate = frameRate;
//            return this;
//        }
//
//        public CameraConfigBuilder setVideoEncoder(int videoEncoder) {
//            this.recorderConfig.videoEncoder = videoEncoder;
//            return this;
//        }
//
//        public CameraConfigBuilder setVideoSize(Size size) {
//            this.recorderConfig.videSize = size;
//            return this;
//        }
//
//        public CameraConfigBuilder setVideoIFrameInterval(int iFrameInterval) {
//            this.recorderConfig.iFrameInterval = iFrameInterval;
//            return this;
//        }
//
//        public CameraConfigBuilder setSurface(Surface surface) {
//            this.recorderConfig.surface = surface;
//            return this;
//        }
    }

    public static final class Builder {
        private int cameraId;
        private boolean useFlash;
        private Size targetResolution;
        private Size previewSize;
        private Rational targetAspectRatio;
        private WeakReference<Context> context;
        private ICameraListener iCameraListener;
        private ArrayList<Surface> surfaces;

        public Builder() {
        }

        public Builder cameraId(int val) {
            cameraId = val;
            return this;
        }

        public Builder useFlash(boolean val) {
            useFlash = val;
            return this;
        }

        public Builder targetResolution(Size val) {
            targetResolution = val;
            return this;
        }

        public Builder targetAspectRatio(Rational val) {
            targetAspectRatio = val;
            return this;
        }

        public Builder context(WeakReference<Context> val) {
            context = val;
            return this;
        }

        public Builder iCameraListener(ICameraListener val) {
            iCameraListener = val;
            return this;
        }

        public Builder surfaces(ArrayList<Surface> val) {
            surfaces = val;
            return this;
        }

        public Builder previewSize(Size val) {
            previewSize = val;
            return this;
        }

        public Builder setPreviewSize(Size size) {
            this.previewSize = size;
            return this;
        }

        public CameraConfig build() {
            return new CameraConfig(this);
        }
    }
}
