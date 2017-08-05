package mythic.adrian.imageprocessor.render.handler;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import javax.microedition.khronos.opengles.GL10;

import mythic.adrian.imageprocessor.render.DirectDrawer;

/**
 * Created by Adrian on 2017/8/4.
 * E-mail:aliu@in66.com
 */

public class DrawCameraHandler extends DrawHandler {

    private SurfaceTexture mSurfaceTexture;
    private DirectDrawer mDirectDrawer;

    private float mDegrees;
    private boolean mFlipH;
    private boolean mFlipV;

    public DrawCameraHandler() {

    }

    public void setRotation(float degrees, boolean flipH, boolean flipV) {
        mDegrees = degrees;
        mFlipH = flipH;
        mFlipV = flipV;
    }

    public void setSurfaceTexture(Context context, SurfaceTexture surfaceTexture, int surfaceTextureId) {
        mSurfaceTexture = surfaceTexture;
        mDirectDrawer = new DirectDrawer(context, surfaceTextureId, mDegrees, mFlipH, mFlipV);
    }

    @Override
    public Object handleDraw(GL10 gl10) {

        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurfaceTexture.updateTexImage();

        mDirectDrawer.draw();

        if (getSuccessor() != null) {
            getSuccessor().handleDraw(gl10);
        }
        return null;
    }

    @Override
    public void createAction(Object o, Object o2, Object... params) {
        setSurfaceTexture((Context) params[0], (SurfaceTexture) params[1], (int) params[2]);
    }

    @Override
    public void changeAction(Object o, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void destroyAction() {

    }
}
