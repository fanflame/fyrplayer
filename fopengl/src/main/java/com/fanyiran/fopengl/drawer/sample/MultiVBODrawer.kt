package com.fanyiran.fopengl.drawer.sample

import android.opengl.GLES30
import android.os.Handler
import android.os.Looper
import com.fanyiran.fopengl.GLESHelper
import com.fanyiran.fopengl.drawer.idrawer.DrawerConfig
import com.fanyiran.fopengl.drawer.idrawer.IDrawer
import java.nio.ByteBuffer
import java.nio.ByteOrder

/*
 使用两个vbo切换绘制
 */
class MultiVBODrawer : IDrawer() {
    val vbo = IntArray(2)
    var drawId = 1
    val handle: Handler = Handler(Looper.getMainLooper())

    init {
        handle.postDelayed(object : Runnable {
            override fun run() {
                drawId++
                handle.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private val vertexCoord1 = floatArrayOf(
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            0.0f, 1.0f, 1.0f
    )

    private val vertexCoord2 = floatArrayOf(
            -1.0f, 1.0f, 1.0f,
            0.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f
    )

    override fun getVertexShader(): String {
        return "layout(location = 0) attribute vec3 vertexCoord;" +
                "void main(){" +
                "gl_Position = vec4(vertexCoord,1.0);" +
                "}"
    }

    override fun getFragmentShader(): String {
        return "void main(){" +
                "gl_FragColor = vec4(0.5f,0.5f,0.5f,1.0f);" +
                "}"
    }

    override fun config(drawerConfig: DrawerConfig?) {
        GLES30.glGenBuffers(2, vbo, 0)
        config1()
        config2()
    }

    private fun config2() {
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0])
        val buffer = ByteBuffer.allocateDirect(vertexCoord1.size * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(vertexCoord1).position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexCoord1.size * FLOAT_SIZE, buffer, GLES30.GL_STATIC_DRAW)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 4 * 3, 0)
        GLES30.glEnableVertexAttribArray(0)
        GLESHelper.checkError()
    }

    private fun config1() {
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[1])
        val buffer = ByteBuffer.allocateDirect(vertexCoord2.size * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(vertexCoord2).position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexCoord2.size * FLOAT_SIZE, buffer, GLES30.GL_STATIC_DRAW)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 4 * 3, 0)
        GLES30.glEnableVertexAttribArray(0)
        GLESHelper.checkError()
    }

    override fun draw() {
        GLES30.glUseProgram(program)
        if (drawId % 2 == 0) {
            config1()
        } else {
            config2()
        }
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)
    }

    override fun release() {
        TODO("Not yet implemented")
    }
}