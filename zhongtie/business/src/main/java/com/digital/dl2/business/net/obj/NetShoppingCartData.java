package com.digital.dl2.business.net.obj;

import java.util.Arrays;

/**
 * Created by digital.dl2 on 15/10/8.
 */
public class NetShoppingCartData {
    private int type;
    private int number;
    private float all_price;
    private float preferential_price;
    private NetShoppingCartBookData[] book_list;

    @Override
    public String toString() {
        return "NetShoppingCartData{" +
                "type=" + type +
                ", number=" + number +
                ", all_price=" + all_price +
                ", preferential_price=" + preferential_price +
                ", book_list=" + Arrays.toString(book_list) +
                '}';
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public NetShoppingCartBookData[] getBook_list() {
        return book_list;
    }

    public void setBook_list(NetShoppingCartBookData[] book_list) {
        this.book_list = book_list;
    }
}
