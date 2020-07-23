package com.fanyiran.fopengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.fanyiran.fopengl.drawer.MultiAttributePointDrawer

class GlSurfaceViewTest(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {
    //    private val render by lazy { GLRender(VBODrawer()) }//单独vbo
//    private val render by lazy { GLRender(TowVBOManager()) }//vbo切换
//    private val render by lazy { GLRender(VAODrawer()) }//vao
//    private val render by lazy { GLRender(TowVAODrawer()) }//vao切换
//    private val render by lazy { GLRender(EBODrawer()) }//ebo
    private val render by lazy { GLRender(MultiAttributePointDrawer()) }//multi attribute

    init {
        setEGLContextClientVersion(3)//入股使用默认EGLContextFactory和EGLConfigChooser，必须设置这个才会有效果！！！
        setRenderer(render)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}