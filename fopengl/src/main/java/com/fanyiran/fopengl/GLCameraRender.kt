package com.fanyiran.fopengl

import android.app.Activity
import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Size
import com.fanyiran.fcamera.camera.CameraImpl
import com.fanyiran.fcamera.camera.CameraManager
import com.fanyiran.fcamera.camera.callback.OnPreviewDataCallback
import com.fanyiran.fopengl.drawer.sample.fbo.IDrawerPipeLine
import com.fanyiran.utils.LogUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLCameraRender(drawer: IDrawerPipeLine, surfaceView: GLSurfaceView) : GLSurfaceView.Renderer, OnPreviewDataCallback {
    private val TAG = "GLCameraRender"
    private val drawer: IDrawerPipeLine = drawer
    private lateinit var cameraManager: CameraManager
    private var context = surfaceView.context as Activity
    private var surfaceView = surfaceView
    override fun onDrawFrame(gl: GL10?) {
        LogUtil.v(TAG, "onDrawFrame")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val surfaceTexture = SurfaceTexture(genTexture())
        cameraManager.preview(surfaceTexture, this)
    }

    private fun genTexture(): Int {
        val texTureArray = IntArray(1)
        GLES30.glGenTextures(1, texTureArray, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texTureArray[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        return texTureArray[0]
    }

    override fun onPreviewData(data: ByteArray) {
        // NOTE: 2020/8/5 这里GLSurfaceview 创建的线程中有GLContext,camera相机open的线程中没有looper（即GLSurfaceView渲染线程没有looper）
        // NOTE: 2020/8/5 因此，camera的数据回调会放到主线程中，因此要想GL绘制,需要queueEvent到gl线程中
        surfaceView.queueEvent {
//            val dddd = ByteBuffer.allocate(data.size).put(data)
            drawer.draw(IDrawerPipeLine.TYPE.YUV_NV21, 1280, 720, data, 0)//todo 如果将camera吐的数据放入异步线程；那么当onPreviewData返回的时候，data可能已经回收?
            surfaceView.requestRender()//NOTE : 主动调用这个方法才会swapBuffer
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // todo 判断当前线程是否有glContext？
        cameraManager = CameraManager(null, CameraImpl())
        cameraManager.init(context)
        cameraManager.open(true)
        cameraManager.setPreviewFps(15, 15)
        cameraManager.setPreviewSize(Size(1280, 720))
        drawer.config()
    }
}