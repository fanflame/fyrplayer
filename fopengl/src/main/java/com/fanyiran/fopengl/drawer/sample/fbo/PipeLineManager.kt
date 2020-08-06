package com.fanyiran.fopengl.drawer.sample.fbo

class PipeLineManager : IDrawerPipeLine() {
    override fun drawSelf(type: TYPE, width: Int, height: Int, data: ByteArray, texture: Int): Int {
        return texture
    }

    override fun getVertexShader(): String? {
        return null
    }

    override fun getFragmentShader(): String? {
        return null
    }

    override fun config() {
        addDrawer(FBODrawer())
        addDrawer(YUVDrawer())
        addDrawer(ScreenDrawer())
    }

    override fun release() {
        // todo 修改pipeline release
        drawerNext?.release()
    }
}