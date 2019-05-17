package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/10/3.
 */
public class NetListOfOrdersData {
    private int order_id;
    private String order_sn;
    private int user_id;
    private int order_status;
    private String goods_name_one;
    private String goods_name_two;
    private float goods_amount;
    private float preferentialPrice;
    private float pay_amount;
    private int type;
    private int addr_id;

    @Override
    public String toString() {
        return "NetListOfOrdersData{" +
                "order_id=" + order_id +
                ", order_sn='" + order_sn + '\'' +
                ", user_id=" + user_id +
                ", order_status=" + order_status +
                ", goods_name_one='" + goods_name_one + '\'' +
                ", goods_name_two='" + goods_name_two + '\'' +
                ", goods_amount=" + goods_amount +
                ", preferentialPrice=" + preferentialPrice +
                ", pay_amount=" + pay_amount +
                ", type=" + type +
                ", addr_id=" + addr_id +
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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public String getGoods_name_one() {
        return goods_name_one;
    }

    public void setGoods_name_one(String goods_name_one) {
        this.goods_name_one = goods_name_one;
    }

    public String getGoods_name_two() {
        return goods_name_two;
    }

    public void setGoods_name_two(String goods_name_two) {
        this.goods_name_two = goods_name_two;
    }

    public float getGoods_amount() {
        return goods_amount;
    }

    public void setGoods_amount(float goods_amount) {
        this.goods_amount = goods_amount;
    }

    public float getPreferentialPrice() {
        return preferentialPrice;
    }

    public void setPreferentialPrice(float preferentialPrice) {
        this.preferentialPrice = preferentialPrice;
    }

    public float getPay_amount() {
        return pay_amount;
    }

    public void setPay_amount(float pay_amount) {
        this.pay_amount = pay_amount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAddr_id() {
        return addr_id;
    }

    public void setAddr_id(int addr_id) {
        this.addr_id = addr_id;
    }
}
