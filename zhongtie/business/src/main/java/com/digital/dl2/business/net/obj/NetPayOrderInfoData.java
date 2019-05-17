package com.digital.dl2.business.net.obj;

/**
 * Created by qinqi on 15/12/11.
 */
public class NetPayOrderInfoData {
    private int order_id;
    private String order_sn;
    private int user_id;
    private long confirm_time;
    private int order_status;
    private float all_price;
    private float preferential_price;
    private int type;
    private NetOrderAddressData address;

    @Override
    public String toString() {
        return "NetOrderInfoData{" +
                "order_id=" + order_id +
                ", order_sn='" + order_sn + '\'' +
                ", user_id=" + user_id +
                ", confirm_time=" + confirm_time +
                ", order_status=" + order_status +
                ", all_price=" + all_price +
                ", preferential_price=" + preferential_price +
                ", type=" + type +
                ", address=" + address +
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

    public float getAll_price() {
        return all_price;
    }

    public void setAll_price(float all_price) {
        this.all_price = all_price;
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

    public NetOrderAddressData getAddress() {
        return address;
    }

    public void setAddress(NetOrderAddressData address) {
        this.address = address;
    }
}
