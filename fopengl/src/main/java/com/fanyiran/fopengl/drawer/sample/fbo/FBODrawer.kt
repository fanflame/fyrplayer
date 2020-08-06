package com.fanyiran.fopengl.drawer.sample.fbo

import android.opengl.GLES30
import com.fanyiran.utils.LogUtil

class FBODrawer : IDrawerPipeLine() {
    private val TAG = "FBODrawer"
    private val fboArray = IntArray(1)
    private val textureArray = IntArray(1)

    override fun getVertexShader(): String? {
        return null
    }

    override fun getFragmentShader(): String? {
        return null
    }

    override fun config() {
        GLES30.glGenFramebuffers(1, fboArray, 0)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboArray[0])

        GLES30.glGenTextures(1, textureArray, 0)
        textureID = textureArray[0]
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureArray[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 200,
                200, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null)

        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, textureArray[0], 0)
        val checkStatus = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER)
        if (checkStatus != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            LogUtil.v(TAG, "fb not complete")
        }
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
    }

    override fun release() {
        TODO("Not yet implemented")
    }

    override fun drawSelf() {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboArray[0])
    }
}