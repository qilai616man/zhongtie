package com.digital.dl2.business.net.obj;

/**
 * Created by qinqi on 15/12/1.
 */
public class NetShoppingCartTotalData {
    private NetShoppingCartData real_book;
    private NetShoppingCartData e_book;

    @Override
    public String toString() {
        return "NetShoppingCartTotalData{" +
                "real_book=" + real_book +
                ", e_book=" + e_book +
                '}';
    }

    public NetShoppingCartData getReal_book() {
        return real_book;
    }

    public void setReal_book(NetShoppingCartData real_book) {
        this.real_book = real_book;
    }

    public NetShoppingCartData getE_book() {
        return e_book;
    }

    public void setE_book(NetShoppingCartData e_book) {
        this.e_book = e_book;
    }
}
