package com.crphdm.dl2.qrcode.camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * 此对象包装Camera服务对象，并期望成为唯一与之对话的对象。
 * 该实现封装了拍摄预览大小图像所需的步骤
 * 预览和解码。
 */
public final class CameraManager {

  private static final String TAG = CameraManager.class.getSimpleName();

  private static final int MIN_FRAME_WIDTH = 240;
  private static final int MIN_FRAME_HEIGHT = 240;
  private static final int MAX_FRAME_WIDTH = 480;
  private static final int MAX_FRAME_HEIGHT = 360;

  private static CameraManager cameraManager;

  static final int SDK_INT; // Later we can use Build.VERSION.SDK_INT
  static {
    int sdkInt;
    try {
      sdkInt = Integer.parseInt(Build.VERSION.SDK);
    } catch (NumberFormatException nfe) {
      // Just to be safe
      sdkInt = 10000;
    }
    SDK_INT = sdkInt;
  }

  private final Context context;
  private final CameraConfigurationManager configManager;
  private Camera camera;
  private Rect framingRect;
  private Rect framingRectInPreview;
  private boolean initialized;
  private boolean previewing;
  private final boolean useOneShotPreviewCallback;
  /**
   * 预览帧在这里传递，我们将其传递给注册的处理程序。
   * 确保清除处理程序，这样它只会收到一条消息。
   */
  private final PreviewCallback previewCallback;
  /** 自动对焦回调到达此处，并被分派到请求它们的处理程序。 */
  private final AutoFocusCallback autoFocusCallback;

  /**
   * 使用调用Activity的Context初始化此静态对象。
   *
   * @param context 这个界面想要使用相机.
   */
  public static void init(Context context) {
    if (cameraManager == null) {
      cameraManager = new CameraManager(context);
    }
  }

  /**
   * 获取CameraManager单例实例
   *
   * @return 对CameraManager单例的引用。
   */
  public static CameraManager get() {
    return cameraManager;
  }

  private CameraManager(Context context) {

    this.context = context;
    this.configManager = new CameraConfigurationManager(context);

    // Camera.setOneShotPreviewCallback() has a race condition in Cupcake, so we use the older
    // Camera.setPreviewCallback() on 1.5 and earlier. For Donut and later, we need to use
    // the more efficient one shot callback, as the older one can swamp the system and cause it
    // to run out of memory. We can't use SDK_INT because it was introduced in the Donut SDK.
    //useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.CUPCAKE;
    useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > 3; // 3 = Cupcake

    previewCallback = new PreviewCallback(configManager, useOneShotPreviewCallback);
    autoFocusCallback = new AutoFocusCallback();
  }

  /**
   * 打开相机驱动程序并初始化硬件参数。
   *
   * @param holder 相机将预览帧绘制到的表面对象。
   * @throws IOException 表示相机驱动程序无法打开。
   */
  public void openDriver(SurfaceHolder holder) throws IOException {
    if (camera == null) {
      camera = Camera.open();
      if (camera == null) {
        throw new IOException();
      }
      camera.setPreviewDisplay(holder);

      if (!initialized) {
        initialized = true;
        configManager.initFromCameraParameters(camera);
      }
      configManager.setDesiredCameraParameters(camera);

      //FIXME
      //     SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      //是否使用前灯
//      if (prefs.getBoolean(PreferencesActivity.KEY_FRONT_LIGHT, false)) {
//        FlashlightManager.enableFlashlight();
//      }
      FlashlightManager.enableFlashlight();
    }
  }

  /**
   * 如果仍在使用，请关闭相机驱动程序。
   */
  public void closeDriver() {
    if (camera != null) {
      FlashlightManager.disableFlashlight();
      camera.release();
      camera = null;
    }
  }

  /**
   * 要求相机硬件开始将预览帧绘制到屏幕上。
   */
  public void startPreview() {
    if (camera != null && !previewing) {
      camera.startPreview();
      previewing = true;
    }
  }

  /**
   * 告诉相机停止绘制预览帧。
   */
  public void stopPreview() {
    if (camera != null && previewing) {
      if (!useOneShotPreviewCallback) {
        camera.setPreviewCallback(null);
      }
      camera.stopPreview();
      previewCallback.setHandler(null, 0);
      autoFocusCallback.setHandler(null, 0);
      previewing = false;
    }
  }

  /**
   * 单个预览框将返回到提供的处理程序。 数据将以byte []的形式到达
   * 在message.obj字段中，宽度和高度编码分别为message.arg1和message.arg2。
   *
   * @param handler 发送消息的处理程序。
   * @param message 要发送的消息的哪个字段。
   */
  public void requestPreviewFrame(Handler handler, int message) {
    if (camera != null && previewing) {
      previewCallback.setHandler(handler, message);
      if (useOneShotPreviewCallback) {
        camera.setOneShotPreviewCallback(previewCallback);
      } else {
        camera.setPreviewCallback(previewCallback);
      }
    }
  }

