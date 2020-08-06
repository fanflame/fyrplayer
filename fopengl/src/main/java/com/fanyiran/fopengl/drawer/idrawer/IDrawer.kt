package com.fanyiran.fopengl.drawer.idrawer

import com.fanyiran.fopengl.GLESHelper

abstract class IDrawer {
    private val pointAttributeData by lazy {
        floatArrayOf(
                -0.5f, -0.5f, 1.0f, 0f, 1f,
                0.5f, -0.5f, 1.0f, 1f, 1f,
                -0.5f, 0.5f, 1.0f, 0f, 0f,
                0.5f, 0.5f, 1.0f, 1f, 0f
        )
    }

    protected val eboIndice by lazy {
        intArrayOf(
                0, 1, 2, 1, 3, 2
        )
    }
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

    open fun pointAttributeData(): FloatArray {
        return pointAttributeData
    }
}