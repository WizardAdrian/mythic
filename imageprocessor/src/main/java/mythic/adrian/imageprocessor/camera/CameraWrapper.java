package mythic.adrian.imageprocessor.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import mythic.adrian.imageprocessor.utils.DisplayUtil;
import mythic.adrian.imageprocessor.utils.ExceptionUtil;

/**
 * Created by Adrian on 2017/6/5.
 * E-mail:aliu@in66.com
 */

public final class CameraWrapper implements CameraInterface {

    private static final String TAG = "CameraWrapper";

    private Context mContext;

    private AudioManager mAudioManager;

    private Camera mCamera;
    private int mCameraId;

    private Handler mHandler;
    private HandlerThread mHandlerThread;

    private BufferWrapper mBuffer;

    private Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
    private Camera.Parameters mParameters;

    private Parameter mCameraInfoCustom;

    private ImageCallBack mCallBack;

    private Camera.PictureCallback mTakePictureCallBack;

    private AtomicBoolean mIsTakingPicture = new AtomicBoolean(false);
    private AtomicBoolean mPreviewOpen = new AtomicBoolean(false);

    private int mError;
    private Camera.ErrorCallback mErrHandler = new Camera.ErrorCallback() {
        @Override
        public void onError(int error, Camera camera) {
            mError = error;
            Log.e(TAG, "err: " + error);
        }
    };

