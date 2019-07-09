/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crphdm.dl2.qrcode.decoding;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.crphdm.dl2.R;
import com.crphdm.dl2.qrcode.MipcaActivityCapture;
import com.crphdm.dl2.qrcode.camera.CameraManager;
import com.crphdm.dl2.qrcode.view.ViewfinderResultPointCallback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Vector;

/**
 * 此类处理包含要捕获的状态机的所有消息传递。
 */
public final class CaptureActivityHandler extends Handler {

  private static final String TAG = CaptureActivityHandler.class.getSimpleName();

  private final MipcaActivityCapture activity;
  private final DecodeThread decodeThread;
  private State state;

  private enum State {
    PREVIEW,
    SUCCESS,
    DONE
  }

  public CaptureActivityHandler(MipcaActivityCapture activity, Vector<BarcodeFormat> decodeFormats,
      String characterSet) {
    this.activity = activity;
    decodeThread = new DecodeThread(activity, decodeFormats, characterSet,
            new ViewfinderResultPointCallback(activity.getViewfinderView()));
    decodeThread.start();
    state = State.SUCCESS;
    // 开始捕捉预览和解码。
    CameraManager.get().startPreview();
    restartPreviewAndDecode();
  }

  @Override
  public void handleMessage(Message message) {
    switch (message.what) {
      case R.id.auto_focus:
        //Log.d(TAG, "Got auto-focus message");
        //当一个自动对焦传递完成时，启动另一个。 这是最接近的事情
        //连续自动对焦。 它似乎确实有点狩猎，但我不知道还能做什么。
        if (state == State.PREVIEW) {
          CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
        }
        break;
      case R.id.restart_preview:
        Log.d(TAG, "Got restart preview message");
        restartPreviewAndDecode();
        break;
      case R.id.decode_succeeded:
        Log.d(TAG, "Got decode succeeded message");
        state = State.SUCCESS;
        Bundle bundle = message.getData();

        Bitmap barcode = bundle == null ? null :
            (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
        
        activity.handleDecode((Result) message.obj, barcode);
        break;
      case R.id.decode_failed:
        // 我们正在尽可能快地解码，因此当一个解码失败时，启动另一个。
        state = State.PREVIEW;
        CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        break;
      case R.id.return_scan_result:
        Log.d(TAG, "Got return scan result message");
        activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
        activity.finish();
        break;
      case R.id.launch_product_query:
        Log.d(TAG, "Got product query message");
        String url = (String) message.obj;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        activity.startActivity(intent);
        break;
    }
  }
          //同步退出
  public void quitSynchronously() {
    state = State.DONE;
    CameraManager.get().stopPreview();
    Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
    quit.sendToTarget();
    try {
      decodeThread.join();
    } catch (InterruptedException e) {
      // continue
    }

    // 确保不发送排队的消息
    removeMessages(R.id.decode_succeeded);
    removeMessages(R.id.decode_failed);
  }

  private void restartPreviewAndDecode() {
    if (state == State.SUCCESS) {
      state = State.PREVIEW;
      CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
      CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
      activity.drawViewfinder();
    }
  }

}
