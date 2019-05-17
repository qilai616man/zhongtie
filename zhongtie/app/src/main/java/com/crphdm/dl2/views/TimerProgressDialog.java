package com.crphdm.dl2.views;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by zhangcirui on 15/12/19.
 */
public class TimerProgressDialog extends ProgressDialog {

    private static String TAG = TimerProgressDialog.class.getSimpleName();

    private ProgressDialog mProgressDialog;

    public TimerProgressDialog(Context context) {
        super(context);

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setMessage("加载中...");

            mProgressDialog.setOnShowListener(new OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Log.d(TAG, "setOnShowListener");

                    new CountDownTimer(15 * 1000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                        }

                    }.start();
                }
            });

            mProgressDialog.show();
        }

    }

    public void dismiss() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public boolean isShowing(){
        if (mProgressDialog != null) {
            return mProgressDialog.isShowing();
        }

        return  false;
    }

    public void setOnDismissListener(final OnDismissListener dismissListener){

        mProgressDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dismissListener.onDismiss(dialog);
            }
        });
    }

}
