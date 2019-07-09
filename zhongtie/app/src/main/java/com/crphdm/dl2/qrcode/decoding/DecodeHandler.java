package com.crphdm.dl2.qrcode.decoding;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.crphdm.dl2.R;
import com.crphdm.dl2.qrcode.MipcaActivityCapture;
import com.crphdm.dl2.qrcode.camera.CameraManager;
import com.crphdm.dl2.qrcode.camera.PlanarYUVLuminanceSource;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Hashtable;
       //解码
final class DecodeHandler extends Handler {

  private static final String TAG = DecodeHandler.class.getSimpleName();

  private final MipcaActivityCapture activity;
  private final MultiFormatReader multiFormatReader;

  DecodeHandler(MipcaActivityCapture activity, Hashtable<DecodeHintType, Object> hints) {
    multiFormatReader = new MultiFormatReader();
    multiFormatReader.setHints(hints);
    this.activity = activity;
  }
            //处理消息
  @Override
  public void handleMessage(Message message) {
    switch (message.what) {
      case R.id.decode:
        //Log.d(TAG, "Got decode message");
        decode((byte[]) message.obj, message.arg1, message.arg2);
        break;
      case R.id.quit:
        Looper.myLooper().quit();
        break;
    }
  }

  /**
   * 解码取景器矩形内的数据，并计算所需的时间。
   * 为了提高效率从一个解码到下一个解码重用相同的读取器对象。
   *
   * @param data   YUV预览框。
   * @param width  预览框的宽度。
   * @param height 预览框的高度。
   */
  private void decode(byte[] data, int width, int height) {
    long start = System.currentTimeMillis();
    Result rawResult = null;
    
    //modify here
    byte[] rotatedData = new byte[data.length];
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++)
            rotatedData[x * height + height - y - 1] = data[x + y * width];
    }
    int tmp = width; // Here we are swapping, that's the difference to #11
    width = height;
    height = tmp;
    
    PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(rotatedData, width, height);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
    try {
      rawResult = multiFormatReader.decodeWithState(bitmap);
    } catch (ReaderException re) {
      // continue
    } finally {
      multiFormatReader.reset();
    }

    if (rawResult != null) {
      long end = System.currentTimeMillis();
      Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
      Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
      Bundle bundle = new Bundle();
      bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
      message.setData(bundle);
      //Log.d(TAG, "Sending decode succeeded message...");
      message.sendToTarget();
    } else {
      Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
      message.sendToTarget();
    }
  }

}
