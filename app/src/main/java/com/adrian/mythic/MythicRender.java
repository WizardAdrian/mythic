package com.adrian.mythic;

import android.content.Context;

import mythic.adrian.imageprocessor.render.BaseRender;

/**
 * Created by Adrian on 2017/8/4.
 * E-mail:aliu@in66.com
 */

public class MythicRender extends BaseRender {

    private Context mContext;

    public MythicRender(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }
}
