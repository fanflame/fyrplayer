package com.fanyiran.fyrrecorder.recorderview.opengl;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;

import com.fanyiran.utils.LogUtil;

public class EGLHelper {
    private static final String TAG = "EGLHelper";
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
        boolean result = EGL14.eglMakeCurrent(display, eglSurface, eglSurface, eglContext);
        if (!result) {
            LogUtil.v(TAG, String.format("eglMakeCurrent: %d!", EGL14.eglGetError()));
        }
    }

    public boolean swapBuffers(EGLSurface eglSurface) {
        return EGL14.eglSwapBuffers(display, eglSurface);
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
//                EGL14.EGL_RENDERABLE_TYPE, EGL_RECORDABLE_ANDROID,
//                1, 0,// placeholder for recordable [@-3]
                EGL14.EGL_NONE
        };
        configs = new EGLConfig[1];
        result = EGL14.eglChooseConfig(display, attrib_list, 0, configs, 0, configs.length, num_config, 0);
        if (!result) {
            LogUtil.v(TAG, "eglGetConfigs failed!");
            return -1;
        }
        int[] attrList = {0x3098, 3, EGL14.EGL_NONE};
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
        if (EGL14.eglGetError() != EGL14.EGL_SUCCESS) {
            LogUtil.v(TAG, String.format("eglGetError: %d!", EGL14.eglGetError()));
            return false;
        }
        return true;
    }
}
