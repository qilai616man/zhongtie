package com.crphdm.dl2.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.crphdm.dl2.R;

/**
 * Created by gaoyufei on 15/9/30.
 */
public class BeautifulProgressbar extends View {

    protected float progress = 0.0F;
    private Paint mPaint;
    private RectF mRect;
    private Animator animator;
    private float size;
    private int color;

    public BeautifulProgressbar(Context context) {
        super(context);
        init();
    }

    public BeautifulProgressbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BeautifulProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BeautifulProgressbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }


    private void init() {
        size = 80;
        color = getResources().getColor(android.R.color.white);
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.BeautifulProgressbar);
        size = a.getDimensionPixelSize(R.styleable.BeautifulProgressbar_progress_size, 80);
        color = a.getColor(R.styleable.BeautifulProgressbar_progress_color, getResources().getColor(android.R.color.white));
        a.recycle();

        mPaint = new Paint();
        mPaint.setColor(color);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void to(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void animatorTo(float progress, int duration) {
        if (animator != null && animator.isRunning())
            animator.cancel();
        animator = ObjectAnimator.ofFloat(this, "progress", this.progress, progress);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRect = new RectF((getWidth() - size) / 2, (getHeight() - size) / 2, (getWidth() + size) / 2, (getHeight() + size) / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mRect, -90f, (360 * progress), true, mPaint);
        if (progress >= 1)
            setVisibility(View.GONE);
        else
            setVisibility(View.VISIBLE);
    }


}