    private Runnable mForceTakePicture = new Runnable() {
        @Override
        public void run() {
            boolean forbidSound = mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
            if (null != mAudioManager) {
                try {
                    forbidSound |= mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) == 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!forbidSound) {
                mCamera.takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {
                        /* 必须加此回调，不然拍照没有声音 */
                    }
                }, null, mTakePictureCallBack);
            } else {
                mCamera.takePicture(null, null, mTakePictureCallBack);
            }
        }
    };

    public CameraWrapper(Context context) {
        mContext = context;
        mHandlerThread = new HandlerThread("Camera1");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mAudioManager = (AudioManager) (mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE));
        mBuffer = new BufferWrapper();
    }

    @Override
    public int getCurrentCameraId() {
        return mCameraId;
    }

    @Override
    public void openCamera(int id) {
        if (null == mCamera) {
            try {
                mCamera = Camera.open(id);
                mCameraId = id;
            } catch (Exception e1) {
                try {
                    mCamera = Camera.open();
                    mCameraId = 0;
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            Log.i(TAG, "openCamera: " + mCamera);
            if (null == mCamera) {
                return;
            }
            setParameters();
            mError = 0;
            mCamera.setErrorCallback(mErrHandler);
        }
    }

    @Override
    public void closeCamera() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        try {
            mCamera.setErrorCallback(null);
            stopPreview();
        } catch (RuntimeException e) {
            Log.e(TAG, "closeCamera: " + e);
        }
        if (null != mCamera) {
            mCamera.release();
        }
        mCamera = null;
        mTakePictureCallBack = null;
    }

    @Override
    public void startPreview(SurfaceTexture texture, ImageCallBack callBack) {
        if (null == mCamera) {
            return;
        }
        if (mPreviewOpen.get()) {
            return;
        }
        mCallBack = callBack;
        if (null != texture) {
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        resetPreviewStatus();
        mPreviewOpen.set(true);
    }

    @Override
    public void stopPreview() {
        if (null == mCamera) {
            Log.i(TAG, "Error in stopPreview, not open!!! JXT");
            return;
        }
        if (mPreviewOpen.get()) {
            /** PS：
             * mCamera.setPreviewCallback(null);关闭时要
             * 将mCamera的预览从mPreviewCallback中释放出来
             * 否则mCamera更换前置后置摄像头时，导致出错
             * @司南
             */
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mPreviewOpen.set(false);
            mBuffer.release();
        }
    }

    @Override
    public void takePicture(final ImageCallBack imageCallBack) {
        mIsTakingPicture.set(true);
        if (null == mCamera) {
            return;
        }
        if (!mPreviewOpen.get()) {
            return;
        }
        invalidateParameters();
        final Camera.PictureCallback pic_callback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] arg0, Camera arg1) {
                try {
                    resetPreviewStatus();
                } catch (RuntimeException e) {
                    Log.e(TAG, "onPictureTaken: " + e);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (mContext != null && mContext instanceof Activity && !((Activity) mContext).isFinishing()) {
                                    if (mCamera != null) {
                                        mCamera.startPreview();
                                    }
                                }
                            } catch (RuntimeException e) {
                                Log.e(TAG, "onPictureTaken again: " + e);
                            }
                        }
                    }, 1000);
                }
                mPreviewOpen.set(true);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageCallBack.onHandle(arg0, 1, arg0.length, 0);
                        mIsTakingPicture.set(false);
                        if (mContext != null && mContext instanceof Activity && !((Activity) mContext).isFinishing()) {
                            if (mCamera != null) {
                                try {
                                    mCamera.startPreview();
                                } catch (RuntimeException e) {
                                    String log = ExceptionUtil.getErrorString(new Throwable(e));
                                    imageCallBack.onError(log);
                                }
                            }
                        }
                    }
                });
            }
        };
        mPreviewOpen.set(false);
        mTakePictureCallBack = pic_callback;
        mCamera.cancelAutoFocus();
        if (!Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE.equals(mParameters.getFocusMode())
                && !Camera.Parameters.FLASH_MODE_OFF.equals(mParameters.getFlashMode())
                && Build.MODEL.equals("MI 3")) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    mHandler.removeCallbacks(mForceTakePicture);
                    mCamera.takePicture(null, null, mTakePictureCallBack);
                }
            });
            mHandler.postDelayed(mForceTakePicture, 5000);
            return;
        }
        mForceTakePicture.run();
    }

    @Override
    public void focus(final FocusCallBack focusCallBack, int x, int y, int r) {
        if (null == mCamera || !mPreviewOpen.get()) {
            return;
        }
        mCamera.cancelAutoFocus();
        // 拍照模式的连续自动对焦
//        mParameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        Camera.AutoFocusCallback callback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (null != focusCallBack) {
                    focusCallBack.onHandle(success);
                }
            }
        };
        if (r < 0) {
            String flash = mParameters.getFlashMode();
            invalidateParameters();
            if (null == flash || flash.equals(Camera.Parameters.FLASH_MODE_OFF)) {
                mCamera.autoFocus(callback);
            }
            return;
        }

        //目前设置的测光、对焦区域为相同区域
        List<Camera.Area> areas = CameraUtils.computeAreas(mCameraId, x, y, r, 1000);
        //设置测光区域
        if (mParameters.getMaxNumMeteringAreas() > 0) {
            mParameters.setMeteringAreas(areas);
        }
        if (null == focusCallBack) {
            //应用层设置无须对焦，只用补光
            invalidateParameters();
            return;
        }
        //设置对焦区域
        if (mParameters.getMaxNumFocusAreas() > 0) {
            mParameters.setFocusAreas(areas);
        }
        invalidateParameters();
        mCamera.autoFocus(callback);
    }

    @Override
    public void focus(final FocusCallBack focusCallBack, List<Photometry> photometryList) {
        if (null == mCamera || !mPreviewOpen.get()) {
            return;
        }
        mCamera.cancelAutoFocus();
        // 拍照模式的连续自动对焦
//        mParameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        Camera.Parameters parameters = mParameters;

        Camera.AutoFocusCallback callback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (null != focusCallBack) {
                    focusCallBack.onHandle(success);
                }
            }
        };

        List<Camera.Area> areas = new ArrayList<>();

        if (photometryList == null || photometryList.size() == 0) {
            String flash = mParameters.getFlashMode();
            invalidateParameters();
            if (null == flash || flash.equals(Camera.Parameters.FLASH_MODE_OFF)) {
                mCamera.autoFocus(callback);
            }
            return;
        }
        for (Photometry photometry : photometryList) {
            areas.add(CameraUtils.computeArea(mCameraId, photometry.cx, photometry.cy, photometry.r, photometry.weight));
        }

        //对焦（补光）区域策略，首先先判断手机支持最大的对焦区域个数以及外部传入的对焦区域个数，
        //均大于0才调用setMeteringAreas
        int maxNumMeteringAreas = parameters.getMaxNumMeteringAreas();
        if (maxNumMeteringAreas > 0 && areas.size() > 0) {
            if (maxNumMeteringAreas >= areas.size()) {
                parameters.setMeteringAreas(areas);
            } else {
                List<Camera.Area> tmp = new ArrayList<>();
                tmp.add(areas.get(0));
                parameters.setMeteringAreas(tmp);
            }
        }
        if (null == focusCallBack) {
            /* 应用层设置无须对焦，只用补光 */
            invalidateParameters();
            return;
        }
        // 不支持设置自定义聚焦，则使用自动聚焦，返回
        if (parameters.getMaxNumFocusAreas() > 0) {
            parameters.setFocusAreas(areas);
        }
        invalidateParameters();
        mCamera.autoFocus(callback);
    }

    @Override
    public void getParameter(Parameter parameter) {
        parameter.valid = null != mCamera;
        parameter.error = mError;
        if (null == mCamera) {
            return;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, cameraInfo);
        parameter.degree = cameraInfo.orientation;
        if (null == mCamera) {
            return;
        }
        parameter.flipH = cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
        Camera.Parameters p = mParameters;
        if (null == p) {
            parameter.valid = false;
            return;
        }
        parameter.previewWidth = p.getPreviewSize().width;
        parameter.previewHeight = p.getPreviewSize().height;
        parameter.flash = CameraUtils.getFlashStatusInt(p.getFlashMode());
        Log.i(TAG, "getParameter preview width: " + parameter.previewWidth + " height: " + parameter.previewHeight);

        List<String> flashmodes = p.getSupportedFlashModes();
        parameter.supportFlash = null;
        if (null != flashmodes && flashmodes.size() > 0) {
            int num;
            ArrayList<Integer> flashmodes_map = new ArrayList<>();
            for (int i = 0; i < flashmodes.size(); ++i) {
                int v = CameraUtils.getFlashStatusInt(flashmodes.get(i));
                if (Parameter.INVALID != v && !flashmodes_map.contains(v)) {
                    flashmodes_map.add(v);
                }
            }
            num = flashmodes_map.size();
            if (num >= 1) {
                parameter.supportFlash = new int[num];
                for (int i = 0; i < flashmodes_map.size(); ++i) {
                    parameter.supportFlash[i] = flashmodes_map.get(i);
                }
            }
        }
    }

    @Override
    public void setParameter(Parameter info, boolean invalidate) {
        if (null == mCamera) {
            return;
        }
        Camera.Parameters parameters = mParameters;
        // if (p.isZoomSupported()) {
        // int value = (int) (info.zoomRate * p.getMaxZoom());
        // p.setZoom(value);
        // }
        if (info.maxPictureSize > 0) {
            setSize(parameters, info.maxPictureSize, info.desiredW, info.desiredH);
        }
        if (invalidate) {
            invalidateParameters();
        }
        info.previewWidth = parameters.getPreviewSize().width;
        info.previewHeight = parameters.getPreviewSize().height;
        mCameraInfoCustom = info;
    }

    @Override
    public Info getInfo() {
        Info info = new Info();
        info.mCameraNumber = Camera.getNumberOfCameras();
        return info;
    }

    @Override
    public void invalidate() {

    }

    @Override
    public void quit() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            mHandlerThread.quitSafely();
