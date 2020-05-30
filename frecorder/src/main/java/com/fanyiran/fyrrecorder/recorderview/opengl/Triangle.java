package com.fanyiran.fyrrecorder.recorderview.opengl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Triangle {
    private static final String TAG = "Triangle";

    private String vertextShaderSource =
//            "uniform mat4 uMVPMatrix;"+
            "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "attribute vec4 vPosition;" +
                    "void main(){" +
//                    "    gl_Position = vPosition*uMVPMatrix;" +
                    "    v_TextureCoordinates = a_TextureCoordinates;" +
                    "    gl_Position = vPosition;" +
                    "}";
    private String fragmentShaderSource =
            "varying vec2 v_TextureCoordinates;" +
                    "uniform simpler2D u_TextureUnit;" +
                    "uniform vec4 vColor;" +
                    "void main(){" +
                    "gl_FragColor = texture2D(u_TextureUnit,v_TextureCoordinates);" +
//                    "    gl_FragColor=vColor;" +
                    "}";

    private int COORDS_PER_VERTEX = 3;
    private float[] triangleCoords = {
            -0.5f, 0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f
    };

    private short[] drawOrder = {
            0, 1, 2, 0, 2, 3
    };

    private float[] color = {
            .1f, .5f, .1f, 1
    };

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawOrderBuffer;

    private int program;

    public Triangle() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length * Float.SIZE / 8).order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);


        ByteBuffer orderBuffer = ByteBuffer.allocateDirect(drawOrder.length * Short.SIZE / 8).order(ByteOrder.nativeOrder());
        drawOrderBuffer = orderBuffer.asShortBuffer();
        drawOrderBuffer.put(drawOrder);
        drawOrderBuffer.position(0);

        int vertexSharder = cretaeShader(GLES20.GL_VERTEX_SHADER, vertextShaderSource);
        int fragmentSharder = cretaeShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource);
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexSharder);
        GLES20.glAttachShader(program, fragmentSharder);
        GLES20.glLinkProgram(program);
    }

    private int cretaeShader(int glAttachedShaders, String shaderSource) {
        int shader = GLES20.glCreateShader(glAttachedShaders);
        GLES20.glShaderSource(shader, shaderSource);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private int loadTexture() {
        int[] textureids = new int[1];
        GLES20.glGenTextures(1, textureids, 0);
        if (textureids[0] == 0) {
            return -1;
        }
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureids[0]);
//        GLES20.glTexImage2D(textureids[0],);
        return textureids[0];
    }

    public void draw(float[] matrix) {
        GLES20.glUseProgram(program);
        int index = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(index);
        GLES20.glVertexAttribPointer(index, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, COORDS_PER_VERTEX * 4, vertexBuffer);
        int colorLocation = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glUniform4fv(colorLocation, 1, color, 0);

//        int positionLocation = GLES20.glGetUniformLocation(program,"uMVPMatrix");
//        GLES20.glUniformMatrix4fv(positionLocation,1,false,matrix,0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6,GLES20.GL_SHORT ,drawOrderBuffer);

        GLES20.glDisableVertexAttribArray(index);
    }
}
