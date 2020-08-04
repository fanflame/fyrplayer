package com.fanyiran.fopengl.drawer.idrawer

import com.fanyiran.fopengl.GLESHelper

abstract class IDrawer {
    val program by lazy { GLESHelper.createProgram(getVertexShader(), getFragmentShader()) }
    val FLOAT_SIZE: Int
        get() = 4
    val INT_SIZE: Int
        get() = 4

    abstract fun getVertexShader(): String?
    abstract fun getFragmentShader(): String?
    abstract fun config()

    //    abstract fun config(drawerConfig: DrawerConfig)
    abstract fun draw()
    abstract fun release()
}