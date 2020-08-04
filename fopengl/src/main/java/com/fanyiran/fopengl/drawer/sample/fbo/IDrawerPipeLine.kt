package com.fanyiran.fopengl.drawer.sample.fbo

import com.fanyiran.fopengl.drawer.idrawer.IDrawer

abstract class IDrawerPipeLine : IDrawer() {
    protected var drawerNext: IDrawerPipeLine? = null
    protected var textureID: Int = 0

    fun draw(textureID: Int) {
        this.textureID = textureID
        this.draw()
    }

    override fun draw() {
        drawSelf()
        if (drawerNext != null) {
            drawerNext?.draw(textureID)
        }
    }

    abstract fun drawSelf()

    fun addDrawer(drawer: IDrawerPipeLine) {
        if (drawerNext == null) {
            drawerNext = drawer
            drawer.config()
        } else {
            drawerNext?.addDrawer(drawer)
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