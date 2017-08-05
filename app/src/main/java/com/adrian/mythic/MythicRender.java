package com.adrian.mythic;

import android.content.Context;
import android.graphics.SurfaceTexture;

import mythic.adrian.imageprocessor.render.BaseRender;

/**
 * Created by Adrian on 2017/8/4.
 * E-mail:aliu@in66.com
 */

public class MythicRender extends BaseRender {

    public MythicRender(Context context, SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener) {
        super(context, onFrameAvailableListener);
    }
}
