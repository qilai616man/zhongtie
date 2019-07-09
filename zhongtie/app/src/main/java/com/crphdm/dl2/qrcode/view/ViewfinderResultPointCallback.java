
package com.crphdm.dl2.qrcode.view;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;

/**
 * 查看查找器结果点回调
 */
public final class ViewfinderResultPointCallback implements ResultPointCallback {

  private final ViewfinderView viewfinderView;
        //查看查找器结果点回调
  public ViewfinderResultPointCallback(ViewfinderView viewfinderView) {
    this.viewfinderView = viewfinderView;
  }
        //找到了可能的结果点
  public void foundPossibleResultPoint(ResultPoint point) {
    viewfinderView.addPossibleResultPoint(point);
  }

}
