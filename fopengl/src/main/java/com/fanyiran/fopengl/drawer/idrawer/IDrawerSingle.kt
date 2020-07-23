package com.fanyiran.fopengl.drawer.idrawer

import com.fanyiran.fopengl.OpenglProgramHelper

abstract class IDrawerSingle : IDrawer() {
    val program by lazy { OpenglProgramHelper.createProgram(getVertexShader(), getFragmentShader()) }
    abstract fun getVertexShader(): String
    abstract fun getFragmentShader(): String
}