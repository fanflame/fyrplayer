package com.fanyiran.fopengl.drawer.sample

import android.opengl.GLES30
import com.fanyiran.fopengl.GLESHelper
import com.fanyiran.fopengl.drawer.idrawer.IDrawer
import java.nio.ByteBuffer
import java.nio.ByteOrder

/*
 * uniform biling biling
 */
class BlingDrawer : IDrawer() {
    private val vaoArray = IntArray(1)
    private val vertexArray = floatArrayOf(
            -0.5f, -0.5f, 1.0f, 1.0f, 0.5f, 1.0f, 1.0f,
            0.5f, -0.5f, 1.0f, 1.0f, 1.0f, 0.5f, 1.0f,
            -0.5f, 0.5f, 1.0f, 0.5f, 1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f
    )
    private val eboIndice = intArrayOf(
            0, 1, 2, 1, 3, 2
    )

    override fun getVertexShader(): String {
        return "layout(location = 0) attribute vec3 vertexCoord;" +
                "layout(location = 1) attribute vec4 aColor;" +
                "varying vec4 color;" +
                "void main(){" +
                "gl_Position = vec4(vertexCoord,1.0f);" +
                "color = aColor;" +
                "}"
    }

    override fun getFragmentShader(): String {
        return "uniform float alphaFactor;" +
                "varying vec4 color;" +
                "void main(){" +
                "gl_FragColor = color*alphaFactor;" +
                "}"
    }

    override fun config() {
//        GLES30.glEnable(GLES30.GL_CULL_FACE)
//        GLES30.glCullFace(GLES30.GL_BACK)

        GLES30.glGenVertexArrays(1, vaoArray, 0)
        GLES30.glBindVertexArray(vaoArray[0])
        val vboArray = IntArray(1)
        GLES30.glGenBuffers(1, vboArray, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboArray[0])
        val vboBuffer = ByteBuffer.allocateDirect(vertexArray.size * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexArray)
                .position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexArray.size * FLOAT_SIZE, vboBuffer, GLES30.GL_STATIC_DRAW)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 7 * FLOAT_SIZE, 0)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 7 * FLOAT_SIZE, 3 * FLOAT_SIZE)
        GLES30.glEnableVertexAttribArray(1)
        GLESHelper.checkError()

        val eboArray = IntArray(1)
        GLES30.glGenBuffers(1, eboArray, 0)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboArray[0])
        val eboBuffer = ByteBuffer.allocateDirect(eboIndice.size * INT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(eboIndice)
                .position(0)
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboIndice.size * INT_SIZE, eboBuffer, GLES30.GL_STATIC_DRAW)
    }

    override fun draw() {
        GLES30.glUseProgram(program)
        GLES30.glBindVertexArray(vaoArray[0])
        val alphaFactor = GLES30.glGetUniformLocation(program, "alphaFactor")
        GLES30.glUniform1f(alphaFactor, (System.currentTimeMillis() % 1000) / 1000.0f)
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0)
    }

    override fun release() {
        TODO("Not yet implemented")
    }
}