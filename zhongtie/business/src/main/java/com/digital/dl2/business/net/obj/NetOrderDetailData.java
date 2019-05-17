package com.digital.dl2.business.net.obj;

import com.digital.dl2.business.core.obj.PgAddress;

import java.util.Arrays;

/**
 * Created by digital.dl2 on 15/10/12.
 */
public class NetOrderDetailData {
    private int order_id;//订单ID
    private String order_sn;//订单号
    private long confirm_time;
    private int order_status;
    private float goods_amount;
    private float preferential_price;
    private int type;
    private PgAddress address;
    private NetCommodity[] order_detail;

    @Override
    public String toString() {
        return "NetOrderDetailData{" +
                "order_id=" + order_id +
                ", order_sn='" + order_sn + '\'' +
                ", confirm_time=" + confirm_time +
                ", order_status=" + order_status +
                ", goods_amount=" + goods_amount +
                ", preferential_price=" + preferential_price +
                ", type=" + type +
                ", address=" + address +
                ", order_detail=" + Arrays.toString(order_detail) +
                '}';
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public long getConfirm_time() {
        return confirm_time;
    }

    public void setConfirm_time(long confirm_time) {
        this.confirm_time = confirm_time;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public float getGoods_amount() {
        return goods_amount;
    }

    public void setGoods_amount(float goods_amount) {
        this.goods_amount = goods_amount;
    }

    public float getPreferential_price() {
        return preferential_price;
    }

    public void setPreferential_price(float preferential_price) {
        this.preferential_price = preferential_price;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PgAddress getAddress() {
        return address;
    }

    public void setAddress(PgAddress address) {
        this.address = address;
    }

    public NetCommodity[] getOrder_detail() {
        return order_detail;
    }

    public void setOrder_detail(NetCommodity[] order_detail) {
        this.order_detail = order_detail;
    }
}
