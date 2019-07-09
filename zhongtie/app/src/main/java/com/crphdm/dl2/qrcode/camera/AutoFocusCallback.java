package com.crphdm.dl2.qrcode.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
    //自动聚焦间隔
final class AutoFocusCallback implements Camera.AutoFocusCallback {

  private static final String TAG = AutoFocusCallback.class.getSimpleName();

  private static final long AUTOFOCUS_INTERVAL_MS = 1500L;

  private Handler autoFocusHandler;
  private int autoFocusMessage;
      //设置Handler
  void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
    this.autoFocusHandler = autoFocusHandler;
    this.autoFocusMessage = autoFocusMessage;
  }
      //自动对焦
  public void onAutoFocus(boolean success, Camera camera) {
    if (autoFocusHandler != null) {
      Message message = autoFocusHandler.obtainMessage(autoFocusMessage, success);
      autoFocusHandler.sendMessageDelayed(message, AUTOFOCUS_INTERVAL_MS);
      autoFocusHandler = null;
    } else {
      //有自动焦点回调，但没有处理程序
      Log.d(TAG, "Got auto-focus callback, but no handler for it");
    }
  }

}
