package com.fanyiran.fopengl.drawer

import android.opengl.GLES30
import android.os.Handler
import com.fanyiran.fopengl.OpenglProgramHelper
import com.fanyiran.fopengl.drawer.idrawer.IDrawer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TowVBOManager : IDrawer() {
    val vbo = IntArray(2)
    val program1 by lazy { OpenglProgramHelper.createProgram(getVertexShader(), getFragmentShader()) }
    var drawId = 1
    val handle: Handler = Handler()

    init {
        handle.postDelayed(object : Runnable {
            override fun run() {
                drawId = if (drawId == 1) {
                    2
                } else {
                    1
                }
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

    fun getVertexShader(): String {
        return "layout(location = 0) attribute vec3 vertexCoord;" +
                "void main(){" +
                "gl_Position = vec4(vertexCoord,1.0);" +
                "}"
    }

    fun getFragmentShader(): String {
        return "void main(){" +
                "gl_FragColor = vec4(0.5f,0.5f,0.5f,1.0f);" +
                "}"
    }

    override fun config() {
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
        OpenglProgramHelper.checkError()
    }

    private fun config1() {
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[1])
        val buffer = ByteBuffer.allocateDirect(vertexCoord2.size * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(vertexCoord2).position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexCoord2.size * FLOAT_SIZE, buffer, GLES30.GL_STATIC_DRAW)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 4 * 3, 0)
        GLES30.glEnableVertexAttribArray(0)
        OpenglProgramHelper.checkError()
    }

    override fun draw() {
        GLES30.glUseProgram(program1)
        if (drawId == 1) {
            config1()
        } else {
            config2()
        }
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)
    }
}