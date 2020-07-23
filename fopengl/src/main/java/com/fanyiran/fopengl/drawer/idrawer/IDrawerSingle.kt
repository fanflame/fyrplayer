package com.fanyiran.fopengl.drawer.idrawer

import com.fanyiran.fopengl.GLESHelper

abstract class IDrawerSingle : IDrawer() {
    val program by lazy { GLESHelper.createProgram(getVertexShader(), getFragmentShader()) }
    abstract fun getVertexShader(): String
    abstract fun getFragmentShader(): String
}