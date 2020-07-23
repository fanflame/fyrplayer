package com.fanyiran.fopengl.drawer

import android.opengl.GLES30
import android.os.Handler
import com.fanyiran.fopengl.drawer.idrawer.IDrawerSingle
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TowVAODrawer : IDrawerSingle() {
    val vaoArray = IntArray(2)
    val vertexCoord = floatArrayOf(
            -1.0f, -1.0f, 1.0f,
            0f, 1f, 1.0f,
            1.0f, -1.0f, 1.0f
    )
    val vertexCoord1 = floatArrayOf(
            -1.0f, 1.0f, 1.0f,
            0f, -1f, 1.0f,
            1.0f, 1.0f, 1.0f
    )
    var index = 0
    var handler = Handler()

    init {
        handler.postDelayed(object : Runnable {
            override fun run() {
                index++
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    override fun getVertexShader(): String {
        return "layout(location = 0) attribute vec3 vertexCoord;" +
                "void main(){" +
                "gl_Position = vec4(vertexCoord,1.0f);" +
                "}"
    }

    override fun getFragmentShader(): String {
        return "void main(){" +
                "gl_FragColor = vec4(0.5f,0.5f,0.5f,1.0f);" +
                "}"
    }

    override fun config() {
        GLES30.glGenVertexArrays(2, vaoArray, 0)

        GLES30.glBindVertexArray(vaoArray[0])
        val vboArray = IntArray(2)
        GLES30.glGenBuffers(2, vboArray, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboArray[0])
        val buffer = ByteBuffer.allocateDirect(vertexCoord.size * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
                .put(vertexCoord).put(vertexCoord1).position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexCoord.size * FLOAT_SIZE, buffer, GLES30.GL_STATIC_DRAW)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 3 * FLOAT_SIZE, 0)
        GLES30.glEnableVertexAttribArray(0)

        GLES30.glBindVertexArray(vaoArray[1])
        //这里不能用同一个vbo,如果用同一个vbo，即使绘制的时候glBindVertexArray(vaoArray[0]),也会显示vertexCoord1的三角形
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboArray[1])
        val buffer1 = ByteBuffer.allocateDirect(vertexCoord1.size * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexCoord1).position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexCoord1.size * FLOAT_SIZE, buffer1, GLES30.GL_STATIC_DRAW)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 3 * FLOAT_SIZE, 0)
        GLES30.glEnableVertexAttribArray(0)
    }

    override fun draw() {
        GLES30.glUseProgram(program)
        if (index % 2 == 0) {
            GLES30.glBindVertexArray(vaoArray[0])
        } else {
            GLES30.glBindVertexArray(vaoArray[1])
        }
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3)
    }
}