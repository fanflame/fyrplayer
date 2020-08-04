package com.fanyiran.fopengl.drawer.sample

import android.opengl.GLES20
import com.fanyiran.fopengl.GLESHelper
import com.fanyiran.fopengl.drawer.idrawer.IDrawer
import java.nio.ByteBuffer
import java.nio.ByteOrder
/*
 使用vbo
 */
class VBODrawer : IDrawer() {
    // TODO: 2020/7/28 fbo 实现滤镜
    // TODO: 2020/7/28 yuv转纹理
    // TODO:glShaderBinary
    //  TODO:glTexSubImage2D
    // TODO: 3D  多个
    // todo 矩阵
    // todo 顶点坐标不归一化？即不是在-1到1之间的数值，

    private val vertexPoint = floatArrayOf(
            -1f, -1f,
            1f, -1f,
            0.0f, 1f
    )

    override fun getVertexShader(): String {
        return "layout (location = 0) attribute vec4 vertexCoord;" +
                "void main(){" +
                "gl_Position = vertexCoord;" +
                "}"
    }

    override fun getFragmentShader(): String {
        return "void main(){" +
                "gl_FragColor = vec4(0.5f,1.0f,0.5f,1.0f);" +
                "}"
    }

    override fun config() {
        val vbo = IntArray(1)
        GLES20.glGenBuffers(1, vbo, 0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0])
        val buffer = ByteBuffer.allocateDirect(vertexPoint.size * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(vertexPoint).position(0)
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexPoint.size * FLOAT_SIZE, buffer, GLES20.GL_STATIC_DRAW)
        GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, FLOAT_SIZE * 2, buffer)
        GLES20.glEnableVertexAttribArray(0)
        GLESHelper.checkError()
    }

    override fun draw() {
        //NOTE 如果只有一个vbo,其他什么都不画，不需要每次调用config()
        //NOTE 如果有两个vbo切换，需要每次调用自己的config()，详见 {@link com.fanyiran.fopengl.drawer.TowVBOManager}
        GLES20.glUseProgram(program)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
    }

    override fun release() {
        TODO("Not yet implemented")
    }
}