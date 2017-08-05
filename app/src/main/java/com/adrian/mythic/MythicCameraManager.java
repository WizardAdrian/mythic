package com.adrian.mythic;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import mythic.adrian.imageprocessor.camera.CameraInterface;
import mythic.adrian.imageprocessor.camera.CameraManager;
import mythic.adrian.imageprocessor.camera.CameraUtils;
import mythic.adrian.imageprocessor.camera.view.CameraView;

/**
 * Created by Adrian on 2017/8/4.
 * E-mail:aliu@in66.com
 */

public class MythicCameraManager extends CameraManager {

    private CameraInterface.ImageCallBack mImageCallBack;
    private MythicRender mRenderer;
    /**
     * w/h
     */
    private float mPreviewRatio;

    private GestureDetector mGesture;

    private boolean forbidFocusView;

    public interface ICheckPermission {
        void isValid(boolean valid);
    }

    public void setICheckPermission(ICheckPermission iCheckPermission) {
        this.iCheckPermission = iCheckPermission;
    }

    private ICheckPermission iCheckPermission;

    public MythicCameraManager(final Activity activity, Object... extra) {
        super(activity, extra);
        mGesture = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
//                 预览区域之外的点击无效
//                if (e.getY() < mTop || e.getY() > mBottom) {
//                    return true;
//                }

                Point point = new Point((int) e.getX(), (int) e.getY());
                if (0 == getCurrentCameraId()) {
                    mCamera.focus(autoFocusCallback, (int) e.getX(), (int) e.getY(), 300);
                    getCameraViewContainer().getFocusView().startFocus(point);
                } else {
                    mCamera.focus(autoFocusCallback, (int) e.getX(), (int) e.getY(), 300);
                    getCameraViewContainer().getFocusView().startFocus(point);
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return true;
            }

        });

        if (getCameraViewContainer() != null) {
            getCameraViewContainer().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (!forbidFocusView) {
                        mGesture.onTouchEvent(motionEvent);
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
    }

    public void setRatio(float ratio) {
        mPreviewRatio = ratio;
    }

    public void forbidFocusView(boolean forbid) {
        forbidFocusView = forbid;
    }

    @Override
    protected void initializeRenderer(Activity activity, Object... extra) {

        mRenderer = new MythicRender(activity, new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });
        mCameraContainer.initialize(new CameraView.OnGLDestroyedListener() {
            @Override
            public void onDestroyed(SurfaceHolder holder) {
                if (null != mRenderer) {
                    mRenderer.onSurfaceDestroyed();
                }
            }
        }, mRenderer);
        mImageCallBack = new CameraInterface.ImageCallBack() {
            @Override
            public void onHandle(byte[] data, int w, int h, int format) {

            }

            @Override
            public void onError(String msg) {

            }
        };
    }

    @Override
    protected void launchCameraInner(final int cameraId, final CameraInterface.Parameter parameters) {
        mRenderer.runOnDraw(new Runnable() {
            @Override
            public void run() {
                final int _degrees = (parameters.degree - CameraUtils.getDisplayDegree((Activity) mRenderer.getContext()) + 360) % 360;
                mRenderer.setRotation(_degrees, parameters.flipH, false);
//                mRenderer.setSize(parameters.previewWidth, parameters.previewHeight);
//                mRenderer.setSizeRatio(0, 0, mPreviewRatio);
//                mRenderer.setScaleType(ClipHandler.SCALE_TYPE_CENTER_INSIDE);
                if (iCheckPermission != null) {
                    iCheckPermission.isValid(parameters.valid);
                }
                mCamera.startPreview(mRenderer.getSurfaceTexture(), mImageCallBack);
                mCamera.invalidate();
            }
        });
        requestRender();
    }

    private final CameraInterface.FocusCallBack autoFocusCallback = new CameraInterface.FocusCallBack() {
        @Override
        public void onHandle(boolean success) {
            if (success) {
                ((Activity) mRenderer.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getCameraViewContainer().getFocusView() != null) {
                            getCameraViewContainer().getFocusView().onFocusSuccess();
                        }
                    }
                });
            } else {
                ((Activity) mRenderer.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getCameraViewContainer().getFocusView() != null) {
                            getCameraViewContainer().getFocusView().onFocusFailed();
                        }
                    }
                });
            }
        }
    };
}
