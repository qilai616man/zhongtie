package com.crphdm.dl2.qrcode.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
    //预览回调
final class PreviewCallback implements Camera.PreviewCallback {

  private static final String TAG = PreviewCallback.class.getSimpleName();
        //相机配置管理器
  private final CameraConfigurationManager configManager;
  private final boolean useOneShotPreviewCallback;
  private Handler previewHandler;
  private int previewMessage;

  PreviewCallback(CameraConfigurationManager configManager, boolean useOneShotPreviewCallback) {
    this.configManager = configManager;
    this.useOneShotPreviewCallback = useOneShotPreviewCallback;
  }
        //设置Handler
  void setHandler(Handler previewHandler, int previewMessage) {
    this.previewHandler = previewHandler;
    this.previewMessage = previewMessage;
  }
            //预览框架
  public void onPreviewFrame(byte[] data, Camera camera) {
    Point cameraResolution = configManager.getCameraResolution();
    if (!useOneShotPreviewCallback) {
      camera.setPreviewCallback(null);
    }
    if (previewHandler != null) {
      Message message = previewHandler.obtainMessage(previewMessage, cameraResolution.x,
          cameraResolution.y, data);
      message.sendToTarget();
      previewHandler = null;
    } else {
      Log.d(TAG, "Got preview callback, but no handler for it");
    }
  }

}
