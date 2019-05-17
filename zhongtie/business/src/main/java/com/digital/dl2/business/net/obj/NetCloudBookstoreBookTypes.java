package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/9/28.
 */
public class NetCloudBookstoreBookTypes {
    private int bookId;
    private float price;
    private float pressprice;

    @Override
    public String toString() {
        return "NetCloudBookstoreBookTypes{" +
                "bookId=" + bookId +
                ", price=" + price +
                ", pressprice=" + pressprice +
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

    public float getPressprice() {
        return pressprice;
    }

    public void setPressprice(float pressprice) {
        this.pressprice = pressprice;
    }
}
