package com.digital.dl2.business.net.obj;

import java.util.Arrays;

/**
 * Created by qinqi on 15/12/12.
 */
public class NetOrderListData {
    private int order_id;
    private String order_sn;
    private long confirm_time;
    private int order_status;
    private float preferential_price;
    private int type;
    private NetOrderListBookData[] goods;

    @Override
    public String toString() {
        return "NetOrderListData{" +
                "order_id=" + order_id +
                ", order_sn='" + order_sn + '\'' +
                ", confirm_time=" + confirm_time +
                ", order_status=" + order_status +
                ", preferential_price=" + preferential_price +
                ", type=" + type +
                ", goods=" + Arrays.toString(goods) +
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

    public NetOrderListBookData[] getGoods() {
        return goods;
    }

    public void setGoods(NetOrderListBookData[] goods) {
        this.goods = goods;
    }
}
