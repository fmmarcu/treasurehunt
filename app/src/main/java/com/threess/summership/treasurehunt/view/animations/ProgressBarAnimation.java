package com.threess.summership.treasurehunt.view.animations;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

public class ProgressBarAnimation extends Animation {
    private static final String TAG = ProgressBarAnimation.class.getSimpleName();
    private ProgressBar mProgressBar;

    public ProgressBarAnimation(ProgressBar mProgressBar) {
        this.mProgressBar = mProgressBar;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = interpolatedTime*100;
        mProgressBar.setProgress((int)value);
    }
}
