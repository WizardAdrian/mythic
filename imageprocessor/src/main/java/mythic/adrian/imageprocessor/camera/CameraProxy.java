package mythic.adrian.imageprocessor.camera;

import android.graphics.SurfaceTexture;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.List;

/**
 * Created by Adrian on 2017/6/5.
 * E-mail:aliu@in66.com
 */

public class CameraProxy implements CameraInterface {

    private CameraInterface mCameraInterface;
    private Handler mHandle;
    private HandlerThread mThread;

    public interface OnCameraLifeCircleListener {
        void onCameraOpened(int cameraId);

        void onGetParameter(Parameter p);

        void onSetParameter(Parameter p);

        void onStartPreview();
    }

    private OnCameraLifeCircleListener mOnCameraLifeCircleListener;

    private static abstract class CameraRunnable implements Runnable {
        protected abstract void onRun();

        @Override
        public void run() {
            try {
                onRun();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CameraProxy(CameraInterface cameraInterface, boolean inMainThread, OnCameraLifeCircleListener lifeCircleListener) {
        mCameraInterface = cameraInterface;
        mOnCameraLifeCircleListener = lifeCircleListener;
        if (inMainThread) {
            mHandle = new Handler(Looper.getMainLooper());
        } else {
            mThread = new HandlerThread("CameraSubThread");
            mThread.start();
            mHandle = new Handler(mThread.getLooper());
        }
    }

    @Override
    public int getCurrentCameraId() {
        return mCameraInterface.getCurrentCameraId();
    }

    @Override
    public void openCamera(final int id) {
        mHandle.post(new CameraRunnable() {
            @Override
            public void onRun() {
                mCameraInterface.openCamera(id);
                if (mOnCameraLifeCircleListener != null) {
                    mOnCameraLifeCircleListener.onCameraOpened(id);
                }
            }
        });
    }

    @Override
    public void closeCamera() {
        mHandle.post(new CameraRunnable() {
            @Override
            public void onRun() {
                mCameraInterface.closeCamera();
            }
        });
    }

    @Override
    public void startPreview(final SurfaceTexture texture, final ImageCallBack b) {
        mHandle.post(new CameraRunnable() {
            @Override
            public void onRun() {
                mCameraInterface.startPreview(texture, b);
                if (mOnCameraLifeCircleListener != null) {
                    mOnCameraLifeCircleListener.onStartPreview();
                }
            }
        });
    }

    @Override
    public void stopPreview() {
        mHandle.post(new CameraRunnable() {
            @Override
            public void onRun() {
                mCameraInterface.stopPreview();
            }
        });
    }

    @Override
    public void takePicture(final ImageCallBack b) {
        mHandle.post(new CameraRunnable() {
            @Override
            public void onRun() {
                mCameraInterface.takePicture(b);
            }
        });
    }

    @Override
    public void focus(final FocusCallBack acb, final int x, final int y, final int r) {
        mHandle.post(new CameraRunnable() {
            @Override
            public void onRun() {
                mCameraInterface.focus(acb, x, y, r);
            }
        });
    }

    @Override
    public void getParameter(final Parameter p) {
        mHandle.post(new CameraRunnable() {
            @Override
            public void onRun() {
                mCameraInterface.getParameter(p);
                if (mOnCameraLifeCircleListener != null) {
                    mOnCameraLifeCircleListener.onGetParameter(p);
                }
            }
        });
    }

    @Override
    public void setParameter(final Parameter info, final boolean invalidate) {
        mHandle.post(new CameraRunnable() {
            @Override
            public void onRun() {
                mCameraInterface.setParameter(info, invalidate);
                if (mOnCameraLifeCircleListener != null) {
                    mOnCameraLifeCircleListener.onSetParameter(info);
                }
            }
        });
    }

    @Override
    public Info getInfo() {
        return mCameraInterface.getInfo();
    }

    @Override
    public void invalidate() {
        if (Thread.currentThread().getId() == mThread.getId()) {
            return;
        }

        final ConditionVariable c = new ConditionVariable();
        mHandle.post(new CameraRunnable() {
            @Override
            public void onRun() {
                c.open();
            }
        });
        c.block();
    }

    @Override
    public void quit() {
        mHandle.post(new CameraRunnable() {
            @Override
            public void onRun() {
                mCameraInterface.quit();
            }
        });
    }

    @Override
    public void openFlashLight() {
        mHandle.post(new CameraRunnable() {
            @Override
            public void onRun() {
                mCameraInterface.openFlashLight();
            }
        });
    }

    @Override
    public void closeFlashLight() {
        mHandle.post(new CameraRunnable() {
            @Override
            public void onRun() {
                mCameraInterface.closeFlashLight();
            }
        });
    }

    @Override
    public void focus(final FocusCallBack acb,final List<Photometry> photometryList) {
        mHandle.post(new CameraRunnable() {
            @Override
            public void onRun() {
                mCameraInterface.focus(acb, photometryList);
            }
        });
    }
}
