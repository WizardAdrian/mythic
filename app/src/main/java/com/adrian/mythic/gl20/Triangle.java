package com.adrian.mythic.gl20;

import android.content.Context;
import android.opengl.GLES20;

import com.adrian.mythic.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import mythic.adrian.imageprocessor.utils.TextResourceReader;

/**
 * Created by Adrian on 2017/8/14.
 * E-mail:aliu@in66.com
 */

public class Triangle {
    private FloatBuffer vertexBuffer;

    // 数组中每个顶点的坐标数
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = { // 按逆时针方向顺序:
            0.0f, 0.622008459f, 0.0f,   // top
            -0.5f, -0.311004243f, 0.0f,   // bottom left
            0.5f, -0.311004243f, 0.0f    // bottom right
    };

    // 设置颜色，分别为red, green, blue 和alpha (opacity)
    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    private final int mProgram;

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;

    public Triangle(Context context) {
        // 为存放形状的坐标，初始化顶点字节缓冲
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (坐标数 * 4)float占四字节
                triangleCoords.length * 4);
        // 设用设备的本点字节序
        bb.order(ByteOrder.nativeOrder());

        // 从ByteBuffer创建一个浮点缓冲
        vertexBuffer = bb.asFloatBuffer();
        // 把坐标们加入FloatBuffer中
        vertexBuffer.put(triangleCoords);
        // 设置buffer，从第一个坐标开始读
        vertexBuffer.position(0);

        String vertexShaderCode = TextResourceReader.readTextFileFromResource(context
                , R.raw.triangle_vertex_shader);
        String fragmentShaderCode = TextResourceReader.readTextFileFromResource(context
                , R.raw.triangle_fragment_shader);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // 创建一个空的OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // 将vertex shader添加到program
        GLES20.glAttachShader(mProgram, fragmentShader); // 将fragment shader添加到program
        GLES20.glLinkProgram(mProgram);
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public void draw(float[] mvpMatrix) {
        // 将program加入OpenGL ES环境中
        GLES20.glUseProgram(mProgram);

        // 获取指向vertex shader的成员vPosition的 handle
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // 启用一个指向三角形的顶点数组的handle
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // 准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // 获取指向fragment shader的成员vColor的handle
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // 设置三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // 获得形状的变换矩阵的handle
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // 应用投影和视口变换
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // 禁用指向三角形的顶点数组
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
