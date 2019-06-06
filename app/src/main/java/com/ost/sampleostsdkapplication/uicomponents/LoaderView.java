/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.sampleostsdkapplication.uicomponents;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.ost.sampleostsdkapplication.R;

/**
 * Simplest custom view possible, using CircularProgressDrawable
 */
public class LoaderView extends View {

    private CircularProgressDrawable mDrawable;

    public LoaderView(Context context) {
        this(context, null);
    }

    public LoaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDrawable = new CircularProgressDrawable(getResources().getColor(R.color.colorAccent), 10);
        mDrawable.setCallback(this);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            mDrawable.start();
        } else {
            mDrawable.stop();
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawable.setBounds(0, 0, w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        mDrawable.draw(canvas);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mDrawable || super.verifyDrawable(who);
    }
}