package com.fanyiran.fopengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.fanyiran.fopengl.drawer.TowVBOManager

class GlSurfaceViewTest(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {
    //    private val render by lazy { GLRender(VBODrawer()) }
    private val render by lazy { GLRender(TowVBOManager()) }

    init {
        setEGLContextClientVersion(3)//入股使用默认EGLContextFactory和EGLConfigChooser，必须设置这个才会有效果！！！
        setRenderer(render)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}