package com.fanyiran.fopengl.drawer

import android.opengl.GLES20
import com.fanyiran.fopengl.OpenglProgramHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder

class VBODrawer : IDrawer() {
    private val program by lazy { OpenglProgramHelper.createProgram(getVertexShader(), getFragmentShader()) }
    private val vertexPoint = floatArrayOf(
            -1f, -1f, 0.0f,
            1f, -1f, 0.0f,
            0.0f, 1f, 0.0f
    )

    private fun getVertexShader(): String {
        return "layout (location = 0) attribute vec3 vertexCoord;" +
                "void main(){" +
                "gl_Position = vec4(vertexCoord,1.0f);" +
                "}"
    }

    private fun getFragmentShader(): String {
        return "void main(){" +
                "gl_FragColor = vec4(0.5f,1.0f,0.5f,1.0f);" +
                "}"
    }

    override fun config() {
        val vbo = IntArray(1)
        GLES20.glGenBuffers(1, vbo, 0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0])
        val buffer = ByteBuffer.allocateDirect(vertexPoint.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(vertexPoint).position(0)
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexPoint.size * 4, buffer, GLES20.GL_STATIC_DRAW)
        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 4 * 3, 0)
        GLES20.glEnableVertexAttribArray(0)
        OpenglProgramHelper.checkError()
    }

    override fun draw() {
        GLES20.glUseProgram(program)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
    }
}