package com.adrian.mythic.gl10;

import android.content.Context;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Adrian on 2017/8/10.
 * E-mail:aliu@in66.com
 */

public class Test {

    private Context mContext;

    private TextureCube cube;
    private static float angleCube = 0;     // rotational angle in degree for cube
    private static float speedCube = -1.5f; // rotational speed for cube

//    Triangle triangle;     // ( NEW )
//    Square quad;           // ( NEW )
//
//    private float angleTriangle = 0.0f; // (NEW)
//    private float angleQuad = 0.0f;     // (NEW)
//    private float speedTriangle = 0.5f; // (NEW)
//    private float speedQuad = -0.4f;    // (NEW)
//
//    private Pyramid pyramid;    // (NEW)
//    private Cube cube;          // (NEW)
//
//    private static float anglePyramid = 0; // Rotational angle in degree for pyramid (NEW)
//    private static float angleCube = 0;    // Rotational angle in degree for cube (NEW)
//    private static float speedPyramid = 2.0f; // Rotational speed for pyramid (NEW)
//    private static float speedCube = -1.5f;   // Rotational speed for cube (NEW)

    public Test(Context context) {
        mContext = context;

//        triangle = new Triangle();   // ( NEW )
//        quad = new Square();         // ( NEW )
//
//        pyramid = new Pyramid();   // (NEW)
//        cube = new Cube();         // (NEW)

        cube = new TextureCube();
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config){
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);  // Set color's clear-value to black
        gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
        gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
        gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
        gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance

        // Setup Texture, each time the surface is created (NEW)
        cube.loadTexture(gl, mContext);    // Load image into Texture (NEW)
        gl.glEnable(GL10.GL_TEXTURE_2D);  // Enable texture (NEW)
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1;   // To prevent divide by zero
        float aspect = (float)width / height;

        // Set the viewport (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
        gl.glLoadIdentity();                 // Reset projection matrix
        // Use perspective projection
        GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
        gl.glLoadIdentity();                 // Reset
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//
//        gl.glLoadIdentity();                 // Reset model-view matrix
//        gl.glTranslatef(-1.5f, 0.0f, -6.0f); // Translate left and into the screen
//        gl.glRotatef(angleTriangle, 0.0f, 1.0f, 0.0f); // Rotate the triangle about the y-axis (NEW)
//        triangle.draw(gl);                   // Draw triangle
//
//        gl.glLoadIdentity();                 // Reset the mode-view matrix (NEW)
//        gl.glTranslatef(1.5f, 0.0f, -6.0f);  // Translate right and into the screen (NEW)
//        gl.glRotatef(angleQuad, 1.0f, 0.0f, 0.0f); // Rotate the square about the x-axis (NEW)
//        quad.draw(gl);                       // Draw quad
//
//        // Update the rotational angle after each refresh (NEW)
//        angleTriangle += speedTriangle; // (NEW)
//        angleQuad += speedQuad;         // (NEW)
//
//        // ----- Render the Pyramid -----
//        gl.glLoadIdentity();                 // Reset the model-view matrix
//        gl.glTranslatef(-1.5f, 0.0f, -6.0f); // Translate left and into the screen
//        gl.glRotatef(anglePyramid, 0.1f, 1.0f, -0.1f); // Rotate (NEW)
//        pyramid.draw(gl);                              // Draw the pyramid (NEW)
//
//        // ----- Render the Color Cube -----
//        gl.glLoadIdentity();                // Reset the model-view matrix
//        gl.glTranslatef(1.5f, 0.0f, -6.0f); // Translate right and into the screen
//        gl.glScalef(0.8f, 0.8f, 0.8f);      // Scale down (NEW)
//        gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the axis (1,1,1) (NEW)
//        cube.draw(gl);                      // Draw the cube (NEW)
//
//        // Update the rotational angle after each refresh (NEW)
//        anglePyramid += speedPyramid;   // (NEW)
//        angleCube += speedCube;         // (NEW)

        // ----- Render the Cube -----
        gl.glLoadIdentity();                  // Reset the current model-view matrix
        gl.glTranslatef(0.0f, 0.0f, -6.0f);   // Translate into the screen
        gl.glRotatef(angleCube, 0.1f, 1.0f, 0.2f); // Rotate
        cube.draw(gl);

        // Update the rotational angle after each refresh.
        angleCube += speedCube;
    }
}
