package mythic.adrian.imageprocessor.render;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import mythic.adrian.imageprocessor.render.handler.DrawCameraHandler;
import mythic.adrian.imageprocessor.render.handler.TexturePrepareHandler;

/**
 * Created by Adrian on 2017/8/4.
 * E-mail:aliu@in66.com
 */

public abstract class BaseRender implements GLSurfaceView.Renderer {

    private TexturePrepareHandler mTexturePrepareHandler;
    private DrawCameraHandler mDrawCameraHandler;
    protected Context mContext;

    public BaseRender(Context context) {
        mContext = context;
        mRunOnDraw = new ConcurrentLinkedQueue<>();
        mRunOnDrawEnd = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTexturePrepareHandler = new TexturePrepareHandler(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {

            }
        });
        mDrawCameraHandler = new DrawCameraHandler(mContext, getSurfaceTexture(), getSurfaceTextureId());

        mTexturePrepareHandler.createAction(gl, config);
        mDrawCameraHandler.createAction(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mTexturePrepareHandler.changeAction(gl, width, height);
        mDrawCameraHandler.changeAction(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        runAll(mRunOnDraw);
        mTexturePrepareHandler.handleDraw(gl);
        runAll(mRunOnDrawEnd);
    }

    public void onSurfaceDestroyed() {
        mTexturePrepareHandler.destroyAction();
    }

    public int getSurfaceTextureId() {
        return mTexturePrepareHandler.getSurfaceTextureId();
    }

    public SurfaceTexture getSurfaceTexture() {
        return mTexturePrepareHandler.getSurfaceTexture();
    }

    public Context getContext() {
        return mContext;
    }

    private final ConcurrentLinkedQueue<Runnable> mRunOnDraw;//绘制队列1
    private final ConcurrentLinkedQueue<Runnable> mRunOnDrawEnd;//绘制队列2

    /**
     * 将任务添加到绘制队列1中
     *
     * @param runnable 需要在onDrawFrame中执行的任务
     */
    public void runOnDraw(final Runnable runnable) {
        mRunOnDraw.add(runnable);
    }

    /**
     * 将任务添加到绘制队列2中
     *
     * @param runnable 需要在onDrawFrame中执行的任务，时机与队列1有所不同
     */
    public void runOnDrawEnd(final Runnable runnable) {
        mRunOnDrawEnd.add(runnable);
    }

    private void runAll(ConcurrentLinkedQueue<Runnable> queue) {
        while (!queue.isEmpty()) {
            queue.poll().run();
        }
    }
}
