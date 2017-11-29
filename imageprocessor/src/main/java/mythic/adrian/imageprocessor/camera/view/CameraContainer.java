package mythic.adrian.imageprocessor.camera.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


/**
 * Created by Adrian on 2017/6/6.
 * E-mail:aliu@in66.com
 */

public class CameraContainer extends FrameLayout {

    private CameraView mCameraView;
    private CameraFocusImageView mFocusView;

    public CameraContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CameraContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraContainer(Context context) {
        super(context);
    }

    public void initialize(CameraView.OnGLDestroyedListener listener) {
        mCameraView = new CameraView(getContext());
        mCameraView.setOnGLDestroyedListener(listener);
        addView(mCameraView);

        mFocusView = new CameraFocusImageView(getContext());
        final int value = ViewGroup.LayoutParams.WRAP_CONTENT;
        LayoutParams params = new LayoutParams(value, value);
        params.gravity = Gravity.LEFT | Gravity.TOP;
        mFocusView.setLayoutParams(params);
        addView(mFocusView);
    }

    public void initialize(CameraView.OnGLDestroyedListener listener, GLSurfaceView.Renderer renderer) {
        mCameraView = new CameraView(getContext());
        mCameraView.setOnGLDestroyedListener(listener);
        mCameraView.initialize(renderer);
        addView(mCameraView);
    }

    public CameraView getCameraView() {
        return mCameraView;
    }

    public CameraFocusImageView getFocusView() {
        return mFocusView;
    }

    public void requestRender() {
        mCameraView.requestRender();
    }

    public void onResume() {
        mCameraView.onResume();
    }

    public void onPause() {
        mCameraView.onPause();
    }

    public void setCameraViewTouchEvent(OnTouchListener listener) {
        if (mCameraView != null) {
            mCameraView.setOnTouchListener(listener);
        }
    }
}
