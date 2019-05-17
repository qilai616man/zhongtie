package com.digital.dl2.business.core.obj;

/**
 * Created by qinqi on 15/12/11.
 */
public class PgPayOrderDetail {
    private int id;
    private int goodsId;
    private String goodsName;
    private int goodsNumber;
    private float pressPrice;
    private float price;

    @Override
    public String toString() {
        return "PgPayOrderDetail{" +
                "id=" + id +
                ", goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", goodsNumber=" + goodsNumber +
                ", pressPrice=" + pressPrice +
                ", price=" + price +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(int goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public float getPressPrice() {
        return pressPrice;
    }

    public void setPressPrice(float pressPrice) {
        this.pressPrice = pressPrice;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
