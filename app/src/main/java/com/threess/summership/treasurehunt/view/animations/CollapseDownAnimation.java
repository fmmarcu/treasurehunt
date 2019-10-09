package com.threess.summership.treasurehunt.view.animations;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;

import com.threess.summership.treasurehunt.R;

public class CollapseDownAnimation extends Animation {
    private EditText mDescription;
    private boolean mDown;

    public CollapseDownAnimation(EditText description, boolean down) {
        this.mDescription=description;
        this.mDown=down;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        int newHeight=mDescription.getHeight();
        if(mDown) {
            mDescription.setGravity(0);
            mDescription.setHint("");
            newHeight=(int) (600 * interpolatedTime);
        } else {
            if(mDescription.getText().toString().isEmpty())
            {
                mDescription.setGravity(Gravity.CENTER);
                mDescription.setHint(R.string.description);
                newHeight=(int) (600 * (1-interpolatedTime));
            }
            mDescription.setSelection(0);
        }
        mDescription.getLayoutParams().height=newHeight;
        mDescription.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

}
