package com.fanyiran.fyrrecorder.recorderview.opengl;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;

import com.fanyiran.utils.LogUtil;

public class EGLHelper {
    private static final String TAG = "EGLHelper";
    // Android-specific extension.
    private static final int EGL_RECORDABLE_ANDROID = 0x3142;
    private EGLDisplay display;
    private EGLConfig[] configs;
    private EGLContext eglContext;

    private EGLHelper() {
        init();
    }

    public EGLDisplay getEGLDislplay() {
        return display;
    }

    public EGLContext getEglContext() {
        return eglContext;
    }

    public void makeCurrent(EGLSurface eglSurface) {
        if (display == null || eglContext == null) {
            LogUtil.v(TAG, "eglMakeCurrent: display or eglContext may be null");
            return;
        }
        boolean result = EGL14.eglMakeCurrent(display, eglSurface, eglSurface, eglContext);
        if (!result) {
            LogUtil.v(TAG, String.format("eglMakeCurrent: %d!", EGL14.eglGetError()));
        }
    }

    public boolean swapBuffers(EGLSurface eglSurface) {
        if (display == null) {
            LogUtil.v(TAG, "swapBuffers: display is null");
            return false;
        }
        return EGL14.eglSwapBuffers(display, eglSurface);
    }

    public void setPresentationTime(EGLSurface eglSurface, long time) {
        EGLExt.eglPresentationTimeANDROID(display, eglSurface, time);
    }

    static class EGLHelperWrapper {
        static EGLHelper eglHelper = new EGLHelper();
    }

    public static EGLHelper getInstance() {
        return EGLHelperWrapper.eglHelper;
    }

    private int init() {
        display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);

        if (display == EGL14.EGL_NO_DISPLAY) {
            LogUtil.v(TAG, "eglGetDisplay failed!");
            return -1;
        }
        int[] version = new int[2];
        boolean result = EGL14.eglInitialize(display, version, 0, version, 1);
        if (!result) {
            LogUtil.v(TAG, "initialize failed!");
            return -1;
        }
        int[] num_config = new int[1];
        int[] attrib_list = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL_RECORDABLE_ANDROID, 1,// placeholder for recordable [@-3]
                EGL14.EGL_NONE
        };
        configs = new EGLConfig[1];
        result = EGL14.eglChooseConfig(display, attrib_list, 0, configs, 0, configs.length, num_config, 0);
        if (!result) {
            LogUtil.v(TAG, "eglGetConfigs failed!");
            return -1;
        }
        int[] attrList = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        eglContext = EGL14.eglCreateContext(display, configs[0], EGL14.EGL_NO_CONTEXT, attrList, 0);
        if (eglContext == EGL14.EGL_NO_CONTEXT) {
            LogUtil.v(TAG, "eglCreateContext failed!");
            return -1;
        }

        return 0;
    }

    public EGLSurface genEglSurface(Object object) {
        int[] surfaceAttr = {
                EGL14.EGL_NONE
        };
        EGLSurface eglSurface = EGL14.eglCreateWindowSurface(display, configs[0], object, surfaceAttr, 0);
        if (eglSurface == null || !check()) {
            LogUtil.v(TAG, "eglCreateWindowSurface failed!");
            return null;
        }
        return eglSurface;
    }

    private boolean check() {
        int error = EGL14.eglGetError();
        if (error != EGL14.EGL_SUCCESS) {
            LogUtil.v(TAG, String.format("eglGetError: 0x%x!", error));
            return false;
        }
        return true;
    }
}
