package mythic.adrian.imageprocessor.camera.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

/**
 * Created by Adrian on 2017/6/6.
 * E-mail:aliu@in66.com
 */

public class CameraView extends GLSurfaceView {
    public final static String TAG = "CameraView";
    public SurfaceHolder holder;

    public interface OnGLDestroyedListener {
        /**
         * run on GL thread
         */
        void onDestroyed(SurfaceHolder holder);
    }

    private OnGLDestroyedListener mOnGLDestroyedListener;

    public void setOnGLDestroyedListener(OnGLDestroyedListener listener) {
        mOnGLDestroyedListener = listener;
    }

    public CameraView(Context context) {
        super(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initialize(Renderer render) {
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        setRenderer(render);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        requestRender();
    }

    @Override
    public void setRenderer(Renderer r) {
        super.setRenderer(r);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mOnGLDestroyedListener != null) {
            mOnGLDestroyedListener.onDestroyed(holder);
        }
        super.surfaceDestroyed(holder);
    }
}
