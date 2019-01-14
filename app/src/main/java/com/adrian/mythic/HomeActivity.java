package com.adrian.mythic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import mythic.adrian.imageprocessor.camera.CameraInterface;
import mythic.adrian.imageprocessor.camera.CameraUtils;
import mythic.adrian.imageprocessor.utils.BitmapUtil;

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

        findViewById(R.id.btn_switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });

        findViewById(R.id.btn_take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraManager.takePicture(new CameraInterface.ImageCallBack() {
                    @Override
                    public void onHandle(byte[] data, int w, int h, int format) {
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapUtil.getPicFromBytes(data, opts);
                        BitmapUtil.checkBitmap(bitmap);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraManager.onResume();
        launchCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraManager.onPause();
        closeCamera();
    }

    private void initCamera() {
        if (!CameraUtils.checkHardware(this)) {
            finish();
            return;
        }
//        float ratio = (float) 3 / (float) 4;
        float ratio = (float) 9 / (float) 16;

        mCameraManager = new MythicCameraManager(this, true);
        mCameraManager.setRatio(ratio);
        mCameraManager.forbidFocusView(true);

        mFlCameraContainer = findViewById(R.id.fl_camera_container);
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
