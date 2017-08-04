package mythic.adrian.imageprocessor.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mythic.adrian.imageprocessor.utils.DisplayUtil;

/**
 * Created by Adrian on 2017/6/5.
 * E-mail:aliu@in66.com
 */

public class CameraUtils {

    public static int getDisplayDegree(Activity activity) {
        int rotation = (activity).getWindowManager().getDefaultDisplay().getRotation();
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
        return degrees;
    }

    public static int getFlashStatusInt(final String s) {
        if (Camera.Parameters.FLASH_MODE_AUTO.equals(s)) {
            return CameraInterface.Parameter.FLASH_AUTO;
        } else if (Camera.Parameters.FLASH_MODE_OFF.equals(s)) {
            return CameraInterface.Parameter.FLASH_OFF;
        } else if (Camera.Parameters.FLASH_MODE_ON.equals(s)) {
            return CameraInterface.Parameter.FLASH_ON;
        }
        return CameraInterface.Parameter.INVALID;
    }

    public static String getFlashStatusString(int info_id) {
        String s = null;
        switch (info_id) {
            case CameraInterface.Parameter.FLASH_AUTO:
                s = Camera.Parameters.FLASH_MODE_AUTO;
                break;
            case CameraInterface.Parameter.FLASH_OFF:
                s = Camera.Parameters.FLASH_MODE_OFF;
                break;
            case CameraInterface.Parameter.FLASH_ON:
                s = Camera.Parameters.FLASH_MODE_ON;
                break;
            default:
                break;
        }
        return s;
    }

    public static List<Camera.Area> computeAreas(int cameraId, final int x, final int y, final int r) {
        List<Camera.Area> areas = new ArrayList<>();
        float fx = ((float) x / (float) DisplayUtil.getScreenWidth()) * 2000f - 1000f;
        float fy = ((float) y / (float) DisplayUtil.getScreenHeight()) * 2000f - 1000f;
        float fr = (float) r;
        RectF rectf = new RectF(fx - fr, fy - fr, fx + fr, fy + fr);
        if (rectf.left < -1000f) {
            rectf.left = -1000;
        }
        if (rectf.top < -1000f) {
            rectf.top = -1000;
        }
        if (rectf.right > 1000f) {
            rectf.right = 1000;
        }
        if (rectf.bottom > 1000f) {
            rectf.bottom = 1000f;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        Matrix m = new Matrix();
        m.reset();
        m.preRotate(360 - cameraInfo.orientation, 0f, 0f);
        m.mapRect(rectf);
        Rect rect = new Rect();
        rectf.round(rect);

        areas.add(new Camera.Area(rect, 1));
        // Log.i(TAG, "Camera Area " + rect.left + ", " + rect.top + ", "
        // + " -> " + rect.right + ", " + rect.bottom);
        return areas;
    }

    public static float getRatio(Camera.Size size, int width, int height) {
        float h1 = (size.width * size.height) / (float) (width * height);
        if (h1 > 1.0f) {
            h1 = 1.0f / h1;
        }
        return h1;
    }

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();
    }

    public static boolean checkHardware(Activity activity) {
        if (!(activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))) {
            Toast.makeText(activity, "很抱歉，您的设备可能不支持摄像头功能！", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static int determinSizeFromFreq(int freq) {
        final int[] FREQ = {2000000, 1750000, 1500000, 0};
        final int[] SIZE = {8000, 4000, 2500, 2000};
        for (int i = 0; i < FREQ.length; ++i) {
            if (freq > FREQ[i]) {
                return SIZE[i];
            }
        }
        return 2000;
    }

    public static int getMaxCpuFreq() {
        int maxfreq = 0;
        FileReader r = null;
        try {
            r = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
            char[] buffer = new char[32];
            int length = r.read(buffer);
            for (int i = 0; i < length; ++i) {
                if ('0' > buffer[i] || '9' < buffer[i]) {
                    length = i;
                    break;
                }
            }
            if (0 < length) {
                String s = new String(buffer, 0, length);
                maxfreq = Integer.parseInt(s);
            }
            r.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return maxfreq;
    }
}