  /**
   * 要求相机硬件执行自动对焦。
   *
   * @param handler 处理程序在自动对焦完成时通知。
   * @param message 要传递的信息。
   */
  public void requestAutoFocus(Handler handler, int message) {
    if (camera != null && previewing) {
      autoFocusCallback.setHandler(handler, message);
      //Log.d(TAG, "Requesting auto-focus callback");
      camera.autoFocus(autoFocusCallback);
    }
  }

  /**
   * 计算UI应绘制的框架矩形，以显示用户放置的位置条码。
   * 该目标有助于对齐并强制用户握住设备足够远以确保图像聚焦。
   *
   * @return 在窗口坐标中在屏幕上绘制的矩形。
   */
  public Rect getFramingRect() {
    Point screenResolution = configManager.getScreenResolution();
    if (framingRect == null) {
      if (camera == null) {
        return null;
      }
      int width = screenResolution.x * 3 / 4;
      if (width < MIN_FRAME_WIDTH) {
        width = MIN_FRAME_WIDTH;
      } else if (width > MAX_FRAME_WIDTH) {
        width = MAX_FRAME_WIDTH;
      }
      int height = screenResolution.y * 3 / 4;
      if (height < MIN_FRAME_HEIGHT) {
        height = MIN_FRAME_HEIGHT;
      } else if (height > MAX_FRAME_HEIGHT) {
        height = MAX_FRAME_HEIGHT;
      }
      int leftOffset = (screenResolution.x - width) / 2;
      int topOffset = (screenResolution.y - height) / 2;
      framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
      Log.d(TAG, "Calculated framing rect: " + framingRect);
    }
    return framingRect;
  }

  /**
   * 与{@link #getFramingRect}类似，但坐标是预览框架，
   * not UI/屏幕.
   */
  public Rect getFramingRectInPreview() {
    if (framingRectInPreview == null) {
      Rect rect = new Rect(getFramingRect());
      Point cameraResolution = configManager.getCameraResolution();
      Point screenResolution = configManager.getScreenResolution();
      //modify here
//      rect.left = rect.left * cameraResolution.x / screenResolution.x;
//      rect.right = rect.right * cameraResolution.x / screenResolution.x;
//      rect.top = rect.top * cameraResolution.y / screenResolution.y;
//      rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
      rect.left = rect.left * cameraResolution.y / screenResolution.x;
      rect.right = rect.right * cameraResolution.y / screenResolution.x;
      rect.top = rect.top * cameraResolution.x / screenResolution.y;
      rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
      framingRectInPreview = rect;
    }
    return framingRectInPreview;
  }

  /**
   * 将结果点从静止分辨率坐标转换为屏幕坐标。
   *
   * @param points Reader子类通过Result.getResultPoints（）返回的点。
   * @return 一个点数组缩放到框架矩形的大小并适当地偏移，因此它们可以在屏幕坐标中绘制。
   */
  /*
  public Point[] convertResultPoints(ResultPoint[] points) {
    Rect frame = getFramingRectInPreview();
    int count = points.length;
    Point[] output = new Point[count];
    for (int x = 0; x < count; x++) {
      output[x] = new Point();
      output[x].x = frame.left + (int) (points[x].getX() + 0.5f);
      output[x].y = frame.top + (int) (points[x].getY() + 0.5f);
    }
    return output;
  }
   */

  /**
   * 一种工厂方法，用于根据预览缓冲区的格式构建适当的LuminanceSource对象，如Camera.Parameters所述。
   *
   * @param data 预览框架。
   * @param width 图像的宽度。
   * @param height 图像的高度。
   * @return 平面YUV亮度源实例。
   */
  public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
    Rect rect = getFramingRectInPreview();
    int previewFormat = configManager.getPreviewFormat();
    String previewFormatString = configManager.getPreviewFormatString();
    switch (previewFormat) {
      // This is the standard Android format which all devices are REQUIRED to support.
      // In theory, it's the only one we should ever care about.
      case PixelFormat.YCbCr_420_SP:
        // This format has never been seen in the wild, but is compatible as we only care
        // about the Y channel, so allow it.
      case PixelFormat.YCbCr_422_SP:
        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                rect.width(), rect.height());
      default:
        // The Samsung Moment incorrectly uses this variant instead of the 'sp' version.
        // Fortunately, it too has all the Y data up front, so we can read it.
        if ("yuv420p".equals(previewFormatString)) {
          return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                  rect.width(), rect.height());
        }
    }
    throw new IllegalArgumentException("Unsupported picture format: " +
            previewFormat + '/' + previewFormatString);
  }

  public Context getContext() {
    return context;
  }

}
