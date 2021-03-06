package com.fanyiran.fopengl.drawer.sample.fbo

import android.opengl.GLES30
import com.fanyiran.fopengl.drawer.idrawer.DrawerConfig
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ScreenDrawer : IDrawerPipeLine() {
    private var drawerConfig: DrawerConfig? = null

    private val pointAttributeData by lazy {
        floatArrayOf(
                -1f, -1f, 1.0f, 0f, 0f,
                1f, -1f, 1.0f, 0f, 1f,
                -1f, 1f, 1.0f, 1f, 0f,
                1f, 1f, 1.0f, 1f, 1f
        )
    }

    private val vao = IntArray(1)

    override fun getVertexShader(): String {
        return "layout(location = 0) attribute vec3 vertexCoord;" +
                "layout(location = 1) attribute vec2 textureCoordIn;" +
                "varying vec2 textureCoord;" +
                "void main(){" +
                "gl_Position = vec4(vertexCoord,1.0);" +
                "textureCoord = textureCoordIn;" +
                "}"
    }

    override fun getFragmentShader(): String {
        return "uniform sampler2D texture;" +
                "varying vec2 textureCoord;" +
                "void main(){" +
                "gl_FragColor = texture2D(texture,textureCoord);" +
                "}"
    }

    override fun pointAttributeData(): FloatArray {
        return pointAttributeData
    }

    override fun config(drawerConfig: DrawerConfig?) {
        this.drawerConfig = drawerConfig
        GLES30.glGenVertexArrays(1, vao, 0)
        GLES30.glBindVertexArray(vao[0])

        val vbo = IntArray(1)
        GLES30.glGenBuffers(1, vbo, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0])
        val vboBuffer = ByteBuffer.allocateDirect(pointAttributeData().size * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(pointAttributeData())
                .position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, pointAttributeData().size * FLOAT_SIZE, vboBuffer, GLES30.GL_STATIC_DRAW)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 5 * FLOAT_SIZE, 0)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 5 * FLOAT_SIZE, 3 * FLOAT_SIZE)
        GLES30.glEnableVertexAttribArray(1)


        val ebo = IntArray(1)
        GLES30.glGenBuffers(1, ebo, 0)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ebo[0])
        val eboBuffer = ByteBuffer.allocateDirect(eboIndice.size * INT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(eboIndice)
                .position(0)
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboIndice.size * INT_SIZE, eboBuffer, GLES30.GL_STATIC_DRAW)
    }

    override fun drawSelf(type: TYPE, data: ByteArray, texture: Int): Int {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        GLES30.glViewport(0, 0, drawerConfig!!.viewWidth, drawerConfig!!.viewHeight);
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)


        GLES30.glUseProgram(program)
        GLES30.glBindVertexArray(vao[0])
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture)
        GLES30.glUniform1i(GLES30.glGetUniformLocation(program, "texture"), 0)
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0)
        return texture
    }

    override fun release() {
        TODO("Not yet implemented")
    }
}