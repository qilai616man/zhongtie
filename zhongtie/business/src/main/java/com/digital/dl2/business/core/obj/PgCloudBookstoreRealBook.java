package com.digital.dl2.business.core.obj;

/**
 * Created by qinqi on 15/12/12.
 */
public class PgCloudBookstoreRealBook {
    private int bookId;
    private float price;
    private float pressPrice;

    @Override
    public String toString() {
        return "PgCloudBookstoreRealBook{" +
                "bookId=" + bookId +
                ", price=" + price +
                ", pressPrice=" + pressPrice +
                '}';
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPressPrice() {
        return pressPrice;
    }

    public void setPressPrice(float pressPrice) {
        this.pressPrice = pressPrice;
    }
}
