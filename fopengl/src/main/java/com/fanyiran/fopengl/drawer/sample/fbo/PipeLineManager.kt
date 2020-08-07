package com.fanyiran.fopengl.drawer.sample.fbo

import com.fanyiran.fopengl.drawer.idrawer.DrawerConfig

class PipeLineManager : IDrawerPipeLine() {
    override fun drawSelf(type: TYPE, data: ByteArray, texture: Int): Int {
        return texture
    }

    override fun getVertexShader(): String? {
        return null
    }

    override fun getFragmentShader(): String? {
        return null
    }

    override fun config(drawerConfig: DrawerConfig?) {
        addDrawer(FBODrawer(), drawerConfig)
        addDrawer(YUVDrawer(), drawerConfig)
        addDrawer(ScreenDrawer(), drawerConfig)
    }

    override fun release() {
        // todo 修改pipeline release
        drawerNext?.release()
    }
}