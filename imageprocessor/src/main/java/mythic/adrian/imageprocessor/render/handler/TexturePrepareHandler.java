package mythic.adrian.imageprocessor.render.handler;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Adrian on 2017/8/4.
 * E-mail:aliu@in66.com
 */

public class TexturePrepareHandler extends DrawHandler {

    private int mSurfaceTextureId = -1;
    private SurfaceTexture mSurfaceTexture = null;
    private SurfaceTexture.OnFrameAvailableListener mListener;

    public TexturePrepareHandler(SurfaceTexture.OnFrameAvailableListener listener) {
        mListener = listener;
    }

    @Override
    public Object handleDraw(GL10 gl10) {

        if (getSuccessor() != null) {
            getSuccessor().handleDraw(gl10);
        }
        return null;
    }

    @Override
    public void createAction(Object o, Object o2, Object... params) {
        genSurfaceTexture(mListener);
    }

    @Override
    public void changeAction(Object o, int width, int height) {

    }

    @Override
    public void destroyAction() {

    }

    public void genSurfaceTexture(SurfaceTexture.OnFrameAvailableListener listener) {
        int textureId[] = new int[1];
        GLES20.glGenTextures(1, textureId, 0);
        mSurfaceTextureId = textureId[0];
        mSurfaceTexture = new SurfaceTexture(textureId[0]);
        if (listener != null) {
            mSurfaceTexture.setOnFrameAvailableListener(listener);
        }
    }

    public int getSurfaceTextureId() {
        return mSurfaceTextureId;
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }
}
