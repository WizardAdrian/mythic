package mythic.adrian.imageprocessor.camera;

import android.graphics.SurfaceTexture;

import java.util.List;

public interface CameraInterface {
    interface ImageCallBack {
        void onHandle(final byte[] data, int w, int h, int format);

        void onError(String msg);
    }

    interface FocusCallBack {
        void onHandle(boolean success);
    }

    class Parameter {
        public static final int INVALID = -1;
        public static final int FLASH_AUTO = 0;
        public static final int FLASH_OFF = 1;
        public static final int FLASH_ON = 2;
        public static final int FLASH_TORCH = 3;
        public static final int FLASH_RED_EYE = 4;

        public boolean valid = false;
        public int id = -1;
        public int error;
        public int maxPictureSize = -1;
        public int previewWidth;
        public int previewHeight;
        public int degree;
        public int flash;
        public int supportFlash[];
        public float zoomRate = 0.0f;
        public boolean flipH = false;
        public boolean flipV = false;

        public int desiredW;//预览控件的宽，需要根据预览控件的宽高来确定相机的previewSize
        public int desiredH;//预览控件的高，需要根据预览控件的宽高来确定相机的previewSize
    }

    class Info {
        public int mCameraNumber;
    }

    int getCurrentCameraId();

    void openCamera(int id);

    void closeCamera();

    void startPreview(final SurfaceTexture texture, final ImageCallBack b);

    void stopPreview();

    void takePicture(final ImageCallBack b);

    void focus(final FocusCallBack acb, final int x, final int y,
               final int r);

    void getParameter(final Parameter p);

    void setParameter(final Parameter info, final boolean invalidate);

    Info getInfo();

    /* Wait until all command is excuted */
    void invalidate();

    void quit();

    void openFlashLight();

    void closeFlashLight();

    void focus(final FocusCallBack acb, List<Photometry> photometryList);

    class Photometry {

        public Photometry(int x, int y, int r) {
            cx = x;
            cy = y;
            this.r = r;
        }

        public int cx;
        public int cy;
        public int r;
    }
}
