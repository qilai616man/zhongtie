package com.crphdm.dl2.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;

import com.crphdm.dl2.R;


/**
 * Created by gaoyf on 14/12/17.
 */
public class CustomLoadProgressbar extends ProgressBar {
    private Drawable drawable;

    public CustomLoadProgressbar(Context context) {
        super(context);
        drawable = getResources().getDrawable(R.drawable.gray_sun);
        setIndeterminateDrawable(drawable);
        rotate();
    }

    public CustomLoadProgressbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawable = getResources().getDrawable(R.drawable.gray_sun);
        setIndeterminateDrawable(drawable);
        rotate();
    }

    public CustomLoadProgressbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        drawable = getResources().getDrawable(R.drawable.gray_sun);
        setIndeterminateDrawable(drawable);
        rotate();
    }

    private void rotate() {

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(2000);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        startAnimation(rotateAnimation);
    }
}
