package com.fanyiran.fopengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.fanyiran.fopengl.drawer.sample.*
import com.fanyiran.fopengl.drawer.sample.fbo.PipeLineManager

class GlSurfaceViewTest(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {
    private val DRAWER_TYPE = "GLCameraRender"
    private val render by lazy {
        when (DRAWER_TYPE) {
            "VBODrawer" -> GLRender(VBODrawer())
            "VBONormalizeDrawer" -> GLRender(VBONormalizeDrawer())
            "VAODrawer" -> GLRender(VAODrawer())
            "MultiVAODrawer" -> GLRender(MultiVAODrawer())
            "EBODrawer" -> GLRender(EBODrawer())
            "MultiAttributePointDrawer" -> GLRender(MultiAttributePointDrawer())
            "BlingDrawer" -> GLRender(BlingDrawer())
            "TextureDrawer" -> GLRender(TextureDrawer())
            "MultiTextureDrawer" -> GLRender(MultiTextureDrawer())
            "PipeLineManager" -> GLRender(PipeLineManager())
            "GLCameraRender" -> GLCameraRender(PipeLineManager(), this)
            else -> GLRender(PipeLineManager())
        }
    }
    // TODO: 2020/7/24 使用NDK实现？


    init {
        setEGLContextClientVersion(3)//如果使用默认EGLContextFactory和EGLConfigChooser，必须设置这个才会有效果！！！
        setRenderer(render)
//        renderMode = RENDERMODE_CONTINUOUSLY
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}