package com.crphdm.dl2.views;

/**
 * Modify by songzixuan on 19/07/04.
 * 二维码
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
            //获取书号
    public int getBookid() {
        return bookid;
    }
            //设置书号
    public void setBookid(int bookid) {
        this.bookid = bookid;
    }
            //获取类别
    public int getType() {
        return type;
    }
            //设置类别
    public void setType(int type) {
        this.type = type;
    }
}
