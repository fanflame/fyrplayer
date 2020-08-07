package com.fanyiran.fopengl.drawer.sample

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import com.fanyiran.fopengl.MyApplication
import com.fanyiran.fopengl.R
import com.fanyiran.fopengl.drawer.idrawer.DrawerConfig
import com.fanyiran.fopengl.drawer.idrawer.IDrawer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TextureDrawer : IDrawer() {
    private val vaoArray = IntArray(1)
    private val texture = IntArray(1)
    private val vertexCoord = floatArrayOf(
            -0.5f, -0.5f, 1.0f,
            0.5f, -0.5f, 1.0f,
            -0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 1.0f
    )

    //NOTE Android 中纹理坐标(0,0)点在左上角,x/y轴方向与view一致
    private val textureCoord = floatArrayOf(
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    )

    override fun getVertexShader(): String {
        return "layout(location = 0) attribute vec3 vertexCoord;" +
                "layout(location = 1) attribute vec2 inTextureCoord;" +
                "varying vec2 textureCoord;" +
                "void main(){" +
                "gl_Position = vec4(vertexCoord,1.0f);" +
                "textureCoord = inTextureCoord;" +
                "}"
    }

    override fun getFragmentShader(): String {
        return "uniform sampler2D textureSampler;" +
                "varying vec2 textureCoord;" +
                "void main(){" +
                "gl_FragColor = texture2D(textureSampler,textureCoord);" +
                "}"
    }

    override fun config(drawerConfig: DrawerConfig?) {
        GLES30.glEnable(GLES30.GL_CULL_FACE)
        GLES30.glCullFace(GLES30.GL_BACK)

        GLES30.glGenVertexArrays(1, vaoArray, 0)
        GLES30.glBindVertexArray(vaoArray[0])
        val vbo = IntArray(2)
        GLES30.glGenBuffers(2, vbo, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0])
        val vertexBuffer = ByteBuffer.allocateDirect(vertexCoord.size * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexCoord)
                .position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexCoord.size * FLOAT_SIZE, vertexBuffer, GLES30.GL_STATIC_DRAW)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 3 * FLOAT_SIZE, 0)
        GLES30.glEnableVertexAttribArray(0)

        // NOTE 这里故意使用一个VAO绑定两个VAO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[1])
        val textureCoordBuffer = ByteBuffer.allocateDirect(textureCoord.size * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureCoord)
                .position(0)
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, textureCoord.size * FLOAT_SIZE, textureCoordBuffer, GLES30.GL_STATIC_DRAW)
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 2 * FLOAT_SIZE, 0)
        GLES30.glEnableVertexAttribArray(1)

        genTexture()

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

    private fun genTexture() {
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        var textureSamplerLocation = GLES30.glGetUniformLocation(program, "textureSampler")
        GLES30.glUseProgram(program)
        GLES30.glUniform1i(textureSamplerLocation, 1)

        GLES30.glGenTextures(1, texture, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        var bitmap = BitmapFactory.decodeResource(MyApplication.myApplication.resources, R.drawable.ic_launcher)

        bufferData(bitmap)
//        bufferData1(bitmap) NOTE 这个方法也可以实现
        bitmap.recycle()
    }


    private fun bufferData(bitmap: Bitmap) {
        var textureBuffer = ByteBuffer.allocateDirect(bitmap.byteCount)
                .order(ByteOrder.nativeOrder())
        bitmap.copyPixelsToBuffer(textureBuffer)
        textureBuffer.position(0)

        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,
                0, GLES30.GL_RGBA, bitmap.width, bitmap.height, 0,
                GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, textureBuffer)
    }

    private fun bufferData1(bitmap: Bitmap) = GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)

    override fun draw() {
        GLES30.glUseProgram(program)
        GLES30.glBindVertexArray(vaoArray[0])
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0)
    }

    override fun release() {
        TODO("Not yet implemented")
    }
}