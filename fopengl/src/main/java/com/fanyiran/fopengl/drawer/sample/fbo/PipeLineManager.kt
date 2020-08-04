package com.fanyiran.fopengl.drawer.sample.fbo

class PipeLineManager : IDrawerPipeLine() {
    override fun draw() {
        drawerNext?.draw()
    }

    override fun drawSelf() {
    }

    override fun getVertexShader(): String? {
        return null
    }

    override fun getFragmentShader(): String? {
        return null
    }

    override fun config() {
        addDrawer(FBODrawer())
        addDrawer(BmpDrawer())
        addDrawer(ScreenDrawer())
    }

    override fun release() {
        // todo 修改pipeline release
        drawerNext?.release()
    }
}