//        } else {
//            mHandlerThread.quit();
//        }
        mHandlerThread.quit();
    }

    @Override
    public void openFlashLight() {
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeFlashLight() {
        try {
            Camera.Parameters parameter = mCamera.getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取相机的parameters以及cameraInfo
     */
    private void setParameters() {
        Camera.Parameters param;
        try {
            param = mCamera.getParameters();
        } catch (Exception e) {
            mCamera = null;
            return;
        }
        /* 防止出现条纹（小米手机的auto反条纹模式有问题） */
        List<String> supportAnti = param.getSupportedAntibanding();
        final String needAntiMode = Camera.Parameters.ANTIBANDING_50HZ;
        if (null != supportAnti && supportAnti.contains(needAntiMode)) {
            param.setAntibanding(needAntiMode);
        }
        param.setJpegQuality(100);
        Camera.getCameraInfo(mCameraId, mCameraInfo);
        mParameters = param;
    }

    private void resetPreviewStatus() {
        invalidateParameters();
        mCamera.stopPreview();
        if (null != mCallBack) {
            final int width = mCamera.getParameters().getPreviewSize().width;
            final int height = mCamera.getParameters().getPreviewSize().height;
            mBuffer.refreshCache(width, height);
            mCamera.addCallbackBuffer(mBuffer.writeBuffer());
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] arg0, Camera arg1) {
                    mBuffer.swapBuffers();
                    if (mCamera != null) {
                        mCamera.addCallbackBuffer(mBuffer.writeBuffer());
                        int w = width;
                        int h = height;
                        int format = ImageFormat.NV21;
                        if (mParameters != null && mParameters.getPreviewSize() != null) {
                            w = mParameters.getPreviewSize().width;
                            h = mParameters.getPreviewSize().height;
                            format = mParameters.getPreviewFormat();
                        }
                        mCallBack.onHandle(arg0, w, h, format);
                    }
                }
            });
        }
        if (!mIsTakingPicture.get()) {
            mCamera.startPreview();
        }
    }

    /**
     * 重新设置相机的parameters
     */
    private void invalidateParameters() {
        try {
            String mode = CameraUtils.getFlashStatusString(mCameraInfoCustom.flash);
            if (null != mode && null != mParameters.getSupportedFlashModes()
                    && mParameters.getSupportedFlashModes().contains(mode)) {
                mParameters.setFlashMode(mode);
            }
            /** PS： 设置视频格式为NV21 */
            mParameters.setPreviewFormat(ImageFormat.NV21);
            // some device not support 40000.need to check rollback to 30000
            //mParameters.setPreviewFpsRange(15000, 30000);
            mCamera.setParameters(mParameters);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e(TAG, "invalidateParameters ========================");
            Log.e(TAG, "invalidateParameters error: " + e);
            Log.e(TAG, "invalidateParameters thread name: " + Thread.currentThread().getName());
            Log.e(TAG, "invalidateParameters thread id: " + Thread.currentThread().getId());
            Log.e(TAG, "invalidateParameters ========================");
            try {
                mCamera.setParameters(mParameters);
                mParameters = mCamera.getParameters();
            } catch (RuntimeException e2) {
                e2.printStackTrace();
            }
        }
    }

    private void setSize(Camera.Parameters p, int maxPictureSize, int desiredW, int desiredH) {
        //add by xiaosheng 尝试设置合适的比例
        Camera.Size jpegSize = getOptimizeSize(p.getSupportedPictureSizes(), DisplayUtil.getScreenWidth() * 3 / 2, DisplayUtil.getScreenHeight() * 3 / 2, maxPictureSize);

        // didn't work very will on some phone. set max size to 1280*720
        if (desiredW == 0 || desiredH == 0) {
            desiredW = DisplayUtil.getScreenWidth();
            desiredH = DisplayUtil.getScreenHeight();
        }
        int maxW = 1280;
//        int maxH = 1280 * DisplayUtil.getScreenWidth() / DisplayUtil.getScreenHeight();
        int maxH = 720;
        Camera.Size previewSize = getOptimizeSize(p.getSupportedPreviewSizes(), desiredW, desiredH, maxW, maxH);

        if (null != jpegSize && null != previewSize) {
            p.setPictureSize(jpegSize.width, jpegSize.height);
            p.setPreviewSize(previewSize.width, previewSize.height);
            Log.i(TAG, "jpegSize.width=: " + jpegSize.width + ", jpegSize.height=: " + jpegSize.height);
            Log.i(TAG, "previewSize.width=: " + previewSize.width + ", previewSize.height=: " + previewSize.height);
        }
    }

    private static Camera.Size getOptimizeSize(List<Camera.Size> list, int w, int h, int maxW, int maxH) {
        float maxRatio = 0.0f;
        Camera.Size proper = null;//最合适的尺寸

        /**
         *记录屏幕显示比例，使结果图片的比例与显示比例接近或者相同
         *   0.11 是个经验值，防止某些安卓机器特殊的比例
         *@author xiaosheng
         *@time 2016/11/27 17:45
         *@小圣添加的注释
         */
        float disPlayRatio = (w * 1.0f) / (h * 1.0f);
        disPlayRatio = disPlayRatio > 1.0f ? 1.0f / disPlayRatio : disPlayRatio;//记录屏幕显示比例

        for (Camera.Size s : list) {
            float ratio = CameraUtils.getRatio(s, w, h);
            float srcRatio = (s.height * 1.0f / s.width * 1.0f);
            srcRatio = srcRatio > 1.0f ? 1.0f / srcRatio : srcRatio;
            //  0.11 是个经验值，防止某些安卓机器特殊的比例
            if (ratio > maxRatio && s.width <= maxW && s.height <= maxH && Math.abs(disPlayRatio - srcRatio) < 0.11) {
                maxRatio = ratio;
                proper = s;
            }
        }

        if (proper == null) {
            for (Camera.Size s : list) {
                float ratio = CameraUtils.getRatio(s, w, h);
                float srcRatio = (s.height * 1.0f / s.width * 1.0f);
                srcRatio = srcRatio > 1.0f ? 1.0f / srcRatio : srcRatio;
                if (ratio > maxRatio && Math.abs(disPlayRatio - srcRatio) < 0.11) {
                    maxRatio = ratio;
                    proper = s;
                }
            }
        }
        return proper;
    }

    private static Camera.Size getOptimizeSize(List<Camera.Size> list, int w, int h, int maxWH) {
        float maxRatio = 0.0f;
        Camera.Size proper = null;
        /**
         *记录屏幕显示比例，使结果图片的比例与显示比例接近或者相同
         *
         *  0.11 是个经验值，防止某些安卓机器特殊的比例
         *@author xiaosheng
         *@time 2016/11/27 17:45
         *@小圣添加的注释
         */
        float disPlayRatio = (w * 1.0f) / (h * 1.0f);
        disPlayRatio = disPlayRatio > 1.0f ? 1.0f / disPlayRatio : disPlayRatio;//记录屏幕显示比例
        for (Camera.Size s : list) {
            float ratio = CameraUtils.getRatio(s, w, h);
            float srcRatio = (s.height * 1.0f / s.width * 1.0f);
            srcRatio = srcRatio > 1.0f ? 1.0f / srcRatio : srcRatio;
            //  0.11 是个经验值，防止某些安卓机器特殊的比例
            if (ratio > maxRatio && s.width <= maxWH && s.height <= maxWH && Math.abs(disPlayRatio - srcRatio) < 0.11) {
                maxRatio = ratio;
                proper = s;
            }
        }

        if (proper == null) {
            for (Camera.Size s : list) {
                float ratio = CameraUtils.getRatio(s, w, h);
                float srcRatio = (s.height * 1.0f / s.width * 1.0f);
                srcRatio = srcRatio > 1.0f ? 1.0f / srcRatio : srcRatio;
                if (ratio > maxRatio && Math.abs(disPlayRatio - srcRatio) < 0.11) {
                    maxRatio = ratio;
                    proper = s;
                }
            }
        }
        return proper;
    }
}
