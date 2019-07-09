package com.crphdm.dl2.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.crphdm.dl2.R;


/**
 * https://gist.github.com/tylerchesley/5d15d859be4f3ce31213
 * 图片着色
 */
public class TintableImageView extends ImageView {

    private ColorStateList tint;
    private Drawable dNormal;
    private Drawable dSelect;
            //图片着色
    public TintableImageView(Context context) {
        super(context);
    }

    public TintableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TintableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.TintableImageView, defStyle, 0);
        tint = a.getColorStateList(
                R.styleable.TintableImageView_tint);
        dNormal = a.getDrawable(
                R.styleable.TintableImageView_normal);
        dSelect = a.getDrawable(
                R.styleable.TintableImageView_select);
        a.recycle();
    }
                //可绘制状态已更改
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (tint != null && tint.isStateful()) {
//            updateTintColor();
            updateDrawable();
        }
    }
        //设置颜色过滤器
    public void setColorFilter(ColorStateList tint) {
        this.tint = tint;
        super.setColorFilter(tint.getColorForState(getDrawableState(), 0));
    }
        //更新色调颜色
    private void updateTintColor() {
        int color = tint.getColorForState(getDrawableState(), 0);
        setColorFilter(color);
    }
        //更新图片
    public void updateDrawable() {
        if (isSelected()) {
            Log.d("", "I am select:" + dSelect);
            if (dSelect != null)
                setImageDrawable(dSelect);
        } else {
            Log.d("", "I am not select");
            if (dNormal != null)
                setImageDrawable(dNormal);
        }

    }


}