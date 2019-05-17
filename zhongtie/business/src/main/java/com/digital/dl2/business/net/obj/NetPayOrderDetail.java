package com.digital.dl2.business.net.obj;

import java.util.Arrays;

/**
 * Created by qinqi on 15/12/11.
 */
public class NetPayOrderDetail {
    private NetPayOrderInfoData order_info;
    private NetPayOrderDetailData[] order_detail;

    @Override
    public String toString() {
        return "NetPayOrderDetail{" +
                "order_info=" + order_info +
                ", order_detail=" + Arrays.toString(order_detail) +
                '}';
    }

    public NetPayOrderInfoData getOrder_info() {
        return order_info;
    }

    public void setOrder_info(NetPayOrderInfoData order_info) {
        this.order_info = order_info;
    }

    public NetPayOrderDetailData[] getOrder_detail() {
        return order_detail;
    }

    public void setOrder_detail(NetPayOrderDetailData[] order_detail) {
        this.order_detail = order_detail;
    }
}
