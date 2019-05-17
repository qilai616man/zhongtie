package com.crphdm.dl2.utils;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by gaoyufei on 15/9/30.
 */
public class DelayHelper {
    private int WAIT_TIME = 30;
    private int time;

    public DelayHelper() {

    }

    public DelayHelper(int waitTime) {
        WAIT_TIME = waitTime;
    }

    public void delayButton(final Button view) {
        time = WAIT_TIME;
        view.setEnabled(false);
        view.post(new Runnable() {
            @Override
            public void run() {
                if (view == null)
                    return;
                if (time > 0) {
                    time--;
                    view.setText(time + "秒");
                    view.postDelayed(this, 1000);
                } else {
                    view.setText("获取验证码");
                    view.setEnabled(true);
                }
            }
        });
    }

    public void delayTextView(final TextView view) {
        time = WAIT_TIME;
        view.setEnabled(false);
        view.post(new Runnable() {
            @Override
            public void run() {
                if (view == null)
                    return;
                if (time > 0) {
                    time--;
                    view.setText(time + "秒");
                    view.postDelayed(this, 1000);
                } else {
                    view.setText("获取验证码");
                    view.setEnabled(true);
                }
            }
        });
    }

    public void cancel() {
        time = 0;
    }
}
