package com.fanyiran.fcamera.camera;

import android.content.Context;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CameraConfig {
    private int cameraId = ICamera.CAMREA_BACK;
    private boolean useFlash;
    private Rational targetAspectRatio;
    private WeakReference<Context> context;
    private ArrayList<Surface> surfaces;
    private Size previewSize;
    private int previewMaxFps;
    private int previewMinFps;

    private CameraConfig(Builder builder) {
        cameraId = builder.cameraId;
        useFlash = builder.useFlash;
        targetAspectRatio = builder.targetAspectRatio;
        context = builder.context;
        surfaces = builder.surfaces;
        previewSize = builder.previewSize;
        previewMaxFps = builder.previewMaxFps;
        previewMinFps = builder.previewMinFps;
    }

    public int getCameraId() {
        return cameraId;
    }

    public boolean isUseFlash() {
        return useFlash;
    }

    public Rational getTargetAspectRatio() {
        return targetAspectRatio;
    }

    public WeakReference<Context> getContext() {
        return context;
    }

    public ArrayList<Surface> getSurfaces() {
        return surfaces;
    }

    public Size getPreviewSize() {
        return previewSize;
    }

    public int getPreviewMaxFps() {
        return previewMaxFps;
    }

    public int getPreviewMinFps() {
        return previewMinFps;
    }

    public static final class Builder {
        private int cameraId;
        private boolean useFlash;
        private Size previewSize;
        private int previewMaxFps = 30;
        private int previewMinFps = 30;
        private Rational targetAspectRatio;
        private WeakReference<Context> context;
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

        public Builder targetAspectRatio(Rational val) {
            targetAspectRatio = val;
            return this;
        }

        public Builder context(Context val) {
            context = new WeakReference<>(val);
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

        public Builder previewMaxFps(int val) {
            previewMaxFps = val;
            return this;
        }

        public Builder previewMinFps(int val) {
            previewMinFps = val;
            return this;
        }

        public Builder setPreviewSize(Size size) {
            this.previewSize = size;
            return this;
        }

        public CameraConfig build() {
            if (context == null) {
                throw new IllegalStateException("context is null");
            }
            return new CameraConfig(this);
        }
    }
}
