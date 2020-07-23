package com.fanyiran.fopengl.drawer.idrawer

abstract class IDrawer {
    val FLOAT_SIZE = 4
    abstract fun config()
    abstract fun draw()
}