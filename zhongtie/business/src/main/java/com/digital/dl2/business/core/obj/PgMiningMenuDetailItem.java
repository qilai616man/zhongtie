package com.digital.dl2.business.core.obj;

/**
 * Created by qinqi on 15/12/8.
 */
public class PgMiningMenuDetailItem {
    private int id;
    private int orgId;
    private int goodsId;
    private String thumb;
    private int number;
    private String title;
    private String author;
    private float price;
    private float oldPrice;
    private long addTime;

    @Override
    public String toString() {
        return "PgMiningMenuDetailItem{" +
                "id=" + id +
                ", orgId=" + orgId +
                ", goodsId=" + goodsId +
                ", thumb='" + thumb + '\'' +
                ", number=" + number +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price +
                ", oldPrice=" + oldPrice +
                ", addTime=" + addTime +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(float oldPrice) {
        this.oldPrice = oldPrice;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }


}
