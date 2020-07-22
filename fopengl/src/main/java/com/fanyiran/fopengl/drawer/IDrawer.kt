package com.fanyiran.fopengl.drawer

import com.fanyiran.fopengl.OpenglProgramHelper

abstract class IDrawer {
    val program by lazy { OpenglProgramHelper.createProgram(getVertexShader(), getFragmentShader()) }
    abstract fun getVertexShader(): String
    abstract fun getFragmentShader(): String
    abstract fun config()
    abstract fun draw()
}