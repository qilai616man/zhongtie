package com.crphdm.dl2.qrcode.camera;

import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 此类用于激活某些拍照手机（不是闪光灯）的微弱光线
 * 为了照亮表面进行扫描。 没有正式的方法来做到这一点，
 * 但是，某些设备上仍然存在允许访问此功能的类。
 * 因此，这需要经过大量的反思。
 *
 * 请参考 <a href="http://almondmendoza.com/2009/01/05/changing-the-screen-brightness-programatically/">
 * http://almondmendoza.com/2009/01/05/changing-the-screen-brightness-programatically/</a> 和
 * <a href="http://code.google.com/p/droidled/source/browse/trunk/src/com/droidled/demo/DroidLED.java">
 * http://code.google.com/p/droidled/source/browse/trunk/src/com/droidled/demo/DroidLED.java</a>.
 * Thanks to Ryan Alford for pointing out the availability of this class.
 */
final class FlashlightManager {

  private static final String TAG = FlashlightManager.class.getSimpleName();

  private static final Object iHardwareService;
  private static final Method setFlashEnabledMethod;
  static {
    iHardwareService = getHardwareService();
    setFlashEnabledMethod = getSetFlashEnabledMethod(iHardwareService);
    if (iHardwareService == null) {
      //该设备支持控制手电筒
      Log.v(TAG, "This device does supports control of a flashlight");
    } else {
      //此设备不支持控制手电筒
      Log.v(TAG, "This device does not support control of a flashlight");
    }
  }

  private FlashlightManager() {
  }

  /**
   * 控制相机闪光灯开关
   */
  //FIXME
  static void enableFlashlight() {
    setFlashlight(false);
  }

  static void disableFlashlight() {
    setFlashlight(false);
  }


  /**
   * 获取硬件服务
   */
  private static Object getHardwareService() {
    Class<?> serviceManagerClass = maybeForName("android.os.ServiceManager");
    if (serviceManagerClass == null) {
      return null;
    }

    Method getServiceMethod = maybeGetMethod(serviceManagerClass, "getService", String.class);
    if (getServiceMethod == null) {
      return null;
    }

    Object hardwareService = invoke(getServiceMethod, null, "hardware");
    if (hardwareService == null) {
      return null;
    }

    Class<?> iHardwareServiceStubClass = maybeForName("android.os.IHardwareService$Stub");
    if (iHardwareServiceStubClass == null) {
      return null;
    }

    Method asInterfaceMethod = maybeGetMethod(iHardwareServiceStubClass, "asInterface", IBinder.class);
    if (asInterfaceMethod == null) {
      return null;
    }

    return invoke(asInterfaceMethod, null, hardwareService);
  }
  /**
   * 获取设置Flash启用方法
   */
  private static Method getSetFlashEnabledMethod(Object iHardwareService) {
    if (iHardwareService == null) {
      return null;
    }
    Class<?> proxyClass = iHardwareService.getClass();
    return maybeGetMethod(proxyClass, "setFlashlightEnabled", boolean.class);
  }

  private static Class<?> maybeForName(String name) {
    try {
      return Class.forName(name);
    } catch (ClassNotFoundException cnfe) {
      // OK
      return null;
    } catch (RuntimeException re) {
      Log.w(TAG, "Unexpected error while finding class " + name, re);
      return null;
    }
  }

  private static Method maybeGetMethod(Class<?> clazz, String name, Class<?>... argClasses) {
    try {
      return clazz.getMethod(name, argClasses);
    } catch (NoSuchMethodException nsme) {
      // OK
      return null;
    } catch (RuntimeException re) {
      Log.w(TAG, "Unexpected error while finding method " + name, re);
      return null;
    }
  }
        //调用
  private static Object invoke(Method method, Object instance, Object... args) {
    try {
      return method.invoke(instance, args);
    } catch (IllegalAccessException e) {
      Log.w(TAG, "Unexpected error while invoking " + method, e);
      return null;
    } catch (InvocationTargetException e) {
      Log.w(TAG, "Unexpected error while invoking " + method, e.getCause());
      return null;
    } catch (RuntimeException re) {
      Log.w(TAG, "Unexpected error while invoking " + method, re);
      return null;
    }
  }
    //设置闪光灯
  private static void setFlashlight(boolean active) {
    if (iHardwareService != null) {
      invoke(setFlashEnabledMethod, iHardwareService, active);
    }
  }

}
