package com.adrian.mythic;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import mythic.adrian.imageprocessor.camera.CameraUtils;

/**
 * Created by Adrian on 2017/8/4.
 * E-mail:aliu@in66.com
 */

public class HomeActivity extends Activity {

    private MythicCameraManager mCameraManager;
    private int width;
    private int height;
    private FrameLayout mFlCameraContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mythic_activity_home);

        initCamera();
    }

    private void initCamera() {
        if (!CameraUtils.checkHardware(this)) {
           finish();
            return;
        }
        float ratio = (float) 3 / (float) 4;

        mCameraManager = new MythicCameraManager(this, true);
        mCameraManager.setRatio(ratio);

        mFlCameraContainer = (FrameLayout) findViewById(R.id.fl_camera_container);
        width = CameraUtils.getScreenWidth(this);
        height = (int) (width / ratio);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        mFlCameraContainer.addView(mCameraManager.getCameraViewContainer(), 0, layoutParams);
    }

    private void launchCamera() {
        mCameraManager.launchCamera(1, width, height, 9999, true);
    }

    private void closeCamera() {
        mCameraManager.closeCamera();
    }

    /**
     * 切换前后置摄像头
     */
    private void switchCamera() {
        mCameraManager.switchCamera(9999, width, height, true);
    }
}
