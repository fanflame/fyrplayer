package com.fanyiran.fopengl.drawer.sample.fbo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import com.fanyiran.fopengl.MyApplication
import com.fanyiran.fopengl.R
import java.nio.ByteBuffer
import java.nio.ByteOrder

class YUVDrawer : IDrawerPipeLine() {
    protected val ssssss by lazy {
        floatArrayOf(
                -0.7f, -0.5f, 1.0f, 0f, 0f,
                0.7f, -0.5f, 1.0f, 0f, 1f,
                -0.5f, 0.5f, 1.0f, 1f, 0f,
                0.5f, 0.5f, 1.0f, 1f, 1f
        )
    }
    private var lastWidth = 0
    private var lastHeight = 0
    private lateinit var yData: ByteBuffer
    private lateinit var uvData: ByteBuffer
    private val vaoArray = IntArray(1)
    private var yTexture = 0
    private var uvTexture = 0

    override fun draw(data: ByteArray) {
        updateYUV(data, 1280, 720)
        super.draw()
    }

    override fun drawSelf() {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        GLES30.glUseProgram(program)
        GLES30.glBindVertexArray(vaoArray[0])
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, yTexture)
        GLES30.glUniform1i(GLES30.glGetUniformLocation(program, "yTexture"), 0)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE, lastWidth, lastHeight,
                0, GLES30.GL_LUMINANCE, GLES30.GL_UNSIGNED_BYTE, yData)// note 如果用GLES30_RGBA，绘制结果有重复图像

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, uvTexture)
        GLES30.glUniform1i(GLES30.glGetUniformLocation(program, "uvTexture"), 1)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE_ALPHA, lastWidth / 2, lastHeight / 2,
                0, GLES30.GL_LUMINANCE_ALPHA, GLES30.GL_UNSIGNED_BYTE, uvData)

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0)
    }

    private fun getBmp(): Bitmap {
        return BitmapFactory.decodeResource(MyApplication.myApplication.resources, R.drawable.awesomeface)
    }

    private fun getBmp2(): Bitmap {
        return BitmapFactory.decodeResource(MyApplication.myApplication.resources, R.drawable.awesomeface)
    }

    fun updateYUV(byteBuffer: ByteArray, width: Int, height: Int) {
        if (lastWidth != width || lastHeight != height) {
            lastWidth = width
            lastHeight = height
            yData = ByteBuffer.allocateDirect(width * height)//.order(ByteOrder.nativeOrder())
            uvData = ByteBuffer.allocateDirect(width * height / 2)//.order(ByteOrder.nativeOrder())
        }
        yData.clear().position(0)
        uvData.clear().position(0)
        yData.put(byteBuffer, 0, width * height).position(0)
        uvData.put(byteBuffer, width * height, width * height / 2).position(0)
    }

    override fun getVertexShader(): String? {
        return "layout(location = 0) attribute vec3 vertexCoord;" +
                "layout(location = 1) attribute vec2 textureCoordIn;" +
                "varying vec2 textureCoord;" +
                "void main(){" +
                "gl_Position = vec4(vertexCoord,1.0f);" +
                "textureCoord = textureCoordIn;" +
                "}"
    }

    override fun getFragmentShader(): String? {
        return "precision lowp float;" +
                "varying vec2 textureCoord;" +
                "uniform sampler2D yTexture;" +
                "uniform sampler2D uvTexture;" +
                "void main(){" +
                "float y = texture2D(yTexture,textureCoord).r;" +
                "float u = texture2D(uvTexture,textureCoord).a - 0.5f;" +
                "float v = texture2D(uvTexture,textureCoord).r - 0.5f;" +
                "float r = y + 1.13983*v;" +
                "float g = y - 0.39465*u - 0.58060*v;" +
                "float b = y + 2.03211*u;" +
                "gl_FragColor = vec4(r,g,b,1.0f);" +
                "}"
    }

    override fun config() {
        GLES30.glGenVertexArrays(1, vaoArray, 0)
        GLES30.glBindVertexArray(vaoArray[0])
        val bufferArray = IntArray(2)
        GLES30.glGenBuffers(2, bufferArray, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, bufferArray[0])

        val vertexBuffer = ByteBuffer.allocateDirect(ssssss.size * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(ssssss)
                .position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, ssssss.size * FLOAT_SIZE, vertexBuffer, GLES30.GL_STATIC_DRAW)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 5 * FLOAT_SIZE, 0)
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 5 * FLOAT_SIZE, 3 * FLOAT_SIZE)
        GLES30.glEnableVertexAttribArray(1)

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, bufferArray[1])
        val eboBuffer = ByteBuffer.allocateDirect(eboIndice.size * INT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(eboIndice)
                .position(0)
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboIndice.size * INT_SIZE, eboBuffer, GLES30.GL_STATIC_DRAW)

        val textureArray = IntArray(2)
        GLES30.glGenTextures(2, textureArray, 0)
        yTexture = textureArray[0]
        uvTexture = textureArray[1]
        for (i in textureArray) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, i)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        }

    }

    override fun release() {
        TODO("Not yet implemented")
    }
}