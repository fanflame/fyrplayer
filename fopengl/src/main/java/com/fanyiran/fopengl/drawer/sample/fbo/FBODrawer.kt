package com.fanyiran.fopengl.drawer.sample.fbo

import android.graphics.BitmapFactory
import android.opengl.GLES30
import com.fanyiran.fopengl.MyApplication
import com.fanyiran.fopengl.R
import com.fanyiran.utils.LogUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FBODrawer : IDrawerPipeLine() {
    private val TAG = "FBODrawer"
    private val fboArray = IntArray(1)
    private val textureArray = IntArray(1)
    private val vaoArray = IntArray(1)
    private val vertexCoordArray = floatArrayOf(
            -0.5f, -0.5f, 1.0f, 0f, 1f,
            0.5f, -0.5f, 1.0f, 1f, 1f,
            -0.5f, 0.5f, 1.0f, 0f, 0f,
            0.5f, 0.5f, 1.0f, 1f, 0f
    )

    private val eboIndice = intArrayOf(
            0, 1, 2, 1, 3, 2
    )

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
        return "varying vec2 textureCoord;" +
                "uniform sampler2D texture;" +
                "void main(){" +
                "gl_FragColor = texture2D(texture,textureCoord);" +
                "}"
    }

    override fun config() {
        configFBO()
        GLES30.glGenVertexArrays(1, vaoArray, 0)
        GLES30.glBindVertexArray(vaoArray[0])
        val vboArray = IntArray(1)
        GLES30.glGenBuffers(1, vboArray, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboArray[0])
        val vboBuffer = ByteBuffer.allocateDirect(vertexCoordArray.size * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexCoordArray)
                .position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexCoordArray.size * FLOAT_SIZE, vboBuffer, GLES30.GL_STATIC_DRAW)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 5 * FLOAT_SIZE, 0)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 5 * FLOAT_SIZE, 3 * FLOAT_SIZE)
        GLES30.glEnableVertexAttribArray(1)

        val eboArray = IntArray(1)
        GLES30.glGenBuffers(1, eboArray, 0)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboArray[0])
        val eboBuffer = ByteBuffer.allocateDirect(eboIndice.size * INT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer().put(eboIndice).position(0)
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboIndice.size * INT_SIZE, eboBuffer, GLES30.GL_STATIC_DRAW)
    }

    override fun release() {
        TODO("Not yet implemented")
    }

    private fun configFBO() {
        GLES30.glGenFramebuffers(1, fboArray, 0)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboArray[0])

        GLES30.glGenTextures(1, textureArray, 0)
        textureID = textureArray[0]
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureArray[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 100,
                100, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null)

        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, textureArray[0], 0)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        val checkStatus = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER)
        if (checkStatus != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            LogUtil.v(TAG, "fb not complete")
        }
    }

    override fun drawSelf() {
        GLES30.glUseProgram(program)
        GLES30.glBindVertexArray(vaoArray[0])
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboArray[0])
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureArray[0])
        GLES30.glUniform1i(GLES30.glGetUniformLocation(program, "texture"), 0)
        val bmp = BitmapFactory.decodeResource(MyApplication.myApplication.resources, R.drawable.ic_launcher)
        val pixelBuffer = ByteBuffer.allocateDirect(bmp.byteCount).order(ByteOrder.nativeOrder())
        bmp.copyPixelsToBuffer(pixelBuffer)
        pixelBuffer.position(0)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, bmp.width, bmp.height,
                0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, pixelBuffer)

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0)
    }
}