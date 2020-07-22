package com.fanyiran.fopengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.fanyiran.fopengl.drawer.IDrawer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRender(drawer: IDrawer) : GLSurfaceView.Renderer {
    val drawer: IDrawer = drawer
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(1.0f, 1.0f, 1.4f, 1.5f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        drawer.draw()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        drawer.config()
    }
}