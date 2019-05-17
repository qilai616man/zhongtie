package com.digital.dl2.business.net.obj;

/**
 * Created by qinqi on 15/12/11.
 */
public class NetPayOrderDetailData {
    private int id;
    private int goods_id;
    private String goods_name;
    private int goods_number;
    private float pressprice;
    private float price;

    @Override
    public String toString() {
        return "NetPayOrderDetailData{" +
                "id=" + id +
                ", goods_id=" + goods_id +
                ", goods_name='" + goods_name + '\'' +
                ", goods_number=" + goods_number +
                ", pressprice=" + pressprice +
                ", price=" + price +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public int getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(int goods_number) {
        this.goods_number = goods_number;
    }

    public float getPressprice() {
        return pressprice;
    }

    public void setPressprice(float pressprice) {
        this.pressprice = pressprice;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
