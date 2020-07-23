package com.fanyiran.fopengl.drawer

import android.opengl.GLES30
import com.fanyiran.fopengl.drawer.idrawer.IDrawerSingle
import java.nio.ByteBuffer
import java.nio.ByteOrder

class EBODrawer : IDrawerSingle() {
    private val vaoArray = IntArray(1)
    private val vertexCoord = floatArrayOf(
            -0.5f, -0.5f, 1.0f,
            0.5f, -0.5f, 1.0f,
            -0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 1.0f
    )
    private val eboIndex = intArrayOf(
            0, 1, 2, 1, 3, 2
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

    override fun config() {
        GLES30.glEnable(GLES30.GL_CULL_FACE)
        GLES30.glCullFace(GLES30.GL_BACK)

        GLES30.glGenVertexArrays(1, vaoArray, 0)
        GLES30.glBindVertexArray(vaoArray[0])
        val vboArray = IntArray(1)
        GLES30.glGenBuffers(1, vboArray, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboArray[0])
        val buffer = ByteBuffer.allocateDirect(vertexCoord.size * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexCoord)
                .position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexCoord.size * FLOAT_SIZE, buffer, GLES30.GL_STATIC_DRAW)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 3 * FLOAT_SIZE, 0)
        GLES30.glEnableVertexAttribArray(0)

        val eboArray = IntArray(1)
        GLES30.glGenBuffers(1, eboArray, 0)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboArray[0])
        val eboBuffer = ByteBuffer.allocateDirect(eboIndex.size * INT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(eboIndex)
                .position(0)
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboIndex.size * INT_SIZE, eboBuffer, GLES30.GL_STATIC_DRAW)
    }

    override fun draw() {
        GLES30.glUseProgram(program)
        GLES30.glBindVertexArray(vaoArray[0])
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0)
    }
}