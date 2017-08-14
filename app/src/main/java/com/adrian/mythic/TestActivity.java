package com.adrian.mythic;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.adrian.mythic.gl10.Test;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Adrian on 2017/8/10.
 * E-mail:aliu@in66.com
 */

public class TestActivity extends Activity {

    private OpenGLView mOpenGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mOpenGLView = new OpenGLView(this);
        setContentView(mOpenGLView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOpenGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOpenGLView.onPause();
    }

    public class OpenGLView extends GLSurfaceView {

        private OpenGLRenderer mRenderer;

        public OpenGLView(Context context) {
            super(context);
            mRenderer = new OpenGLRenderer();
            setEGLContextClientVersion(2);
            setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            getHolder().setFormat(PixelFormat.RGBA_8888);
            setRenderer(mRenderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            requestRender();
        }
    }

    public class OpenGLRenderer implements GLSurfaceView.Renderer {

        private Test mTest = new Test(TestActivity.this);

        @Override
        public void onDrawFrame(GL10 gl) {
//            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//            gl.glLoadIdentity();

            mTest.onDrawFrame(gl);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
//            gl.glViewport(0, 0, width, height);
            mTest.onSurfaceChanged(gl, width, height);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//            gl.glShadeModel(GL10.GL_SMOOTH);
//            gl.glClearColor(0f, 0f, 0f, 0f);
//            gl.glClearDepthf(1.0f);
//            gl.glEnable(GL10.GL_DEPTH_TEST);
//            gl.glDepthFunc(GL10.GL_LEQUAL);
//            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
            mTest.onSurfaceCreated(gl, config);
        }
    }
}
