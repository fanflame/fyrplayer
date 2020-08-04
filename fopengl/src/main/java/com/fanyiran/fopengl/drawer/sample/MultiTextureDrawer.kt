package com.fanyiran.fopengl.drawer.sample

import android.graphics.BitmapFactory
import android.opengl.GLES30
import com.fanyiran.fopengl.GLESHelper
import com.fanyiran.fopengl.MyApplication
import com.fanyiran.fopengl.R
import com.fanyiran.fopengl.drawer.idrawer.IDrawer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MultiTextureDrawer : IDrawer() {
    private val vaoArray = IntArray(1)
    private val vertexArray = floatArrayOf(
            -.5f, -.5f, 1f, 0f, 1f,
            .5f, -.5f, 1f, 1f, 1f,
            -.5f, .5f, 1f, 0f, 0f,
            .5f, .5f, 1f, 1f, 0f
    )

    private val eboIndice = intArrayOf(
            0, 1, 2, 1, 3, 2
    )

    override fun getVertexShader(): String {
        return "attribute vec3 vertexCoord;" +
                "attribute vec2 texCoordIn;" +
                "varying vec2 texCoord;" +
                "void main(){" +
                "gl_Position = vec4(vertexCoord,1.0f);" +
                "texCoord = texCoordIn;" +
                "}"
    }

    override fun getFragmentShader(): String {
        return "uniform sampler2D tex1;" +
                "uniform sampler2D tex2;" +
                "varying vec2 texCoord;" +
                "void main(){" +
                "gl_FragColor = mix(texture2D(tex2,texCoord),texture2D(tex1,texCoord),0.8f);" +
                "}"
    }

    override fun config() {
        GLES30.glGenVertexArrays(1, vaoArray, 0)
        GLES30.glBindVertexArray(vaoArray[0])

        val vboArray = IntArray(1)
        GLES30.glGenBuffers(1, vboArray, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboArray[0])
        val attributeBuffer = ByteBuffer.allocateDirect(vertexArray.size * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexArray)
                .position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexArray.size * FLOAT_SIZE, attributeBuffer, GLES30.GL_STATIC_DRAW)
        val vertexPosition = GLES30.glGetAttribLocation(program, "vertexCoord")
        GLES30.glVertexAttribPointer(vertexPosition, 3, GLES30.GL_FLOAT, false, 5 * FLOAT_SIZE, 0)
        GLES30.glEnableVertexAttribArray(vertexPosition)
        val texCoordLocaion = GLES30.glGetAttribLocation(program, "texCoordIn")
        GLES30.glVertexAttribPointer(texCoordLocaion, 2, GLES30.GL_FLOAT, false, 5 * FLOAT_SIZE, 3 * FLOAT_SIZE)
        GLES30.glEnableVertexAttribArray(texCoordLocaion)

        val eboArray = IntArray(1)
        GLES30.glGenBuffers(1, eboArray, 0)
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboArray[0])
        val eboBuffer = ByteBuffer.allocateDirect(eboIndice.size * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(eboIndice)
                .position(0)
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboIndice.size * FLOAT_SIZE, eboBuffer, GLES30.GL_STATIC_DRAW)

        genTex()
    }

    private fun genTex() {
        val textureArray = IntArray(2)
        GLES30.glGenTextures(2, textureArray, 0)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureArray[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glUseProgram(program)// NOTE 如果不调用这句，下边的glUniform1i无效
        GLES30.glUniform1i(GLES30.glGetUniformLocation(program, "tex1"), 1)
        val bmp1 = BitmapFactory.decodeResource(MyApplication.myApplication.resources, R.drawable.ic_launcher)
        val bmp1Buffer = ByteBuffer.allocateDirect(bmp1.byteCount)
                .order(ByteOrder.nativeOrder())
        bmp1.copyPixelsToBuffer(bmp1Buffer)
        bmp1Buffer.position(0)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, bmp1.width, bmp1.height,
                0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, bmp1Buffer)
        bmp1.recycle()

        GLES30.glActiveTexture(GLES30.GL_TEXTURE12)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureArray[1])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glUniform1i(GLES30.glGetUniformLocation(program, "tex2"), 12)
        val bmp2 = BitmapFactory.decodeResource(MyApplication.myApplication.resources, R.drawable.awesomeface)
        val bmp2Buffer = ByteBuffer.allocateDirect(bmp2.byteCount)
                .order(ByteOrder.nativeOrder())
        bmp2.copyPixelsToBuffer(bmp2Buffer)
        bmp2Buffer.position(0)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, bmp2.width, bmp2.height,
                0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, bmp2Buffer)
        bmp2.recycle()
    }

    override fun draw() {
        GLES30.glUseProgram(program)
        GLES30.glBindVertexArray(vaoArray[0])
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0)
        GLESHelper.checkError()
    }

    override fun release() {
        TODO("Not yet implemented")
    }
}