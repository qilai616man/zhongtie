package com.crphdm.dl2.qrcode.decoding;

import android.app.Activity;
import android.content.DialogInterface;

/**
 * 在少数情况下，简单的侦听器用于退出应用程序。
 *
 */
public final class FinishListener
    implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener, Runnable {

  private final Activity activityToFinish;

  public FinishListener(Activity activityToFinish) {
    this.activityToFinish = activityToFinish;
  }

  public void onCancel(DialogInterface dialogInterface) {
    run();
  }

  public void onClick(DialogInterface dialogInterface, int i) {
    run();
  }

  public void run() {
    activityToFinish.finish();
  }

}
