package com.fanyiran.fopengl.drawer.sample.fbo

import android.opengl.GLES30
import com.fanyiran.fopengl.drawer.idrawer.DrawerConfig
import com.fanyiran.utils.LogUtil

class FBODrawer : IDrawerPipeLine() {
    private var drawerConfig: DrawerConfig? = null
    private val TAG = "FBODrawer"
    private val fboArray = IntArray(1)
    private val textureArray = IntArray(1)

    override fun drawSelf(type: TYPE, data: ByteArray, texture: Int): Int {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboArray[0])
        GLES30.glClearColor(0.5f, 1.0f, 1.0f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glViewport(0, 0, drawerConfig!!.previewWidth, drawerConfig!!.previewHeight);
        return textureArray[0]
    }

    override fun getVertexShader(): String? {
        return null
    }

    override fun getFragmentShader(): String? {
        return null
    }

    override fun config(drawerConfig: DrawerConfig?) {
        this.drawerConfig = drawerConfig
        GLES30.glPixelStorei(GLES30.GL_UNPACK_ALIGNMENT, 1)
        GLES30.glGenFramebuffers(1, fboArray, 0)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboArray[0])

        GLES30.glGenTextures(1, textureArray, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureArray[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, drawerConfig!!.previewWidth,
                drawerConfig.previewHeight, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null)

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
}