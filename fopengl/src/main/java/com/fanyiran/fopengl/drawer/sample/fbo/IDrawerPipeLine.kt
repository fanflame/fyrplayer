package com.fanyiran.fopengl.drawer.sample.fbo

import com.fanyiran.fopengl.drawer.idrawer.DrawerConfig
import com.fanyiran.fopengl.drawer.idrawer.IDrawer

abstract class IDrawerPipeLine : IDrawer() {
    enum class TYPE {
        LOCAL_IMAGE,
        YUV_NV21
    }
    protected var drawerNext: IDrawerPipeLine? = null

    override fun draw() {
        TODO("Not yet implemented")
    }

    fun draw(type: TYPE, data: ByteArray, texture: Int) {
        var resultTexture = drawSelf(type, data, texture)
        drawerNext?.draw(type, data, resultTexture)
    }

    abstract fun drawSelf(type: TYPE, data: ByteArray, texture: Int): Int

    fun addDrawer(drawer: IDrawerPipeLine, drawerConfig: DrawerConfig?) {
        if (drawerNext == null) {
            drawerNext = drawer
            drawer.config(drawerConfig)
        } else {
            drawerNext?.addDrawer(drawer, drawerConfig)
        }
    }

    fun deleteDrawer(drawer: IDrawerPipeLine) {
        if (drawerNext == drawer) {
            drawerNext = drawer.drawerNext
        } else {
            drawerNext?.deleteDrawer(drawer)
        }
    }

//    override fun release() {
//        drawerNext?.release()
//    }
}