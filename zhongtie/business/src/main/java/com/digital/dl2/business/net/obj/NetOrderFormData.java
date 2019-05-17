package com.digital.dl2.business.net.obj;

import com.digital.dl2.business.core.obj.PgAddress;

import java.util.Arrays;

/**
 * Created by digital.dl2 on 15/9/28.
 */
public class NetOrderFormData {
    private int order_id;//订单ID
    private String order_sn;//订单号
    private int confirm_time;
    private int order_status;
    private int type;
    private float goods_amount;
    private float preferential_price;
    private float pay_amount;
    private PgAddress address;
    private NetCommodity[] netCommodities;
    private NetBookCategoryData[] netBookCategories;

    @Override
    public String toString() {
        return "NetOrderFormData{" +
                "order_id=" + order_id +
                ", order_sn='" + order_sn + '\'' +
                ", confirm_time=" + confirm_time +
                ", order_status=" + order_status +
                ", type=" + type +
                ", goods_amount=" + goods_amount +
                ", preferential_price=" + preferential_price +
                ", pay_amount=" + pay_amount +
                ", address=" + address +
                ", netCommodities=" + Arrays.toString(netCommodities) +
                ", netBookCategories=" + Arrays.toString(netBookCategories) +
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

    public int getConfirm_time() {
        return confirm_time;
    }

    public void setConfirm_time(int confirm_time) {
        this.confirm_time = confirm_time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public float getPay_amount() {
        return pay_amount;
    }

    public void setPay_amount(float pay_amount) {
        this.pay_amount = pay_amount;
    }

    public PgAddress getAddress() {
        return address;
    }

    public void setAddress(PgAddress address) {
        this.address = address;
    }

    public NetCommodity[] getNetCommodities() {
        return netCommodities;
    }

    public void setNetCommodities(NetCommodity[] netCommodities) {
        this.netCommodities = netCommodities;
    }

    public NetBookCategoryData[] getNetBookCategories() {
        return netBookCategories;
    }

    public void setNetBookCategories(NetBookCategoryData[] netBookCategories) {
        this.netBookCategories = netBookCategories;
    }
}
