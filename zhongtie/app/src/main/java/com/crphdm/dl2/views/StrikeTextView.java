package com.crphdm.dl2.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by gaoyufei on 15/9/26.
 */
public class StrikeTextView extends TextView{
    public StrikeTextView(Context context) {
        super(context);
        init();
    }

    public StrikeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StrikeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StrikeTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }
}
