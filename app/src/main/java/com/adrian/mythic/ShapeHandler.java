package com.adrian.mythic;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.adrian.mythic.gl20.Triangle;

import javax.microedition.khronos.opengles.GL10;

import mythic.adrian.imageprocessor.render.handler.DrawHandler;

/**
 * Created by Adrian on 2017/8/14.
 * E-mail:aliu@in66.com
 */

public class ShapeHandler extends DrawHandler {

    private Context mContext;
    private Triangle mTriangle;
    private float[] mMVPMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];

    public ShapeHandler(Context context) {
        mContext = context;
    }

    @Override
    public Object handleDraw(GL10 gl10) {

        // 设置相机的位置(视口矩阵)
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // 计算投影和视口变换
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

        // 绘制形状
        mTriangle.draw(mMVPMatrix);

        if (getSuccessor() != null) {
            getSuccessor().handleDraw(gl10);
        }
        return null;
    }

    @Override
    public void createAction(Object o, Object o2, Object... params) {
        mTriangle = new Triangle(mContext);
    }

    @Override
    public void changeAction(Object o, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // 此投影矩阵在onDrawFrame()中将应用到对象的坐标
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void destroyAction() {

    }
}
