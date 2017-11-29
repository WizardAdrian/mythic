package mythic.adrian.imageprocessor.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.view.Surface;
import android.view.View;

import java.lang.reflect.Method;

import mythic.adrian.imageprocessor.camera.view.CameraContainer;
import mythic.adrian.imageprocessor.utils.DisplayUtil;


/**
 * Created by Adrian on 2017/6/6.
 * E-mail:aliu@in66.com
 */

public abstract class CameraManager {

    protected final CameraProxy mCamera;
    protected final CameraContainer mCameraContainer;

    private final CameraInterface.Parameter mParameters = new CameraInterface.Parameter();

    private int desiredW;
    private int desiredH;
    private int maxPictureSize;

    private boolean mAutoFocus;

    public CameraManager(Activity activity, Object... extra) {
        mCamera = new CameraProxy(new CameraWrapper(activity), false, new CameraProxy.OnCameraLifeCircleListener() {
            @Override
            public void onCameraOpened(int cameraId) {
                mCamera.getParameter(mParameters);
            }

            @Override
            public void onGetParameter(CameraInterface.Parameter p) {
                mParameters.maxPictureSize = setMaxSPictureSize(maxPictureSize);
                mParameters.desiredW = desiredW;
                mParameters.desiredH = desiredH;
                mCamera.setParameter(mParameters, false);
                mCamera.focus(null, 0, 0, -1);
//                setDisplay(mParameters,mCamera);
            }

            @Override
            public void onSetParameter(CameraInterface.Parameter p) {
                launchCameraInner(mCamera.getCurrentCameraId(), p);
            }

            @Override
            public void onStartPreview() {
                checkPermission(mParameters.valid);
                if (mAutoFocus) {
                    mCamera.focus(null, 0, 0, -1);
                }
            }
        });
        mCameraContainer = new CameraContainer(activity);
        DisplayUtil.init(activity.getApplication());
        initializeRenderer(activity, extra);
    }

    public void launchCamera(int cameraId, int desiredW, int desiredH, int maxPictureSize, boolean autoFocus) {
        this.desiredW = desiredW;
        this.desiredH = desiredH;
        this.maxPictureSize = maxPictureSize;
        this.mAutoFocus = autoFocus;
        mCamera.openCamera(cameraId);
    }

    public void switchCamera(int maxPictureSize, int desiredW, int desiredH, boolean autoFocus) {
        int cameraNum = 2;
        closeCamera();
        int newCameraId = (mCamera.getCurrentCameraId() + 1) % cameraNum;
        launchCamera(newCameraId, maxPictureSize, desiredW, desiredH, autoFocus);
    }

    public void closeCamera() {
        mCamera.closeCamera();
    }

    public int getCurrentCameraId() {
        return mCamera.getCurrentCameraId();
    }

    public void requestRender() {
        mCameraContainer.requestRender();
    }

    public void onResume(){
        mCameraContainer.onResume();
    }

    public void onPause() {
        mCameraContainer.onPause();
    }

    protected abstract void initializeRenderer(Activity activity, Object... extra);

    protected abstract void launchCameraInner(int cameraId, CameraInterface.Parameter parameters);

    protected abstract void checkPermission(boolean hasPermission);

    public CameraContainer getCameraViewContainer() {
        return mCameraContainer;
    }

    public void setCameraViewTouchEvent(View.OnTouchListener listener) {
        if (mCameraContainer != null) {
            mCameraContainer.setCameraViewTouchEvent(listener);
        }
    }

    private int setMaxSPictureSize(int maxPictureSize) {
        int limitSize = CameraUtils.determinSizeFromFreq(CameraUtils.getMaxCpuFreq());
        if (maxPictureSize > limitSize) {
            maxPictureSize = limitSize;
        }
        return maxPictureSize;
    }

    /**
     * 控制图像的正确显示方向
     */
    private void setDisplay(Camera camera) {
        setDisplayOrientation(camera, 90);
    }

    /**
     * 实现的图像的正确显示
     */
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}
