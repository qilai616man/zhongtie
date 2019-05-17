package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/9/28.
 */
public class NetCommodity {
    private String goods_name;
    private int goods_number;
    private float market_price;
    private float goods_price;
    private float price;

    @Override
    public String toString() {
        return "NetCommodity{" +
                "goods_name='" + goods_name + '\'' +
                ", goods_number=" + goods_number +
                ", market_price=" + market_price +
                ", goods_price=" + goods_price +
                ", price=" + price +
                '}';
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

    public float getMarket_price() {
        return market_price;
    }

    public void setMarket_price(float market_price) {
        this.market_price = market_price;
    }

    public float getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(float goods_price) {
        this.goods_price = goods_price;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
