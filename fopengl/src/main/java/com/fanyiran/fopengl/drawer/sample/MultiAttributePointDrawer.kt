package com.fanyiran.fopengl.drawer.sample

import android.opengl.GLES30
import com.fanyiran.fopengl.drawer.idrawer.IDrawer
import java.nio.ByteBuffer
import java.nio.ByteOrder

/*
 使用多个顶点属性
 */
class MultiAttributePointDrawer : IDrawer() {
    private val vaoArray = IntArray(1)

    //NOTE 顶点属性的x,y可以取<-1或>1.0f的值，但是z必须在-1到1之间，否则该顶点不能显示,因为已经处于不可见范围
    private val attributeArray = floatArrayOf(
            -0.5f, -0.5f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f,
            0.5f, -0.5f, 1.0f, 0.5f, 0.5f, 0.5f, 1.0f,
            -0.5f, 0.5f, 1.0f, 0.5f, 1.0f, 0.5f, 1.0f,
            0.5f, 0.5f, 1.0f, 0.5f, 0.5f, 1.0f, 1.0f
    )

    private val eboIndice = intArrayOf(
            0, 1, 2, 1, 3, 2
    )

    override fun getVertexShader(): String {
        return "layout(location = 0) attribute vec3 vertexCoord;" +
                "layout(location = 1) attribute vec4 color;" +
                "varying vec4 fragColor;" +
                "void main(){" +
                "gl_Position = vec4(vertexCoord,1.0f);" +
                "fragColor = color;" +
                "}"
    }

    override fun getFragmentShader(): String {
        return "varying vec4 fragColor;" +
                "void main(){" +
                "gl_FragColor = fragColor;" +
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
        val buffer = ByteBuffer.allocateDirect(attributeArray.size * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
                .put(attributeArray).position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, attributeArray.size * FLOAT_SIZE, buffer, GLES30.GL_STATIC_DRAW)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 7 * FLOAT_SIZE, 0)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 7 * FLOAT_SIZE, 3 * FLOAT_SIZE)
        GLES30.glEnableVertexAttribArray(1)
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
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0)
    }

    override fun release() {
        TODO("Not yet implemented")
    }
}