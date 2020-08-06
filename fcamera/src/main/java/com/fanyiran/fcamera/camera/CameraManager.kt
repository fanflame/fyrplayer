package com.fanyiran.fcamera.camera

import android.graphics.SurfaceTexture
import android.os.Handler
import android.util.Size
import com.fanyiran.fcamera.camera.callback.OnPreviewDataCallback

class CameraManager constructor(handler: Handler?, cameraImpl: CameraImpl) : ICamera by cameraImpl {
    private val cameraImpl: CameraImpl = cameraImpl
    private val handler: Handler? = handler

    override fun open(isFront: Boolean): Int {
        if (handler == null) {
            return cameraImpl.open(isFront)
        }
        handler.post {
            this@CameraManager.cameraImpl.open(isFront)
        }
        return 0
    }

    override fun setPreviewFps(minFps: Int, maxFps: Int) {
        if (handler == null) {
            return cameraImpl.setPreviewFps(minFps, maxFps)
        }
        handler.post {
            this@CameraManager.cameraImpl.setPreviewFps(minFps, maxFps)
        }
    }

    override fun setPreviewSize(size: Size) {
        if (handler == null) {
            return cameraImpl.setPreviewSize(size)
        }
        handler.post {
            this@CameraManager.cameraImpl.setPreviewSize(size)
        }
    }

    override fun preview(
            surface: SurfaceTexture, onPreviewDataCallback: OnPreviewDataCallback
    ): Boolean {
        if (handler == null) {
            return cameraImpl.preview(surface, onPreviewDataCallback)
        }
        handler.post {
            this@CameraManager.cameraImpl.preview(surface, onPreviewDataCallback)
        }
        return true
    }

    override fun switchCamera(onRelaseCallback: ICamera.ReleaseCallBack?): Boolean {
        if (handler == null) {
            return cameraImpl.switchCamera(onRelaseCallback)
        }
        handler.post {
            this@CameraManager.cameraImpl.switchCamera(onRelaseCallback)
        }
        return true
    }
}