package com.digital.dl2.business.net.obj;

import java.util.Arrays;

/**
 * Created by digital.dl2 on 15/9/25.
 */
public class NetBookForLibraryDetailData {
    private int goods_id;
    private String thumb;
    private float price;
    private String title;
    private String author;
    private String publish_date;
    private String book_number;
    private String issuers;
    private String description;
    private int type;//1.图书  2.自有资源   3.共享资源
    private boolean is_borrowing;
    private boolean is_can_borrowing;
    private boolean is_can_renew;
    private boolean is_reservations;
    private String qr_code;
    private String[] catalog;
    private String[] trial;
    private String download_url;

    @Override
    public String toString() {
        return "NetBookForLibraryDetailData{" +
                "goods_id=" + goods_id +
                ", thumb='" + thumb + '\'' +
                ", price=" + price +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publish_date=" + publish_date +
                ", book_number='" + book_number + '\'' +
                ", issuers='" + issuers + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", is_borrowing=" + is_borrowing +
                ", is_can_borrowing=" + is_can_borrowing +
                ", is_can_renew=" + is_can_renew +
                ", is_reservations=" + is_reservations +
                ", qr_code='" + qr_code + '\'' +
                ", catalog=" + Arrays.toString(catalog) +
                ", trial=" + Arrays.toString(trial) +
                ", download_url='" + download_url + '\'' +
                '}';
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public String getBook_number() {
        return book_number;
    }

    public void setBook_number(String book_number) {
        this.book_number = book_number;
    }

    public String getIssuers() {
        return issuers;
    }

    public void setIssuers(String issuers) {
        this.issuers = issuers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean is_borrowing() {
        return is_borrowing;
//        return false;
    }

    public void setIs_borrowing(boolean is_borrowing) {
        this.is_borrowing = is_borrowing;
    }

    public boolean is_can_borrowing() {
        return is_can_borrowing;
//        return true;
    }

    public void setIs_can_borrowing(boolean is_can_borrowing) {
        this.is_can_borrowing = is_can_borrowing;
    }

    public boolean is_can_renew() {
        return is_can_renew;
        //return false;
    }

    public void setIs_can_renew(boolean is_can_renew) {
        this.is_can_renew = is_can_renew;
    }

    public boolean is_reservations() {
        return is_reservations;
//        return false;
    }

    public void setIs_reservations(boolean is_reservations) {
        this.is_reservations = is_reservations;
    }

    public String[] getCatalog() {
        return catalog;
    }

    public void setCatalog(String[] catalog) {
        this.catalog = catalog;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public String[] getTrial() {
        return trial;
    }

    public void setTrial(String[] trial) {
        this.trial = trial;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }
}
