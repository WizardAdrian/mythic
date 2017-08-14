package com.adrian.mythic;

import android.content.Context;
import android.graphics.SurfaceTexture;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import mythic.adrian.imageprocessor.render.BaseRender;
import mythic.adrian.imageprocessor.render.handler.DrawHandler;

/**
 * Created by Adrian on 2017/8/4.
 * E-mail:aliu@in66.com
 */

public class MythicRender extends BaseRender {

    private ShapeHandler mShapeHandler;

    public MythicRender(Context context, SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener) {
        super(context, onFrameAvailableListener);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        mShapeHandler.createAction(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        mShapeHandler.changeAction(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
    }

    @Override
    protected DrawHandler attachHandler() {
        mShapeHandler = new ShapeHandler(mContext);
        return mShapeHandler;
    }
}
