package com.digital.dl2.business.net.obj;

/**
 * Created by qinqi on 15/12/12.
 */
public class NetOrderListBookData {
    private String goods_name;
    private int number;

    @Override
    public String toString() {
        return "NetOrderListBookData{" +
                "goods_name='" + goods_name + '\'' +
                ", number=" + number +
                '}';
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
