//package com.fanyiran.fopengl.drawer
//
//import android.opengl.GLES30
//
//class VAODrawer: IDrawer() {
//    private var vao:Int = 0
//    private val vertexCoord = floatArrayOf(
//            -1f,-1f,0f,
//            1f,-1f,0f,
//            0f,1f,0f
//    )
//    override fun getVertexShader(): String {
//        return "layout(location = 0) vec3 vertexCoord;" +
//                "void main(){" +
//                "gl_Position = vec4(vertexCoord,1.0);" +
//                "}"
//    }
//
//    override fun getFragmentShader(): String {
//        return "" +
//                "void main(){" +
//                "gl_FragColor = vec4(0.5f,0.5f,0.5f,1.0);" +
//                "}"
//    }
//
//    override fun config() {
//        val vaoArray = IntArray(1)
//        GLES30.glGenVertexArrays(1,vaoArray,0)
//        GLES30.glBindVertexArray(vaoArray[0])
//        val vboArray = IntArray(0)
//        GLES30.glGenBuffers(1,vboArray,0)
//        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vboArray[0])
//        GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,4 * 3,0)
//        GLES30.glEnableVertexAttribArray(0)
//    }
//
//    override fun draw() {
//
//    }
//}