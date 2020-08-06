package com.fanyiran.fopengl.drawer.sample

import android.opengl.GLES20
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
    // todo pipeline 不清晰

    private val vertexPoint = intArrayOf(
            Int.MIN_VALUE, Int.MIN_VALUE, 1,
            Int.MAX_VALUE, Int.MIN_VALUE, 1,
            0, Int.MAX_VALUE, 1
    )

    override fun getVertexShader(): String {
        return "layout (location = 0) attribute vec3 vertexCoord;" +
                "void main(){" +
                "gl_Position = vec4(vertexCoord,1.0f);" +
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
        val buffer = ByteBuffer.allocateDirect(vertexPoint.size * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asIntBuffer()
        buffer.put(vertexPoint).position(0)
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexPoint.size * FLOAT_SIZE, buffer, GLES20.GL_STATIC_DRAW)
        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_INT, true, FLOAT_SIZE * 3, 0)
        GLES20.glEnableVertexAttribArray(0)
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