package com.digital.dl2.business.core.obj;

import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgCommodity {
    private String name;//商品名字
    private int number;//商品数量
    private float oldPrice;//商品原价
    private float preferentialPrice;//优惠价格
    private float price;//价格
    private List<PgBookCategory> bookCategoryList; //类名+数量

    @Override
    public String toString() {
        return "PgCommodity{" +
                "name='" + name + '\'' +
                ", number=" + number +
                ", oldPrice=" + oldPrice +
                ", preferentialPrice=" + preferentialPrice +
                ", price=" + price +
                ", bookCategoryList=" + bookCategoryList +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public float getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(float oldPrice) {
        this.oldPrice = oldPrice;
    }

    public float getPreferentialPrice() {
        return preferentialPrice;
    }

    public void setPreferentialPrice(float preferentialPrice) {
        this.preferentialPrice = preferentialPrice;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public List<PgBookCategory> getBookCategoryList() {
        return bookCategoryList;
    }

    public void setBookCategoryList(List<PgBookCategory> bookCategoryList) {
        this.bookCategoryList = bookCategoryList;
    }
}
