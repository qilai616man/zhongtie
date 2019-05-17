package com.crphdm.dl2.views;

/**
 * Created by qinqi on 15/12/11.
 */
public class QrCode {
    private int bookid;
    private int type;

    @Override
    public String toString() {
        return "QrCode{" +
                "bookid=" + bookid +
                ", type=" + type +
                '}';
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
