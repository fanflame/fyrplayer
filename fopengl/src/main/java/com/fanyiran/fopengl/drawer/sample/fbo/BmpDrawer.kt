package com.fanyiran.fopengl.drawer.sample.fbo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.os.Handler
import android.os.Looper
import com.fanyiran.fopengl.MyApplication
import com.fanyiran.fopengl.R
import com.fanyiran.fopengl.drawer.idrawer.DrawerConfig
import java.nio.ByteBuffer
import java.nio.ByteOrder

class BmpDrawer : IDrawerPipeLine() {
    var handler = Handler(Looper.getMainLooper())
    var index: Int = 0

    init {
        handler.postDelayed(object : Runnable {
            override fun run() {
                index++
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private val vaoArray = IntArray(1)

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


    override fun drawSelf(type: TYPE, data: ByteArray, texture: Int): Int {
        GLES30.glUseProgram(program)
        GLES30.glBindVertexArray(vaoArray[0])
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture)
        GLES30.glUniform1i(GLES30.glGetUniformLocation(program, "texture"), 0)
        val bmp = getBmp()
        val pixelBuffer = ByteBuffer.allocateDirect(bmp.byteCount).order(ByteOrder.nativeOrder())
        bmp.copyPixelsToBuffer(pixelBuffer)
        pixelBuffer.position(0)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, bmp.width, bmp.height,
                0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, pixelBuffer)

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0)
        return texture
    }

    private fun getBmp(): Bitmap {
        return BitmapFactory.decodeResource(MyApplication.myApplication.resources,
                if (index % 2 == 0) R.drawable.ic_launcher else R.drawable.awesomeface)
    }

    override fun config(drawerConfig: DrawerConfig?) {
        GLES30.glGenVertexArrays(1, vaoArray, 0)
        GLES30.glBindVertexArray(vaoArray[0])
        val vboArray = IntArray(1)
        GLES30.glGenBuffers(1, vboArray, 0)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboArray[0])
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
}